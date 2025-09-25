package br.com.studios.sketchbook.service_management_core.product.price_related.infra.repositories;

import br.com.studios.sketchbook.service_management_core.product.price_related.domain.model.PriceEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PriceEntryRepository extends JpaRepository<PriceEntry, UUID> {
}
