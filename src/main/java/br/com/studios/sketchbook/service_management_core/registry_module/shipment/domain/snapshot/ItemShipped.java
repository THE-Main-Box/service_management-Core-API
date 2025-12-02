package br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.snapshot;

import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;
import jakarta.persistence.Embeddable;

@Embeddable
public record ItemShipped(
        String name,
        Long units,
        VolumeType volumeType
){
}
