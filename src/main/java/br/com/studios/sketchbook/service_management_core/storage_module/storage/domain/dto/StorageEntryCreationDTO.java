package br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.dto;

import br.com.studios.sketchbook.service_management_core.storage_module.product.shared.enums.VolumeType;

import java.util.UUID;

public record StorageEntryCreationDTO (
        UUID ownerId,
        VolumeType volumeType,
        Long quantity,
        Long quantityPerUnit,
        Boolean isValuesRaw

){
}
