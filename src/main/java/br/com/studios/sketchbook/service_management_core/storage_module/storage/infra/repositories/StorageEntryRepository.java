package br.com.studios.sketchbook.service_management_core.storage_module.storage.infra.repositories;

import br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.model.StorageEntry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.ConfigRefNames.StorageConfig.storage_entity_manager_factory_ref;

@Repository
public interface StorageEntryRepository extends JpaRepository<StorageEntry, UUID> {
    @Query("SELECT p FROM StorageEntry p WHERE p.ownerId = :ownerId")
    Optional<StorageEntry> findByOwnerId(@Param("ownerId") UUID ownerId);
}
