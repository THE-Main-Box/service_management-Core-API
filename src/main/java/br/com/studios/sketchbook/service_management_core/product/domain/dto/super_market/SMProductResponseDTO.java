package br.com.studios.sketchbook.service_management_core.product.domain.dto.super_market;

import br.com.studios.sketchbook.service_management_core.product.domain.model.SuperMarketProduct;

import java.util.UUID;

public record SMProductResponseDTO(
        UUID id,
        String name,
        String barCode
) {
    public SMProductResponseDTO(SuperMarketProduct model) {
        this(
                model.getId(),
                model.getName(),
                model.getBarcode()
        );
    }

}
