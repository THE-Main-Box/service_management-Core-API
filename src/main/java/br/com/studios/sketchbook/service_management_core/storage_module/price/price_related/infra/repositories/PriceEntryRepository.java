package br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.infra.repositories;

import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.model.PriceEntry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.ConfigRefNames.StorageConfig.storage_entity_manager_factory_ref;

@Repository
public interface PriceEntryRepository extends JpaRepository<PriceEntry, UUID> {

    @Query("SELECT p FROM PriceEntry p WHERE p.ownerId = :ownerId")
    Optional<PriceEntry> findByOwnerId( UUID ownerId);

}
