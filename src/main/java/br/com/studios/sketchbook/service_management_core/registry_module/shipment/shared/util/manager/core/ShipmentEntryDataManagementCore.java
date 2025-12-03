package br.com.studios.sketchbook.service_management_core.registry_module.shipment.shared.util.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req.ShipmentEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model.ShipmentEntry;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.snapshot.AddressRef;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.snapshot.ItemShipped;

import java.time.LocalDate;

public class ShipmentEntryDataManagementCore {


    /**
     * Cria uma entry usando um dto
     * @param dto DTO de entrada que irá conter os dados base e outros DTOs para a criação do objeto
     * @return Retornamos um novo objeto usando os dados obtidos do DTO
     */
    public ShipmentEntry createByDTO(ShipmentEntryCreationDTO dto) {
        AddressRef originRef = new AddressRef(
                dto.originAddress()
        );

        AddressRef destinationRef = new AddressRef(
                dto.destinationAddress()
        );

        ItemShipped itemShipped = new ItemShipped(
                dto.itemShipped()
        );

        LocalDate issueDate = LocalDate.now();

        return new ShipmentEntry(
                dto.tripDate(),
                originRef,
                destinationRef,
                itemShipped,
                issueDate
        );
    }
}
