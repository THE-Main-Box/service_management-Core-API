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
        CellJSONSerialModel model = objectMapper.readValue(
                json,
                CellJSONSerialModel.class
        );

        // Converte string de volta para tipo correto
        Object value = convertToType(
                model.value(),
                model.valueType()
        );
        return new Cell(
                model.id(),
                model.tableId(),
                model.rowId(),
                value
        );
    }

    public void saveCellInJson(Cell cell) {
        try {
            String json = serializeCell(cell);
            String fileName = cellFileName(
                    cell.getTableId(),
                    cell.getRowId(),
                    cell.getId()
            );
            Path filePath = document_cell_folder_path.resolve(fileName);

            FileDocumentManagerUtils.save(json, filePath);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar célula em JSON: ", e);
        }
    }

    /// Salva uma lista de células com base no modelo
    public void saveCellListInJson(List<Cell> cellList) {
        try {
            String fileName;//Nome do arquivo
            Path filePath;//Caminho do arquivo
            String json;//Conteúdo do arquivo json

            for (Cell cell : cellList) { //itera pela lista de id

                json = serializeCell(cell);//Converte para um json

                fileName = cellFileName(
                        cell.getTableId(),
                        cell.getRowId(),
                        cell.getId()
                );//Cria um nome de arquivo

                filePath = document_cell_folder_path.resolve(fileName);//Cria um path com base no nome do arquivo

                FileDocumentManagerUtils.save(json, filePath);//Salva
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar a lista de células em JSON: ", e);
        }
    }

    /// Carrega um objeto de célula contendo dados, a partir de um documento json
    public Cell loadCellFromJson(Integer tableId, Integer rowId, Integer cellId) {
        try {
            String fileName = cellFileName(
                    tableId,
                    rowId,
                    cellId
            );
            Path filePath = document_cell_folder_path.resolve(fileName);

            String json = FileDocumentManagerUtils.read(filePath);
            return deserializeCell(json);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar célula em JSON: ", e);
        }
    }

    /// Carrega uma lista de cells com base numa lista de dados passada
    public List<Cell> loadCellListFromJson(List<Integer> tableIdList, List<Integer> rowIdList, List<Integer> cellIdList) {
        try {
            List<Cell> toReturnList = new ArrayList<>();//salva em lista
            String fileName;//Nome do arquivo
            Path filePath;//Caminho do arquivo
            String json;//Conteúdo do arquivo json

            for (Integer tableId : tableIdList) {
                for (Integer rowId : rowIdList) {
                    for (Integer cellId : cellIdList) { //itera pela lista de id

                        fileName = cellFileName(
                                tableId,
                                rowId,
                                cellId
                        ); //Atualiza o nome do arquivo

                        filePath = document_cell_folder_path.resolve(fileName);//Atualiza o path do arquivo

                        if (!FileDocumentManagerUtils.exists(filePath)) {
                            continue;//se não existir prossegue pra próxima iteração
                        }

                        json = FileDocumentManagerUtils.read(filePath);//Armazena o conteúdo

                        toReturnList.add(//Adiciona o objeto criado a partir do conteúdo dentro da lista
                                deserializeCell(json)
                        );
                    }
                }
            }
            return toReturnList; //retorna lista
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar a lista de células em JSON: ", e);
        }
    }

    public void deleteCellJsonIfPresent(Integer tableId, Integer rowId, Integer cellId) {
        try {

            String fileName = cellFileName(
                    tableId,
                    rowId,
                    cellId
            );

            Path filePath = document_cell_folder_path.resolve(fileName);

            if (FileDocumentManagerUtils.exists(filePath)) {
                FileDocumentManagerUtils.delete(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar célula em JSON: ", e);
        }
    }

    /// Deleta uma lista de células caso encontremos os arquivos com os nomes contendo os ids deles
    public void deleteCellListJsonIfPresent(List<Integer> tableIdList, List<Integer> rowIdList, List<Integer> cellIdList) {
        try {
            String fileName; // Nome do arquivo
            Path filePath;   // Caminho do arquivo

            for (Integer tableId : tableIdList) {
                for (Integer rowId : rowIdList) { // itera pela lista de id de coluna
                    for (Integer cellId : cellIdList) { //Itera pela lista de id de células

                        fileName = cellFileName(
                                tableId,
                                rowId,
                                cellId
                        ); //Atualiza o nome do arquivo

                        filePath = document_cell_folder_path.resolve(fileName);//Atualiza o path do arquivo

                        if (FileDocumentManagerUtils.exists(filePath)) {
                            FileDocumentManagerUtils.delete(filePath);
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar lista de colunas em JSON: ", e);
        }
    }

    public boolean isCellJsonPresent(Integer tableId, Integer rowId, Integer cellId) {
        String fileName = cellFileName(
                tableId,
                rowId,
                cellId
        );
        Path filePath = document_cell_folder_path.resolve(fileName);

        return FileDocumentManagerUtils.exists(filePath);
    }

}
