package br.com.studios.sketchbook.service_management_core.registry_module.shipment.infra.repositories;

import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model.ShipmentEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShipmentEntryRepository extends JpaRepository<ShipmentEntry, UUID> {

    // Busca por descrição do endereço de ORIGEM
    @Query("""
            SELECT s FROM ShipmentEntry s
            WHERE LOWER(s.originAddressRef.description) LIKE LOWER(CONCAT('%', :description, '%'))
            ORDER BY s.originAddressRef.description
            """)
    Page<ShipmentEntry> findByOriginAddressDescription(@Param("description") String description, Pageable pageable);

    // Busca por descrição do endereço de DESTINO
    @Query("""
            SELECT s FROM ShipmentEntry s
            WHERE LOWER(s.destinationAddressRef.description) LIKE LOWER(CONCAT('%', :description, '%'))
            ORDER BY s.destinationAddressRef.description
            """)
    Page<ShipmentEntry> findByDestinationAddressDescription(@Param("description") String description, Pageable pageable);

    // Busca por nome do item enviado
    @Query("""
            SELECT s FROM ShipmentEntry s
            WHERE LOWER(s.itemShipped.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY s.itemShipped.name
            """)
    Page<ShipmentEntry> findByItemName(@Param("name") String name, Pageable pageable);

    // Busca por ID do endereço de ORIGEM
    @Query("""
            SELECT s FROM ShipmentEntry s
            WHERE s.originAddressRef.addressId = :addressId
            """)
    Page<ShipmentEntry> findByOriginAddressId(@Param("addressId") UUID addressId, Pageable pageable);

    // Busca por ID do endereço de DESTINO
    @Query("""
            SELECT s FROM ShipmentEntry s
            WHERE s.destinationAddressRef.addressId = :addressId
            """)
    Page<ShipmentEntry> findByDestinationAddressId(@Param("addressId") UUID addressId, Pageable pageable);

    // Busca por ID do item enviado
    @Query("""
            SELECT s FROM ShipmentEntry s
            WHERE s.itemShipped.itemId = :itemId
            """)
    Page<ShipmentEntry> findByItemId(@Param("itemId") UUID itemId, Pageable pageable);
}