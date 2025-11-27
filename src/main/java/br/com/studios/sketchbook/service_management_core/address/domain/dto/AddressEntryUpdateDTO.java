package br.com.studios.sketchbook.service_management_core.address.domain.dto;

public record AddressEntryUpdateDTO(
        String description,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String zipCode
){
}
