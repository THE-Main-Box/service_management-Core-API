package br.com.studios.sketchbook.service_management_core.product.price_related.api;

import br.com.studios.sketchbook.service_management_core.product.price_related.infra.services.PriceEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/price/entry")
public class PriceEntryController {

    private final PriceEntryService service;

    @Autowired
    public PriceEntryController(PriceEntryService service) {
        this.service = service;
    }

//    @PutMapping("/new")
//    public ResponseEntity<?> create(){
//
//    }
}
