package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.dto.res;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.enumerators.DocumentPrefix;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;

import java.time.LocalDateTime;

public record TableSumResponse (
        Integer id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String name,
        DocumentPrefix prefix,
        boolean canBeOverridden
){

    public TableSumResponse(Table model){
        this(
                model.getId(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getName(),
                model.getDocumentPrefix(),
                model.isCanBeOverridden()
        );
    }

}
