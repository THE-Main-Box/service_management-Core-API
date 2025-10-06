package br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_entry;

import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntry;

import java.util.UUID;

public record PriceEntryResponseDTO (
        UUID id,
        String price
){
    public PriceEntryResponseDTO(PriceEntry entry){
        this(
                entry.getId(),
                entry.getPrice().getValue().toString()
        );
    }
}
