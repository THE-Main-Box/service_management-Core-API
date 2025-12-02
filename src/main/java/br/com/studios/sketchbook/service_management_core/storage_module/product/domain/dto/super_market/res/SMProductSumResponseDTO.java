package br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.super_market.res;

import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model.SuperMarketProduct;

import java.util.UUID;

public record SMProductSumResponseDTO(
        UUID id,
        String name
) {
    public SMProductSumResponseDTO(SuperMarketProduct model) {
        this(
                model.getId(),
                model.getName()
        );
    }
}
