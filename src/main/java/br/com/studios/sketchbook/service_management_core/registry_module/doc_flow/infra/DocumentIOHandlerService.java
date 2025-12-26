package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.infra;

import br.com.studios.sketchbook.service_management_core.application.api_utils.util.FileDocumentManagerUtils;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.dto.res.DocumentDetailedResponse;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.dto.res.TableSumResponse;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_export_related.DocumentExportConverter;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_export_related.PdfDocumentExporter;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentGenerator;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentIO;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer.JsonTableDocumentSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.PathDirection.document_json_table_folder_path;
import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.PathDirection.document_pdf_folder_path;
import static br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.naming.NamingArchives.pdfFileName;
import static br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.naming.NamingArchives.tableFileName;

@Service
public class DocumentIOHandlerService {

    private final DocumentGenerator docGen;
    private final JsonTableDocumentSerializer tableGen;
    private final DocumentIO docIO;
    private final DocumentExportConverter exportConverter;
    private final PdfDocumentExporter pdfExporter;

    @Autowired
    public DocumentIOHandlerService(ObjectMapper mapper) {
        docGen = new DocumentGenerator();
        tableGen = new JsonTableDocumentSerializer(mapper);
        docIO = new DocumentIO(mapper);

        pdfExporter = new PdfDocumentExporter();
        exportConverter = new DocumentExportConverter();
    }

    public List<Integer> loadAllPdfIds() {
        try (Stream<Path> files = Files.list(document_pdf_folder_path)) {

            return files
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".pdf"))
                    .map(p -> {
                        String name = p.getFileName().toString();
                        return Integer.parseInt(
                                name.replaceAll("\\D+", "")
                        );
                    })
                    .sorted()
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao listar PDFs", e);
        }
    }

    public byte[] loadPdfByTableId(Integer tableId) {
        Path pdfPath = document_pdf_folder_path.resolve(
                pdfFileName(tableId)
        );

        if (!Files.exists(pdfPath)) {
            throw new EntityNotFoundException("PDF não encontrado");
        }

        try {
            return Files.readAllBytes(pdfPath);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler PDF", e);
        }
    }

    public void deletePdfByTableId(Integer tableId) {
        Path pdfPath = document_pdf_folder_path.resolve(
                pdfFileName(tableId)
        );

        if (!Files.exists(pdfPath)) {
            throw new EntityNotFoundException("PDF não encontrado");
        }

        try {
            Files.delete(pdfPath);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar PDF", e);
        }
    }


    public void exportToPdf(int tableId) throws IOException {
        DocumentData data = docIO.loadDocumentIfPresent(tableId);
        if(data == null)
            throw new EntityNotFoundException("Não achamos o pdf para exportar");

        String fileName = pdfFileName(tableId);
        Path filePath = document_pdf_folder_path.resolve(fileName);

        FileDocumentManagerUtils.save(
                pdfExporter.export(
                        exportConverter.toExportModel(data)
                ),
                filePath
        );
    }

    public boolean deleteMany(List<Integer> idList) {
        if (idList == null || idList.isEmpty()) {
            return false;
        }

        for (Integer id : idList) {
            boolean deleted = docIO.deleteDocumentByTableId(id);

            if (!deleted) {
                return false;
            }
        }

        return true;
    }

    public DocumentDetailedResponse loadTableData(int tableId) {
        DocumentData documentData = docIO.loadDocumentIfPresent(tableId);

        if(documentData == null) {
            throw new EntityNotFoundException("Documento não encontrado");
        }

        return new DocumentDetailedResponse(
                documentData,
                docGen.toListOfLists(documentData),
                docGen.getColumnNames(documentData)
        );

    }

    public Page<TableSumResponse> loadAllTableSumPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return loadAllTablesPaged(
                pageable
        ).map(TableSumResponse::new);
    }

    private Page<Table> loadAllTablesPaged(
            Pageable pageable
    ) {

        try (Stream<Path> files = Files.list(document_json_table_folder_path)) {

            // lista materializada UMA vez
            List<Path> jsonFiles =
                    files
                            .filter(Files::isRegularFile)
                            .filter(p -> p.getFileName().toString().endsWith(".json"))
                            .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                            .toList();

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), jsonFiles.size());

            List<Table> pageContent =
                    jsonFiles.subList(start, end)
                            .stream()
                            .map(path -> {
                                try {
                                    String json = FileDocumentManagerUtils.read(path);
                                    return tableGen.deserializeTable(json);
                                } catch (Exception e) {
                                    throw new RuntimeException(
                                            "Erro ao ler table: " + path.getFileName(), e
                                    );
                                }
                            })
                            .toList();

            return new PageImpl<>(
                    pageContent,
                    pageable,
                    jsonFiles.size()
            );

        } catch (IOException e) {
            throw new RuntimeException("Erro ao listar tabelas", e);
        }
    }

}
