package br.com.studios.sketchbook.service_management_core.price.shared.dto;

import br.com.studios.sketchbook.service_management_core.price.shared.util.annotations.ValidPriceInString;

public record MoneyDTO(
        @ValidPriceInString
        String value,
        String currency
) {
}
