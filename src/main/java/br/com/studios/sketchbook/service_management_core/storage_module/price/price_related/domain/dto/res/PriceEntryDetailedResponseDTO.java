package br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.dto.res;

import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.model.PriceEntry;

import java.util.UUID;

public record PriceEntryDetailedResponseDTO(
        UUID id,
        Double price,
        String currency,
        UUID ownerId,
        String ownerType
){
    public PriceEntryDetailedResponseDTO(PriceEntry entry){
        this(
                entry.getId(),
                entry.getPrice().getPrice().doubleValue(),
                entry.getPrice().getCurrency(),
                entry.getOwnerId(),
                entry.getOwnerType().getSimpleName()
        );
    }
}
