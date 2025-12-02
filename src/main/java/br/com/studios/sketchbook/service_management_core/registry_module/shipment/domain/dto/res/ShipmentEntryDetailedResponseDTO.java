package br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.res;

import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model.ShipmentEntry;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.snapshot.AddressRef;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.snapshot.ItemShipped;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record ShipmentEntryDetailedResponseDTO (
        UUID id,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tripDate,

        // Snapshot de Origem
        AddressRef originAddressRef,

        // Snapshot de Destino
        AddressRef destinationAddressRef,

        // Snapshot do Item Enviado
        ItemShipped itemShipped,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate issueDate
){

    public ShipmentEntryDetailedResponseDTO(ShipmentEntry entry){
        this(
                entry.getId(),
                entry.getTripDate(),
                entry.getOriginAddressRef(),
                entry.getDestinationAddressRef(),
                entry.getItemShipped(),
                entry.getIssueDate()
        );
    }

}
