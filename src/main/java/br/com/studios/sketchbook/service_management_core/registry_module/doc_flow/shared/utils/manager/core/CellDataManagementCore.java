package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer.JsonCellDocumentSerializer;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer.JsonRowDocumentSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class CellDataManagementCore {

    private final JsonCellDocumentSerializer cellJSONSerializer;

    public CellDataManagementCore() {
        cellJSONSerializer = new JsonCellDocumentSerializer(new ObjectMapper());
    }

    // // // // JSON // // // // // //
    public Cell loadCellFromJson(Integer rowId, Integer cellId) {
        return cellJSONSerializer.loadCell(rowId, cellId);
    }

    public List<Cell> loadCellListFromJson(Integer rowId, List<Integer> cellIdList) {
        return cellJSONSerializer.loadCellList(rowId, cellIdList);
    }

    public void saveCellFromJson(Cell cell) {
        cellJSONSerializer.saveCell(cell);
    }

    public void deleteCellJsonIfPresent(Integer rowId, Integer cellId) {
        cellJSONSerializer.deleteCellJsonIfPresent(rowId, cellId);
    }

    public boolean isCellJsonPresent(Integer rowId, Integer cellId){
        return cellJSONSerializer.isCellJsonPresent(rowId, cellId);
    }


}
