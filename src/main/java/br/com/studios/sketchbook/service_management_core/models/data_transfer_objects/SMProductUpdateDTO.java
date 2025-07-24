package br.com.studios.sketchbook.service_management_core.models.data_transfer_objects;

import br.com.studios.sketchbook.service_management_core.infra.util.ValidPrice;

public record SMProductUpdateDTO(
        String name,
        @ValidPrice
        String value,
        String barCode
) {
}
