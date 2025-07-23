package br.com.studios.sketchbook.service_management_core.models.data_transfer_objects;

import java.util.UUID;

public record SMProductResponseDTO(
        UUID id,
        String name,
        String value
) {
}
