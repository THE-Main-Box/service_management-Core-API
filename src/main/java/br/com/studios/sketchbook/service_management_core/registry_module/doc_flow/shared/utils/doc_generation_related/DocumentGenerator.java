package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.enumerators.DocumentPrefix;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentGenerator {

    private final DocumentComponentGenerator componentGenerator;

    public DocumentGenerator() {
        this.componentGenerator = new DocumentComponentGenerator();
    }

    /**
     * Gera uma table com base nos dados passados.
     *
     * @param tableData Lista de linhas, onde cada linha contém uma lista de valores para geração das cells.
     * @param tableName Nome da tabela
     * @param columnNames Lista de nomes de coluna
     */
    public DocumentData generateDocument(
            List<List<Object>> tableData,
            List<String> columnNames,
            String tableName,
            boolean canBeOverridden,
            DocumentPrefix prefix
    ) {

        Table table = createTable(
                tableName,
                canBeOverridden,
                prefix
        );

        List<Row> rowList = new ArrayList<>();
        Map<Integer, List<Cell>> rowCellListMap = new HashMap<>();

        for (List<Object> cellDataList : tableData) {

            Row row = createAndRegisterRow(table);
            rowList.add(row);

            processCellsForRow(
                    table,
                    row,
                    cellDataList,
                    columnNames,
                    rowCellListMap
            );
        }

        return new DocumentData(
                table,
                rowList,
                rowCellListMap
        );
    }

    /**
     * Sobrescreve os dados de uma tabela existente mantendo os mesmos IDs.
     *
     * @param tableId           Id da table existente existente que será editada
     * @param tableName         Nome da tabela
     * @param tableCreationTime Data e hora da criação da table original
     * @param newTableData      Novos dados para sobrescrever
     * @return GeneratedTableData com os mesmos IDs mas valores atualizados
     */
    public DocumentData overrideDocumentData(
            Integer tableId,
            String tableName,
            DocumentPrefix prefix,
            LocalDateTime tableCreationTime,
            List<List<Object>> newTableData,
            List<String> cellNameList
    ) {
        Table table = new Table(tableId, tableCreationTime);
        table.updateUpdateAtValue();
        table.setName(tableName);
        table.setDocumentPrefix(prefix);

        List<Row> rowList = new ArrayList<>();
        Map<Integer, List<Cell>> rowCellListMap = new HashMap<>();

        for (List<Object> cellDataList : newTableData) {

            Row row = createAndRegisterRow(table);
            rowList.add(row);

            processCellsForRow(
                    table,
                    row,
                    cellDataList,
                    cellNameList,
                    rowCellListMap
            );
        }

        return new DocumentData(
                table,
                rowList,
                rowCellListMap
        );
    }

    public List<List<Object>> toListOfLists(DocumentData data) {
        List<List<Object>> result = new ArrayList<>();

        for (Row row : data.rowList()) {
            List<Cell> cells = data.rowCellListMap().get(row.getId());
            List<Object> rowValues = cells.stream()
                    .map(Cell::getValue)
                    .toList();
            result.add(rowValues);
        }

        return result;
    }

    // Cria uma tabela nova
    private Table createTable(
            String name,
            boolean canBeOverridden,
            DocumentPrefix prefix
    ) {
        Table table = new Table(generateDocumentTableId());
        table.setName(name);
        table.setDocumentPrefix(prefix);
        table.setCanBeOverridden(canBeOverridden);
        return table;
    }

    // Gera uma nova row e registra seu ID dentro da table
    private Row createAndRegisterRow(Table table) {
        return componentGenerator.generateRow(table);
    }

    // Processa a lista de dados de células pertencentes a uma row
    private void processCellsForRow(
            Table table,
            Row row,
            List<Object> cellDataList,
            List<String> columnNameList,
            Map<Integer, List<Cell>> rowCellListMap
    ) {
        for (int cellDataIndex = 0; cellDataIndex < cellDataList.size(); cellDataIndex++) {
            Cell cell = createAndRegisterCell(
                    table,
                    row,
                    cellDataList.get(cellDataIndex),
                    columnNameList.get(cellDataIndex)
            );

            rowCellListMap
                    .computeIfAbsent(row.getId(), x -> new ArrayList<>())
                    .add(cell);
        }
    }

    // Cria uma cell, registra dentro da row e retorna
    private Cell createAndRegisterCell(Table table, Row row, Object value, String columnName) {
        if (!isPrimitiveOrWrapper(value)) {
            throw new IllegalArgumentException(
                    "O valor de: "
                            + value.getClass()
                            + " não é um tipo de dado compatível com o nosso sistema"
            );
        }

        return componentGenerator.generateCell(
                table.getId(),
                row,
                value,
                columnName
        );
    }

    /**
     * Gera ID da tabela com base em data/hora.
     * Formato "DD-SS-MMM-HH"
     */
    private Integer generateDocumentTableId() {

        LocalDateTime now = LocalDateTime.now();

        int day = now.getDayOfMonth();
        int second = now.getSecond();
        int ms = (now.getNano() / 1_000_000) % 1000;
        int nanoHash = Math.toIntExact(Math.abs(System.nanoTime()) % 100);

        String idStr = String.format("%02d%02d%03d%02d", day, second, ms, nanoHash);
        return Integer.parseInt(idStr);
    }

    public boolean isPrimitiveOrWrapper(Object obj) {
        if (obj == null) return true;

        Class<?> type = obj.getClass();

        return type == String.class ||
                type == Integer.class ||
                type == Long.class ||
                type == Double.class ||
                type == Float.class ||
                type == Boolean.class ||
                type == Character.class ||
                type == Byte.class ||
                type == Short.class;
    }

}
