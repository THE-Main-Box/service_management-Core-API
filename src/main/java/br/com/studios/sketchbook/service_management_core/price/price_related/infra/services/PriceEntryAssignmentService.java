package br.com.studios.sketchbook.service_management_core.price.price_related.infra.services;

import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntryAssignment;
import br.com.studios.sketchbook.service_management_core.price.price_related.infra.components.PriceEntryAssignmentCleaner;
import br.com.studios.sketchbook.service_management_core.price.price_related.infra.repositories.PriceEntryAssignmentRepository;
import br.com.studios.sketchbook.service_management_core.price.price_related.shared.interfaces.PriceOwner;
import br.com.studios.sketchbook.service_management_core.product.product_related.api.util.ApiUtils;
import br.com.studios.sketchbook.service_management_core.product.product_related.infra.repositories.ProductRepository;
import br.com.studios.sketchbook.service_management_core.product.product_related.infra.repositories.SMProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;

@Service
public class PriceEntryAssignmentService {

    /// Repositório do assignment
    private final PriceEntryAssignmentRepository assignmentRepository;

    /// Componente de limpeza orfã
    private final PriceEntryAssignmentCleaner cleaner;

    /// Repositório de produto
    private final ProductRepository productRepository;
    /// Repositório de produto de super mercado
    private final SMProductRepository smProductRepository;

    /// Lista para facilitar a iteração futura
    private final List<JpaRepository<?, UUID>> priceOwnerRepositoryList;

    @Autowired
    public PriceEntryAssignmentService(
            PriceEntryAssignmentRepository assignmentRepository,
            PriceEntryAssignmentCleaner cleaner,
            ProductRepository productRepository,
            SMProductRepository smProductRepository
    ) {
        this.cleaner = cleaner;
        this.assignmentRepository = assignmentRepository;
        this.productRepository = productRepository;
        this.smProductRepository = smProductRepository;

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

    public PriceEntryAssignment getById(UUID id){
        return assignmentRepository.getReferenceById(id);
    }

    public PriceEntryAssignment getByOwnerId(UUID id){
        return assignmentRepository.findByOwnerId(id);
    }

    public PriceEntryAssignment getByEntryId(UUID id){
        return assignmentRepository.findByEntryId(id);
    }

    public void cleanInvalidAssignments(){
        this.cleaner.cleanupOrphanAssignments();
    }

    public URI getUriForPersistedObject(PriceEntryAssignment model) {
        return ApiUtils.getUriForPersistedObject(model.getId().toString(), "/entry/price/assignment/id/{id}");
    }

}
