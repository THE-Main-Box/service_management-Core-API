package br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.res;

import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.model.AddressEntry;

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

    public AddressEntryDetailedResponseDTO(AddressEntry entry){
        this(
                entry.getId(),
                entry.getDescription(),
                entry.getZipCode(),
                entry.getNumber(),
                entry.getStreetName(),
                entry.getComplement(),
                entry.getDistrict(),
                entry.getCity(),
                entry.getState()
        );
    }

}
