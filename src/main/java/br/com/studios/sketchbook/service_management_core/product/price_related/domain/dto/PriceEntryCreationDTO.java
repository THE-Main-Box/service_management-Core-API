package br.com.studios.sketchbook.service_management_core.product.price_related.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PriceEntryCreationDTO(
        @NotNull
        Double price,
        @NotBlank
        String currency
) {
}
