package br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market;

import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.SuperMarketProduct;
import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;

import java.util.UUID;

public record SMProductResponseDTO(
        UUID id,
        String name,
        String barCode,
        VolumeType volume
) {
    public SMProductResponseDTO(SuperMarketProduct model) {
        this(
                model.getId(),
                model.getName(),
                model.getBarcode(),
                model.getVolumeType()
        );
    }

}
