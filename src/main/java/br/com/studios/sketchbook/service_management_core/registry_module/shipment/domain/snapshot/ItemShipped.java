package br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.snapshot;

import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req.ItemShippedCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record ItemShipped(
        UUID itemId,
        String itemName,
        Long units,
        Long unitsPerUnit,
        VolumeType volumeType
) {
    public ItemShipped(ItemShippedCreationDTO dto) {
        this(
                dto.itemId(),
                dto.itemName(),
                dto.units(),
                dto.unitsPerUnit(),
                dto.volumeType()
        );

        if (dto.volumeType().isCompostType()
                && unitsPerUnit == null
        ) throw new IllegalArgumentException("Tipos de dado composto precisam dizer o quanto tem por unidade");

    }

}
