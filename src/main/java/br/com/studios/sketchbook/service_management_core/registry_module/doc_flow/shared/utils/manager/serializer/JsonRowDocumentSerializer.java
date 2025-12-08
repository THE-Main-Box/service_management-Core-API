package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer;

import br.com.studios.sketchbook.service_management_core.application.api_utils.util.FileDocumentManagerUtils;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models.RowJsonSerialModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.PathDirection.document_row_folder_path;
import static br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.naming.NamingArchives.rowFileName;

public class JsonRowDocumentSerializer {

    private final ObjectMapper mapper;

    public JsonRowDocumentSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /// Converte para JSON a partir do modelo original
    private String serializeRow(Row row) throws IOException {
        RowJsonSerialModel serialModel = new RowJsonSerialModel(row);
        return mapper.writeValueAsString(serialModel);
    }

    /// des-serializa pra modelo interno
    private Row deserializeRow(String json) throws IOException {
        RowJsonSerialModel model = mapper.readValue(json, RowJsonSerialModel.class);

        return new Row(
                model.id(),
                model.cellIds()
        );
    }

    /// Salva em json o dado de coluna
    public void saveRow(Row row) {
        try {
            String json = serializeRow(row);
            String fileName = rowFileName(row.getId());
            Path filePath = document_row_folder_path.resolve(fileName);

            FileDocumentManagerUtils.save(json, filePath);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar coluna em JSON", e);
        }
    }

    public Row loadRow(Integer rowId) {
        try {
            String fileName = rowFileName(rowId);

            Path filePath = document_row_folder_path.resolve(fileName);

            String json = FileDocumentManagerUtils.read(filePath);
            return deserializeRow(json);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar coluna em JSON", e);
        }
    }

    public List<Row> loadRowList(List<Integer> rowId) {
        try {
            List<Row> toReturnList = new ArrayList<>();//salva em lista
            String fileName;//Nome do arquivo
            Path filePath;//Caminho do arquivo
            String json;//Conteúdo do arquivo json

            for(Integer id : rowId){ //itera pela lista de id

                fileName = rowFileName(id); //Atualiza o nome do arquivo
                filePath = document_row_folder_path.resolve(fileName);//Atualiza o path do arquivo

                if(!FileDocumentManagerUtils.exists(filePath)) continue;//se não existir prossegue pra próxima iteração

                json = FileDocumentManagerUtils.read(filePath);//Armazena o conteúdo

                toReturnList.add(//Adiciona o objeto criado a partir do conteúdo dentro da lista
                        deserializeRow(json)
                );
            }

            return toReturnList; //retorna lista
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar a lista de coluna em JSON", e);
        }
    }

    public void deleteColumnJsonIfPresent(Integer rowId) {
        try {
            String fileName = rowFileName(rowId);
            Path filePath = document_row_folder_path.resolve(fileName);

            if(FileDocumentManagerUtils.exists(filePath)) {
                FileDocumentManagerUtils.delete(filePath);
            } else {
                throw new IOException("coluna não existe");
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar coluna em JSON", e);
        }
    }

    public boolean isColumnJsonPresent(Integer rowId){
        String fileName = rowFileName(rowId);
        Path filePath = document_row_folder_path.resolve(fileName);

        return FileDocumentManagerUtils.exists(filePath);
    }
}
