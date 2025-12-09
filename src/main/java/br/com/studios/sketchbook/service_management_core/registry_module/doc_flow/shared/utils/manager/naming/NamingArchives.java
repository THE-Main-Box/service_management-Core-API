package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.naming;

public class NamingArchives {
    /**
     * Cria um nome de arquivo para Cell
     *
     * @param tableId id da tabela a quem a cell pertence
     * @param rowId  id da coluna em que estamos inserindo a cell
     * @param cellId id da Cell
     */
    public static String cellFileName(Integer tableId, Integer rowId, Integer cellId) {
        return "table_"+ tableId + "_" +"cell_" + rowId + "_" + cellId + ".json";
    }

    /**
     * Cria um nome para o arquivo do objeto Row
     * @param tableId id da tabela a quem o objeto pertence
     * @param rowId id da coluna
     */
    public static String rowFileName(Integer tableId, Integer rowId) {
        return "table_" + tableId + "_" + "row_" + rowId + ".json";
    }


}
