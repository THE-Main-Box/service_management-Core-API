package br.com.studios.sketchbook.service_management_core.models.entities;

import br.com.studios.sketchbook.service_management_core.models.enumerators.VolumeType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static br.com.studios.sketchbook.service_management_core.models.enumerators.ValuesScaleConstants.*;

@Entity
@Table(name = "TB_STORAGE_DATA")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StorageEntry {

    @Id
    @Getter
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    @Getter
    /// Referencia ao produto a quem isso daqui pertende
    //TODO:Tornar essa referencia final, para evitar que alteremos a referencia em um momento aleatório
    private Product product;

    @Getter
    private VolumeType vType;

    @Getter
    /// Unidades completas existentes
    private Long units;

    /**
     * Litros inteiros existentes no armazém
     * Estamos nos referindo aos litros que podem ser usados como unidade inteira,
     * em vez de uma conta quebrada fora de unidade de venda
     */
    private Long liters;

    /**
     * Quilos inteiros existentes no armazém
     * Serve para contar os quilos que podem ser vendidos dentro do armazém
     * que não estão quebrados fora de unidade, caso haja uma
     */
    private Long kilograms;

    /**
     * Unidades para tipos especiais de "unidade por unidade"
     * Serve para contar as unidades por unidades existentes
     */
    @Getter
    private Long subUnits;

    /**
     * Escala para produtos de tipo de volume especial
     * 1 > 10 de unidades, significa que para cada unidade teríamos 10 unidades menores,
     * ou seja, é uma escala contando objetos reais, e não de um pra outro, como 0.5, ou 1.9,
     * é um valor de produto real, como 10 pra uma unidade ou coisa parecida
     */
    @Getter
    private Long scale;

    /**
     * Restante que não pode ser contado com unidade completa
     * em vez de contarmos nas unidades, como um valor quebrado(1.5, 0.9, ou coisas do tipo),
     * mantemos ele como um valor inteiro,
     * e colocamos aqui o que é restante da unidade quebrada
     */
    private Long remainder;


    public StorageEntry(Product product, Long quantity, Long scale) {
        this.product = product;
        this.vType = product.volumeType;

        this.resetValues();

        if (product.volumeType.isSpecialType()) {
            initSpecialType(this, quantity, scale);
        } else {
            initBasicType(this, quantity);
        }

    }

    /**
     * Função estática para permitir uma interpretação comum dos dados de forma interna
     *
     * @param entry    Produto a ter sua entrada alterada
     * @param quantity Quantidade a respeito do seu tipo de volume
     */
    private static void initBasicType(StorageEntry entry, Long quantity) {
        switch (entry.getVType()) {
            case UNIT -> entry.setUnits(quantity);
            case LITER -> entry.setLiters(BigDecimal.valueOf(quantity));
            case KILOGRAM -> entry.setKilograms(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * Ajusta a escala de um StorageEntry de tipo especial,
     * sem nunca incluir o remainder nos cálculos.
     */
    public static void changeScale(StorageEntry entry, long newScale) {
        if (!entry.getVType().isSpecialType()) {
            throw new IllegalArgumentException("Apenas tipos especiais suportam mudança de escala.");
        }

        if (newScale <= 0) {
            throw new IllegalArgumentException("Nova escala deve ser positiva.");
        }

        // Considera apenas as unidades válidas (sem remainder)
        BigDecimal total = switch (entry.getVType()) {
            case KILOGRAM_PER_UNIT -> {
                BigDecimal kg = entry.getKilograms();
                yield (kg == null) ? BigDecimal.ZERO
                        : kg.multiply(BigDecimal.valueOf(KILOGRAMS.getScale())); // em g
            }
            case LITER_PER_UNITY -> {
                BigDecimal lt = entry.getLiters();
                yield (lt == null) ? BigDecimal.ZERO
                        : lt.multiply(BigDecimal.valueOf(LITERS.getScale())); // em ml
            }
            case UNITY_PER_UNITY -> {
                long u = (entry.getUnits() == null ? 0L : entry.getUnits());
                yield BigDecimal.valueOf(u * entry.getScale());
            }
            default -> throw new IllegalStateException("Tipo inválido para mudança de escala: " + entry.getVType());
        };

        // Atualiza escala
        entry.setScale(newScale);

        // Calcula quantas unidades inteiras cabem na nova escala
        long fullUnits = total.divide(BigDecimal.valueOf(newScale), RoundingMode.DOWN).longValueExact();

        entry.setUnits(fullUnits);

    }


    /**
     * Função estática para iniciar de modo comum os dados de interpretação especial
     *
     * @param entry    Produto a ser afetado
     * @param quantity Quantidade do produto conforme o seu tipo de volume
     * @param scale    Escala de unidade para tipo especial
     */
    private static void initSpecialType(StorageEntry entry, Long quantity, Long scale) {
        entry.setScale(scale);

        switch (entry.getVType()) {
            case LITER_PER_UNITY -> {
                entry.setUnits(quantity);
                entry.setLiters(BigDecimal.valueOf(quantity * scale));
            }
            case KILOGRAM_PER_UNIT -> {
                entry.setUnits(quantity);
                entry.setKilograms(BigDecimal.valueOf(quantity * scale));
            }
            case UNITY_PER_UNITY -> {
                entry.setUnits(quantity);
                entry.setRemainder(BigDecimal.valueOf(quantity * scale));
            }
        }
    }


    private void resetValues() {
        this.units = null;
        this.liters = null;
        this.kilograms = null;
        this.scale = null;
        this.remainder = null;
    }


    // Litros
    public BigDecimal getLiters() {
        if (this.liters == null) return null;
        return BigDecimal.valueOf(this.liters, 3); // converte ml -> L
    }

    public void setLiters(BigDecimal liters) {
        if (liters == null) {
            this.liters = null;
        } else {
            this.liters = liters.multiply(BigDecimal.valueOf(LITERS.getScale())).longValueExact();
        }
    }

    // Quilogramas
    public BigDecimal getKilograms() {
        if (this.kilograms == null) return null;
        return BigDecimal.valueOf(this.kilograms, 3); // g -> kg
    }

    private void setKilograms(BigDecimal kilograms) {
        if (kilograms == null) {
            this.kilograms = null;
        } else {
            this.kilograms = kilograms.multiply(BigDecimal.valueOf(KILOGRAMS.getScale())).longValueExact();
        }
    }

    private void setUnits(Long units) {
        this.units = units;
    }

    // Remainder interpretado pelo VolumeType
    public BigDecimal getRemainder() {
        if (this.remainder == null) return null;

        if (vType == null) return BigDecimal.valueOf(this.remainder);

        return switch (vType) {
            case UNITY_PER_UNITY -> BigDecimal.valueOf(this.remainder); // 1:1
            case LITER_PER_UNITY -> BigDecimal.valueOf(this.remainder, 3); // ml -> L
            case KILOGRAM_PER_UNIT -> BigDecimal.valueOf(this.remainder, 3); // g -> kg
            default -> BigDecimal.valueOf(this.remainder); // UNIT/LITER/KG normais
        };
    }

    private void setRemainder(BigDecimal remainder) {
        if (remainder == null) {
            this.remainder = null;
            return;
        }

        if (vType == null) {
            this.remainder = remainder.longValueExact();
            return;
        }

        switch (vType) {
            case UNITY_PER_UNITY -> this.remainder = remainder.longValueExact(); // 1:1
            case LITER_PER_UNITY ->
                    this.remainder = remainder.multiply(BigDecimal.valueOf(LITERS.getScale())).longValueExact(); // L -> ml
            case KILOGRAM_PER_UNIT ->
                    this.remainder = remainder.multiply(BigDecimal.valueOf(KILOGRAMS.getScale())).longValueExact(); // kg -> g
            default -> this.remainder = remainder.longValueExact();
        }
    }

    public void setScale(Long scale) {
        if (scale == null || scale <= 0) {
            throw new IllegalArgumentException("Scale deve ser um número inteiro positivo.");
        }
        this.scale = scale;
    }

    private void setSubUnits(Long subUnits) {
        this.subUnits = subUnits;
    }

}
