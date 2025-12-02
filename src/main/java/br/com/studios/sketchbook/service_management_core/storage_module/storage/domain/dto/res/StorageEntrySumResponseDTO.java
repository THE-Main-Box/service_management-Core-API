package br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.dto.res;

import br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.model.StorageEntry;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;

import java.util.UUID;

public record StorageEntrySumResponseDTO(
        UUID id,
        UUID ownerId,
        VolumeType volumeType
) {
    public StorageEntrySumResponseDTO(StorageEntry entry){
        this(
                entry.getId(),
                entry.getOwnerId(),
                entry.getVolumeType()
        );
    }
}
