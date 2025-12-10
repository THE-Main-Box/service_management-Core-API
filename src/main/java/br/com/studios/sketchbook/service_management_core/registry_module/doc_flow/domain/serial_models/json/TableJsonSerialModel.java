package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models.json;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;

import java.util.List;

public record TableJsonSerialModel(
        Integer id,
        List<Integer> rowIdList
) {
    public TableJsonSerialModel(Table table) {
        this(
                table.getId(),
                table.getRowIdList()
        );
    }

}
