package br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.api;

import br.com.studios.sketchbook.service_management_core.aplication.ServiceManagementCoreApiApplication;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.dto.PriceEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.dto.PriceEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model.Product;
import br.com.studios.sketchbook.service_management_core.storage_module.product.infra.repositories.ProductRepository;
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

import java.util.UUID;

import static br.com.studios.sketchbook.service_management_core.aplication.api_utils.references.ConfigRefNames.StorageConfig.storage_transaction_manager_ref;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles({"test", "storage"})
@AutoConfigureMockMvc
@Transactional(storage_transaction_manager_ref)
@SpringBootTest(classes = ServiceManagementCoreApiApplication.class)
public class PriceEntryControllerTest {

    private final MockMvc mock;
    private final ObjectMapper mapper;
    private final ProductRepository productRepository;

    @Autowired
    public PriceEntryControllerTest(MockMvc mock, ObjectMapper mapper, ProductRepository productRepository) {
        this.mock = mock;
        this.mapper = mapper;
        this.productRepository = productRepository;
    }

    private UUID createDummyOwner() {
        Product product = new Product();
        product.setName("Dummy Product");
        productRepository.save(product);
        return product.getId();
    }

    private JsonNode createPriceEntry(Double value, String currency) throws Exception {
        UUID ownerId = createDummyOwner();
        PriceEntryCreationDTO dto = new PriceEntryCreationDTO(value, currency, ownerId);
        String json = mapper.writeValueAsString(dto);

        String response = mock.perform(
                        put("/entry/price/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readTree(response);
    }

    // ------------------------------
    // CRIAÇÃO
    // ------------------------------

    @Test
    public void createWithValidOwner_ShouldCreateSuccessfully() throws Exception {
        JsonNode created = createPriceEntry(10.5, "USD");

        mock.perform(get("/entry/price/id/{id}", created.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(10.5))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.ownerId").isNotEmpty());
    }

    @Test
    public void createWithoutOwner_ShouldFail() throws Exception {
        PriceEntryCreationDTO dto = new PriceEntryCreationDTO(12.0, "BRL", null);
        String json = mapper.writeValueAsString(dto);

        mock.perform(put("/entry/price/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------
    // UPDATE
    // ------------------------------

    @Test
    public void update_ShouldChangeValueAndCurrency() throws Exception {
        JsonNode created = createPriceEntry(20.0, "BRL");
        String id = created.get("id").asText();

        PriceEntryUpdateDTO dto = new PriceEntryUpdateDTO(25.0, "USD");
        String json = mapper.writeValueAsString(dto);

        mock.perform(patch("/entry/price/update/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(25.0))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    // ------------------------------
    // GET
    // ------------------------------

    @Test
    public void getById_ShouldReturnCorrectEntry() throws Exception {
        JsonNode created = createPriceEntry(50.0, "EUR");
        String id = created.get("id").asText();

        mock.perform(get("/entry/price/id/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.price").value(50.0))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    public void getByOwnerId_ShouldReturnEntry() throws Exception {
        JsonNode created = createPriceEntry(60.0, "GBP");
        String ownerId = created.get("ownerId").asText();

        mock.perform(get("/entry/price/owner/id/{ownerId}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(60.0))
                .andExpect(jsonPath("$.currency").value("GBP"));
    }

    // ------------------------------
    // DELETE SINGLE
    // ------------------------------

    @Test
    public void deleteEntryWithoutDeletingOwner_ShouldNotDelete() throws Exception {
        JsonNode created = createPriceEntry(100.0, "JPY");
        String id = created.get("id").asText();

        mock.perform(delete("/entry/price/delete/id/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Não foi possivel deletar")));
    }

    @Test
    public void deleteEntryAfterDeletingOwner_ShouldDelete() throws Exception {
        JsonNode created = createPriceEntry(120.0, "CAD");
        String id = created.get("id").asText();
        UUID ownerId = UUID.fromString(created.get("ownerId").asText());

        productRepository.deleteById(ownerId);

        mock.perform(delete("/entry/price/delete/id/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("apagada com sucesso")));
    }

}
