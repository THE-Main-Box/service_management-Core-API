package br.com.studios.sketchbook.service_management_core.storage.api;

import br.com.studios.sketchbook.service_management_core.product.domain.dto.product.ProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.shared.enums.VolumeType;
import br.com.studios.sketchbook.service_management_core.storage.domain.dto.StorageEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage.domain.dto.StorageEntryUpdateDTO;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para o StorageEntryController.
 *
 * Casos cobertos:
 *  - Criar entry com dono válido
 *  - Criar entry com dono inexistente (erro 404)
 *  - Editar entry existente
 *  - Deletar entry com dono existente (deve falhar)
 *  - Deletar entry após remoção do dono (deve ocorrer)
 *  - Buscar entry por id e por id do dono
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class StorageEntryControllerTest {

    private final MockMvc mock;
    private final ObjectMapper mapper;

    @Autowired
    public StorageEntryControllerTest(MockMvc mock, ObjectMapper mapper) {
        this.mock = mock;
        this.mapper = mapper;
    }

    /* helper: cria um produto que servirá como owner */
    private JsonNode createOwnerProduct(String name) throws Exception {
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

    /* helper: cria uma StorageEntry via endpoint e retorna o JSON da resposta */
    private JsonNode createEntry(UUID ownerId, VolumeType volumeType, long quantity, long quantityPerUnit, boolean isRaw) throws Exception {
        StorageEntryCreationDTO dto = new StorageEntryCreationDTO(ownerId, volumeType, quantity, quantityPerUnit, isRaw);
        String json = mapper.writeValueAsString(dto);

        String response = mock.perform(
                        put("/entry/storage/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readTree(response);
    }

    @Test
    public void createEntryWithOwnerTest() throws Exception {
        JsonNode owner = createOwnerProduct("Produto Estoque 1");
        UUID ownerId = UUID.fromString(owner.get("id").asText());

        mock.perform(put("/entry/storage/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new StorageEntryCreationDTO(ownerId, VolumeType.UNIT, 10L, 2L, true)
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").value(ownerId.toString()))
                .andExpect(jsonPath("$.volumeType").value("UNIT"));
    }

    @Test
    public void createEntryWithoutOwnerTest() throws Exception {
        UUID fakeOwnerId = UUID.randomUUID();

        mock.perform(put("/entry/storage/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new StorageEntryCreationDTO(fakeOwnerId, VolumeType.UNIT, 5L, 1L, true)
                        )))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateEntryTest() throws Exception {
        JsonNode owner = createOwnerProduct("Produto Estoque 2");
        UUID ownerId = UUID.fromString(owner.get("id").asText());

        JsonNode created = createEntry(ownerId, VolumeType.UNITY_PER_UNITY, 20L, 4L, true);
        String id = created.get("id").asText();

        StorageEntryUpdateDTO dto = new StorageEntryUpdateDTO(VolumeType.UNIT, 50L, 0L, 4L, false);

        mock.perform(patch("/entry/storage/update/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.units").value(50))
                .andExpect(jsonPath("$.volumeType").value("UNIT"));
    }

    @Test
    public void deleteEntryWithOwnerExistingTest() throws Exception {
        JsonNode owner = createOwnerProduct("Produto Estoque 3");
        UUID ownerId = UUID.fromString(owner.get("id").asText());

        JsonNode created = createEntry(ownerId, VolumeType.UNIT, 10L, 2L, true);
        String id = created.get("id").asText();

        mock.perform(delete("/entry/storage/delete/id/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Não foi possivel deletar a entry"));
    }

    @Test
    public void deleteEntryWithoutOwnerTest() throws Exception {
        JsonNode owner = createOwnerProduct("Produto Estoque 4");
        UUID ownerId = UUID.fromString(owner.get("id").asText());
        JsonNode created = createEntry(ownerId, VolumeType.UNIT, 10L, 2L, true);
        String id = created.get("id").asText();

        // remove o dono primeiro
        mock.perform(delete("/products/product/delete/id/{id}", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().string("Produto Removido com sucesso"));

        // agora a entry deve ser apagada pelo endpoint
        mock.perform(delete("/entry/storage/delete/id/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Entrada de armazenamento apagada com sucesso"));
    }

    @Test
    public void getByOwnerIdTest() throws Exception {
        JsonNode owner = createOwnerProduct("Produto Estoque 5");
        UUID ownerId = UUID.fromString(owner.get("id").asText());

        JsonNode created = createEntry(ownerId, VolumeType.UNITY_PER_UNITY, 15L, 3L, true);
        String entryId = created.get("id").asText();

        mock.perform(get("/entry/storage/owner/id/{id}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entryId))
                .andExpect(jsonPath("$.ownerId").value(ownerId.toString()));
    }

    @Test
    public void getByIdTest() throws Exception {
        JsonNode owner = createOwnerProduct("Produto Estoque 6");
        UUID ownerId = UUID.fromString(owner.get("id").asText());

        JsonNode created = createEntry(ownerId, VolumeType.UNITY_PER_UNITY, 25L, 5L, true);
        String entryId = created.get("id").asText();

        mock.perform(get("/entry/storage/id/{id}", entryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entryId))
                .andExpect(jsonPath("$.ownerId").value(ownerId.toString()));
    }
}
