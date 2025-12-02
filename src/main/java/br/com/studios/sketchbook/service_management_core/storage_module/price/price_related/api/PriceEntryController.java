package br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.api;

import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.dto.req.PriceEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.dto.res.PriceEntryDetailedResponseDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.dto.req.PriceEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.dto.res.PriceEntrySumResponseDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.infra.services.PriceEntryService;
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
    public ResponseEntity<PriceEntryDetailedResponseDTO> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new PriceEntryDetailedResponseDTO(service.getInstanceById(id))
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
                    service.getAllInstances(page, size).map(PriceEntrySumResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/owner/id/{id}")
    public ResponseEntity<PriceEntryDetailedResponseDTO> getByOwnerId(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new PriceEntryDetailedResponseDTO(service.getInstanceByOwnerId(id))
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/new")
    public ResponseEntity<PriceEntryDetailedResponseDTO> create(@Valid @RequestBody PriceEntryCreationDTO dtoObj) {
        try {
            PriceEntry entry = service.createAndSave(dtoObj);
            URI uri = service.getUriForPersistedObject(entry);

            return ResponseEntity.created(uri).body(new PriceEntryDetailedResponseDTO(entry));
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

    @PutMapping("/update/{id}")
    public ResponseEntity<PriceEntryDetailedResponseDTO> updateById(
            @PathVariable UUID id,
            @RequestBody PriceEntryUpdateDTO dto
    ) {
        return ResponseEntity.ok().body(
                new PriceEntryDetailedResponseDTO(
                        service.update(
                                service.getInstanceById(id),
                                dto
                        )
                )
        );
    }
}
