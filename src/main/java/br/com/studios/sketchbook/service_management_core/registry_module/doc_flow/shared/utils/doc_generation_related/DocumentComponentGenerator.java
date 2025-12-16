package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;

public class DocumentComponentGenerator {

    /**
     * Gera uma linha dentro da tabela passada
     *
     * @param rowTable Tabela a quem nossa linha pertence
     * @return retornamos o objeto da row já inserido na table
     */
    public Row generateRow(Table rowTable) {
        Row row = new Row(
                rowTable.getRowIdList().size(),
                rowTable.getId()
        );

        rowTable.getRowIdList().add(
                row.getId()
        );

        return row;
    }

    /**
     * Gera uma celula dentro de uma table, dentro de uma row
     *
     * @param cellTableId  id da tabela a quem a nossa cell pertence
     * @param cellRow      linha a qual a cell pertence
     * @param valueToStore valor que iremos armazenar
     * @param columnName   nome da coluna que a cell foi gerada
     * @return Retornamos a cell já inserida dentro da table e row, contendo os dados desejados
     */
    public Cell generateCell(Integer cellTableId, Row cellRow, Object valueToStore, String columnName) {
        Cell cell = new Cell(                   //Geramos a cell
                cellRow.getCellIdList().size(), //Obtemos o último id disponível
                cellTableId,              //Obtemos o id da tabela
                cellRow.getId(),                //Obtemos o id da linha
                valueToStore                    //Salvamos o valor
        );

        cell.setName(columnName);

        cellRow.getCellIdList().add(            //Adicionamos o id da cell dentro de row
                cell.getId()                    //Passamos o id, que corresponde a posição dela na lista
        );

        return cell;
    }

}
