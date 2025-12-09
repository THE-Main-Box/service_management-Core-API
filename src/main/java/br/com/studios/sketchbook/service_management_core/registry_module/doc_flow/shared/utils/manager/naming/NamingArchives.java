package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.naming;

public class NamingArchives {
    /**
     * Cria um nome de arquivo para Cell
     *
     * @param rowId id da coluna em que estamos inserindo a cell
     * @param cellId id da Cell
     */
    public static String cellFileName(Integer rowId, Integer cellId) {
        return "cell_" + rowId + "_" + cellId + ".json";
    }

    /**
     * Cria um nome para o arquivo do objeto Row
     *
     * @param rowId id da coluna
     */
    public static String rowFileName(Integer rowId) {
        return "row_" + rowId + ".json";
    }


}
