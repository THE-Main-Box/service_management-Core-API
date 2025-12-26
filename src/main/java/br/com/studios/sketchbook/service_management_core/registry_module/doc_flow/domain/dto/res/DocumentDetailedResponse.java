package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.dto.res;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.enumerators.DocumentPrefix;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;

import java.time.LocalDateTime;
import java.util.List;

public record DocumentDetailedResponse(
        Integer id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String name,
        DocumentPrefix prefix,
        List<List<Object>> tableData,
        List<String> columnNames,
        boolean canBeOverridden
) {

    public DocumentDetailedResponse(
            DocumentData documentData,
            List<List<Object>> tableDataToReturn,
            List<String> columnNames
    ) {
        this(
                documentData.table().getId(),
                documentData.table().getCreatedAt(),
                documentData.table().getUpdatedAt(),
                documentData.table().getName(),
                documentData.table().getDocumentPrefix(),
                tableDataToReturn,
                columnNames,
                documentData.table().isCanBeOverridden()
        );
    }

}
