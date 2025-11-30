package br.com.studios.sketchbook.service_management_core.storage_module.product.api;

import br.com.studios.sketchbook.service_management_core.application.ServiceManagementCoreApiApplication;
import br.com.studios.sketchbook.service_management_core.application.api_utils.config.StorageDataSourceConfig;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.super_market.SMProductCreationDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.ConfigRefNames.StorageConfig.storage_transaction_manager_ref;
import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.PackageNames.storage_module_path;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para o controller de Produtos de Supermercado.
 * Os testes validam:
 * - Criação de produto
 * - Atualização de produto
 * - Busca por nome
 * - Remoção por ID
 * Cada teste roda dentro de uma transação e é revertido no final,
 * garantindo independência entre eles.
 */
@ActiveProfiles({"test", "storage"})
@AutoConfigureMockMvc
@Transactional(storage_transaction_manager_ref)
@SpringBootTest(classes = {
        ServiceManagementCoreApiApplication.class,
        StorageDataSourceConfig.class
})
public class SMProductControllerTest {

    private final MockMvc mock;
    private final ObjectMapper mapper;

    @Autowired
    public SMProductControllerTest(MockMvc mock, ObjectMapper mapper) {
        this.mock = mock;
        this.mapper = mapper;
    }

    /**
     * Cria um produto de supermercado e retorna o JSON da resposta.
     */
    private JsonNode createProduct(String name,String barcode) throws Exception {
        SMProductCreationDTO dto = new SMProductCreationDTO(name, barcode);
        String json = mapper.writeValueAsString(dto);

        String response = mock.perform(
                        post("/products/super-market/new")
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
        // Cria produto
        JsonNode createdProduct = createProduct("arroz", "0999.0888.0777");

        // Verifica retorno
        mock.perform(
                        get("/products/super-market/id/{id}", createdProduct.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("arroz"));
    }

    @Test
    public void updateTest() throws Exception {
        // Cria produto inicial
        JsonNode createdProduct = createProduct("arroz", "0999.0888.0777");
        String productId = createdProduct.get("id").asText();

        // DTO de atualização
        SMProductCreationDTO updateDto = new SMProductCreationDTO(
                "arroz especial",
                "0999.0888.0777"
        );
        String updateJson = mapper.writeValueAsString(updateDto);

        // Faz update
        mock.perform(
                        patch("/products/super-market/update/{id}", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("arroz especial"));
    }

    @Test
    public void getByNameTest() throws Exception {
        // Cria produto
        createProduct("arroz",  "0999.0888.0777");

        // Busca por nome
        mock.perform(
                        get("/products/super-market/name/{name}", "arroz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("arroz"));
    }

    @Test
    public void removeByIdTest() throws Exception {
        // Cria produto
        JsonNode createdProduct = createProduct("feijão", "0888.0777.0666");
        String productId = createdProduct.get("id").asText();

        // Remove produto
        mock.perform(
                        delete("/products/super-market/delete/id/{id}", productId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Produto Removido com sucesso"));

        // Verifica que não existe mais
        mock.perform(
                        get("/products/super-market/id/{id}", productId))
                .andExpect(status().isNotFound());
    }
}
