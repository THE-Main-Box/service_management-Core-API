package br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_entry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PriceEntryCreationDTO(
        @NotNull
        Double price,
        @NotBlank
        String currency
) {
}
