package br.com.studios.sketchbook.service_management_core.address.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressCreationDTO (
        @NotBlank
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
