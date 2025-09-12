package br.com.studios.sketchbook.service_management_core.product.product_related.api.util;

import org.springframework.data.domain.Page;

import java.net.URI;
import java.util.UUID;

/**
 * Garante que todas as sevices para uso rest tenham os   métodos corretos
 *
 * @param <T> objeto que será gerenciado pela service
 */
public interface ProductRestServiceContract<T> {

    /// Obtém uma instancia do objeto pelo id passado
    T getInstanceById(UUID id);

    URI getUriForPersistedObject(T model);

    /// Usa generics para retornar um objeto atualizado com um dto para atualização
    T update(T model, Record dtoObject);

    /// Usa generics para obter uma page com todas as instancias do banco de dados
    Page<T> getAllInstances(int page, int size);

    /// Usa generics para obter uma page contendo todas as instancias com o nome passado
    Page<T> getInstancesByName(String name, int page, int size);

    /// Cria um objeto com base no dto de criação e retorna ele
    T createAndSave(Record creationDTO);

    /// Deletar o objeto com a id passada
    boolean delete(UUID id);

}
