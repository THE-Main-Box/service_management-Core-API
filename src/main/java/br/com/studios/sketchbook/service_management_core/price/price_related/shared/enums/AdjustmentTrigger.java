package br.com.studios.sketchbook.service_management_core.price.price_related.shared.enums;

public enum AdjustmentTrigger {
    APPLY_ON_MINIMUM,       // aplica quando atinge ou passa da quantidade mínima
    APPLY_UNTIL,            // aplica somente até X unidades
    APPLY_WHEN_MULTIPLE,    // aplica quando a quantidade for múltiplo de X
    APPLY_ALWAYS            // aplica enquanto dissermos para aplicar, independentemente da quantidade
}
