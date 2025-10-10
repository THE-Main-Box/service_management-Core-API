package br.com.studios.sketchbook.service_management_core.product.product_related.infra.services;

import br.com.studios.sketchbook.service_management_core.product.product_related.api.util.ApiUtils;
import br.com.studios.sketchbook.service_management_core.product.product_related.api.util.ProductRestServiceContract;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market.SMProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market.SMProductUpdateDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.SuperMarketProduct;
import br.com.studios.sketchbook.service_management_core.product.product_related.infra.repositories.SMProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Service
public class SMProductService implements ProductRestServiceContract<SuperMarketProduct> {

    private final SMProductRepository repository;

    @Autowired
    public SMProductService(SMProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public boolean delete(UUID id) {
        Optional<SuperMarketProduct> model = repository.findById(id);
        if (model.isEmpty()) return false;
        repository.delete(model.get());
        return true;
    }

    @Transactional
    public SuperMarketProduct update(SuperMarketProduct model, Record dtoObject) {
        SMProductUpdateDTO dto = (SMProductUpdateDTO) dtoObject;

        if (dto.name() != null) model.setName(dto.name());
        if (dto.barCode() != null) model.setBarcode(dto.barCode());

        return repository.save(model);
    }

    /// Retorna uma página com todas as instancias registradas
    public Page<SuperMarketProduct> getAllInstances(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return repository.findAll(pageable);
    }

    /// Cria e salva uma instancia com base em um dto no banco
    @Transactional
    public SuperMarketProduct createAndSave(Record dto) {
        return repository.save(new SuperMarketProduct((SMProductCreationDTO) dto));
    }


    /// Obtém uma instância de um modelo com base no id passado
    public SuperMarketProduct getInstanceById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    }

    /// Obtém uma Page de instâncias de modelos do banco de dados conforme o nome
    public Page<SuperMarketProduct> getInstancesByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return repository.ListByName(name, pageable);
    }


    /// Obtém uma uri para levar até o produto, com um "/id"
    public URI getUriForPersistedObject(SuperMarketProduct model) {
        return ApiUtils.getUriForPersistedObject(model.getId().toString(), "/products/super-market/id/{id}");
    }


}
