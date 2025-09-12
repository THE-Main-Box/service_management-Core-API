package br.com.studios.sketchbook.service_management_core.product.product_related.infra.repositories;

import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
