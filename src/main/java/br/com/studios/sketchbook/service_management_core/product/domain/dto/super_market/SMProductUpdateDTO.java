package br.com.studios.sketchbook.service_management_core.product.domain.dto.super_market;

import br.com.studios.sketchbook.service_management_core.product.shared.enums.VolumeType;

public record SMProductUpdateDTO(
        String name,
        String barCode,
        VolumeType volumeType
) {
}
