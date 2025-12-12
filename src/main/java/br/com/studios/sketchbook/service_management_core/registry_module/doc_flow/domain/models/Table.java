package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Table {

    /// Id da tabela
    @Getter
    private final Integer id;

    /// Lista de linhas que pertencem a essa tabela
    @Getter
    private final List<Integer> rowIdList;

    public Table(Integer id) {
        this.id = id;
        rowIdList = new ArrayList<>();
    }

    public Table(Integer id, List<Integer> rowIdList) {
        this.id = id;
        this.rowIdList = rowIdList;
    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", rowIdList=" + rowIdList +
                '}';
    }
}
