package br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.product.res;

import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model.Product;

import java.util.UUID;

public record ProductDetailedResponseDTO(
        UUID id,
        String name
) {

    public ProductDetailedResponseDTO(Product product) {
        this(
                product.getId(),
                product.getName()
        );
    }

}
