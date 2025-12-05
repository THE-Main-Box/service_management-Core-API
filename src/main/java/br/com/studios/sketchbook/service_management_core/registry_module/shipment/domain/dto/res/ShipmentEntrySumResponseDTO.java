package br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.res;

import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model.ShipmentEntry;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record ShipmentEntrySumResponseDTO(
        UUID id,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tripDate,

        UUID originAddressId,
        String originDescription,

        UUID destinationAddressId,
        String destinationDescription,

        UUID itemId,
        String itemName,
        Long itemUnits,
        VolumeType itemVolume
) {
    public ShipmentEntrySumResponseDTO(ShipmentEntry entry){
        this(
                entry.getId(),
                entry.getTripDate(),
                entry.getOriginAddressRef().addressId(),
                entry.getOriginAddressRef().description(),
                entry.getDestinationAddressRef().addressId(),
                entry.getDestinationAddressRef().description(),
                entry.getItemShipped().itemId(),
                entry.getItemShipped().itemName(),
                entry.getItemShipped().units(),
                entry.getItemShipped().volumeType()
        );
    }

}
