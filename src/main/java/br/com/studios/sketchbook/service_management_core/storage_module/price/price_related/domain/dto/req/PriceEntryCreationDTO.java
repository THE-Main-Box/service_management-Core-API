package br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.dto.req;

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
