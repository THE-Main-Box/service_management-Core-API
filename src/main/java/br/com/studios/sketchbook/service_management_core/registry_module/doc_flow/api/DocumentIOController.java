package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.api;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.dto.res.TableSumResponse;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.infra.DocumentIOHandlerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/document")
public class DocumentIOController {

    private final DocumentIOHandlerService service;

    @Autowired
    public DocumentIOController(DocumentIOHandlerService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<TableSumResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    service.loadAllTableSumPaged(page, size)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/delete/many")
    public ResponseEntity<?> deleteMany(@RequestBody List<Integer> idList) {
        if (service.deleteMany(idList)) {
            return ResponseEntity.ok().body("Entradas apagadas com sucesso");
        } else {
            return ResponseEntity.badRequest().body("Não foi possivel deletar as entrys");
        }
    }

    //TODO: Adicionar a capacidade de gerar e editar documentos personalizados

}
