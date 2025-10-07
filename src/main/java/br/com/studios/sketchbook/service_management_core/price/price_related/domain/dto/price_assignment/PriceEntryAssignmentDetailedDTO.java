package br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_assignment;

import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntryAssignment;

import java.util.UUID;

public record PriceEntryAssignmentDetailedDTO(
        UUID id,
        UUID entryId,
        UUID ownerId,
        String ownerType
) {

    public PriceEntryAssignmentDetailedDTO(PriceEntryAssignment assignment) {
        this(
                assignment.getId(),
                assignment.getEntryId(),
                assignment.getOwnerId(),
                assignment.getOwnerClass().getSimpleName()
        );
    }

}
