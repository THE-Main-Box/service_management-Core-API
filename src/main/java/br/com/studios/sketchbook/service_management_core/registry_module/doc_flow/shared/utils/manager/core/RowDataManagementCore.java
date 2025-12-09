package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer.JsonRowDocumentSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class RowDataManagementCore {

    private final JsonRowDocumentSerializer rowJSONSerializer;

    public RowDataManagementCore() {
        rowJSONSerializer = new JsonRowDocumentSerializer(new ObjectMapper());
    }

    // // // // JSON // // // // // //

    public Row loadRowFromJson(Integer rowId) {
        return rowJSONSerializer.loadRowInJson(rowId);
    }

    public List<Row> loadRowListFromJson(List<Integer> rowIdList) {
        return rowJSONSerializer.loadRowListInJson(rowIdList);
    }

    public void saveRowToJson(Row row) {
        rowJSONSerializer.saveRowInJson(row);
    }

    public void saveRowListToJson(List<Row> rowList) {
        rowJSONSerializer.saveRowListInJson(rowList);
    }

    public void deleteRowJsonIfPresent(Integer rowId) {
        rowJSONSerializer.deleteColumnJsonIfPresent(rowId);
    }

    public void deleteRowListJsonIfPresent(List<Integer> rowIdList) {
        rowJSONSerializer.deleteColumnListJsonIfPresent(rowIdList);
    }

    public boolean isRowJsonPresent(Integer rowId){
        return rowJSONSerializer.isColumnJsonPresent(rowId);
    }

}
