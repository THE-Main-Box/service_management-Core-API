package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_export_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models.export.ExportTableModel;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentGenerator;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;

import java.util.List;
import java.util.Map;

public class DocumentExportConverter {

    private final DocumentGenerator docGen;

    public DocumentExportConverter() {
        this.docGen = new DocumentGenerator();
    }

    public ExportTableModel toExportModel(DocumentData data) {

        List<String> columns = docGen.getColumnNames(data);
        List<List<Object>> rows = docGen.toListOfLists(data);

        Map<String, Object> metadata = Map.of(
                "tableId", data.table().getId(),
                "createdAt", data.table().getCreatedAt(),
                "updatedAt", data.table().getUpdatedAt(),
                "prefix", data.table().getDocumentPrefix().name()
        );

        return new ExportTableModel(
                data.table().getName(),
                columns,
                rows,
                metadata
        );
    }
}
