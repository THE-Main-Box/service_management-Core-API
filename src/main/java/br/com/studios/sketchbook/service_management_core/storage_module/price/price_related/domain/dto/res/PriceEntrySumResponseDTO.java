package br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.dto.res;

import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.model.PriceEntry;

import java.util.UUID;

public record PriceEntrySumResponseDTO (
        UUID id,
        Double price,
        UUID ownerId
){
    public PriceEntrySumResponseDTO(PriceEntry entry){
        this(
                entry.getId(),
                entry.getPrice().getPrice().doubleValue(),
                entry.getOwnerId()
        );
    }
}
