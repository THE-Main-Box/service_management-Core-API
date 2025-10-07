package br.com.studios.sketchbook.service_management_core.price.price_related.infra.repositories;

import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntryAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PriceEntryAssignmentRepository extends JpaRepository<PriceEntryAssignment, UUID> {

    // Query para obter todas as assignments de um owner específico
    @Query("SELECT p FROM PriceEntryAssignment p WHERE p.ownerId = :ownerId")
    Optional<PriceEntryAssignment> findByOwnerId(@Param("ownerId") UUID ownerId);

    // Query para obter todas as assignments de uma entry específica
    @Query("SELECT p FROM PriceEntryAssignment p WHERE p.entryId = :entryId")
    Optional<PriceEntryAssignment> findByEntryId(@Param("entryId") UUID entryId);

}
