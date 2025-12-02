package br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.super_market.res;

import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model.SuperMarketProduct;

import java.util.UUID;

public record SMProductDetailedResponseDTO(
        UUID id,
        String name,
        String barCode
) {
    public SMProductDetailedResponseDTO(SuperMarketProduct model) {
        this(
                model.getId(),
                model.getName(),
                model.getBarcode()
        );
    }

}
