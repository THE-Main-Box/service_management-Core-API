package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer.JsonRowDocumentSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class RowSerialDataManagementCore {

    private final JsonRowDocumentSerializer rowJsonSerializer;

    public RowSerialDataManagementCore(ObjectMapper mapper) {
        rowJsonSerializer = new JsonRowDocumentSerializer(mapper);
    }

    // // // // JSON // // // // // //

    public Row loadRowFromJson(Integer tableId, Integer rowId) {
        return rowJsonSerializer.loadRow(tableId, rowId);
    }

    public List<Row> loadRowListFromJson(List<Integer> tableIdList, List<Integer> rowIdList) {
        return rowJsonSerializer.loadRowList(tableIdList, rowIdList);
    }

    public void saveRowToJson(Row row) {
        rowJsonSerializer.saveRow(row);
    }

    public void saveRowListToJson(List<Row> rowList) {
        rowJsonSerializer.saveRowList(rowList);
    }

    public boolean deleteRowIfPresentInJson(Integer tableId, Integer rowId) {
        return rowJsonSerializer.deleteRowListIfPresent(tableId, rowId);
    }

    public boolean isRowPresentInJson(Integer tableId, Integer rowId) {
        return rowJsonSerializer.isRowPresent(tableId, rowId);
    }

}
