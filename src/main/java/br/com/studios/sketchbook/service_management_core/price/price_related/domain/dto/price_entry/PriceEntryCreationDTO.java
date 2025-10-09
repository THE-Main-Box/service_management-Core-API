package br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_entry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PriceEntryCreationDTO(
        @NotNull
        Double price,
        @NotBlank
        String currency,
        @NotNull
        UUID ownerId
) {
}
