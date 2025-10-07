package br.com.studios.sketchbook.service_management_core.product.price_related.api;

import br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_assignment.PriceEntryAssignmentCreationDTO;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_entry.PriceEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.def_product.ProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class PriceEntryAssignmentControllerTest {

    private final MockMvc mock;
    private final ObjectMapper mapper;

    @Autowired
    public PriceEntryAssignmentControllerTest(MockMvc mock, ObjectMapper mapper) {
        this.mock = mock;
        this.mapper = mapper;
    }

    // Helpers para criar product, price entry e assignment via API
    private JsonNode createProduct(String name, VolumeType type) throws Exception {
        ProductCreationDTO dto = new ProductCreationDTO(name, type);
        String json = mapper.writeValueAsString(dto);

        String resp = mock.perform(put("/products/product/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readTree(resp);
    }

    private JsonNode createPriceEntry(Double value) throws Exception {
        PriceEntryCreationDTO dto = new PriceEntryCreationDTO(value, "BRL");
        String json = mapper.writeValueAsString(dto);

        String resp = mock.perform(put("/entry/price/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readTree(resp);
    }

    private JsonNode createAssignment(UUID entryId, UUID ownerId) throws Exception {
        PriceEntryAssignmentCreationDTO dto = new PriceEntryAssignmentCreationDTO(entryId, ownerId);
        String json = mapper.writeValueAsString(dto);

        String resp = mock.perform(put("/entry/price/assignment/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readTree(resp);
    }

    @Test
    @DisplayName("Criar assignment e validar retorno")
    void createAssignmentTest() throws Exception {
        JsonNode prod = createProduct("Produto A", VolumeType.UNIT);
        JsonNode entry = createPriceEntry(10.0);

        UUID ownerId = UUID.fromString(prod.get("id").asText());
        UUID entryId = UUID.fromString(entry.get("id").asText());

        JsonNode assignment = createAssignment(entryId, ownerId);

        // Verifica campos mínimos do DTO retornado
        mock.perform(get("/entry/price/assignment/id/{id}", assignment.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(assignment.get("id").asText()))
                .andExpect(jsonPath("$.entryId").value(entryId.toString()))
                .andExpect(jsonPath("$.ownerId").value(ownerId.toString()));
    }

    @Test
    @DisplayName("Buscar assignment por ownerId e por entryId")
    void getByOwnerAndEntryTest() throws Exception {
        JsonNode prod = createProduct("Produto B", VolumeType.KILOGRAM);
        JsonNode entry = createPriceEntry(5.5);

        UUID ownerId = UUID.fromString(prod.get("id").asText());
        UUID entryId = UUID.fromString(entry.get("id").asText());

        JsonNode assignment = createAssignment(entryId, ownerId);
        String assignmentId = assignment.get("id").asText();

        // Buscar por owner id
        mock.perform(get("/entry/price/assignment/owner/id/{id}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(assignmentId))
                .andExpect(jsonPath("$.ownerId").value(ownerId.toString()));

        // Buscar por entry id
        mock.perform(get("/entry/price/assignment/entry/id/{id}", entryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(assignmentId))
                .andExpect(jsonPath("$.entryId").value(entryId.toString()));
    }

    @Test
    @DisplayName("Limpeza de assignments órfãos remove assignment quando owner é deletado")
    void cleanOrphanAfterOwnerDeleteTest() throws Exception {
        // cria product + entry + assignment
        JsonNode prod = createProduct("Produto C", VolumeType.LITER);
        JsonNode entry = createPriceEntry(7.25);

        UUID ownerId = UUID.fromString(prod.get("id").asText());
        UUID entryId = UUID.fromString(entry.get("id").asText());

        JsonNode assignment = createAssignment(entryId, ownerId);
        UUID assignmentId = UUID.fromString(assignment.get("id").asText());

        // deleta owner (produto)
        mock.perform(delete("/products/product/delete/id/{id}", ownerId))
                .andExpect(status().isOk());

        // executa limpeza do assignment órfão passando o id
        mock.perform(delete("/entry/price/assignment/clean_orphan/id{id}", assignmentId))
                .andExpect(status().isOk())
                .andExpect(content().string("ligações entre as entidades de preço foram limpas"));

        // assignment deve ter sido removido
        mock.perform(get("/entry/price/assignment/id/{id}", assignmentId))
                .andExpect(status().isNotFound());
    }

}
