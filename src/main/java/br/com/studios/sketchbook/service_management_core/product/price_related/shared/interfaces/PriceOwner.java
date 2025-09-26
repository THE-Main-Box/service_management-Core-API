package br.com.studios.sketchbook.service_management_core.product.price_related.shared.interfaces;

import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;

import java.util.UUID;

public interface PriceOwner {

    UUID getId();
    VolumeType getVolumeType();
}
