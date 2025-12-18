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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

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



}
