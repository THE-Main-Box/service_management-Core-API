package br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.req;

import jakarta.validation.constraints.NotBlank;

public record AddressEntryCreationDTO(
        @NotBlank
        String description,
        String zipCode,
        String number,
        String streetName,
        String complement,
        String district,
        String city,
        String state
) {
}
