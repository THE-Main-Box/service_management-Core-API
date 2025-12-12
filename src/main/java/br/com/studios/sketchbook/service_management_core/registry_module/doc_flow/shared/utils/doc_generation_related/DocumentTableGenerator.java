package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.GeneratedTableData;

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
     * Geramos uma table com base nos dados passados
     *
     * @param tableData dado abstrato passado
     *                  <p>
     *                  List<> corresponde à lista de linhas(instancia de dados)
     *                  <p>
     *                  List< List<> > corresponde a uma lista de dados
     *                  (ela que contém as celulas contendo os dados que iremos salvar)
     */
    public GeneratedTableData generateTable(List<List<Object>> tableData) {

        Table table = createTable();
        Map<Row, List<Cell>> totalCellRowMap = new HashMap<>();

        for (List<Object> cellDataList : tableData) {
            Row row = createAndRegisterRow(table);
            processCellsForRow(table, row, cellDataList, totalCellRowMap);
        }

        return buildGeneratedData(table, totalCellRowMap);
    }

    /// Gera uma table
    private Table createTable() {
        return new Table(generateTableId());
    }

    /// Cria uma row e insere ela dentro da table
    private Row createAndRegisterRow(Table table) {
        Row row = componentGenerator.generateRow(table);
        table.getRowIdList().add(row.getId());
        return row;
    }

    /// Processa a lista de Objetos que corresponde à cell dentro de uma Row
    private void processCellsForRow(
            Table table,
            Row row,
            List<Object> cellDataList,
            Map<Row, List<Cell>> totalCellRowMap
    ) {
        for (Object cellValue : cellDataList) {
            Cell cell = createAndRegisterCell(table, row, cellValue);
            totalCellRowMap
                    .computeIfAbsent(row, r -> new ArrayList<>())
                    .add(cell);
        }
    }

    /// Cria e insere uma Cell dentro de uma Row
    private Cell createAndRegisterCell(Table table, Row row, Object value) {
        Cell cell = componentGenerator.generateCell(
                table.getId(),
                row,
                value
        );

        row.getCellIdList().add(cell.getId());
        return cell;
    }

    /// Gera um dado que irá conter todas as informações a respeito da table que criamos
    private GeneratedTableData buildGeneratedData(
            Table table,
            Map<Row, List<Cell>> totalCellRowMap
    ) {
        return new GeneratedTableData(
                table,
                new ArrayList<>(totalCellRowMap.keySet()),
                totalCellRowMap
        );
    }

    /**
     * Gera um id com base nos dados de tempo e data
     * <p>
     * Formato resultante (string): "DDSSMMMHH"
     * - DD   : dia do mês (2 dígitos, 01–31)
     * - SS   : segundo do minuto (2 dígitos, 00–59)
     * - MMM  : milissegundos do instante (3 dígitos, 000–999)
     * - HH   : hash derivado de System.nanoTime() mod 100 (2 dígitos, 00–99)
     */
    private Integer generateTableId() {
        // Obtém a data e hora atuais
        LocalDateTime now = LocalDateTime.now();

        // Captura o dia do mês (vai de 01 até 31)
        int day = now.getDayOfMonth();

        // Captura o segundo atual (00 a 59)
        int second = now.getSecond();

        // Converte nanos para milissegundos (0–999)
        // O "% 1000" garante que o valor sempre caiba em 3 dígitos
        int ms = (now.getNano() / 1_000_000) % 1000;

        // Usa System.nanoTime() para gerar um valor altamente variável
        // Depois aplica módulo 100 para manter o resultado com 2 dígitos (00–99)
        int nanoHash = Math.toIntExact(Math.abs(System.nanoTime()) % 100);

        // Monta a string do ID no formato:
        // DD SS MMM HH
        // Cada parte tem tamanho fixo para evitar colisões por formatação
        String idStr = String.format("%02d%02d%03d%02d", day, second, ms, nanoHash);

        // Converte a string final para Integer e retorna
        return Integer.parseInt(idStr);
    }


}
