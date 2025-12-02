package br.com.studios.sketchbook.service_management_core.registry_module.address.api;

import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.req.AddressEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.req.AddressEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.res.AddressEntryDetailedResponseDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.res.AddressEntrySumResponseDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.model.AddressEntry;
import br.com.studios.sketchbook.service_management_core.registry_module.address.infra.services.AddressEntryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Controller
@RequestMapping("/entry/address")
public class AddressEntryController{

    private final AddressEntryService service;

    @Autowired
    public AddressEntryController(AddressEntryService service) {
        this.service = service;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<AddressEntryDetailedResponseDTO> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new AddressEntryDetailedResponseDTO(service.getInstanceById(id))
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
                    service.getAllInstances(page, size).map(AddressEntrySumResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/new")
    public ResponseEntity<AddressEntryDetailedResponseDTO> create(@Valid @RequestBody AddressEntryCreationDTO dtoObj) {
        try {
            AddressEntry entry = service.createAndSave(dtoObj);
            URI uri = service.getUriForPersistedObject(entry);

            return ResponseEntity.created(uri).body(new AddressEntryDetailedResponseDTO(entry));
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
    public ResponseEntity<AddressEntryDetailedResponseDTO> updateById(
            @PathVariable UUID id,
            @RequestBody AddressEntryUpdateDTO dto
    ) {
        return ResponseEntity.ok().body(
                new AddressEntryDetailedResponseDTO(
                        service.update(
                                service.getInstanceById(id),
                                dto
                        )
                )
        );
    }

}
