package br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.def_product;

import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreationDTO(
        @NotBlank
        String name,
        @NotNull
        VolumeType volumeType
) {
}
