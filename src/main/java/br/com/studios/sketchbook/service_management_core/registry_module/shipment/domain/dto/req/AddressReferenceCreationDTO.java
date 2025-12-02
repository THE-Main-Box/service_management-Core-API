package br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AddressReferenceCreationDTO(
        UUID id,
        @NotBlank(message = "Por favor insira uma descrição")
        String description
) {
}
