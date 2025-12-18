package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.infra;

import br.com.studios.sketchbook.service_management_core.application.api_utils.util.FileDocumentManagerUtils;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.dto.res.TableSumResponse;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentIO;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core.TableSerialDataManagementCore;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer.JsonTableDocumentSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Service
public class DocumentIOHandlerService {

    private final JsonTableDocumentSerializer docGen;
    private final DocumentIO docIO;

    @Autowired
    public DocumentIOHandlerService(ObjectMapper mapper) {
        docGen = new JsonTableDocumentSerializer(mapper);
        docIO = new DocumentIO(mapper);
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
                                    return docGen.deserializeTable(json);
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
