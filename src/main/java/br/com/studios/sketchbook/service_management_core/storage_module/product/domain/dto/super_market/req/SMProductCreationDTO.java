package br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.super_market.req;

import jakarta.validation.constraints.NotBlank;

public record SMProductCreationDTO(
        @NotBlank(message = "O nome do produto é OBRIGATÓRIO")
        String name,
        @NotBlank(message = "O código de barras é OBRIGATÓRIO")
        String barCode
) {
}
