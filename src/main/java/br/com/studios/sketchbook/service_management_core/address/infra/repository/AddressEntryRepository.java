package br.com.studios.sketchbook.service_management_core.address.infra.repository;

import br.com.studios.sketchbook.service_management_core.address.domain.AddressEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AddressEntryRepository extends JpaRepository<AddressEntry, UUID> {

    @Query("""
            SELECT a FROM AddressEntry a
            WHERE LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))
            ORDER BY a.description
            """)
    Page<AddressEntry> listByDescription(String description, Pageable pageable);

}
