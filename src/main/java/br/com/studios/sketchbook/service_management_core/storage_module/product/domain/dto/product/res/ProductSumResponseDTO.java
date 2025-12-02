package br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.product.res;

import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model.Product;

import java.util.UUID;

public record ProductSumResponseDTO(
        UUID id,
        String name
) {
    public ProductSumResponseDTO(Product product) {
        this(
                product.getId(),
                product.getName()
        );
    }

}
