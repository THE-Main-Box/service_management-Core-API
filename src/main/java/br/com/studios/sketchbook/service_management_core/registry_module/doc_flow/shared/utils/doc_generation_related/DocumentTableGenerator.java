package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentTableGenerator {

    private final DocumentComponentGenerator componentGenerator;

    public DocumentTableGenerator() {
        this.componentGenerator = new DocumentComponentGenerator();
    }

    /**
     * Gera uma table com base nos dados passados.
     *
     * @param tableData Lista de linhas, onde cada linha contém uma lista de valores para geração das cells.
     */
    public DocumentData generateTable(List<List<Object>> tableData, String tableName) {

        Table table = createTable(tableName);

        List<Row> rowList = new ArrayList<>();
        Map<Integer, List<Cell>> rowCellListMap = new HashMap<>();

        for (List<Object> cellDataList : tableData) {

            Row row = createAndRegisterRow(table);
            rowList.add(row);

            processCellsForRow(table, row, cellDataList, rowCellListMap);
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
     * @param tableId               Id da table existente existente que será editada
     * @param tableName             Nome da tabela
     * @param tableCreationTime     Data e hora da criação da table original
     * @param newTableData          Novos dados para sobrescrever
     * @return GeneratedTableData com os mesmos IDs mas valores atualizados
     */
    public DocumentData overrideTableData(
            Integer tableId,
            String tableName,
            LocalDateTime tableCreationTime,
            List<List<Object>> newTableData
    ) {
        Table table = new Table(tableId, tableCreationTime);
        table.updateUpdateAtValue();
        table.setName(tableName);

        List<Row> rowList = new ArrayList<>();
        Map<Integer, List<Cell>> rowCellListMap = new HashMap<>();

        for (List<Object> cellDataList : newTableData) {

            Row row = createAndRegisterRow(table);
            rowList.add(row);

            processCellsForRow(table, row, cellDataList, rowCellListMap);
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
    private Table createTable(String name) {
        return new Table(generateTableId());
    }

    // Gera uma nova row e registra seu ID dentro da table
    private Row createAndRegisterRow(Table table) {
        Row row = componentGenerator.generateRow(table);
        table.getRowIdList().add(row.getId());
        return row;
    }

    // Processa a lista de dados de células pertencentes a uma row
    private void processCellsForRow(
            Table table,
            Row row,
            List<Object> cellDataList,
            Map<Integer, List<Cell>> rowCellListMap
    ) {
        for (Object value : cellDataList) {
            Cell cell = createAndRegisterCell(table, row, value);

            rowCellListMap
                    .computeIfAbsent(row.getId(), x -> new ArrayList<>())
                    .add(cell);
        }
    }

    // Cria uma cell, registra dentro da row e retorna
    private Cell createAndRegisterCell(Table table, Row row, Object value) {
        Cell cell = componentGenerator.generateCell(
                table.getId(),
                row,
                value
        );

        row.getCellIdList().add(cell.getId());
        return cell;
    }

    /**
     * Gera ID da tabela com base em data/hora.
     * Formato "DDSSMMMHH"
     */
    private Integer generateTableId() {

        LocalDateTime now = LocalDateTime.now();

        int day = now.getDayOfMonth();
        int second = now.getSecond();
        int ms = (now.getNano() / 1_000_000) % 1000;
        int nanoHash = Math.toIntExact(Math.abs(System.nanoTime()) % 100);

        String idStr = String.format("%02d%02d%03d%02d", day, second, ms, nanoHash);
        return Integer.parseInt(idStr);
    }

}
