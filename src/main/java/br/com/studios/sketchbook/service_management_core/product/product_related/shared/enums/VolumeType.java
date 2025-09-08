package br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums;

public enum VolumeType {
    KILOGRAM,
    UNIT,
    LITER,
    KILOGRAM_PER_UNIT,
    LITER_PER_UNITY,
    UNITY_PER_UNITY;

    public boolean isCompostType() {
        return switch (this) {
            case KILOGRAM_PER_UNIT,
                 LITER_PER_UNITY,
                 UNITY_PER_UNITY -> true;
            default -> false;
        };
    }

}
