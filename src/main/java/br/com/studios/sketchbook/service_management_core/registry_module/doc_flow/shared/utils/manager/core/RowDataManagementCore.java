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
        return rowJSONSerializer.loadRow(rowId);
    }

    public List<Row> loadRowListFromJson(List<Integer> rowIdList) {
        return rowJSONSerializer.loadRowList(rowIdList);
    }

    public void saveRowFromJson(Row row) {
        rowJSONSerializer.saveRow(row);
    }

    public void deleteRowJsonIfPresent(Integer rowId) {
        rowJSONSerializer.deleteColumnJsonIfPresent(rowId);
    }

    public boolean isRowJsonPresent(Integer rowId){
        return rowJSONSerializer.isColumnJsonPresent(rowId);
    }

}
