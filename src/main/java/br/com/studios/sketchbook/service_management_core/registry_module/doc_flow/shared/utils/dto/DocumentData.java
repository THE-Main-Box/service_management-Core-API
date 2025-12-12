package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;

import java.util.List;
import java.util.Map;

public record DocumentData(
        Table table,
        List<Row> rowList,
        Map<Integer, List<Cell>> rowCellListMap
) {

    @Override
    public String toString() {
        return "GeneratedTableData{" +
                "table=" + table +
                ", rowList=" + rowList +
                ", rowCellListMap=" + rowCellListMap +
                '}';
    }
}
