package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer.JsonCellDocumentSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CellDataManagementCore {

    private final JsonCellDocumentSerializer jsonSerializer;

    public CellDataManagementCore() {
        jsonSerializer = new JsonCellDocumentSerializer(new ObjectMapper());
    }

    public Cell loadCellFromJson(Integer rowId, Integer cellId) {
        return jsonSerializer.loadCell(rowId, cellId);
    }

    public void saveCellFromJson(Cell cell) {
        jsonSerializer.saveCell(cell);
    }

    public void deleteCellJsonIfPresent(Integer rowId, Integer cellId) {
        jsonSerializer.deleteCellJson(rowId, cellId);
    }

    public boolean isCellJsonPresent(Integer rowId, Integer cellId){
        return jsonSerializer.isCellJsonPresent(rowId, cellId);
    }


}
