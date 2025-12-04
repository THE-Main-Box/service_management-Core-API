package br.com.studios.sketchbook.service_management_core.registry_module.shipment.shared.util.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req.AddressReferenceCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req.ItemShippedCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req.ShipmentEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model.ShipmentEntry;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ShipmentEntryDataManagementCoreTest {

    private ShipmentEntryDataManagementCore manager;
    private final LocalDate TRIP_DATE = LocalDate.of(2025, 12, 10);
    private final UUID ORIGIN_ID = UUID.randomUUID();
    private final UUID DESTINATION_ID = UUID.randomUUID();
    private final UUID ITEM_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Inicializa o manager antes de cada teste
        manager = new ShipmentEntryDataManagementCore();
    }

    @Test
    @DisplayName("Deve criar ShipmentEntry com todos os campos preenchidos corretamente, incluindo IDs")
    void shouldCreateShipmentEntry_WhenAllFieldsAreValid() {
        // 1. Preparação dos DTOs Aninhados
        ShipmentEntryCreationDTO creationDTO = getEntryCreationDTO();

        // 3. Execução
        ShipmentEntry result = manager.createByDTO(creationDTO);

        // 4. Verificação
        // A. Verifica a Entidade Principal
        assertThat(result).isNotNull();
        assertThat(result.getTripDate()).isEqualTo(TRIP_DATE);

        // B. Verifica a Data de Emissão (issueDate)
        // Deve ser hoje ou muito próximo de hoje
        assertThat(result.getIssueDate()).isNotNull().isToday();

        // C. Verifica o Snapshot de Origem
        assertThat(result.getOriginAddressRef()).isNotNull();
        assertThat(result.getOriginAddressRef().addressId()).isEqualTo(ORIGIN_ID);
        assertThat(result.getOriginAddressRef().description()).isEqualTo("Porto de Santos - SP");

        // D. Verifica o Snapshot de Destino
        assertThat(result.getDestinationAddressRef()).isNotNull();
        assertThat(result.getDestinationAddressRef().addressId()).isEqualTo(DESTINATION_ID);
        assertThat(result.getDestinationAddressRef().description()).isEqualTo("Galpão Logístico - RJ");

        // E. Verifica o Snapshot do Item
        assertThat(result.getItemShipped()).isNotNull();
        assertThat(result.getItemShipped().itemId()).isEqualTo(ITEM_ID);
        assertThat(result.getItemShipped().itemName()).isEqualTo("Caixa de Eletrônicos");
        assertThat(result.getItemShipped().units()).isEqualTo(50L);
        assertThat(result.getItemShipped().volumeType()).isEqualTo(VolumeType.UNIT);
    }

    private ShipmentEntryCreationDTO getEntryCreationDTO() {
        AddressReferenceCreationDTO originDto = new AddressReferenceCreationDTO(
                ORIGIN_ID,
                "Porto de Santos - SP"
        );

        AddressReferenceCreationDTO destinationDto = new AddressReferenceCreationDTO(
                DESTINATION_ID,
                "Galpão Logístico - RJ"
        );

        ItemShippedCreationDTO itemDto = new ItemShippedCreationDTO(
                ITEM_ID,
                "Caixa de Eletrônicos",
                50L,
                VolumeType.UNIT
        );

        // 2. DTO Principal
        return new ShipmentEntryCreationDTO(
                TRIP_DATE,
                originDto,
                destinationDto,
                itemDto
        );
    }

    @Test
    @DisplayName("Deve criar ShipmentEntry com IDs de Endereço nulos (opcionalidade)")
    void shouldCreateShipmentEntry_WhenAddressIdsAreNull() {
        // 1. Preparação dos DTOs: IDs NULOS (Testando a Regra de Opcionalidade)
        ShipmentEntryCreationDTO creationDTO = getShipmentEntryCreationDTO();

        // 2. Execução
        ShipmentEntry result = manager.createByDTO(creationDTO);

        // 3. Verificação
        assertThat(result).isNotNull();

        // Deve armazenar o 'addressId' como nulo no snapshot
        assertThat(result.getOriginAddressRef().addressId()).isNull();
        assertThat(result.getOriginAddressRef().description()).isEqualTo("Endereço Não Catalogado");

        assertThat(result.getDestinationAddressRef().addressId()).isNull();
        assertThat(result.getDestinationAddressRef().description()).isEqualTo("Destino Provisório");

        // Os demais campos devem estar corretos
        assertThat(result.getItemShipped().itemId()).isEqualTo(ITEM_ID);
    }

    private ShipmentEntryCreationDTO getShipmentEntryCreationDTO() {
        AddressReferenceCreationDTO originDto = new AddressReferenceCreationDTO(
                null, // ID nulo
                "Endereço Não Catalogado"
        );

        AddressReferenceCreationDTO destinationDto = new AddressReferenceCreationDTO(
                null, // ID nulo
                "Destino Provisório"
        );

        ItemShippedCreationDTO itemDto = new ItemShippedCreationDTO(
                ITEM_ID,
                "Produtos Diversos",
                1L,
                VolumeType.KILOGRAM
        );

        return new ShipmentEntryCreationDTO(
                TRIP_DATE,
                originDto,
                destinationDto,
                itemDto
        );
    }
}