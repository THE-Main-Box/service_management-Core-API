package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.serializer;

import br.com.studios.sketchbook.service_management_core.application.api_utils.util.FileDocumentManagerUtils;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models.CellJSONSerialModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.PathDirection.document_cell_folder_path;
import static br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.converter.ConvertFromString.convertToType;
import static br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.naming.NamingArchives.cellFileName;

public class JsonCellDocumentSerializer {

    private final ObjectMapper objectMapper;

    public JsonCellDocumentSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /// Converte para JSON a partir do modelo original
    private String serializeCell(Cell cell) throws IOException {
        CellJSONSerialModel serialModel = new CellJSONSerialModel(cell);
        return objectMapper.writeValueAsString(serialModel);
    }

    private Cell deserializeCell(String json) throws IOException {
        CellJSONSerialModel model = objectMapper.readValue(json, CellJSONSerialModel.class);

        // Converte string de volta para tipo correto
        Object value = convertToType(model.value(), model.valueType());
        return new Cell(model.id(), model.rowId(), value);
    }

    public void saveCell(Cell cell) {
        try {
            String json = serializeCell(cell);
            String fileName = cellFileName(cell.getRowId(), cell.getId());
            Path filePath = document_cell_folder_path.resolve(fileName);

            FileDocumentManagerUtils.save(json, filePath);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar célula em JSON", e);
        }
    }

    public Cell loadCell(Integer rowId, Integer cellId) {
        try {
            String fileName = cellFileName(rowId, cellId);
            Path filePath = document_cell_folder_path.resolve(fileName);

            String json = FileDocumentManagerUtils.read(filePath);
            return deserializeCell(json);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar célula em JSON", e);
        }
    }

    public List<Cell> loadCellList(Integer rowId, List<Integer> cellIdList) {
        try {
            List<Cell> toReturnList = new ArrayList<>();//salva em lista
            String fileName;//Nome do arquivo
            Path filePath;//Caminho do arquivo
            String json;//Conteúdo do arquivo json

            for(Integer cellId : cellIdList){ //itera pela lista de id

                fileName = cellFileName(rowId, cellId); //Atualiza o nome do arquivo
                filePath = document_cell_folder_path.resolve(fileName);//Atualiza o path do arquivo

                if(!FileDocumentManagerUtils.exists(filePath)) continue;//se não existir prossegue pra próxima iteração

                json = FileDocumentManagerUtils.read(filePath);//Armazena o conteúdo

                toReturnList.add(//Adiciona o objeto criado a partir do conteúdo dentro da lista
                        deserializeCell(json)
                );
            }

            return toReturnList; //retorna lista
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar a lista de células em JSON", e);
        }
    }

    public void deleteCellJsonIfPresent(Integer rowId, Integer cellId) {
        try {
            String fileName = cellFileName(rowId, cellId);
            Path filePath = document_cell_folder_path.resolve(fileName);

            if(FileDocumentManagerUtils.exists(filePath)) {
                FileDocumentManagerUtils.delete(filePath);
            } else {
                throw new IOException("célula não existe");
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar célula em JSON", e);
        }
    }

    public boolean isCellJsonPresent(Integer rowId, Integer cellId){
        String fileName = cellFileName(rowId, cellId);
        Path filePath = document_cell_folder_path.resolve(fileName);

        return FileDocumentManagerUtils.exists(filePath);
    }

}
