package br.com.studios.sketchbook.service_management_core.registry_module.shipment.api;

import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req.ShipmentEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.res.ShipmentEntryDetailedResponseDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.res.ShipmentEntrySumResponseDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model.ShipmentEntry;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.infra.services.ShipmentEntryService;
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
@RequestMapping("/entry/shipment")
public class ShipmentEntryController {

    private final ShipmentEntryService service;

    @Autowired
    public ShipmentEntryController(ShipmentEntryService service) {
        this.service = service;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ShipmentEntryDetailedResponseDTO> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new ShipmentEntryDetailedResponseDTO(service.getInstanceById(id))
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
                    service.getAllInstances(page, size).map(ShipmentEntrySumResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/origin-address/description/{description}")
    public ResponseEntity<Page<Object>> getByOriginAddressDescription(
            @PathVariable String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.getByOriginAddressDescription(description, page, size)
                            .map(ShipmentEntrySumResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/destination-address/description/{description}")
    public ResponseEntity<Page<Object>> getByDestinationAddressDescription(
            @PathVariable String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.getByDestinationAddressDescription(description, page, size)
                            .map(ShipmentEntrySumResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/item/name/{name}")
    public ResponseEntity<Page<Object>> getByItemName(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.getByItemName(name, page, size)
                            .map(ShipmentEntrySumResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/origin-address/id/{addressId}")
    public ResponseEntity<Page<Object>> getByOriginAddressId(
            @PathVariable UUID addressId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.getByOriginAddressId(addressId, page, size)
                            .map(ShipmentEntrySumResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/destination-address/id/{addressId}")
    public ResponseEntity<Page<Object>> getByDestinationAddressId(
            @PathVariable UUID addressId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.getByDestinationAddressId(addressId, page, size)
                            .map(ShipmentEntrySumResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/item/id/{itemId}")
    public ResponseEntity<Page<Object>> getByItemId(
            @PathVariable UUID itemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.getByItemId(itemId, page, size)
                            .map(ShipmentEntrySumResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/new")
    public ResponseEntity<ShipmentEntryDetailedResponseDTO> create(@Valid @RequestBody ShipmentEntryCreationDTO dtoObj) {
        try {
            ShipmentEntry entry = service.createAndSave(dtoObj);
            URI uri = service.getUriForPersistedObject(entry);

            return ResponseEntity.created(uri).body(new ShipmentEntryDetailedResponseDTO(entry));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<?> removeById(@PathVariable UUID id) {
        if (service.delete(id)) {
            return ResponseEntity.ok().body("Entrada apagada com sucesso");
        } else {
            return ResponseEntity.badRequest().body("Não foi possivel deletar a entry");
        }
    }
}