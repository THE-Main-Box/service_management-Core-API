package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Row {

    /// Id da linha
    @Getter
    private final Integer id;

    /// Id da tabela a quem pertence
    @Getter
    private final Integer tableId;

    /// Lista de id a respeito das cell
    @Getter
    private final List<Integer> cellIdList;

    public Row(Integer id, Integer tableId) {
        this.id = id;
        this.tableId = tableId;
        this.cellIdList = new ArrayList<>();
    }

    public Row(Integer id, Integer tableId, List<Integer> cellIdList) {
        this.id = id;
        this.tableId = tableId;
        this.cellIdList = cellIdList;
    }

    @Override
    public String toString() {
        return "Row{" +
                "id=" + id +
                ", tableId=" + tableId +
                ", cellIdList=" + cellIdList +
                '}';
    }
}
