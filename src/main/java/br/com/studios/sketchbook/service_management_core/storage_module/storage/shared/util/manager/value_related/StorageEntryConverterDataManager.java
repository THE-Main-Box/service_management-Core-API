package br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.util.manager.value_related;

import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;

import java.math.BigDecimal;
import java.math.MathContext;

import static br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeScaleConstants.KILOGRAMS;
import static br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeScaleConstants.LITERS;

public class StorageEntryConverterDataManager {

    /**
     * Conversão do campo subUnits (sempre escala para a unidade interna quando raw == false).
     */
    public static Long convertSubUnitsField(Long dtoSubUnits, long scale, boolean raw) {
        if (dtoSubUnits == null) return null;
        return raw ? dtoSubUnits : safeMultiply(dtoSubUnits, scale, "subUnits");
    }

    /**
     * Conversão do campo quantityPerUnit (scale aplicado quando raw == false)
     */
    public static Long convertQpuField(Long dtoQpu, long scale, boolean raw) {
        if (dtoQpu == null) return null;
        return raw ? dtoQpu : safeMultiply(dtoQpu, scale, "quantityPerUnit");
    }


    /**
     * Multiplicação segura com tratamento de overflow e mensagem contextual.
     */
    public static long safeMultiply(long a, long b, String context) {
        try {
            return Math.multiplyExact(a, b);
        } catch (ArithmeticException ex) {
            throw new ArithmeticException("Overflow ao multiplicar (" + context + "): " + ex.getMessage());
        }
    }

    /**
     * overloads para Long nullable -> delega a safeMultiply
     */
    public static Long safeMultiply(Long a, long b, String context) {
        if (a == null) return null;
        return safeMultiply(a.longValue(), b, context);
    }

    /**
     * Conversão do campo units conforme o tipo alvo.
     * - Para tipos simples, units é convertido por scale (se raw==false).
     * - Para tipos especiais, units é contagem e NÃO é escalado (raw é ignorado).
     */
    public static Long convertUnitsField(Long dtoUnits, VolumeType targetType, long scale, boolean raw) {
        if (dtoUnits == null) return null;
        if (!targetType.isCompostType()) {
            // tipos simples: armazenamos em escala interna (ex: kg -> g)
            return raw ? dtoUnits : safeMultiply(dtoUnits, scale, "units");
        } else {
            // tipos especiais: units é contagem (embalagens) — não escala
            return dtoUnits;
        }
    }

    /**
     * Retorna o multiplicador de escala para o tipo de volume do produto.
     * Garante que a conversão entre unidades e subunidades seja correta.
     */
    public static Long getScaleByVolumeType(VolumeType vType) {
        return switch (vType) {
            case LITER, LITER_PER_UNIT -> LITERS.getScale();
            case KILOGRAM, KILOGRAM_PER_UNIT -> KILOGRAMS.getScale();
            case UNIT, UNIT_PER_UNIT -> 1L;
        };
    }

    /**
     * Converte um valor em subunidades ou unidades para valor compreensível pelo humano,
     * dividindo pelo scale apropriado e mantendo precisão decimal.
     */
    public static BigDecimal toHumanReadable(long rawValue, long scale) {
        if (scale == 0) return BigDecimal.ZERO; // evitar divisão por zero
        return BigDecimal.valueOf(rawValue)
                .divide(BigDecimal.valueOf(scale), MathContext.DECIMAL128)
                .stripTrailingZeros();
    }

}
