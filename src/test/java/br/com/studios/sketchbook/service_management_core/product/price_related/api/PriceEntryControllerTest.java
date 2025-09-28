package br.com.studios.sketchbook.service_management_core.product.price_related.api;

import br.com.studios.sketchbook.service_management_core.product.price_related.domain.dto.PriceEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.price_related.domain.dto.PriceEntryUpdateDTO;
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

/// Testes de integração para o PriceEntryController.
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class PriceEntryControllerTest {

    private final MockMvc mock;
    private final ObjectMapper mapper;

    @Autowired
    public PriceEntryControllerTest(MockMvc mock, ObjectMapper mapper) {
        this.mock = mock;
        this.mapper = mapper;
    }

    /**
     * Utilitário para criar uma PriceEntry via API e retornar o JSON de resposta.
     */
    private JsonNode createPriceEntry(Double value, String currency) throws Exception {
        PriceEntryCreationDTO dto = new PriceEntryCreationDTO(value, currency);
        String json = mapper.writeValueAsString(dto);

        String response = mock.perform(
                        put("/price/entry/new")
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
        JsonNode created = createPriceEntry(10.5, "USD");

        mock.perform(get("/price/entry/id/{id}", created.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value("10.5"));
    }

    @Test
    public void updateTest() throws Exception {
        JsonNode created = createPriceEntry(20.0, "BRL");
        String id = created.get("id").asText();

        PriceEntryUpdateDTO dto = new PriceEntryUpdateDTO(25.0, "BRL");
        String updateJson = mapper.writeValueAsString(dto);

        mock.perform(
                        patch("/price/entry/update/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value("25.0"));
    }

    @Test
    public void getByIdTest() throws Exception {
        JsonNode created = createPriceEntry(50.0, "EUR");
        String id = created.get("id").asText();

        mock.perform(get("/price/entry/id/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.price").value(50.0));
    }

    @Test
    public void removeByIdTest() throws Exception {
        JsonNode created = createPriceEntry(100.0, "JPY");
        String id = created.get("id").asText();

        mock.perform(delete("/price/entry/delete/id/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Entrada de preço apagada com sucesso"));

        mock.perform(get("/price/entry/id/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void removeManyTest() throws Exception {
        JsonNode e1 = createPriceEntry(200.0, "USD");
        JsonNode e2 = createPriceEntry(300.0, "USD");

        List<UUID> ids = List.of(
                UUID.fromString(e1.get("id").asText()),
                UUID.fromString(e2.get("id").asText())
        );

        String idsJson = mapper.writeValueAsString(ids);

        mock.perform(delete("/price/entry/delete/many_id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(idsJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(e1.get("id").asText())))
                .andExpect(content().string(containsString(e2.get("id").asText())));
    }
}
