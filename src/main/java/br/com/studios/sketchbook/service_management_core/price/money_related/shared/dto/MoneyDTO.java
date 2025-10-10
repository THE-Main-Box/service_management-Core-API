package br.com.studios.sketchbook.service_management_core.price.money_related.shared.dto;

import br.com.studios.sketchbook.service_management_core.price.money_related.shared.annotations.ValidPriceInString;

public record MoneyDTO(
        @ValidPriceInString
        String value,
        String currency
) {
}
