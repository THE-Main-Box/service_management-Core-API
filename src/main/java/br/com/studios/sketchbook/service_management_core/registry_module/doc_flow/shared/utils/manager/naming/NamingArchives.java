package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.naming;

public class NamingArchives {
    public static String cellFileName(Integer rowId, Integer cellId) {
        return "cell_" + rowId + "_" + cellId + ".json";
    }

    public static String rowFileName(Integer rowId) {
        return "row_" + rowId + ".json";
    }


}
