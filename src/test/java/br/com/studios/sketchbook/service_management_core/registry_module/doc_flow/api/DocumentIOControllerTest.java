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
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    /// Gerador sob teste
    private final DocumentGenerator docGen;

    private final DocumentIO docIO;

    private DocumentData currentDocument;
    private final MockMvc mock;


    /// Estrutura mutável usada como entrada para geração
    private static final List<List<Object>> tableMapping = new ArrayList<>();

    @Autowired
    public DocumentIOControllerTest(MockMvc mock, ObjectMapper mapper) {
        this.docIO = new DocumentIO(mapper);
        this.docGen = new DocumentGenerator();

        this.mock = mock;
    }

    /**
     * Limpeza após cada teste para garantir isolamento.
     */
    @AfterEach
    void afterEach() {
        tableMapping.clear();

        if (currentDocument != null) {
            docIO.deleteAllTableComponentsIfPresentAsDocument(currentDocument);
            currentDocument = null;
        }
    }

    public void createAndSaveDummyTable() {
        tableMapping.add(Arrays.asList(
                1010111L,
                "testando_a_table",
                true
        ));

        List<String> cellNames = Arrays.asList(
                "id",
                "nome",
                "ativo"
        );

        // ---------- Act ----------
        currentDocument = docGen.generateDocument(
                tableMapping,
                cellNames,
                "",
                true,
                DocumentPrefix.NON_DEFINED
        );

        docIO.saveDocument(currentDocument);
    }

    @Test
    void shouldCreateTableAndFindItViaControllerPagedEndpoint() throws Exception {
        // ---------- Arrange ----------
        createAndSaveDummyTable();
        assertNotNull(currentDocument, "Documento não foi gerado");

        // ---------- Act ----------
        MvcResult result = mock.perform(
                        get("/document/all")
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);

        // estrutura padrão do Page<T>
        JsonNode contentNode = root.get("content");
        assertNotNull(contentNode, "Campo 'content' não encontrado na resposta");
        assertTrue(contentNode.isArray(), "'content' não é uma lista");

        Integer foundId = null;

        for (JsonNode node : contentNode) {
            int id = node.get("id").asInt();
            String name = node.get("name").asText();
            String prefix = node.get("prefix").asText();
            boolean canBeOverridden = node.get("canBeOverridden").asBoolean();

            // ---------- Print ----------
            System.out.println("---- TABLE FOUND ----");
            System.out.println("ID: " + id);
            System.out.println("Name: " + name);
            System.out.println("Prefix: " + prefix);
            System.out.println("CanBeOverridden: " + canBeOverridden);
            System.out.println("---------------------");

            // critério mínimo para identificar a table criada no teste
            if (currentDocument.table().getId().equals(id)) {
                foundId = id;
                break;
            }
        }

        // ---------- Assert ----------
        assertNotNull(
                foundId,
                "A table criada no teste não foi encontrada na resposta do controller"
        );
    }

    @Test
    void shouldCreateThreeTablesAndDeleteThemViaController() throws Exception {
        // ---------- Arrange ----------
        List<Integer> generatedIds = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            createAndSaveDummyTable();

            assertNotNull(currentDocument, "Documento não foi gerado");

            generatedIds.add(currentDocument.table().getId());

            // impede o @AfterEach de apagar antes da hora
            currentDocument = null;
        }

        assertEquals(3, generatedIds.size(), "Quantidade de IDs gerados incorreta");

        // ---------- Act : DELETE ----------
        ObjectMapper mapper = new ObjectMapper();
        String deletePayload = mapper.writeValueAsString(generatedIds);

        mock.perform(
                        delete("/document/delete/many")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(deletePayload)
                )
                .andExpect(status().isOk());

        // ---------- Assert : confirmar que não existem mais ----------
        MvcResult resultAfterDelete = mock.perform(
                        get("/document/all")
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = resultAfterDelete.getResponse().getContentAsString();
        JsonNode root = mapper.readTree(jsonResponse);
        JsonNode contentNode = root.get("content");

        for (JsonNode node : contentNode) {
            int remainingId = node.get("id").asInt();

            assertFalse(
                    generatedIds.contains(remainingId),
                    "Table com ID deletado ainda existe: " + remainingId
            );
        }
    }

    @Test
    void shouldLoadDocumentDetailedViaController() throws Exception {
        // ---------- Arrange ----------
        createAndSaveDummyTable();
        assertNotNull(currentDocument, "Documento não foi gerado");

        Integer tableId = currentDocument.table().getId();

        // ---------- Act ----------
        MvcResult result = mock.perform(
                        get("/document/id/{id}", tableId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);

        // ---------- Assert ----------
        assertEquals(tableId, root.get("id").asInt());
        assertEquals(
                currentDocument.table().getName(),
                root.get("name").asText()
        );

        assertTrue(root.get("tableData").isArray(), "tableData não é array");
        assertTrue(root.get("columnNames").isArray(), "columnNames não é array");

        assertEquals(
                currentDocument.table().isCanBeOverridden(),
                root.get("canBeOverridden").asBoolean()
        );
    }
    @Test
    void shouldReturn404WhenDocumentDoesNotExist() throws Exception {
        // ---------- Arrange ----------
        int nonexistentId = 999999;

        // ---------- Act / Assert ----------
        mock.perform(
                        get("/document/id/{id}", nonexistentId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldExportDocumentToPdfViaController() throws Exception {
        // ---------- Arrange ----------
        createAndSaveDummyTable();
        assertNotNull(currentDocument, "Documento não foi gerado");

        Integer tableId = currentDocument.table().getId();

        // ---------- Act ----------
        mock.perform(
                        get("/document/pdf/id/{id}/export", tableId)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // ---------- Assert ----------
        // verifica se o PDF foi realmente gerado no filesystem
        Path pdfPath = document_pdf_folder_path.resolve(
                pdfFileName(tableId)
        );

        assertTrue(
                Files.exists(pdfPath),
                "O arquivo PDF não foi gerado no caminho esperado"
        );

        assertTrue(
                Files.size(pdfPath) > 0,
                "O arquivo PDF foi gerado, mas está vazio"
        );

         Files.deleteIfExists(pdfPath);
    }

    @Test
    void shouldListAllGeneratedPdfIds() throws Exception {
        // ---------- Arrange ----------
        createAndSaveDummyTable();
        Integer tableId = currentDocument.table().getId();

        // gera o PDF
        mock.perform(
                get("/document/pdf/id/{id}/export", tableId)
        ).andExpect(status().isOk());

        // ---------- Act ----------
        MvcResult result = mock.perform(
                        get("/document/pdf/all")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(result.getResponse().getContentAsString());

        // ---------- Assert ----------
        assertTrue(root.isArray(), "Resposta não é uma lista");
        assertTrue(
                StreamSupport.stream(root.spliterator(), false)
                        .anyMatch(n -> n.asInt() == tableId),
                "ID do PDF gerado não foi listado"
        );
    }

    @Test
    void shouldLoadPdfBinaryByTableId() throws Exception {
        // ---------- Arrange ----------
        createAndSaveDummyTable();
        Integer tableId = currentDocument.table().getId();

        mock.perform(
                get("/document/pdf/id/{id}/export", tableId)
        ).andExpect(status().isOk());

        // ---------- Act ----------
        MvcResult result = mock.perform(
                        get("/document/pdf/id/{id}", tableId)
                )
                .andExpect(status().isOk())
                .andReturn();

        byte[] pdfBytes = result.getResponse().getContentAsByteArray();

        // ---------- Assert ----------
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0, "PDF retornado está vazio");
    }

    @Test
    void shouldReturn404WhenPdfDoesNotExist() throws Exception {
        // ---------- Arrange ----------
        int nonexistentId = 123456;

        // ---------- Act / Assert ----------
        mock.perform(
                get("/document/pdf/id/{id}", nonexistentId)
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePdfByTableId() throws Exception {
        // ---------- Arrange ----------
        createAndSaveDummyTable();
        Integer tableId = currentDocument.table().getId();

        mock.perform(
                get("/document/pdf/id/{id}/export", tableId)
        ).andExpect(status().isOk());

        Path pdfPath = document_pdf_folder_path.resolve(
                pdfFileName(tableId)
        );

        assertTrue(Files.exists(pdfPath), "PDF não foi criado");

        // ---------- Act ----------
        mock.perform(
                delete("/document/pdf/id/{id}", tableId)
        ).andExpect(status().isOk());

        // ---------- Assert ----------
        assertFalse(
                Files.exists(pdfPath),
                "PDF ainda existe após deleção"
        );
    }

    @Test
    void shouldReturn404WhenDeletingNonexistentPdf() throws Exception {
        // ---------- Arrange ----------
        int nonexistentId = 987654;

        // ---------- Act / Assert ----------
        mock.perform(
                delete("/document/pdf/id/{id}", nonexistentId)
        ).andExpect(status().isNotFound());
    }


}
