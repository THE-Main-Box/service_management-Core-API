package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;

import java.util.List;

public record RowJsonSerialModel (
        Integer id,
        List<Integer> cellIds
){
    public RowJsonSerialModel(Row row){
        this(
                row.getId(),
                row.getCellIdList()
        );
    }

}
