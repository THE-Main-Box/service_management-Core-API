package br.com.studios.sketchbook.service_management_core.price.price_related.infra.services;

import br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_entry.PriceEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.dto.price_entry.PriceEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.price.price_related.infra.repositories.PriceEntryRepository;
import br.com.studios.sketchbook.service_management_core.price.price_related.shared.interfaces.PriceOwner;
import br.com.studios.sketchbook.service_management_core.price.price_related.shared.manager.core.PriceEntryDataManagementCore;
import br.com.studios.sketchbook.service_management_core.product.product_related.api.util.ApiUtils;
import br.com.studios.sketchbook.service_management_core.product.product_related.infra.repositories.ProductRepository;
import br.com.studios.sketchbook.service_management_core.product.product_related.infra.repositories.SMProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

@Service
public class PriceEntryService {

    private final PriceEntryRepository repository;
    private final PriceEntryDataManagementCore manager;

    /// Lista de todos os repositórios dos donos de preço
    private final List<JpaRepository<? extends PriceOwner, UUID>> priceOwnerRepositoryList;

    @Autowired
    public PriceEntryService(
            PriceEntryRepository repository,
            PriceEntryDataManagementCore manager,
            SMProductRepository smProductRepository,
            ProductRepository productRepository
    ) {
        this.repository = repository;
        this.manager = manager;

        priceOwnerRepositoryList = new ArrayList<>();

        priceOwnerRepositoryList.addAll(
                Arrays.asList(
                        smProductRepository,
                        productRepository
                )
        );
    }

    public PriceEntry getInstanceById(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Produto com id [" + id + "] não encontrado")
        );
    }

    public PriceEntry getInstanceByOwnerId(UUID id) {
        return repository.findByOwnerId(id).orElseThrow(
                () -> new EntityNotFoundException("Produto com id [" + id + "] não encontrado")
        );
    }

    @Transactional
    public PriceEntry update(PriceEntry model, PriceEntryUpdateDTO dto) {

        if (dto.currency() != null) {
            model.getPrice().setCurrency(dto.currency());
        }

        if (dto.value() != null) {
            model.getPrice().setValue(BigDecimal.valueOf(dto.value()));
        }

        return repository.save(model);
    }

    @Transactional
    /// Cria a entrada de preço com base no dto e salva ele
    public PriceEntry createAndSave(PriceEntryCreationDTO dto) {
        PriceEntry entry = new PriceEntry(//iniciamos a entry
                discoverOwner(//obtemos o dono
                        dto.ownerId()//passamos o id do dono
                )
        );
        manager.initEntry(entry, dto.price(), dto.currency());
        repository.save(entry);
        return entry;
    }

    @Transactional
    public boolean delete(UUID id) {
        cleanInvalidAssignment(id);
        Optional<PriceEntry> model = repository.findById(id);

        return model.isEmpty();
    }

    @Transactional
    public void cleanInvalidAssignment(UUID assignmentId) {
        Optional<PriceEntry> entryOpt = repository.findById(assignmentId);
        if (entryOpt.isEmpty()) return;

        PriceEntry entry = entryOpt.get();

        // Deleta imediatamente se os IDs forem nulos
        if (entry.getOwnerId() == null) {
            repository.deleteById(assignmentId);
            return;
        }

        // Verifica se existe algum owner
        boolean ownerExists = priceOwnerRepositoryList.stream()
                .anyMatch(repo -> repo.findById(entry.getOwnerId()).isPresent());

        if (!ownerExists) {
            repository.deleteById(assignmentId);
        }
    }

    public PriceOwner discoverOwner(UUID ownerId) {
        return priceOwnerRepositoryList.stream()
                .map(
                        repo -> repo.findById(
                                ownerId
                        ).orElse(
                                null
                        )
                )
                .filter(Objects::nonNull)
                .map(PriceOwner.class::cast)
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Dono não foi descoberto; possivelmente não está mapeado no banco"));
    }


    public URI getUriForPersistedObject(PriceEntry model) {
        return ApiUtils.getUriForPersistedObject(model.getId().toString(), "/entry/price/id/{id}");
    }
}
