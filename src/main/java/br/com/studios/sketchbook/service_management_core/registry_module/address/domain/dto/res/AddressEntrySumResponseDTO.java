package br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.res;

import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.model.AddressEntry;

import java.util.UUID;

public record AddressEntrySumResponseDTO(
        UUID id,
        String description
) {
    public AddressEntrySumResponseDTO(AddressEntry entry){
        this(
                entry.getId(),
                entry.getDescription()
        );
    }
}
