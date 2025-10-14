package br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto;

import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntry;

import java.util.UUID;

public record PriceEntryResponseDTO (
        UUID id,
        Double price,
        String currency,
        UUID ownerId,
        String ownerType
){
    public PriceEntryResponseDTO(PriceEntry entry){
        this(
                entry.getId(),
                entry.getPrice().getValue().doubleValue(),
                entry.getPrice().getCurrency(),
                entry.getOwnerId(),
                entry.getOwnerType().getSimpleName()
        );
    }
}
