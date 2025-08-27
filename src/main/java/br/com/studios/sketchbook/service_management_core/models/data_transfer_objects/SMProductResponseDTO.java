package br.com.studios.sketchbook.service_management_core.models.data_transfer_objects;

import br.com.studios.sketchbook.service_management_core.models.entities.SMProductModel;

import java.util.UUID;

public record SMProductResponseDTO(
        UUID id,
        String name,
        String barCode
) {
    public SMProductResponseDTO(SMProductModel model) {
        this(
                model.getId(),
                model.getName(),
                model.getBarcode()
        );
    }

}
