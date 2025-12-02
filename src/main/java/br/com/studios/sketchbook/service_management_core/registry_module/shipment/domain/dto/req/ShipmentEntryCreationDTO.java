package br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ShipmentEntryCreationDTO(
        @NotNull(message = "Data da viagem precisa ser passada")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate tripDate,

        @NotNull(message = "Endereço de saída precisa ser passado")
        AddressReferenceCreationDTO originAddress,

        @NotNull(message = "Endereço de destino é importante")
        AddressReferenceCreationDTO destinationAddress,

        @NotNull(message = "Item enviado é importante")
        ItemShippedCreationDTO itemShipped
) {
}
