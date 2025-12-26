package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models.export;

import java.util.List;
import java.util.Map;

public record ExportTableModel(
        String title,
        List<String> columns,
        List<List<Object>> rows,
        Map<String, Object> metadata
) {
}
