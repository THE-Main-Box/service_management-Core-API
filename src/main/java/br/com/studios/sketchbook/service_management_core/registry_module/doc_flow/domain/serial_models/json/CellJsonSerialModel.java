package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models.json;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;

public record CellJsonSerialModel(
        Integer id,
        Integer tableId,
        Integer rowId,
        Object value,
        String valueType
) {
    public CellJsonSerialModel(Cell model){
        this(
                model.getId(),
                model.getTableId(),
                model.getRowId(),
                model.getValue(),
                model.getValueType().getSimpleName()
        );
    }
}
