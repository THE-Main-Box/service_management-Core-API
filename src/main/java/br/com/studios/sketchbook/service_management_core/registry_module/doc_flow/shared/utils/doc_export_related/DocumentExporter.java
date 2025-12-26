package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_export_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models.export.ExportTableModel;

public interface DocumentExporter {
    byte[] export(ExportTableModel model);
}
