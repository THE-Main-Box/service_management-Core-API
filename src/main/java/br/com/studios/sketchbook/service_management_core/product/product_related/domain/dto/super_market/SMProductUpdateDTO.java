package br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market;

import br.com.studios.sketchbook.service_management_core.price.shared.util.annotations.ValidPriceInString;

public record SMProductUpdateDTO(
        String name,
        @ValidPriceInString
        String value,
        String barCode
) {
}
