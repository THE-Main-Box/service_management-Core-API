package br.com.studios.sketchbook.service_management_core.registry_core_module.address.api;

import br.com.studios.sketchbook.service_management_core.application.ServiceManagementCoreApiApplication;
import br.com.studios.sketchbook.service_management_core.application.api_utils.config.TestConfig;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.req.AddressEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.req.AddressEntryUpdateDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(classes = {
        ServiceManagementCoreApiApplication.class,
        TestConfig.class
})
@Transactional
public class AddressEntryControllerTest {

    private final MockMvc mock;
    private final ObjectMapper mapper;

    @Autowired
    public AddressEntryControllerTest(MockMvc mock, ObjectMapper mapper) {
        this.mock = mock;
        this.mapper = mapper;
    }

    /* Helper: Cria uma DTO de Criação */
    private AddressEntryCreationDTO createBaseDto(String description) {
        return new AddressEntryCreationDTO(
                description,
                "01001-000",
                "123",
                "Rua de Teste",
                "Apto 405",
                "Centro",
                "São Paulo",
                "SP"
        );
    }

    /* Helper: Cria uma AddressEntry via endpoint e retorna o JSON da resposta */
    private JsonNode createEntry(String description) throws Exception {
        AddressEntryCreationDTO dto = createBaseDto(description);
        String json = mapper.writeValueAsString(dto);

        String response = mock.perform(
                        post("/entry/address/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readTree(response);
    }

    // ---------------------------------------------------------------------------------------------------
    // TESTES
    // ---------------------------------------------------------------------------------------------------

    @Test
    public void createEntryTest() throws Exception {
        AddressEntryCreationDTO dto = createBaseDto("Novo Endereço de Entrega");

        mock.perform(post("/entry/address/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.description").value("Novo Endereço de Entrega"))
                .andExpect(jsonPath("$.zipCode").value("01001-000"));
    }

    @Test
    public void createEntryWithMissingRequiredFieldTest() throws Exception {
        // 'description' é @NotBlank, então deve falhar
        AddressEntryCreationDTO dto = new AddressEntryCreationDTO(
                "", // Descrição vazia
                "01001-000",
                "123",
                "Rua de Teste",
                null,
                "Centro",
                "São Paulo",
                "SP"
        );

        mock.perform(post("/entry/address/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()); // Espera-se HTTP 400
    }

    @Test
    public void getByIdExistingTest() throws Exception {
        JsonNode createdEntry = createEntry("Endereço para Busca");
        UUID entryId = UUID.fromString(createdEntry.get("id").asText());

        mock.perform(get("/entry/address/id/{id}", entryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entryId.toString()))
                .andExpect(jsonPath("$.description").value("Endereço para Busca"));
    }

    @Test
    public void getByIdNotFoundTest() throws Exception {
        UUID fakeId = UUID.randomUUID();

        mock.perform(get("/entry/address/id/{id}", fakeId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateEntryTest() throws Exception {
        JsonNode createdEntry = createEntry("Endereço Antigo");
        UUID entryId = UUID.fromString(createdEntry.get("id").asText());

        AddressEntryUpdateDTO dto = new AddressEntryUpdateDTO(
                null,
                null,
                "999",
                "Rua Atualizada",
                "Portão Vermelho",
                null,
                null,
                null
        );

        mock.perform(patch("/entry/address/update/{id}", entryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zipCode").value("01001-000")) // O zipCode permanece o mesmo, pois não foi alterado
                .andExpect(jsonPath("$.number").value("999"))
                .andExpect(jsonPath("$.streetName").value("Rua Atualizada"))
                .andExpect(jsonPath("$.complement").value("Portão Vermelho"));
    }

    @Test
    public void deleteEntryByIdTest() throws Exception {
        JsonNode createdEntry = createEntry("Endereço para Deletar");
        UUID entryId = UUID.fromString(createdEntry.get("id").asText());

        // 1. Deletar (espera-se sucesso)
        mock.perform(delete("/entry/address/delete/id/{id}", entryId))
                .andExpect(status().isOk())
                .andExpect(content().string("Entrada de armazenamento apagada com sucesso"));

        // 2. Tentar buscar novamente (espera-se 404)
        mock.perform(get("/entry/address/id/{id}", entryId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllEntriesTest() throws Exception {
        // Garante que há pelo menos duas entradas
        createEntry("Endereço Lista 1");
        createEntry("Endereço Lista 2");

        mock.perform(get("/entry/address/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2)); // Pelo menos 2 entradas
    }
}