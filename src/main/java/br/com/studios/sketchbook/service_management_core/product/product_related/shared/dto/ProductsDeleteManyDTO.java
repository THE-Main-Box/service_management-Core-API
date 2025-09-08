package br.com.studios.sketchbook.service_management_core.product.product_related.shared.dto;

import java.util.List;
import java.util.UUID;

public record ProductsDeleteManyDTO(
        List<UUID> IDs
) {
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < IDs.size(); i++) {
            result.append(IDs.get(i));
            if (i < IDs.size() - 1) {
                result.append(System.lineSeparator());
            }
        }
        return result.toString();
    }

}
