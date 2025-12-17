package br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.res;

import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model.ShipmentEntry;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record ShipmentEntrySumResponseDTO(
        /// Id da viagem
        UUID id,

        /// Data da viagem
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tripDate,

        /// Data de emissão de viagem
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate issueDate,

        /// Id da viagem de origem
        UUID originAddressId,

        /// Id da viagem de destino
        UUID destinationAddressId,

        /// Id do item que foi transferido
        UUID itemId,
        /// Quantidade base do item transferido
        Long itemUnits,
        /// Quantidade por unidade de item transferido
        Long itemUnitsPerUnit,
        /// Tipo de volume que o item possui na hora da viagem em si
        VolumeType itemVolume
) {
    public ShipmentEntrySumResponseDTO(ShipmentEntry entry){
        this(
                entry.getId(),
                entry.getTripDate(),
                entry.getIssueDate(),
                entry.getOriginAddressRef().addressId(),
                entry.getDestinationAddressRef().addressId(),
                entry.getItemShipped().itemId(),
                entry.getItemShipped().units(),
                entry.getItemShipped().unitsPerUnit(),
                entry.getItemShipped().volumeType()
        );
    }

}
