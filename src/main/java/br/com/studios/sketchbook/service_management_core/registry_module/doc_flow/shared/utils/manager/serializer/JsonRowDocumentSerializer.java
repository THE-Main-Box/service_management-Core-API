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
        //Criamos um modelo intermediário com base no modelo principal
        RowJsonSerialModel serialModel = new RowJsonSerialModel(row);
        return mapper.writeValueAsString(serialModel);//Retornamos o modelo intermediário como se fosse uma String
    }

    /// des-serializa pra modelo interno
    private Row deserializeRow(String json) throws IOException {
        //Gera um modelo intermediário para carregarmos
        RowJsonSerialModel model = mapper.readValue(
                json,
                RowJsonSerialModel.class
        );

        return new Row(//Criamos um objeto com base no modelo intermediário criado
                model.id(),
                model.cellIds()
        );
    }

    /// Salva em json o dado de coluna
    public void saveRowInJson(Row row) {
        try {
            String json = serializeRow(row);//Cria o arquivo a ser salvo
            String fileName = rowFileName(row.getId());//Cria o nome
            Path filePath = document_row_folder_path.resolve(fileName);//Cria um path

            FileDocumentManagerUtils.save(json, filePath);//Salva
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar coluna em JSON", e);
        }
    }

    /// Salva uma lista de colunas com base no modelo
    public void saveRowListInJson(List<Row> rowList) {
        try {
            String fileName;//Nome do arquivo
            Path filePath;//Caminho do arquivo
            String json;//Conteúdo do arquivo json

            for(Row row : rowList){ //itera pela lista de id

                json = serializeRow(row);//Converte para um json
                fileName = rowFileName(row.getId());//Cria um nome de arquivo
                filePath = document_row_folder_path.resolve(fileName);//Cria um path com base no nome do arquivo

                FileDocumentManagerUtils.save(json, filePath);//Salva
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar a lista de coluna em JSON", e);
        }
    }

    /// Carrega uma coluna com base no id do modelo
    public Row loadRowInJson(Integer rowId) {
        try {
            String fileName = rowFileName(rowId);//Nome do arquivo

            Path filePath = document_row_folder_path.resolve(fileName);//Caminho do arquivo

            String json = FileDocumentManagerUtils.read(filePath);//Lê o path gerado
            return deserializeRow(json);//Retorna o objeto com base no json
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar coluna em JSON", e);
        }
    }

    /// Carrega uma lista de colunas com base numa lista de ids do modelo
    public List<Row> loadRowListInJson(List<Integer> rowId) {
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

    /// Deleta uma coluna caso encontremos o arquivo com o nome contendo o id dele
    public void deleteColumnJsonIfPresent(Integer rowId) {
        try {
            String fileName = rowFileName(rowId);//Cria um nome para o arquivo
            Path filePath = document_row_folder_path.resolve(fileName);//Cria um path pro arquivo

            if(FileDocumentManagerUtils.exists(filePath)) {//Se o path existir
                FileDocumentManagerUtils.delete(filePath);//deleta
            } else {
                throw new IOException("coluna não existe");//se não lança um erro
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar coluna em JSON", e);
        }
    }

    /// Deleta uma lista de colunas caso encontremos os arquivos com os nomes contendo os ids deles
    public void deleteColumnListJsonIfPresent(List<Integer> rowIdList) {
        try {
            String fileName; // Nome do arquivo
            Path filePath;   // Caminho do arquivo

            for(Integer rowId : rowIdList) { // itera pela lista

                fileName = rowFileName(rowId); // cria nome
                filePath = document_row_folder_path.resolve(fileName); // cria path

                if (FileDocumentManagerUtils.exists(filePath)) { // se existe
                    FileDocumentManagerUtils.delete(filePath);   // deleta
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar lista de colunas em JSON", e);
        }
    }


    /// Verifica se a coluna está presente
    public boolean isColumnJsonPresent(Integer rowId){
        String fileName = rowFileName(rowId);//Cria um nome com o id passado
        Path filePath = document_row_folder_path.resolve(fileName); //Cria um path seguindo o nome do arquivo

        return FileDocumentManagerUtils.exists(filePath);//Verifica se o path existe, e consequentemente o arquivo
    }
}
