package br.com.studios.sketchbook.service_management_core.product.product_related.api.util;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

/**
 * Garante que todos os controladores terão os métodos necessários
 *
 * @param <T> entidade do banco (opcional, se precisar na service)
 * @param <C> DTO de criação
 * @param <U> DTO de atualização
 * @param <R> DTO de resposta
 */
public interface ProductRestControllerContract<T, C, U, R> {


    /**
     * Obtém uma page contendo um dto de todos os objetos requisitados em ordem alfabética
     *
     * @param page página em que queremos ler
     * @param size tamanho da página
     */
    ResponseEntity<Page<R>> getAll(int page, int size);

    /**
     * Obtém uma page contendo dtos de uma pesquisa por nome
     *
     * @param page página em que queremos ler
     * @param size tamanho da página
     */
    ResponseEntity<Page<R>> getByName(String name, int page, int size);

    /// Obtém um objeto de transferência (DTO) contendo os dados do objeto obtido
    ResponseEntity<R> getById(UUID id);

    /**
     * Atualiza um objeto
     *
     * @param id     id do objeto que devemos obter
     * @param dtoObj DTO de atualização
     */
    ResponseEntity<R> update(UUID id, U dtoObj);

    /**
     * Recebe um DTO de criação e usa para criar uma nova entidade,
     * e retornamos um DTO de resposta para mostrar que nós criamos corretamente
     */
    ResponseEntity<R> create(C dtoObj);

    /// Obtém um id e usa ele para descobrir o objeto e deletar ele
    ResponseEntity<Object> removeById(UUID id);

    /// Remove todos os objetos com as suas ids presentes na lista
    ResponseEntity<Object> removeAll(List<UUID> idList);
}
