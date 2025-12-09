package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Table {

    /// Id da tabela
    @Getter
    private final Integer id;

    @Getter
    private final List<Row> rowList;

    public Table(Integer id) {
        this.id = id;
        rowList = new ArrayList<>();
    }

    public Table(Integer id, List<Row> rowList) {
        this.id = id;
        this.rowList = rowList;
    }
}
