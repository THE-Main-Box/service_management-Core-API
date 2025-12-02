package br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.res;

import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model.ShipmentEntry;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record ShipmentEntrySumResponseDTO(
        UUID id,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tripDate,

        String originDescription,

        String destinationDescription,

        String itemName,
        Long itemUnits
) {
    public ShipmentEntrySumResponseDTO(ShipmentEntry entry){
        this(
                entry.getId(),
                entry.getTripDate(),
                entry.getOriginAddressRef().description(),
                entry.getDestinationAddressRef().description(),
                entry.getItemShipped().name(),
                entry.getItemShipped().units()
        );
    }

}
