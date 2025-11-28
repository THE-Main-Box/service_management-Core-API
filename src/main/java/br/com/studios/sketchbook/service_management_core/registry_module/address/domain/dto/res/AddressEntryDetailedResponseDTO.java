package br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.res;

import java.util.UUID;

public record AddressEntryDetailedResponseDTO(
        UUID id,
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
