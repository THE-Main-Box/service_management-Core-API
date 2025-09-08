package br.com.studios.sketchbook.service_management_core.product.product_related.infra.services;

import br.com.studios.sketchbook.service_management_core.product.product_related.infra.repositories.SMProductRepository;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market.SMProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market.SMProductResponseDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market.SMProductUpdateDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.SMProductModel;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Service
public class SMProductService {

    private final SMProductRepository repository;

    @Autowired
    public SMProductService(SMProductRepository repository) {
        this.repository = repository;
    }

    /// Remove o objeto do id salvo no banco
    @Transactional
    public boolean delete(UUID id) {
        Optional<SMProductModel> model = repository.findById(id);
        if (model.isEmpty()) return false;
        repository.delete(model.get());
        return true;
    }

    /// Atualiza os campos do modelo passado como parâmetro
    @Transactional
    public SMProductModel update(SMProductModel model, SMProductUpdateDTO dto) {
        if (dto.name() != null) model.setName(dto.name());
        if (dto.barCode() != null) model.setBarcode(dto.barCode());

        return repository.save(model);
    }

    /// Retorna uma página com todas as instancias registradas
    public Page<SMProductModel> getAllInstances(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return repository.findAll(pageable);
    }

    /// Cria e salva uma instancia com base em um dto no banco
    @Transactional
    public SMProductModel createAndSave(SMProductCreationDTO dto){
        return repository.save(new SMProductModel(dto));
    }


    /// Obtém uma instância de um modelo com base no id passado
    public SMProductModel getInstanceById(UUID id){
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
    }
    /// Obtém um dto de uma instância com base no id de um modelo do banco
    public SMProductResponseDTO getResponseById(UUID id){
        return new SMProductResponseDTO(getInstanceById(id));
    }


    /// Obtém uma Page de instâncias de modelos do banco de dados conforme o nome
    public Page<SMProductModel> getInstancesByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return repository.ListByName(name, pageable);
    }
    /// Obtém uma Page de instâncias de modelo do banco de dados e converte para um dto de resposta e o retorna
    public Page<SMProductResponseDTO> getResponseByName(String name, int page, int size) {
        return getInstancesByName(name, page, size).map(SMProductResponseDTO::new);
    }


    /// Obtém uma uri para levar até o produto, com um "/id"
    public URI getUriForPersistedObject(SMProductModel model){
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/products/super-market/id/{id}")
                .buildAndExpand(model.getId())
                .toUri();
    }

}
