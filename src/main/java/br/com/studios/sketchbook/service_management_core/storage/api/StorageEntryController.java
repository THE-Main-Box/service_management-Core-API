package br.com.studios.sketchbook.service_management_core.storage.api;

import br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_entry.PriceEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_entry.PriceEntryResponseDTO;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_entry.PriceEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.storage.domain.dto.StorageEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage.domain.dto.StorageEntryResponseDTO;
import br.com.studios.sketchbook.service_management_core.storage.domain.dto.StorageEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.storage.domain.model.StorageEntry;
import br.com.studios.sketchbook.service_management_core.storage.infra.services.StorageEntryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/entry/storage")
public class StorageEntryController {

    private final StorageEntryService service;

    @Autowired
    public StorageEntryController(StorageEntryService service) {
        this.service = service;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<StorageEntryResponseDTO> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new StorageEntryResponseDTO(service.getInstanceById(id))
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/owner/id/{id}")
    public ResponseEntity<StorageEntryResponseDTO> getByOwnerId(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new StorageEntryResponseDTO(service.getInstanceByOwnerId(id))
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/new")
    public ResponseEntity<StorageEntryResponseDTO> create(@Valid @RequestBody StorageEntryCreationDTO dtoObj) {
        try {
            StorageEntry entry = service.createAndSave(dtoObj);
            URI uri = service.getUriForPersistedObject(entry);

            return ResponseEntity.created(uri).body(new StorageEntryResponseDTO(entry));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<?> removeById(@PathVariable UUID id) {
        if (service.delete(id)) {
            return ResponseEntity.ok().body("Entrada de armazenamento apagada com sucesso");
        } else {
            return ResponseEntity.badRequest().body("Não foi possivel deletar a entry");
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<StorageEntryResponseDTO> updateById(
            @PathVariable UUID id,
            @RequestBody StorageEntryUpdateDTO dto
    ) {
        return ResponseEntity.ok().body(
                new StorageEntryResponseDTO(
                        service.update(
                                service.getInstanceById(id),
                                dto
                        )
                )
        );
    }

}
