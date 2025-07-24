package br.com.studios.sketchbook.service_management_core.models.data_transfer_objects;

import br.com.studios.sketchbook.service_management_core.infra.util.ValidPrice;
import jakarta.validation.constraints.NotBlank;

public record SMProductCreationDTO(
        @NotBlank(message = "O nome do produto é OBRIGATÓRIO")
        String name,
        @NotBlank(message = "O valor é OBRIGATÓRIO")
        @ValidPrice
        String value,
        @NotBlank(message = "O código de barras é OBRIGATÓRIO")
        String barCode
) {
}
