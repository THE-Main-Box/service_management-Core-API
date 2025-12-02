package br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.dto.req;

public record PriceEntryUpdateDTO(
        Double value,
        String currency
) {
}
