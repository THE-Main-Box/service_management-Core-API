package br.com.studios.sketchbook.service_management_core.infra.repositories;

import br.com.studios.sketchbook.service_management_core.models.entities.SMProductModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SMProductRepository extends JpaRepository<SMProductModel, UUID> {
    @Query("""
            SELECT p FROM SMProductModel p
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY p.name
            """)
    Page<SMProductModel> ListByName(@Param("name") String name, Pageable pageable);

    @Query("""
            SELECT p FROM SMProductModel p
             ORDER BY p.name
            """)
    Page<SMProductModel> listAll(Pageable pageable);


}
