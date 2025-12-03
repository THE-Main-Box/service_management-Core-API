package br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model;

import java.util.UUID;

public interface Item {
    UUID getId();

    String getItemType();

    String getName();
}
