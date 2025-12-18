package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models.json;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;

import java.time.LocalDateTime;
import java.util.List;

public record TableJsonSerialModel(
        Integer id,
        String name,
        List<Integer> rowIdList,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean canBeOverridden,
        String documentPrefix
) {
    public TableJsonSerialModel(Table table) {
        this(
                table.getId(),
                table.getName(),
                table.getRowIdList(),
                table.getCreatedAt(),
                table.getUpdatedAt(),
                table.isCanBeOverridden(),
                table.getDocumentPrefix().name()
        );
    }

}
