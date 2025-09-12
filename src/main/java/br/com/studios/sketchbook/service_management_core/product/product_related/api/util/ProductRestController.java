package br.com.studios.sketchbook.service_management_core.product.product_related.api.util;

import br.com.studios.sketchbook.service_management_core.product.product_related.shared.dto.ProductsDeleteManyDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/// Garante que todos os controladores terão os métodos necessários
public interface ProductRestController {
    ResponseEntity<Page<Object>> getAll(int page, int size);

    ResponseEntity<Page<Object>> getByName(String name, int page, int size);

    ResponseEntity<Object> getById(UUID id);

    ResponseEntity<Object> update(UUID id, Object dtoObj);

    ResponseEntity<Object> create(Object dtoObj);

    ResponseEntity<Object> removeById(UUID id);

    ResponseEntity<Object> removeAll(ProductsDeleteManyDTO dto);
}
