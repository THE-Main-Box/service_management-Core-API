package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Row {

    /// Id da coluna
    @Getter
    private final Integer id;

    /// Lista de id a respeito das cell
    @Getter
    private final List<Integer> cellIdList;

    public Row(Integer id) {
        this.id = id;
        cellIdList = new ArrayList<>();
    }

    public Row(Integer id, List<Integer> cellIdList) {
        this.id = id;
        this.cellIdList = cellIdList;
    }

}
