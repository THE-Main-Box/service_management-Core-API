package br.com.studios.sketchbook.service_management_core.storage.domain.dto;

import br.com.studios.sketchbook.service_management_core.product.shared.enums.VolumeType;

import java.util.UUID;

public record StorageEntryCreationDTO (
        UUID ownerId,
        VolumeType volumeType,
        Long quantity,
        Long quantityPerUnit,
        Boolean isValuesRaw

){
}
