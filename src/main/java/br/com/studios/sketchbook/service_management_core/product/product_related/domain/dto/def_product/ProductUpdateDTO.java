package br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.def_product;

import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;

public record ProductUpdateDTO(
        String name,
        VolumeType volumeType
) {
}
