package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;

public record CellJSONSerialModel(
        Integer id,
        Integer rowId,
        Object value,
        String valueType
) {
    public CellJSONSerialModel(Cell model){
        this(
                model.getId(),
                model.getRowId(),
                model.getValue(),
                model.getValueType().getSimpleName()
        );
    }
}
