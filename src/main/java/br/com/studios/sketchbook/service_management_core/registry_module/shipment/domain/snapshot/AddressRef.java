package br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.snapshot;

import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req.AddressReferenceCreationDTO;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record AddressRef(
        UUID addressId,
        String description
) {
    public AddressRef(AddressReferenceCreationDTO dto){
        this(
                dto.id(),
                dto.description()
        );
    }
}
