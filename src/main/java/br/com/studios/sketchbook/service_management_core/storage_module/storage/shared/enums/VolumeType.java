package br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums;

public enum VolumeType {
    KILOGRAM,
    UNIT,
    LITER,
    KILOGRAM_PER_UNIT,
    LITER_PER_UNIT,
    UNIT_PER_UNIT;

    public boolean isCompostType() {
        return switch (this) {
            case KILOGRAM_PER_UNIT,
                 LITER_PER_UNIT,
                 UNIT_PER_UNIT -> true;
            default -> false;
        };
    }

}
