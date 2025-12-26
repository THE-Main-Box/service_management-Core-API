package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_export_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models.export.ExportTableModel;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class PdfDocumentExporter implements DocumentExporter {

    @Override
    public byte[] export(ExportTableModel model) {

        if (model == null || model.columns() == null || model.rows() == null) {
            throw new IllegalArgumentException("Modelo de exportação inválido");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // ---------- TÍTULO ----------
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph(model.title(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // ---------- TABELA ----------
            PdfPTable table = new PdfPTable(model.columns().size());
            table.setWidthPercentage(100);

            // Cabeçalho
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            for (String column : model.columns()) {
                PdfPCell headerCell = new PdfPCell(new Phrase(column, headerFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(headerCell);
            }

            // Dados
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            for (List<Object> row : model.rows()) {

                if (row.size() != model.columns().size()) {
                    throw new IllegalStateException(
                            "Linha com quantidade de colunas incompatível"
                    );
                }

                for (Object value : row) {
                    String text = value == null ? "" : value.toString();
                    table.addCell(new PdfPCell(new Phrase(text, cellFont)));
                }
            }

            document.add(table);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return out.toByteArray();
    }
}
