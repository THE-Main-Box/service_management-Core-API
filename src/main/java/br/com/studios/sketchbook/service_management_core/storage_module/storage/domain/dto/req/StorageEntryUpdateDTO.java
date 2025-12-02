package br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.dto.req;

import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;

public record StorageEntryUpdateDTO(
        VolumeType type,
        Long units,
        Long subUnits,
        Long quantityPerUnit,
        boolean raw
) {
}
