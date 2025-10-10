package br.com.studios.sketchbook.service_management_core.product.product_related.api;

import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.def_product.ProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.def_product.ProductUpdateDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para o ProductController.
 *
 * Endpoints cobertos:
 *  - Criar produto
 *  - Atualizar produto
 *  - Buscar por ID
 *  - Buscar por Nome
 *  - Buscar todos
 *  - Remover por ID
 *  - Remover lista de IDs
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class ProductControllerTest {

    private final MockMvc mock;
    private final ObjectMapper mapper;

    @Autowired
    public ProductControllerTest(MockMvc mock, ObjectMapper mapper) {
        this.mock = mock;
        this.mapper = mapper;
    }

    /**
     * Utilitário para criar um produto via API e retornar o JSON de resposta.
     */
    private JsonNode createProduct(String name) throws Exception {
        ProductCreationDTO dto = new ProductCreationDTO(name);
        String json = mapper.writeValueAsString(dto);

        String response = mock.perform(
                        put("/products/product/new")
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
        // volumeType não aparece no ResponseDTO, então não validamos aqui
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
