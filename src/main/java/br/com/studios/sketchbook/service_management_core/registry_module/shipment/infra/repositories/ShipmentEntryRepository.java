package br.com.studios.sketchbook.service_management_core.registry_module.shipment.infra.repositories;

import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.model.AddressEntry;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model.ShipmentEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShipmentEntryRepository extends JpaRepository<ShipmentEntry, UUID> {
    @Query("""
            SELECT s FROM ShipmentEntry s
            WHERE LOWER(s.description) LIKE LOWER(CONCAT('%', :description, '%'))
            ORDER BY s.description
            """)
    Page<ShipmentEntry> listByDescription(String description, Pageable pageable);
}
