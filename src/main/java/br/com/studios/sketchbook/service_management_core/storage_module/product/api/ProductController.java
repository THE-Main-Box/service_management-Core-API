package br.com.studios.sketchbook.service_management_core.storage_module.product.api;

import br.com.studios.sketchbook.service_management_core.application.api_utils.contracts.ProductRestControllerContract;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.product.req.ProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.product.res.ProductDetailedResponseDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.product.req.ProductUpdateDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.product.res.ProductSumResponseDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model.Product;
import br.com.studios.sketchbook.service_management_core.storage_module.product.infra.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
                    service.getAllInstances(page, size).map(ProductSumResponseDTO::new)
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
                    service.getInstancesByName(name, page, size).map(ProductDetailedResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new ProductDetailedResponseDTO(service.getInstanceById(id))//Transforma em dto a instancia retornada
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
                new ProductDetailedResponseDTO(service.update(service.getInstanceById(id), dto))
        );
    }

    @Override
    @PostMapping("/new")
    public ResponseEntity<Object> create(@Valid @RequestBody Object dtoObj) {
        ProductCreationDTO dto = mapper.convertValue(dtoObj, ProductCreationDTO.class);
        Product model = service.createAndSave(dto);

        URI uri = service.getUriForPersistedObject(model);

        return ResponseEntity.created(uri).body(new ProductDetailedResponseDTO(model));
    }

    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> removeById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().body("Produto Removido com sucesso");
    }
}
