package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;
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
     * TODO: Adicionar um DTO que inclua os dados da tabela em geral como um vetor.
     *  Junto de um dado relacionado ao id da tabela, pois é algo importante de se salvar,
     *  já que os Id das linhas e células se referem às suas posições na row.
     *  O DTO deve conter informações como por exemplo a data e horário de criação e edição
     *  e outras informações não passadas no vetor
     */

    /**
     * Salva uma table e seus componentes de forma coerente
     *
     * @param data Dado intermediário contendo os dados da TABLE, ROW e CELL
     */
    public void saveDocument(DocumentData data) {

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
     * Carrega um documento completo, sua table e componentes internos
     *
     * @param tableId Id da tabela que queremos carregar
     */
    public DocumentData loadDocumentIfPresent(Integer tableId) {

        Table tableToReturn;                                    //Tabela que deverá ser retornada
        List<Row> rowListToReturn;                                  //Linhas que deverão ser retornada
        Map<Integer, List<Cell>> cellMapToReturn;               //Células que deverão ser retornada

        //Impede de carregar uma tabela inexistente
        if (!tableManager.isTablePresentInJson(tableId)
        ) return null;

        tableToReturn = tableManager.loadTableFromJson(         //Carrega uma table caso exista
                tableId
        );
        rowListToReturn = generateRowFromTable(                     //Carrega uma lista de linhas caso exista
                tableToReturn
        );
        cellMapToReturn = generateCellMapFromRowList(           //Carrega um map de células por index da linha
                rowListToReturn
        );

        return new DocumentData(
                tableToReturn,
                rowListToReturn,
                cellMapToReturn
        );
    }

    /**
     * Apaga os documentos antigos e salva os novos, que possuem o mesmo Id
     *
     * @param dataToOverride dados já convertidos, prontos para serem salvos
     */
    public boolean updateDocument(DocumentData originalDocument, DocumentData dataToOverride) {

        //Se a tabela não existir nem tentamos
        if (!tableManager
                .isTablePresentInJson(
                        dataToOverride.table().getId()
                )

                ||

                !deleteAllTableComponentsIfPresentAsDocument(
                        originalDocument
                )
        ) return false;

        saveDocument(dataToOverride);

        return true;
    }

    /**
     * Percorre todos arquivos que correspondem à table e seus componentes e os apaga
     *
     * @param data Data contendo os dados da tabela
     */
    public boolean deleteAllTableComponentsIfPresentAsDocument(DocumentData data) {
        boolean tableDeleted;
        boolean allRowsDeleted = true;
        boolean allCellsDeleted = true;

        // Tenta deletar a table caso esteja presente
        tableDeleted = tableManager.deleteTableIfPresentInJson(
                data.table().getId()
        );

        // Percorre a lista de linhas
        for (Row row : data.rowList()) {
            // Tenta apagar a linha atual caso exista em formato json
            boolean rowDeleted = rowManager.deleteRowIfPresentInJson(
                    row.getTableId(),
                    row.getId()
            );

            //Se a linha não foi deletada, então não deletamos todas as linhas
            if (!rowDeleted) {
                allRowsDeleted = false;
            }

            //Percorre a lista de id de cell dentro da lista de Ids da linha
            for (Integer cellId : row.getCellIdList()) {
                boolean currentCellDeleted = cellManager.deleteCellIfPresentInJson(//Tenta deletar a cell
                        data.table().getId(),
                        row.getId(),
                        cellId
                );


                //Verifica se a célula está presente então não foi deletada
                if (!currentCellDeleted) {
                    allCellsDeleted = false;
                }
            }

        }

        // Retorna true apenas se TUDO foi deletado com sucesso
        return tableDeleted && allRowsDeleted && allCellsDeleted;
    }

    public boolean isTablePresent(Integer tableId){
        return tableManager.isTablePresentInJson(tableId);
    }

    //COMPONENTS TO USE

    /**
     * Gera um mapeamento de lista de células com base numa lista de linhas
     *
     * @param rowList lista das linhas das quais queremos carregar as Cell
     */
    private Map<Integer, List<Cell>> generateCellMapFromRowList(List<Row> rowList) {
        if (rowList.isEmpty()) return null;

        Map<Integer, List<Cell>> cellMapToReturn = new HashMap<>();

        for (Row row : rowList) {                           //Percorremos a lista de linhas

            //Evitamos de carregar a linha caso a lista esteja limpa
            if (row.getCellIdList().isEmpty()
            ) continue;

            List<Cell> cellList = new ArrayList<>();        //Uma lista de cell para cada row

            for (Integer cellId : row.getCellIdList()) {    //Percorremos a lista de id das células

                //Verifica se a célula existe
                if (!cellManager.isCellPresentInJson(
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

        }

        return cellMapToReturn;

    }

    /**
     * Gera uma lista de objetos contendo as linhas das tabelas que foram salvas corretamente
     *
     * @param table Tabela da qual estamos buscando as linhas
     */
    private List<Row> generateRowFromTable(Table table) {
        if (
                table.getRowIdList().isEmpty()
        ) return Collections.emptyList();

        List<Row> rowListToReturn = new ArrayList<>();

        for (Integer rowId : table.getRowIdList()) {

            //Se a linha não existir prosseguimos pra próxima
            if (!rowManager.isRowPresentInJson(
                    table.getId(),
                    rowId
            )) continue;

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
