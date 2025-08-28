package br.com.studios.sketchbook.service_management_core.models.data_transfer_objects;

import br.com.studios.sketchbook.service_management_core.models.enumerators.VolumeType;

public record StorageEntryUpdateDTO(
        VolumeType type,
        Long units,
        Long subUnits,
        Long quantityPerUnit,
        boolean raw
) {
}
