package br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_assignment;

import java.util.UUID;

public record PriceEntryAssignmentCreationDTO(UUID entryId, UUID ownerId) {
}
