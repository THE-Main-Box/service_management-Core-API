package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer.JsonCellDocumentSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class CellDataManagementCore {

    private final JsonCellDocumentSerializer cellJSONSerializer;

    public CellDataManagementCore() {
        cellJSONSerializer = new JsonCellDocumentSerializer(new ObjectMapper());
    }

    // // // // JSON // // // // // //
    public Cell loadCellFromJson(
            Integer tableId,
            Integer rowId,
            Integer cellId
    ) {
        return cellJSONSerializer.loadCellFromJson(
                tableId,
                rowId,
                cellId
        );
    }

    public List<Cell> loadCellListFromJson(
            List<Integer> tableIdList,
            List<Integer> rowIdList,
            List<Integer> cellIdList
    ) {
        return cellJSONSerializer.loadCellListFromJson(
                tableIdList,
                rowIdList,
                cellIdList
        );
    }

    public void saveCellInJson(Cell cell) {
        cellJSONSerializer.saveCellInJson(cell);
    }

    public void saveCellListInJson(List<Cell> cellList) {
        cellJSONSerializer.saveCellListInJson(cellList);
    }

    public void deleteCellJsonIfPresent(
            Integer tableId,
            Integer rowId,
            Integer cellId
    ) {
        cellJSONSerializer.deleteCellJsonIfPresent(
                tableId,
                rowId,
                cellId
        );
    }

    public void deleteCellListJsonIfPresent(
            List<Integer> tableIdList,
            List<Integer> rowIdList,
            List<Integer> cellIdList
    ) {
        cellJSONSerializer.deleteCellListJsonIfPresent(
                tableIdList,
                rowIdList,
                cellIdList
        );
    }

    public boolean isCellJsonPresent(
            Integer tableId,
            Integer rowId,
            Integer cellId
    ) {
        return cellJSONSerializer.isCellJsonPresent(
                tableId,
                rowId,
                cellId
        );
    }


}
