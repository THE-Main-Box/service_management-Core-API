package br.com.studios.sketchbook.service_management_core.product.price_related.infra.services;

import br.com.studios.sketchbook.service_management_core.product.price_related.domain.dto.PriceEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.price_related.domain.dto.PriceEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.product.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.product.price_related.infra.repositories.PriceEntryRepository;
import br.com.studios.sketchbook.service_management_core.product.price_related.shared.manager.core.PriceEntryDataManagementCore;
import br.com.studios.sketchbook.service_management_core.product.product_related.api.util.ApiUtils;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.def_product.ProductUpdateDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.Product;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Service
public class PriceEntryService {

    private final PriceEntryRepository repository;
    private final PriceEntryDataManagementCore manager;

    @Autowired
    public PriceEntryService(PriceEntryRepository repository, PriceEntryDataManagementCore manager) {
        this.repository = repository;
        this.manager = manager;
    }

    public PriceEntry getInstanceById(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Produto com id [" + id + "] não encontrado")
        );
    }

    @Transactional
    public PriceEntry update(PriceEntry model, PriceEntryUpdateDTO dto) {

        if(dto.currency() != null){
            model.getPrice().setCurrency(dto.currency());
        }

        if(dto.value() != null) {
            model.getPrice().setValue(BigDecimal.valueOf(dto.value()));
        }

        return repository.save(model);
    }

    public PriceEntry getById(UUID id) {
        return repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("PriceEntry não encontrada: [" + id + "] favor verificar"));
    }

    @Transactional
    /// Cria a entrada de preço com base no dto e salva ele
    public PriceEntry createAndSave(PriceEntryCreationDTO dto) {
        PriceEntry entry = new PriceEntry();//Inicia o dado mais importante do dto, que irá determinar como ele irá lidar com o preço
        manager.initEntry(entry, dto.price(), dto.currency());
        repository.save(entry);
        return entry;
    }

    @Transactional
    public boolean delete(UUID id){
        Optional<PriceEntry> model = repository.findById(id);
        if (model.isEmpty()) return false;
        repository.delete(model.get());
        return true;
    }

    public URI getUriForPersistedObject(PriceEntry model) {
        return ApiUtils.getUriForPersistedObject(model.getId().toString(), "/price/entry/id/{id}");
    }
}
