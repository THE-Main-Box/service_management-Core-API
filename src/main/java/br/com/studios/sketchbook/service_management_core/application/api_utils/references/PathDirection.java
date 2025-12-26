package br.com.studios.sketchbook.service_management_core.application.api_utils.references;

import java.nio.file.Path;

public class PathDirection {
    /// Path da pasta raiz do projeto
    public static final String base_package_path = "br.com.studios.sketchbook.service_management_core";
    public static final String storage_module_path = base_package_path +".storage_module";
    public static final String registry_module_path = base_package_path + ".registry_module";

    public static final Path projectRoot = Path.of("").toAbsolutePath();

    public static final Path document_folder_path = projectRoot.resolve("data/doc");
    public static final Path document_json_folder_path = document_folder_path.resolve("json");
    public static final Path document_json_cell_folder_path = document_json_folder_path.resolve("cell");
    public static final Path document_json_row_folder_path = document_json_folder_path.resolve("row");
    public static final Path document_json_table_folder_path = document_json_folder_path.resolve("table");

    public static final Path document_pdf_folder_path = document_folder_path.resolve("pdf");


}
