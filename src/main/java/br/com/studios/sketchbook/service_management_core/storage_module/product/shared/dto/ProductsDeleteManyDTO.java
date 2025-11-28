package br.com.studios.sketchbook.service_management_core.storage_module.product.shared.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ProductsDeleteManyDTO(
        List<UUID> IDs
) {


    @Override
    public String toString() {
        return "ProductsDeleteManyDTO{" +
                "IDs=" + IDs +
                '}';
    }

}
