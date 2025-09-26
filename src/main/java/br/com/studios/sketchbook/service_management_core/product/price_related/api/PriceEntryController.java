package br.com.studios.sketchbook.service_management_core.product.price_related.api;

import br.com.studios.sketchbook.service_management_core.product.price_related.domain.dto.PriceEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.price_related.domain.dto.PriceEntryResponseDTO;
import br.com.studios.sketchbook.service_management_core.product.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.product.price_related.infra.services.PriceEntryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/price/entry")
public class PriceEntryController {

    private final PriceEntryService service;

    @Autowired
    public PriceEntryController(PriceEntryService service) {
        this.service = service;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PriceEntryResponseDTO> getById(@PathVariable(name = "id") UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new PriceEntryResponseDTO(service.getById(id))
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/new")
    public ResponseEntity<PriceEntryResponseDTO> create(@Valid @RequestBody PriceEntryCreationDTO dtoObj) {
        PriceEntry entry = service.createAndSave(dtoObj);
        URI uri = service.getUriForPersistedObject(entry);

        return ResponseEntity.created(uri).body(new PriceEntryResponseDTO(entry));
    }
}
