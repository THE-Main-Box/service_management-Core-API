package br.com.studios.sketchbook.service_management_core.api_utils.contracts;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Garante que todos os controladores terão os métodos necessários
 */
public interface ProductRestControllerContract {


    /**
     * Obtém uma page contendo um dto de todos os objetos requisitados em ordem alfabética
     *
     * @param page página em que queremos ler
     * @param size tamanho da página
     */
    ResponseEntity<Page<Object>> getAll(int page, int size);

    /**
     * Obtém uma page contendo dtos de uma pesquisa por nome
     *
     * @param page página em que queremos ler
     * @param size tamanho da página
     */
    ResponseEntity<Page<Object>> getByName(String name, int page, int size);

    /// Obtém um objeto de transferência (DTO) contendo os dados do objeto obtido
    ResponseEntity<Object> getById(UUID id);

    /**
     * Atualiza um objeto
     *
     * @param id     id do objeto que devemos obter
     * @param dtoObj DTO de atualização
     */
    ResponseEntity<Object> update(UUID id, Object dtoObj);

    /**
     * Recebe um DTO de criação e usa para criar uma nova entidade,
     * e retornamos um DTO de resposta para mostrar que nós criamos corretamente
     */
    ResponseEntity<Object> create(Object dtoObj);

    /// Obtém um id e usa ele para descobrir o objeto e deletar ele
    ResponseEntity<Object> removeById(UUID id);

}
