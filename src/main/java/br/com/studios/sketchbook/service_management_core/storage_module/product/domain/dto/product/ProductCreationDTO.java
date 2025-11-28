package br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.product;

import jakarta.validation.constraints.NotBlank;

public record ProductCreationDTO(
        @NotBlank
        String name
) {
}
