package br.com.studios.sketchbook.service_management_core.product.domain.dto.product;

import br.com.studios.sketchbook.service_management_core.product.domain.model.Product;

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
