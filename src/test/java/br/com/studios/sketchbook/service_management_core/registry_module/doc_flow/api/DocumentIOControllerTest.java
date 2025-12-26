package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.api;

import br.com.studios.sketchbook.service_management_core.application.ServiceManagementCoreApiApplication;
import br.com.studios.sketchbook.service_management_core.application.api_utils.config.TestConfig;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.enumerators.DocumentPrefix;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentGenerator;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentIO;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.PathDirection.document_pdf_folder_path;
import static br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.naming.NamingArchives.pdfFileName;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = {
        ServiceManagementCoreApiApplication.class,
        TestConfig.class
})
public class DocumentIOControllerTest {

    private static final List<List<Object>> TABLE_MAPPING = new ArrayList<>();
    private static final List<String> CELL_NAMES = Arrays.asList("id", "nome", "ativo");
    private static final int NONEXISTENT_ID = 999999;

    private final DocumentGenerator docGen;
    private final DocumentIO docIO;
    private final MockMvc mock;
    private final ObjectMapper mapper;

    private DocumentData currentDocument;

    @Autowired
    public DocumentIOControllerTest(MockMvc mock, ObjectMapper mapper) {
        this.mock = mock;
        this.mapper = mapper;
        this.docIO = new DocumentIO(mapper);
        this.docGen = new DocumentGenerator();
    }

    @AfterEach
    void afterEach() {
        TABLE_MAPPING.clear();

        if (currentDocument != null) {
            docIO.deleteAllTableComponentsIfPresentAsDocument(currentDocument);

            // Apaga o PDF se existir
            Integer tableId = currentDocument.table().getId();
            Path pdfPath = document_pdf_folder_path.resolve(pdfFileName(tableId));
            try {
                Files.deleteIfExists(pdfPath);
            } catch (Exception e) {
                // ignora se falhar
            }

            currentDocument = null;
        }
    }

    private void createAndSaveDummyTable() {
        TABLE_MAPPING.add(Arrays.asList(1010111L, "testando_a_table", true));

        currentDocument = docGen.generateDocument(
                TABLE_MAPPING,
                CELL_NAMES,
                "",
                true,
                DocumentPrefix.NON_DEFINED
        );

        docIO.saveDocument(currentDocument);
    }

    private JsonNode performGetAndParseJson(String url, Object... uriVars) throws Exception {
        MvcResult result = mock.perform(get(url, uriVars).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        return mapper.readTree(result.getResponse().getContentAsString());
    }

    @Test
    void shouldCreateTableAndFindItViaControllerPagedEndpoint() throws Exception {
        createAndSaveDummyTable();
        assertNotNull(currentDocument, "Documento não foi gerado");

        JsonNode root = performGetAndParseJson("/document/all?page=0&size=10");
        JsonNode contentNode = root.get("content");

        assertNotNull(contentNode, "Campo 'content' não encontrado");
        assertTrue(contentNode.isArray(), "'content' não é uma lista");

        Integer foundId = StreamSupport.stream(contentNode.spliterator(), false)
                .filter(node -> node.get("id").asInt() == currentDocument.table().getId())
                .map(node -> node.get("id").asInt())
                .findFirst()
                .orElse(null);

        assertNotNull(foundId, "A table criada não foi encontrada");
    }

    @Test
    void shouldCreateThreeTablesAndDeleteThemViaController() throws Exception {
        List<Integer> generatedIds = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            createAndSaveDummyTable();
            assertNotNull(currentDocument, "Documento não foi gerado");
            generatedIds.add(currentDocument.table().getId());
            currentDocument = null;
        }

        assertEquals(3, generatedIds.size(), "Quantidade de IDs gerados incorreta");

        mock.perform(delete("/document/delete/many")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(generatedIds)))
                .andExpect(status().isOk());

        JsonNode root = performGetAndParseJson("/document/all?page=0&size=10");
        JsonNode contentNode = root.get("content");

        StreamSupport.stream(contentNode.spliterator(), false)
                .map(node -> node.get("id").asInt())
                .forEach(id -> assertFalse(
                        generatedIds.contains(id),
                        "Table deletada ainda existe: " + id
                ));
    }

    @Test
    void shouldLoadDocumentDetailedViaController() throws Exception {
        createAndSaveDummyTable();
        assertNotNull(currentDocument, "Documento não foi gerado");

        Integer tableId = currentDocument.table().getId();
        JsonNode root = performGetAndParseJson("/document/id/{id}", tableId);

        assertEquals(tableId, root.get("id").asInt());
        assertEquals(currentDocument.table().getName(), root.get("name").asText());
        assertTrue(root.get("tableData").isArray(), "tableData não é array");
        assertTrue(root.get("columnNames").isArray(), "columnNames não é array");
        assertEquals(currentDocument.table().isCanBeOverridden(), root.get("canBeOverridden").asBoolean());
    }

    @Test
    void shouldReturn404WhenDocumentDoesNotExist() throws Exception {
        mock.perform(get("/document/id/{id}", NONEXISTENT_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldExportDocumentToPdfViaController() throws Exception {
        createAndSaveDummyTable();
        assertNotNull(currentDocument, "Documento não foi gerado");

        Integer tableId = currentDocument.table().getId();
        Path pdfPath = document_pdf_folder_path.resolve(pdfFileName(tableId));

        mock.perform(get("/document/pdf/id/{id}/export", tableId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertTrue(Files.exists(pdfPath), "PDF não foi gerado");
        assertTrue(Files.size(pdfPath) > 0, "PDF está vazio");
    }

    @Test
    void shouldListAllGeneratedPdfIds() throws Exception {
        createAndSaveDummyTable();
        Integer tableId = currentDocument.table().getId();

        mock.perform(get("/document/pdf/id/{id}/export", tableId)).andExpect(status().isOk());

        JsonNode root = performGetAndParseJson("/document/pdf/all");

        assertTrue(root.isArray(), "Resposta não é uma lista");
        assertTrue(
                StreamSupport.stream(root.spliterator(), false).anyMatch(n -> n.asInt() == tableId),
                "ID do PDF não foi listado"
        );
    }

    @Test
    void shouldLoadPdfBinaryByTableId() throws Exception {
        createAndSaveDummyTable();
        Integer tableId = currentDocument.table().getId();

        mock.perform(get("/document/pdf/id/{id}/export", tableId)).andExpect(status().isOk());

        MvcResult result = mock.perform(get("/document/pdf/id/{id}", tableId))
                .andExpect(status().isOk())
                .andReturn();

        byte[] pdfBytes = result.getResponse().getContentAsByteArray();

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0, "PDF retornado está vazio");
    }

    @Test
    void shouldReturn404WhenPdfDoesNotExist() throws Exception {
        mock.perform(get("/document/pdf/id/{id}", NONEXISTENT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePdfByTableId() throws Exception {
        createAndSaveDummyTable();
        Integer tableId = currentDocument.table().getId();
        Path pdfPath = document_pdf_folder_path.resolve(pdfFileName(tableId));

        mock.perform(get("/document/pdf/id/{id}/export", tableId)).andExpect(status().isOk());
        assertTrue(Files.exists(pdfPath), "PDF não foi criado");

        mock.perform(delete("/document/pdf/id/{id}", tableId)).andExpect(status().isOk());
        assertFalse(Files.exists(pdfPath), "PDF ainda existe após deleção");
    }

    @Test
    void shouldReturn404WhenDeletingNonexistentPdf() throws Exception {
        mock.perform(delete("/document/pdf/id/{id}", NONEXISTENT_ID))
                .andExpect(status().isNotFound());
    }
}