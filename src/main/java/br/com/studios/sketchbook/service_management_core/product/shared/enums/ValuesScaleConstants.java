package br.com.studios.sketchbook.service_management_core.product.shared.enums;

import lombok.Getter;

/// NÃO MEXER || APENAS ADICIONAR
@Getter
public enum ValuesScaleConstants {
    LITERS(1000L),
    KILOGRAMS(1000L);

    private final Long scale;

    ValuesScaleConstants(Long scale) {
        this.scale = scale;
    }

}
