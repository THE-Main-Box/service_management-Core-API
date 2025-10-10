package br.com.studios.sketchbook.service_management_core.price.price_related.infra.repositories;

import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriceEntryRepository extends JpaRepository<PriceEntry, UUID> {

    @Query("SELECT p FROM PriceEntry p WHERE p.ownerId = :ownerId")
    Optional<PriceEntry> findByOwnerId(@Param("ownerId") UUID ownerId);

}
