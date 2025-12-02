package br.com.studios.sketchbook.service_management_core.storage_module.storage.api;

import br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.dto.req.StorageEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.dto.res.StorageEntryDetailedResponseDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.dto.req.StorageEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.dto.res.StorageEntrySumResponseDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.model.StorageEntry;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.infra.services.StorageEntryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<StorageEntryDetailedResponseDTO> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new StorageEntryDetailedResponseDTO(service.getInstanceById(id))
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
                    service.getAllInstances(page, size).map(StorageEntrySumResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/owner/id/{id}")
    public ResponseEntity<StorageEntryDetailedResponseDTO> getByOwnerId(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new StorageEntryDetailedResponseDTO(service.getInstanceByOwnerId(id))
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/new")
    public ResponseEntity<StorageEntryDetailedResponseDTO> create(@Valid @RequestBody StorageEntryCreationDTO dtoObj) {
        try {
            StorageEntry entry = service.createAndSave(dtoObj);
            URI uri = service.getUriForPersistedObject(entry);

            return ResponseEntity.created(uri).body(new StorageEntryDetailedResponseDTO(entry));
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

    @PutMapping("/update/{id}")
    public ResponseEntity<StorageEntryDetailedResponseDTO> updateById(
            @PathVariable UUID id,
            @RequestBody StorageEntryUpdateDTO dto
    ) {
        return ResponseEntity.ok().body(
                new StorageEntryDetailedResponseDTO(
                        service.update(
                                service.getInstanceById(id),
                                dto
                        )
                )
        );
    }

}
