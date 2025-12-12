package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer.JsonCellDocumentSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class CellDataSerialManagementCore {

    private final JsonCellDocumentSerializer cellJsonSerializer;

    public CellDataSerialManagementCore(ObjectMapper mapper) {
        cellJsonSerializer = new JsonCellDocumentSerializer(mapper);
    }

    // // // // JSON // // // // // //
    public Cell loadCellFromJson(
            Integer tableId,
            Integer rowId,
            Integer cellId
    ) {
        return cellJsonSerializer.loadCell(
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
        return cellJsonSerializer.loadCellList(
                tableIdList,
                rowIdList,
                cellIdList
        );
    }

    public void saveCellInJson(Cell cell) {
        cellJsonSerializer.saveCell(cell);
    }

    public void saveCellListInJson(List<Cell> cellList) {
        cellJsonSerializer.saveCellList(cellList);
    }

    public void deleteCellJsonIfPresent(
            Integer tableId,
            Integer rowId,
            Integer cellId
    ) {
        cellJsonSerializer.deleteCellIfPresent(
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
        cellJsonSerializer.deleteCellListIfPresent(
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
        return cellJsonSerializer.isCellPresent(
                tableId,
                rowId,
                cellId
        );
    }


}
