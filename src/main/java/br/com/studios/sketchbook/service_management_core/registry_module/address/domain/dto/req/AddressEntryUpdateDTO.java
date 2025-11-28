package br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.req;

public record AddressEntryUpdateDTO(
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
