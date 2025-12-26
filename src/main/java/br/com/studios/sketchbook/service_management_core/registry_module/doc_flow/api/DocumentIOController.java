package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.api;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.dto.res.DocumentDetailedResponse;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.dto.res.TableSumResponse;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.infra.DocumentIOHandlerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @GetMapping("/id/{id}")
    public ResponseEntity<DocumentDetailedResponse> loadDocument(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok().body(
                    service.loadTableData(id)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/export/pdf/id/{id}")
    public ResponseEntity<?> exportDocumentToPdf(@PathVariable Integer id) {
        try {
            service.exportToPdf(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
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

    /*
     * TODO: Adicionar a capacidade de gerar e editar documentos personalizados.
     *  Os documentos que iremos gerar só serão úteis de fato
     *  quando implementarmos a capacidade de gerar e lidar com documentos personalizados,
     *  porém como agora estamos lidando com a documentação de itens já existentes,
     *  não faz sentido no momento atual,
     *  ter que lidar com esse tipo de enpoint
     */

}
