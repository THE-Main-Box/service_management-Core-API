package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.GeneratedTableData;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core.CellDataSerialManagementCore;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core.RowDataSerialManagementCore;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core.TableDataSerialManagementCore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class DocumentTableIO {

    private final TableDataSerialManagementCore tableManager;
    private final RowDataSerialManagementCore rowManager;
    private final CellDataSerialManagementCore cellManager;

    public DocumentTableIO(ObjectMapper mapper) {

        this.tableManager = new TableDataSerialManagementCore(mapper);
        this.rowManager = new RowDataSerialManagementCore(mapper);
        this.cellManager = new CellDataSerialManagementCore(mapper);
    }

    /*
     * TODO: Criar uma espécie de CRUD para lidar com as table.
     *  Precisamos De métodos para: SALVAR, EDITAR, LER, DELETAR, VERIFICAR EXISTÊNCIA.
     *  Para as tabelas e seus dados
     */

    /**
     * Salva uma table e seus componentes de forma coerente
     *
     * @param data Dado intermediário contendo os dados da TABLE, ROW e CELL
     */
    public void saveTable(GeneratedTableData data) {

        tableManager.saveTableInJson(               //Salva em json a tabela vinda dos dados
                data.table()                        //Obtém a table armazenada
        );

        rowManager.saveRowListToJson(               //Salva em json a lista de linhas vindas dos dados
                data.rowList()                      //Obtém a lista de linhas
        );

        cellManager.saveCellListInJson(             //Salva a lista de células presente nas linhas
                data.rowCellListMap().values()          //Percorre os valores da lista mapeada
                        .stream()
                        .flatMap(List::stream)          //Torna o vetor de dentro do map em uma lista única
                        .toList()                       //Retornamos a lista resultante
        );

    }

    /**
     * Carrega uma Table e os seus componentes
     *
     * @param tableId Id da tabela que queremos carregar
     */
    public GeneratedTableData loadTableIfPresent(Integer tableId) {

        Table tableToReturn;                                    //Tabela que deverá ser retornada
        List<Row> rowToReturn;                                  //Linhas que deverão ser retornada
        Map<Integer, List<Cell>> cellMapToReturn;               //Células que deverão ser retornada

        //Impede de carregar uma tabela inexistente
        if (!tableManager.isTablePresentInJson(tableId)) return null;

        tableToReturn = tableManager.loadTableFromJson(tableId);        //Carrega uma table caso exista
        rowToReturn = generateRowFromTable(tableToReturn);              //Carrega uma lista de linhas caso exista
        cellMapToReturn = generateCellMapFromRowList(rowToReturn);      //Carrega um map de células por index da linha

        return new GeneratedTableData(
                tableToReturn,
                rowToReturn,
                cellMapToReturn
        );
    }

    /**
     * Gera um mapeamento de lista de células com base numa lista de linhas
     *
     * @param rowList lista das linhas das quais queremos carregar as Cell
     */
    private Map<Integer, List<Cell>> generateCellMapFromRowList(List<Row> rowList) {
        if (rowList.isEmpty()) return null;

        Map<Integer, List<Cell>> cellMapToReturn = new HashMap<>();

        for (Row row : rowList) {                           //Percorremos a lista de linhas

            if (row.getCellIdList().isEmpty()) continue;     //Nem iteramos caso esteja vazia

            List<Cell> cellList = new ArrayList<>();        //Uma lista de cell para cada row

            for (Integer cellId : row.getCellIdList()) {    //Percorremos a lista de id das células

                if (!cellManager.isCellPresentInJson(        //Verifica se a célula existe
                        row.getTableId(),
                        row.getId(),
                        cellId
                )) continue;


                cellList.add(
                        cellManager.loadCellFromJson(
                                row.getTableId(),
                                row.getId(),
                                cellId
                        )
                );
            }

            cellMapToReturn.put(
                    row.getId(),
                    cellList
            );

            cellList.clear();

        }

        return cellMapToReturn;

    }

    /**
     * Gera uma lista de objetos contendo as linhas das tabelas que foram salvas corretamente
     *
     * @param table Tabela da qual estamos buscando as linhas
     */
    private List<Row> generateRowFromTable(Table table) {
        if (table.getRowIdList().isEmpty()) return Collections.emptyList();

        List<Row> rowListToReturn = new ArrayList<>();

        for (Integer rowId : table.getRowIdList()) {

            //Se a linha não existir prosseguimos pra próxima
            if (
                    !rowManager.isRowPresentInJson(
                            table.getId(),
                            rowId
                    )
            ) continue;

            rowListToReturn.add(
                    rowManager.loadRowFromJson(
                            table.getId(),
                            rowId
                    )
            );

        }

        return rowListToReturn;
    }

}
