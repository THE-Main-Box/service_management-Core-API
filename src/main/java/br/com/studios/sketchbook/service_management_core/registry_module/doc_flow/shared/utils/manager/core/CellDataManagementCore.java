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

    public static String cellFileName(Integer rowId, Integer cellId) {
        return "cell_" + rowId + "_" + cellId + ".json";
    }

    public static String rowFileName(Integer rowId) {
        return "row_" + rowId + ".json";
    }

    public static Object convertToType(Object value, String typeName) {
        // Lógica de conversão baseada no typeName
        if (value == null) return null;

        return switch (typeName.toLowerCase()) {
            case "integer" -> Integer.valueOf(value.toString());
            case "double" -> Double.valueOf(value.toString());
            case "float" -> Float.valueOf(value.toString());
            case "string" -> value.toString();
            default -> value; // Mantém como está
        };
    }
}
