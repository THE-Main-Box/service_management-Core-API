package br.com.studios.sketchbook.service_management_core.product.domain.dto.storage_entry;

import br.com.studios.sketchbook.service_management_core.product.shared.enums.VolumeType;

public record StorageEntryUpdateDTO(
        VolumeType type,
        Long units,
        Long subUnits,
        Long quantityPerUnit,
        boolean raw
) {
}
