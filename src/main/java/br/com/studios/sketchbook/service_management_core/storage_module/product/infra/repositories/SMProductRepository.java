package br.com.studios.sketchbook.service_management_core.storage_module.product.infra.repositories;

import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model.SuperMarketProduct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.ConfigRefNames.StorageConfig.storage_entity_manager_factory_ref;

@Repository
public interface SMProductRepository extends JpaRepository<SuperMarketProduct, UUID> {
    @Query("""
            SELECT p FROM SuperMarketProduct p
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY p.name
            """)
    Page<SuperMarketProduct> ListByName(@Param("name") String name, Pageable pageable);

    @Query("""
            SELECT p FROM SuperMarketProduct p
             ORDER BY p.name
            """)
    Page<SuperMarketProduct> listAll(Pageable pageable);


}
