package br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.product;

import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model.Product;

import java.util.UUID;

public record ProductResponseDTO(
        UUID id,
        String name
) {

    public ProductResponseDTO(Product product) {
        this(
                product.getId(),
                product.getName()
        );
    }

}
