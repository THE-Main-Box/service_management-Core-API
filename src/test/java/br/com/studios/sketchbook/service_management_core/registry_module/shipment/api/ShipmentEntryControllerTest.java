package br.com.studios.sketchbook.service_management_core.registry_module.shipment.api;

import br.com.studios.sketchbook.service_management_core.application.ServiceManagementCoreApiApplication;
import br.com.studios.sketchbook.service_management_core.application.api_utils.config.TestConfig;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentIO;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req.AddressReferenceCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req.ItemShippedCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req.ShipmentEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = {
        ServiceManagementCoreApiApplication.class,
        TestConfig.class
})
@Transactional
public class ShipmentEntryControllerTest {

    private final MockMvc mock;
    private final ObjectMapper mapper;

    private final DocumentIO docIO;

    @Autowired
    public ShipmentEntryControllerTest(MockMvc mock, ObjectMapper mapper) {
        this.mock = mock;
        this.mapper = mapper;
        this.docIO = new DocumentIO(mapper);
    }

    /* ============================================================
       HELPERS
    ============================================================ */

    private ShipmentEntryCreationDTO makeDTO(
            UUID originId,
            UUID destinationId,
            UUID itemId,
            String originDesc,
            String destDesc,
            String itemName
    ) {
        AddressReferenceCreationDTO origin = new AddressReferenceCreationDTO(originId, originDesc);
        AddressReferenceCreationDTO dest = new AddressReferenceCreationDTO(destinationId, destDesc);

        ItemShippedCreationDTO item = new ItemShippedCreationDTO(
                itemId,
                itemName,
                10L,
                null,
                VolumeType.KILOGRAM
        );

        return new ShipmentEntryCreationDTO(
                LocalDate.now(),
                origin,
                dest,
                item
        );
    }

    private JsonNode createShipment(UUID originId, UUID destId, UUID itemId) throws Exception {
        ShipmentEntryCreationDTO dto = makeDTO(
                originId,
                destId,
                itemId,
                "Origem Teste",
                "Destino Teste",
                "Item Teste"
        );

        String response = mock.perform(
                        post("/entry/shipment/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dto))
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readTree(response);
    }

    /* ============================================================
       TESTES
    ============================================================ */

    @Test
    public void createShipmentTest() throws Exception {
        UUID originId = UUID.randomUUID();
        UUID destId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        ShipmentEntryCreationDTO dto = makeDTO(
                originId,
                destId,
                itemId,
                "Origem A",
                "Destino B",
                "Item XYZ"
        );

        mock.perform(
                        post("/entry/shipment/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())

                // origem -> snapshot
                .andExpect(jsonPath("$.originAddressRef.addressId")
                        .value(originId.toString()))
                .andExpect(jsonPath("$.originAddressRef.description")
                        .value("Origem A"))

                // destino -> snapshot
                .andExpect(jsonPath("$.destinationAddressRef.addressId")
                        .value(destId.toString()))
                .andExpect(jsonPath("$.destinationAddressRef.description")
                        .value("Destino B"))

                // item -> snapshot
                .andExpect(jsonPath("$.itemShipped.itemId")
                        .value(itemId.toString()))
                .andExpect(jsonPath("$.itemShipped.itemName")
                        .value("Item XYZ"));
    }


    @Test
    public void getByIdTest() throws Exception {
        JsonNode created = createShipment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        UUID id = UUID.fromString(created.get("id").asText());

        mock.perform(get("/entry/shipment/id/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    public void getAllTest() throws Exception {
        createShipment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        createShipment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        mock.perform(get("/entry/shipment/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void getByOriginAddressDescriptionTest() throws Exception {
        createShipment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        mock.perform(get("/entry/shipment/origin-address/description/{description}", "Origem Teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void getByDestinationAddressDescriptionTest() throws Exception {
        createShipment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        mock.perform(get("/entry/shipment/destination-address/description/{description}", "Destino Teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void getByItemNameTest() throws Exception {
        createShipment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        mock.perform(get("/entry/shipment/item/name/{name}", "Item Teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void getByOriginAddressIdTest() throws Exception {
        UUID originId = UUID.randomUUID();

        createShipment(originId, UUID.randomUUID(), UUID.randomUUID());

        mock.perform(get("/entry/shipment/origin-address/id/{id}", originId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].originAddressId").value(originId.toString()));
    }

    @Test
    public void getByDestinationAddressIdTest() throws Exception {
        UUID destId = UUID.randomUUID();

        createShipment(UUID.randomUUID(), destId, UUID.randomUUID());

        mock.perform(get("/entry/shipment/destination-address/id/{id}", destId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].destinationAddressId").value(destId.toString()));
    }

    @Test
    public void getByItemIdTest() throws Exception {
        UUID itemId = UUID.randomUUID();

        createShipment(UUID.randomUUID(), UUID.randomUUID(), itemId);

        mock.perform(get("/entry/shipment/item/id/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].itemId").value(itemId.toString()));
    }

    @Test
    public void deleteShipmentTest() throws Exception {
        JsonNode created = createShipment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        UUID id = UUID.fromString(created.get("id").asText());

        mock.perform(delete("/entry/shipment/delete/id/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Entrada apagada com sucesso"));
    }

    @Test
    public void getByIssueDateTest() throws Exception {
        UUID originId = UUID.randomUUID();
        UUID destId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        // Cria uma entrada com datas = hoje
        createShipment(originId, destId, itemId);

        LocalDate today = LocalDate.now();

        mock.perform(
                        get("/entry/shipment/issue_date/{date}", today.toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void getByTripDateTest() throws Exception {
        UUID originId = UUID.randomUUID();
        UUID destId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        // Cria uma entrada com datas = hoje
        createShipment(originId, destId, itemId);

        LocalDate today = LocalDate.now();

        mock.perform(
                        get("/entry/shipment/trip_date/{date}", today.toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void createAndDeleteShipmentDocumentTest() throws Exception {

        // cria algumas entries
        JsonNode e1 = createShipment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        JsonNode e2 = createShipment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        String documentName = "";

        String body = mapper.writeValueAsString(
                List.of(
                        UUID.fromString(e1.get("id").asText()),
                        UUID.fromString(e2.get("id").asText())
                )
        );

        // ---------- CRIA DOCUMENTO E CAPTURA O ID ----------
        MvcResult createResult =
                mock.perform(
                                post("/entry/shipment/document/trip/new")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(body)
                                        .param("documentName", documentName)
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        // ---------- EXTRAI O TABLE ID DO RESPONSE ----------
        String responseBody = createResult.getResponse().getContentAsString();
        Integer tableId = mapper.readValue(responseBody, Integer.class);

        // sanity check
        assertNotNull(tableId);

        // ---------- DELETA O DOCUMENTO ----------
        docIO.deleteDocumentByTableId(tableId);
    }




}
