package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer.JsonTableDocumentSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class TableDataSerialManagementCore {

    private final JsonTableDocumentSerializer tableJsonSerializer;

    public TableDataSerialManagementCore(ObjectMapper mapper) {
        this.tableJsonSerializer = new JsonTableDocumentSerializer(mapper);
    }

    public void saveTableInJson(Table table) {
        tableJsonSerializer.saveTable(
                table
        );
    }

    public void saveTableListInJson(List<Table> tableList) {
        tableJsonSerializer.saveTableList(
                tableList
        );
    }

    public Table loadTableFromJson(Integer tableId){
        return tableJsonSerializer.loadTable(
                tableId
        );
    }

    public List<Table> loadTableListFromJson(List<Integer> tableIdList){
        return tableJsonSerializer.loadTableList(tableIdList);
    }

    public boolean deleteTableIfPresentInJson(Integer tableId){
        return tableJsonSerializer.deleteTableIfPresent(tableId);
    }

    public void deleteTableListIfPresentInJson(List<Integer> tableIdList){
        tableJsonSerializer.deleteTableListIfPresent(tableIdList);
    }

    public boolean isTablePresentInJson(Integer tableId){
        return tableJsonSerializer.isTablePresent(tableId);
    }
}
