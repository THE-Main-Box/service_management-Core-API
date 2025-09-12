package br.com.studios.sketchbook.service_management_core.product.product_related.api;

import br.com.studios.sketchbook.service_management_core.product.product_related.api.util.ProductRestControllerContract;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market.SMProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market.SMProductResponseDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market.SMProductUpdateDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.SuperMarketProduct;
import br.com.studios.sketchbook.service_management_core.product.product_related.infra.services.SMProductService;
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
@RequestMapping("/products/super-market")
public class SMProductController implements ProductRestControllerContract<
        SuperMarketProduct,
        SMProductCreationDTO,
        SMProductUpdateDTO,
        SMProductResponseDTO
        > {

    private final SMProductService service;

    @Autowired
    public SMProductController(SMProductService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<SMProductResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.getAllInstances(page, size).map(SMProductResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }


    @GetMapping("/name/{name}")
    public ResponseEntity<Page<SMProductResponseDTO>> getByName(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.getInstancesByName(name, page, size).map(SMProductResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<SMProductResponseDTO> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new SMProductResponseDTO(service.getInstanceById(id))//Transforma em dto a instancia retornada
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<SMProductResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody SMProductUpdateDTO dto
    ) {

        return ResponseEntity.ok().body(
                new SMProductResponseDTO(
                        service.update(
                                service.getInstanceById(id),
                                dto
                        )
                )
        );
    }


    @PutMapping("/new")
    public ResponseEntity<SMProductResponseDTO> create(@Valid @RequestBody SMProductCreationDTO dtoObj) {
        SuperMarketProduct model = service.createAndSave(dtoObj);

        URI uri = service.getUriForPersistedObject(model);

        return ResponseEntity.created(uri).body(new SMProductResponseDTO(model));
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
