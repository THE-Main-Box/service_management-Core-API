package br.com.studios.sketchbook.service_management_core.product.product_related.api;

import br.com.studios.sketchbook.service_management_core.product.product_related.api.util.ProductRestController;
import br.com.studios.sketchbook.service_management_core.product.product_related.infra.services.SMProductService;
import br.com.studios.sketchbook.service_management_core.product.product_related.shared.dto.ProductsDeleteManyDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market.SMProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market.SMProductResponseDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market.SMProductUpdateDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.SuperMarketProduct;
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
public class SMProductController implements ProductRestController {

    private final SMProductService service;

    @Autowired
    public SMProductController(SMProductService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Object>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(
                service.getAllInstances(page, size).map(SMProductResponseDTO::new)
        );
    }

    /// Obtém uma página contendo um dto de todas as instâncias pedidas dentro do banco
    @GetMapping("/name/{name}")
    public ResponseEntity<Page<Object>> getByName(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(
                service.getProductByName(name, page, size).map(SMProductResponseDTO::new)
        );
    }

    /// Obtém um dto com base num ID
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getResponseById(id));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Object> update(@PathVariable UUID id, @RequestBody Object dtoObj) {
        SMProductUpdateDTO dto = (SMProductUpdateDTO) dtoObj; //Convertemos para o dto correto

        return ResponseEntity.ok().body(
                new SMProductResponseDTO(
                        service.update(
                                service.getInstanceById(id),
                                dto
                        )
                )
        );
    }

    /// Cria uma instância com base num dto e salva, depois retorna que foi bem sucedido
    @PutMapping("/new")
    public ResponseEntity<Object> create(@Valid @RequestBody Object dtoObj) {
        SMProductCreationDTO dto = (SMProductCreationDTO) dtoObj;

        SuperMarketProduct model = service.createAndSave(dto);

        URI uri = service.getUriForPersistedObject(model);

        return ResponseEntity.created(uri).body(new SMProductResponseDTO(model));
    }

    /// Remove uma instância do banco de dados
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> removeById(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().body("Produto Removido com sucesso");
    }

    /// Limpa todas as instâncias com os ids passados
    @DeleteMapping("/delete/many_id")
    public ResponseEntity<Object> removeAll(@RequestBody ProductsDeleteManyDTO dto) {
        if (dto.IDs() == null || dto.IDs().isEmpty()) {
            return ResponseEntity.noContent().build(); // 204
        }

        List<UUID> deleted = new ArrayList<>();

        for(UUID id : dto.IDs()){
            if(service.delete(id)){
                deleted.add(id);
            }
        }

        if(deleted.isEmpty()){
            return ResponseEntity.notFound().build(); //404
        } else {
            return ResponseEntity.ok(deleted.toString()); //201
        }
    }


}
