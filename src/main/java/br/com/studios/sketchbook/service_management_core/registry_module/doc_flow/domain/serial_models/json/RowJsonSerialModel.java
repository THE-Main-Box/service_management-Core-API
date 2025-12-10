package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models.json;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;

import java.util.List;

public record RowJsonSerialModel (
        Integer id,
        Integer tableId,
        List<Integer> cellIds
){
    public RowJsonSerialModel(Row row){
        this(
                row.getId(),
                row.getTableId(),
                row.getCellIdList()
        );
    }

}
