package br.com.studios.sketchbook.service_management_core.product.domain.dto.super_market;

import br.com.studios.sketchbook.service_management_core.product.shared.enums.VolumeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SMProductCreationDTO(
        @NotBlank(message = "O nome do produto é OBRIGATÓRIO")
        String name,
        @NotBlank(message = "O código de barras é OBRIGATÓRIO")
        String barCode
) {
}
