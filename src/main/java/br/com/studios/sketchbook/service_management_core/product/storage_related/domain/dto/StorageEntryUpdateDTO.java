package br.com.studios.sketchbook.service_management_core.product.storage_related.domain.dto;

import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;

public record StorageEntryUpdateDTO(
        VolumeType type,
        Long units,
        Long subUnits,
        Long quantityPerUnit,
        boolean raw
) {
}
