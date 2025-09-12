package br.com.studios.sketchbook.service_management_core.product.product_related.api.util;

import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.Product;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class ProductApiUtils {

    /**
     * Cria uma uri para lidar com a criação de um objeto persistido
     * @param model modelo de objeto que possui um id
     * @param path caminho para podermos visualisar os dados do modelo
     * */
    public static URI getUriForPersistedObject(Product model, String path){
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(path)
                .buildAndExpand(model.getId())
                .toUri();
    }

}
