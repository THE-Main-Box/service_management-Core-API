package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models;

import lombok.Getter;

public class Cell {

    /// id da cell dentro da tabela
    @Getter
    private final Integer id;

    /// Id da table a quem a cell pertence
    @Getter
    private final Integer tableId;

    /// Id da linha que a cell pertence
    @Getter
    private final Integer rowId;

    /// Valor dentro da cell
    @Getter
    private Object value;

    /// Tipo de dado do valor que temos aqui dentro
    @Getter
    private Class<?> valueType;

    public Cell(Integer id, Integer tableId, Integer rowId, Object value) {
        this.id = id;
        this.tableId = tableId;
        this.rowId = rowId;
        this.setValue(value);
    }

    public void setValue(Object value) {
        this.value = value;
        this.valueType = value != null ? value.getClass() : Void.class;
    }
}
