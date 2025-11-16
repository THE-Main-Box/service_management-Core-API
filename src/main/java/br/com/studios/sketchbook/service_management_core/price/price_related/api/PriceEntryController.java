package br.com.studios.sketchbook.service_management_core.price.price_related.api;

import br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.PriceEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.PriceEntryResponseDTO;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.PriceEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.price.price_related.infra.services.PriceEntryService;
import br.com.studios.sketchbook.service_management_core.storage.domain.dto.StorageEntryResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/entry/price")
public class PriceEntryController {

    private final PriceEntryService service;

    @Autowired
    public PriceEntryController(PriceEntryService service) {
        this.service = service;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PriceEntryResponseDTO> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new PriceEntryResponseDTO(service.getInstanceById(id))
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Object>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.getAllInstances(page, size).map(PriceEntryResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/owner/id/{id}")
    public ResponseEntity<PriceEntryResponseDTO> getByOwnerId(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new PriceEntryResponseDTO(service.getInstanceByOwnerId(id))
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/new")
    public ResponseEntity<PriceEntryResponseDTO> create(@Valid @RequestBody PriceEntryCreationDTO dtoObj) {
        try {
            PriceEntry entry = service.createAndSave(dtoObj);
            URI uri = service.getUriForPersistedObject(entry);

            return ResponseEntity.created(uri).body(new PriceEntryResponseDTO(entry));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<?> removeById(@PathVariable UUID id) {
        if(service.delete(id)) {
            return ResponseEntity.ok().body("Entrada de preço apagada com sucesso");
        } else {
            return ResponseEntity.badRequest().body("Não foi possivel deletar a entry");
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<PriceEntryResponseDTO> updateById(
            @PathVariable UUID id,
            @RequestBody PriceEntryUpdateDTO dto
    ) {
        return ResponseEntity.ok().body(
                new PriceEntryResponseDTO(
                        service.update(
                                service.getInstanceById(id),
                                dto
                        )
                )
        );
    }
}
