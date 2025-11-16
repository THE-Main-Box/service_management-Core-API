package br.com.studios.sketchbook.service_management_core.storage.infra.services;

import br.com.studios.sketchbook.service_management_core.aplication.api_utils.util.ApiUtils;
import br.com.studios.sketchbook.service_management_core.product.domain.model.Product;
import br.com.studios.sketchbook.service_management_core.product.infra.repositories.ProductRepository;
import br.com.studios.sketchbook.service_management_core.product.infra.repositories.SMProductRepository;
import br.com.studios.sketchbook.service_management_core.storage.domain.dto.StorageEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage.domain.dto.StorageEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.storage.domain.model.StorageEntry;
import br.com.studios.sketchbook.service_management_core.storage.infra.repositories.StorageEntryRepository;
import br.com.studios.sketchbook.service_management_core.storage.shared.interfaces.StorageAble;
import br.com.studios.sketchbook.service_management_core.storage.shared.util.manager.core.StorageEntryDataManagementCore;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.*;

import static br.com.studios.sketchbook.service_management_core.aplication.api_utils.references.ConfigRefNames.StorageConfig.storage_transaction_manager_ref;

@Service
@Transactional(storage_transaction_manager_ref)
public class StorageEntryService {

    private final StorageEntryRepository repository;
    private final StorageEntryDataManagementCore manager;

    /// Lista de repositórios que podem conter dados de armazenamento
    private final List<JpaRepository<? extends StorageAble, UUID>> storageAbleRepositoryList;

    @Autowired
    public StorageEntryService(
            StorageEntryRepository repository,
            SMProductRepository smProductRepository,
            ProductRepository productRepository
    ) {
        this.repository = repository;

        this.storageAbleRepositoryList = new ArrayList<>();
        this.manager = new StorageEntryDataManagementCore();

        storageAbleRepositoryList.addAll(
                Arrays.asList(
                        smProductRepository,
                        productRepository
                )
        );
    }

    public StorageEntry createAndSave(StorageEntryCreationDTO dto) {
        StorageEntry entry = new StorageEntry(
                discoverOwner(dto.ownerId()),
                dto.volumeType()
        );

        manager.initEntry(
                entry,
                dto.quantity(),
                dto.quantityPerUnit(),
                dto.isValuesRaw()
        );

        return repository.save(entry);
    }

    public Page<StorageEntry> getAllInstances(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    public boolean delete(UUID id) {
        cleanInvalidAssignment(id);
        Optional<StorageEntry> model = repository.findById(id);

        return model.isEmpty();
    }

    public StorageEntry update(StorageEntry toUpdate, StorageEntryUpdateDTO dto) {

        manager.editEntry(
                toUpdate,
                dto
        );

        return toUpdate;
    }

    public StorageEntry getInstanceById(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Entrada de armazenamento com id [" + id + "] não encontrado")
        );
    }

    public StorageEntry getInstanceByOwnerId(UUID id) {
        return repository.findByOwnerId(id).orElseThrow(
                () -> new EntityNotFoundException("Entrada de armazenamento com id [" + id + "] não encontrado")
        );
    }

    public URI getUriForPersistedObject(StorageEntry model) {
        return ApiUtils.getUriForPersistedObject(model.getId().toString(), "/entry/storage/id/{id}");
    }

    private void cleanInvalidAssignment(UUID assignmentId) {
        Optional<StorageEntry> entryOpt = repository.findById(assignmentId);
        if (entryOpt.isEmpty()) return;

        StorageEntry entry = entryOpt.get();

        // Deleta imediatamente se os IDs forem nulos
        if (entry.getOwnerId() == null) {
            repository.deleteById(assignmentId);
            return;
        }

        // Verifica se existe algum owner
        boolean ownerExists = storageAbleRepositoryList.stream()
                .anyMatch(repo -> repo.findById(entry.getOwnerId()).isPresent());

        if (!ownerExists) {
            repository.deleteById(assignmentId);
        }
    }

    public StorageAble discoverOwner(UUID ownerId) {
        return storageAbleRepositoryList.stream()
                .map(
                        repo -> repo.findById(
                                ownerId
                        ).orElse(
                                null
                        )
                )
                .filter(Objects::nonNull)
                .map(StorageAble.class::cast)
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Dono não foi descoberto; possivelmente não está mapeado no banco"));
    }
}
