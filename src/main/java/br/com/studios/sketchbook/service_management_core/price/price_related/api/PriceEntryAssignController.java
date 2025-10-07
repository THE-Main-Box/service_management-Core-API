package br.com.studios.sketchbook.service_management_core.price.price_related.api;

import br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_assignment.PriceEntryAssignmentCreationDTO;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_assignment.PriceEntryAssignmentDetailedDTO;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntryAssignment;
import br.com.studios.sketchbook.service_management_core.price.price_related.infra.services.PriceEntryAssignmentService;
import br.com.studios.sketchbook.service_management_core.price.price_related.infra.services.PriceEntryService;
import br.com.studios.sketchbook.service_management_core.price.price_related.shared.interfaces.PriceOwner;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/entry/price/assignment")
public class PriceEntryAssignController {

    private final PriceEntryAssignmentService assignmentService;
    private final PriceEntryService entryService;

    @Autowired
    public PriceEntryAssignController(PriceEntryAssignmentService assignmentService, PriceEntryService entryService) {
        this.assignmentService = assignmentService;
        this.entryService = entryService;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PriceEntryAssignmentDetailedDTO> getAssignmentById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(
                new PriceEntryAssignmentDetailedDTO(assignmentService.getById(id))
        );
    }

    @GetMapping("/owner/id/{id}")
    public ResponseEntity<PriceEntryAssignmentDetailedDTO> getAssignmentByOwnerId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(
                new PriceEntryAssignmentDetailedDTO(assignmentService.getByOwnerId(id))
        );
    }

    @GetMapping("/entry/id/{id}")
    public ResponseEntity<PriceEntryAssignmentDetailedDTO> getAssignmentByEntryId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(
                new PriceEntryAssignmentDetailedDTO(assignmentService.getByEntryId(id))
        );
    }

    @PatchMapping("/clean_orphan")
    public ResponseEntity<?> cleanInvalidAssignments(){
        assignmentService.cleanInvalidAssignments();
        return ResponseEntity.ok().body("ligações entre as entidades de preço foram limpas");
    }

    @PutMapping("/new")
    public ResponseEntity<PriceEntryAssignmentDetailedDTO> createAssignment(@RequestBody PriceEntryAssignmentCreationDTO dto) {
        try {
            PriceEntry entry = entryService.getById(dto.entryId());
            PriceOwner owner = assignmentService.discoverOwner(dto.ownerId());

            PriceEntryAssignment assignment = assignmentService.createAssignment(entry, owner);

            URI uri = assignmentService.getUriForPersistedObject(assignment);

            return ResponseEntity.created(uri).body(
                    new PriceEntryAssignmentDetailedDTO(assignment)
            );
        } catch (EntityNotFoundException e) {

            return ResponseEntity.status(404).build();
        }
    }

}
