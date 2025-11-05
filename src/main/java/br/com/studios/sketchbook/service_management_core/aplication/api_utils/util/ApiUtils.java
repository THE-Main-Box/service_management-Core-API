package br.com.studios.sketchbook.service_management_core.aplication.api_utils.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class ApiUtils {

    /**
     * Cria uma uri para lidar com a criação de um objeto persistido
     * @param modelId id do modelo
     * @param path caminho para podermos visualisar os dados do modelo
     * */
    public static URI getUriForPersistedObject(String modelId, String path){
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(path)
                .buildAndExpand(modelId)
                .toUri();
    }

}
