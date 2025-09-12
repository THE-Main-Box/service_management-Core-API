package br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market;

import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SMProductCreationDTO(
        @NotBlank(message = "O nome do produto é OBRIGATÓRIO")
        String name,
        @NotNull(message = "O tipo de volume do produto é OBRIGATÓRIO")
        VolumeType volumeType,
        @NotBlank(message = "O código de barras é OBRIGATÓRIO")
        String barCode
) {
}
