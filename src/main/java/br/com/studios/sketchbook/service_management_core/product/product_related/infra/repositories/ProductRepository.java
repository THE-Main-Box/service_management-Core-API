package br.com.studios.sketchbook.service_management_core.product.product_related.infra.repositories;

import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.Product;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.SuperMarketProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("""
            SELECT p FROM Product p
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY p.name
            """)
    Page<Product> ListByName(String name, Pageable pageable);
}
