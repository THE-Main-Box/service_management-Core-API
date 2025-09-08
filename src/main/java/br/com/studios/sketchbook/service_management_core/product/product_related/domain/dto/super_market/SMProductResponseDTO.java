package br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market;

import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.SMProductModel;

import java.util.UUID;

public record SMProductResponseDTO(
        UUID id,
        String name,
        String barCode
) {
    public SMProductResponseDTO(SMProductModel model) {
        this(
                model.getId(),
                model.getName(),
                model.getBarcode()
        );
    }

}
