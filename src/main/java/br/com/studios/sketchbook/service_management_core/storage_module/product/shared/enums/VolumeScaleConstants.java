package br.com.studios.sketchbook.service_management_core.storage_module.product.shared.enums;

import lombok.Getter;

/// NÃO MEXER || APENAS ADICIONAR
@Getter
public enum VolumeScaleConstants {
    LITERS(1000L),
    KILOGRAMS(1000L);

    private final Long scale;

    VolumeScaleConstants(Long scale) {
        this.scale = scale;
    }

}
