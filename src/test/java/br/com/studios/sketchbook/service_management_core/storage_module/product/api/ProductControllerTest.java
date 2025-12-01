package br.com.studios.sketchbook.service_management_core.storage_module.product.api;

import br.com.studios.sketchbook.service_management_core.application.ServiceManagementCoreApiApplication;
import br.com.studios.sketchbook.service_management_core.application.api_utils.config.TestConfig;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.product.ProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.product.ProductUpdateDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.ConfigRefNames.StorageConfig.storage_transaction_manager_ref;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = {
        ServiceManagementCoreApiApplication.class,
        TestConfig.class
})
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mock;

    @Autowired
    private ObjectMapper mapper;

    /**
     * Utilitário para criar um produto via API e retornar o JSON de resposta.
     */
    private JsonNode createProduct(String name) throws Exception {
        ProductCreationDTO dto = new ProductCreationDTO(name);
        String json = mapper.writeValueAsString(dto);

        String response = mock.perform(
                        post("/products/product/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readTree(response);
    }

    @Test
    public void createTest() throws Exception {
        JsonNode created = createProduct("Arroz");

        mock.perform(get("/products/product/id/{id}", created.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Arroz"));
    }

    @Test
    public void updateTest() throws Exception {
        JsonNode created = createProduct("Feijão");
        String id = created.get("id").asText();

        ProductUpdateDTO dto = new ProductUpdateDTO("Feijão Preto");
        String updateJson = mapper.writeValueAsString(dto);

        mock.perform(
                        patch("/products/product/update/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Feijão Preto"));
    }

    @Test
    public void getByIdTest() throws Exception {
        JsonNode created = createProduct("Macarrão");
        String id = created.get("id").asText();

        mock.perform(get("/products/product/id/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Macarrão"));
    }

    @Test
    public void getByNameTest() throws Exception {
        createProduct("Azeite");

        mock.perform(get("/products/product/name/{name}", "Azeite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Azeite"));
    }

    @Test
    public void getAllTest() throws Exception {
        createProduct("Óleo");
        createProduct("Sal");

        mock.perform(get("/products/product/all?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    public void removeByIdTest() throws Exception {
        JsonNode created = createProduct("Café");
        String id = created.get("id").asText();

        mock.perform(delete("/products/product/delete/id/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Produto Removido com sucesso"));

        mock.perform(get("/products/product/id/{id}", id))
                .andExpect(status().isNotFound());
    }

}