package br.com.studios.sketchbook.service_management_core.repositories;

import br.com.studios.sketchbook.service_management_core.models.entities.SMProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SMProductRepository extends JpaRepository<SMProductModel, UUID> {
}
