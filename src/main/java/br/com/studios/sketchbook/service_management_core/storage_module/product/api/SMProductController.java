package br.com.studios.sketchbook.service_management_core.storage_module.product.api;

import br.com.studios.sketchbook.service_management_core.application.api_utils.contracts.ProductRestControllerContract;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.super_market.req.SMProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.super_market.res.SMProductDetailedResponseDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.super_market.req.SMProductUpdateDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.super_market.res.SMProductSumResponseDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model.SuperMarketProduct;
import br.com.studios.sketchbook.service_management_core.storage_module.product.infra.services.ProductDocProjectionService;
import br.com.studios.sketchbook.service_management_core.storage_module.product.infra.services.SMProductDocProjectionService;
import br.com.studios.sketchbook.service_management_core.storage_module.product.infra.services.SMProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products/super-market")
public class SMProductController implements ProductRestControllerContract {

    private final SMProductService service;
    private final ObjectMapper mapper;
    private final SMProductDocProjectionService projectionService;

    @Autowired
    public SMProductController(
            SMProductService service,
            ObjectMapper mapper,
            SMProductDocProjectionService projectionService
    ) {
        this.service = service;
        this.mapper = mapper;
        this.projectionService = projectionService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Object>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.getAllInstances(page, size).map(SMProductSumResponseDTO::new)
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
                    service.getInstancesByName(name, page, size).map(SMProductSumResponseDTO::new)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(
                    new SMProductDetailedResponseDTO(service.getInstanceById(id))//Transforma em dto a instancia retornada
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

        SMProductUpdateDTO dto = mapper.convertValue(dtoObj, SMProductUpdateDTO.class);

        return ResponseEntity.ok().body(
                new SMProductDetailedResponseDTO(service.update(service.getInstanceById(id), dto))
        );
    }

    @PostMapping("/new")
    public ResponseEntity<Object> create(@Valid @RequestBody Object dtoObj) {
        SMProductCreationDTO dto = mapper.convertValue(dtoObj, SMProductCreationDTO.class);
        SuperMarketProduct model = service.createAndSave(dto);

        URI uri = service.getUriForPersistedObject(model);

        return ResponseEntity.created(uri).body(new SMProductDetailedResponseDTO(model));
    }

    @PostMapping("/document/stock/new")
    public ResponseEntity<Integer> productStockDocumentGeneration(
            @RequestParam String documentName,
            @RequestBody List<UUID> productIds
    ) {
        try {
            Integer tableId = projectionService.createDocumentForStorageByIdList(
                    documentName,
                    productIds
            );

            // lógica
            return ResponseEntity.ok().body(tableId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> removeById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().body("Produto Removido com sucesso");
    }


}
