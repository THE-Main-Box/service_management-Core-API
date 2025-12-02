package br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.dto.res;

import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.model.StorageEntry;

import java.util.UUID;

public record StorageEntryDetailedResponseDTO(
        UUID id,
        UUID ownerId,
        String ownerName,
        VolumeType volumeType,
        Long units,
        Long subUnits,
        Long quantityPerUnit
) {

    public StorageEntryDetailedResponseDTO(StorageEntry entry){
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
