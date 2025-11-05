package br.com.studios.sketchbook.service_management_core.product.infra.services;

import br.com.studios.sketchbook.service_management_core.aplication.api_utils.util.ApiUtils;
import br.com.studios.sketchbook.service_management_core.aplication.api_utils.contracts.ProductRestServiceContract;
import br.com.studios.sketchbook.service_management_core.product.domain.dto.product.ProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.domain.dto.product.ProductUpdateDTO;
import br.com.studios.sketchbook.service_management_core.product.domain.model.Product;
import br.com.studios.sketchbook.service_management_core.product.infra.repositories.ProductRepository;
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
public class ProductService implements ProductRestServiceContract<Product> {

    private final ProductRepository repository;

    @Autowired
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product getInstanceById(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Produto com id [" + id + "] não encontrado")
        );
    }

    @Override
    public URI getUriForPersistedObject(Product model) {
        return ApiUtils.getUriForPersistedObject(model.getId().toString(), "/products/product/id/{id}");
    }

    @Transactional
    @Override
    public Product update(Product model, Record dtoObject) {
        ProductUpdateDTO dto = (ProductUpdateDTO) dtoObject;

        if (dto.name() != null) model.setName(dto.name());

        return repository.save(model);
    }

    /// Configura o pageable e retorna todas as instancias em uma pagina
    public Page<Product> getAllInstances(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return repository.findAll(pageable);
    }

    /// Obtém uma Page de instâncias de modelos do banco de dados conforme o nome
    public Page<Product> getInstancesByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return repository.ListByName(name, pageable);
    }

    @Transactional
    public Product createAndSave(Record creationDTO) {
        return repository.save(new Product((ProductCreationDTO) creationDTO));
    }

    @Transactional
    public boolean delete(UUID id) {
        Optional<Product> model = repository.findById(id);
        if (model.isEmpty()) return false;
        repository.delete(model.get());
        return true;
    }
}
