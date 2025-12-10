package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer;

import br.com.studios.sketchbook.service_management_core.application.api_utils.util.FileDocumentManagerUtils;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models.json.TableJsonSerialModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.PathDirection.document_table_folder_path;
import static br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.naming.NamingArchives.tableFileName;

public class JsonTableDocumentSerializer {
    private final ObjectMapper mapper;

    public JsonTableDocumentSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    private String serializeTable(Table table) throws IOException {
        TableJsonSerialModel model = new TableJsonSerialModel(table);
        return mapper.writeValueAsString(model);
    }

    private Table deserializeTable(String json) throws JsonProcessingException {
        TableJsonSerialModel model = mapper.readValue(
                json,
                TableJsonSerialModel.class
        );

        return new Table(
                model.id(),
                model.rowIdList()
        );
    }

    public void saveTable(Table table) {
        try {
            String json = serializeTable(table);

            String fileName = tableFileName(table.getId());
            Path filePath = document_table_folder_path.resolve(fileName);

            FileDocumentManagerUtils.save(json, filePath);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar tabela em JSON: ", e);
        }
    }

    public void saveTableList(List<Table> tableList) {
        try {
            String json;
            String fileName;
            Path filePath;

            for (Table table : tableList) {

                json = serializeTable(table);
                fileName = tableFileName(table.getId());
                filePath = document_table_folder_path.resolve(fileName);

                FileDocumentManagerUtils.save(json, filePath);
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar a lista de tabelas em JSON: ", e);
        }
    }

    public Table loadTable(Integer tableId) {
        try {
            String fileName = tableFileName(tableId);
            Path filePath = document_table_folder_path.resolve(fileName);

            String json = FileDocumentManagerUtils.read(filePath);
            return deserializeTable(json);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tabela em JSON: ", e);
        }
    }

    public List<Table> loadTableList(List<Integer> tableIdList) {
        try {
            List<Table> loadedList = new ArrayList<>();

            String fileName;
            Path filePath;
            String json;

            for (Integer tableId : tableIdList) {

                fileName = tableFileName(tableId);
                filePath = document_table_folder_path.resolve(fileName);

                if (!FileDocumentManagerUtils.exists(filePath)) {
                    continue;
                }

                json = FileDocumentManagerUtils.read(filePath);
                loadedList.add(deserializeTable(json));
            }

            return loadedList;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar lista de tabelas em JSON: ", e);
        }
    }

    public void deleteTableIfPresent(Integer tableId) {
        try {
            String fileName = tableFileName(tableId);
            Path filePath = document_table_folder_path.resolve(fileName);

            if (FileDocumentManagerUtils.exists(filePath)) {
                FileDocumentManagerUtils.delete(filePath);
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar tabela em JSON: ", e);
        }
    }

    public void deleteTableListIfPresent(List<Integer> tableIdList) {
        try {
            for (Integer tableId : tableIdList) {
                String fileName = tableFileName(tableId);
                Path filePath = document_table_folder_path.resolve(fileName);

                if (FileDocumentManagerUtils.exists(filePath)) {
                    FileDocumentManagerUtils.delete(filePath);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar lista de tabelas em JSON: ", e);
        }
    }

    public boolean isTablePresent(Integer tableId) {
        String fileName = tableFileName(tableId);
        Path filePath = document_table_folder_path.resolve(fileName);

        return FileDocumentManagerUtils.exists(filePath);
    }


}
