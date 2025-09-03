package br.com.studios.sketchbook.service_management_core.product.domain.dto.super_market;

import br.com.studios.sketchbook.service_management_core.price.shared.util.annotations.ValidPriceInString;
import jakarta.validation.constraints.NotBlank;

public record SMProductCreationDTO(
        @NotBlank(message = "O nome do produto é OBRIGATÓRIO")
        String name,
        @NotBlank(message = "O valor é OBRIGATÓRIO")
        @ValidPriceInString
        String value,
        @NotBlank(message = "O código de barras é OBRIGATÓRIO")
        String barCode
) {
}
