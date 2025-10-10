package br.com.studios.sketchbook.service_management_core.storage.domain.dto;

import br.com.studios.sketchbook.service_management_core.product.shared.enums.VolumeType;
import br.com.studios.sketchbook.service_management_core.storage.domain.model.StorageEntry;

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
                entry.getOwnerType().getSimpleName(),
                entry.getVolumeType(),
                entry.getUnits(),
                entry.getSubUnits(),
                entry.getQuantityPerUnit()
        );
    }

}
