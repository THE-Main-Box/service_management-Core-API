package br.com.studios.sketchbook.service_management_core.price.price_related.infra.services;

import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntryAssignment;
import br.com.studios.sketchbook.service_management_core.price.price_related.infra.repositories.PriceEntryAssignmentRepository;
import br.com.studios.sketchbook.service_management_core.price.price_related.infra.repositories.PriceEntryRepository;
import br.com.studios.sketchbook.service_management_core.price.price_related.shared.interfaces.PriceOwner;
import br.com.studios.sketchbook.service_management_core.product.product_related.api.util.ApiUtils;
import br.com.studios.sketchbook.service_management_core.product.product_related.infra.repositories.ProductRepository;
import br.com.studios.sketchbook.service_management_core.product.product_related.infra.repositories.SMProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;

@Service
public class PriceEntryAssignmentService {

    /// Repositório do assignment
    private final PriceEntryAssignmentRepository assignmentRepository;

    /// Repositório da entry
    private final PriceEntryRepository entryRepository;

    /// Repositório de produto
    private final ProductRepository productRepository;
    /// Repositório de produto de super mercado
    private final SMProductRepository smProductRepository;
    /// Lista para facilitar a iteração futura
    private final List<JpaRepository<? extends PriceOwner, UUID>> priceOwnerRepositoryList;

    @Autowired
    public PriceEntryAssignmentService(
            PriceEntryAssignmentRepository assignmentRepository,
            ProductRepository productRepository,
            SMProductRepository smProductRepository,
            PriceEntryRepository entryRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.productRepository = productRepository;
        this.smProductRepository = smProductRepository;

        this.entryRepository = entryRepository;

        priceOwnerRepositoryList = new ArrayList<>();

        priceOwnerRepositoryList.addAll(
                Arrays.asList(
                        this.productRepository,
                        this.smProductRepository
                )
        );
    }

    public PriceEntryAssignment createAssignment(PriceEntry entry, PriceOwner owner) {
        PriceEntryAssignment model = new PriceEntryAssignment(entry, owner);
        return assignmentRepository.save(model);
    }

    public PriceOwner discoverOwner(UUID ownerID) {
        for (JpaRepository<?, UUID> repository : priceOwnerRepositoryList) {
            Optional<?> entity = repository.findById(ownerID);

            if (entity.isPresent()) {
                if (entity.get() instanceof PriceOwner owner) {
                    return owner;
                }
            }

        }

        throw new EntityNotFoundException("Dono não foi descoberto, provavelmente não está mapeado dentro do banco");
    }

    public PriceEntryAssignment getById(UUID id) {
        Optional<PriceEntryAssignment> assignment = assignmentRepository.findById(id);

        if (assignment.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return assignment.get();
    }

    public PriceEntryAssignment getByOwnerId(UUID id) {
        Optional<PriceEntryAssignment> assignment = assignmentRepository.findByOwnerId(id);

        if (assignment.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return assignment.get();
    }

    public PriceEntryAssignment getByEntryId(UUID id) {
        Optional<PriceEntryAssignment> assignment = assignmentRepository.findByEntryId(id);

        if (assignment.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return assignment.get();

    }

    @Transactional
    public void cleanInvalidAssignment(UUID assignmentId) {
        Optional<PriceEntryAssignment> assignmentOpt = assignmentRepository.findById(assignmentId);
        if (assignmentOpt.isEmpty()) return;

        PriceEntryAssignment assignment = assignmentOpt.get();

        // Deleta imediatamente se os IDs forem nulos
        if (assignment.getOwnerId() == null || assignment.getEntryId() == null) {
            assignmentRepository.deleteById(assignmentId);
            return;
        }

        // Verifica se a entry existe
        if (entryRepository.findById(assignment.getEntryId()).isEmpty()) {
            assignmentRepository.deleteById(assignmentId);
            return;
        }

        // Verifica se existe algum owner
        boolean ownerExists = priceOwnerRepositoryList.stream()
                .anyMatch(repo -> repo.findById(assignment.getOwnerId()).isPresent());

        if (!ownerExists) {
            assignmentRepository.deleteById(assignmentId);
        }
    }


    public URI getUriForPersistedObject(PriceEntryAssignment model) {
        return ApiUtils.getUriForPersistedObject(model.getId().toString(), "/entry/price/assignment/id/{id}");
    }

}
