package br.com.studios.sketchbook.service_management_core.product.product_related.api;

import br.com.studios.sketchbook.service_management_core.product.product_related.api.util.ProductRestControllerContract;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.def_product.ProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.def_product.ProductResponseDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.def_product.ProductUpdateDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.Product;
import br.com.studios.sketchbook.service_management_core.product.product_related.infra.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products/product")
public class ProductController implements ProductRestControllerContract {

    private final ProductService service;
    private final ObjectMapper mapper;

    @Autowired
    public ProductController(ProductService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Object>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.getAllInstances(page, size).map(ProductResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Page<Object>> getByName(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.getInstancesByName(name, page, size).map(ProductResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new ProductResponseDTO(service.getInstanceById(id))//Transforma em dto a instancia retornada
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Object> update(
            @PathVariable UUID id,
            @RequestBody Object dtoObj
    ) {
        ProductUpdateDTO dto = mapper.convertValue(dtoObj, ProductUpdateDTO.class);
        return ResponseEntity.ok().body(
                new ProductResponseDTO(service.update(service.getInstanceById(id), dto))
        );
    }

    @Override
    @PutMapping("/new")
    public ResponseEntity<Object> create(@Valid @RequestBody Object dtoObj) {
        ProductCreationDTO dto = mapper.convertValue(dtoObj, ProductCreationDTO.class);
        Product model = service.createAndSave(dto);

        URI uri = service.getUriForPersistedObject(model);

        return ResponseEntity.created(uri).body(new ProductResponseDTO(model));
    }

    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> removeById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().body("Produto Removido com sucesso");
    }

    @DeleteMapping("/delete/many_id")
    public ResponseEntity<Object> removeAll(@RequestBody List<UUID> idList) {
        if (idList == null || idList.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }
        List<UUID> deleted = new ArrayList<>();

        for (UUID id : idList) {
            if (service.delete(id)) {
                deleted.add(id);
            }
        }

        if (deleted.isEmpty()) {
            return ResponseEntity.notFound().build(); //404
        } else {
            return ResponseEntity.ok(deleted.toString()); //201
        }
    }
}
