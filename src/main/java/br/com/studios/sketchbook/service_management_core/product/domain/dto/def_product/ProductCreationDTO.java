package br.com.studios.sketchbook.service_management_core.product.domain.dto.def_product;

import jakarta.validation.constraints.NotBlank;

public record ProductCreationDTO(
        @NotBlank
        String name
) {
}
