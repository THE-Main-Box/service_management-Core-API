package br.com.studios.sketchbook.service_management_core.product.product_related.api;

import br.com.studios.sketchbook.service_management_core.product.product_related.api.util.ProductRestControllerContract;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.def_product.ProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.def_product.ProductResponseDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.def_product.ProductUpdateDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.Product;
import br.com.studios.sketchbook.service_management_core.product.product_related.infra.services.ProductService;
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
public class ProductController implements ProductRestControllerContract<
        Product,
        ProductCreationDTO,
        ProductUpdateDTO,
        ProductResponseDTO
        > {

    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ProductResponseDTO>> getAll(
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
    public ResponseEntity<Page<ProductResponseDTO>> getByName(
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
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new ProductResponseDTO(service.getInstanceById(id))//Transforma em dto a instancia retornada
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody ProductUpdateDTO dto
    ) {
        return ResponseEntity.ok().body(
                new ProductResponseDTO(
                        service.update(
                                service.getInstanceById(id),
                                dto
                        )
                )
        );
    }

    @PutMapping("/new")
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductCreationDTO dtoObj) {
        Product model = service.createAndSave(dtoObj);

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
