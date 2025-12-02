package br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.dto;

import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.model.StorageEntry;

import java.util.UUID;

public record StorageEntryResponseDTO(
        UUID id,
        UUID ownerId,
        String ownerType,
        VolumeType volumeType,
        Long units,
        Long subUnits,
        Long quantityPerUnit
) {

    public StorageEntryResponseDTO(StorageEntry entry){
        this(
                entry.getId(),
                entry.getOwnerId(),
                entry.getOwnerName(),
                entry.getVolumeType(),
                entry.getUnits(),
                entry.getSubUnits(),
                entry.getQuantityPerUnit()
        );
    }

}
