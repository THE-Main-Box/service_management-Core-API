package br.com.studios.sketchbook.service_management_core.models.data_transfer_objects;

import br.com.studios.sketchbook.service_management_core.infra.util.ValidPriceInString;

public record SMProductUpdateDTO(
        String name,
        @ValidPriceInString
        String value,
        String barCode
) {
}
