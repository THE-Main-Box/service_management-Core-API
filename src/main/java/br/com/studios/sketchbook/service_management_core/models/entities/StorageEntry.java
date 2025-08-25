package br.com.studios.sketchbook.service_management_core.models.entities;

import br.com.studios.sketchbook.service_management_core.models.enumerators.VolumeType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    /// Referência ao produto a quem isso daqui pertence
    private Product product;

    @Getter
    private VolumeType vType;

    @Getter
    @Setter
    /// Unidades completas existentes
    private Long units;

    @Getter
    @Setter
    private Long subUnits;

    /**
     * Escala para produtos de tipo de volume especial
     * 1 > 10 de unidades, significa que para cada unidade teríamos 10 unidades menores,
     * ou seja, é uma escala contando objetos reais, e não de um pra outro, como 0,5, ou 1,9,
     * é um valor de produto real, como 10 pra uma unidade ou coisa parecida
     */
    @Setter
    private Long quantityPerUnit;


    public StorageEntry(Product product, Long quantity, Long quantityPerUnit, boolean raw) {
        this.product = product;
        this.vType = product.volumeType;

        this.resetValues();

        if (product.volumeType.isSpecialType()) {
            initSpecialType(this, quantity, quantityPerUnit, raw);
        } else {
            initBasicType(this, quantity, raw);
        }

    }

    /**
     * Função estática para permitir uma interpretação comum dos dados de forma interna
     *
     * @param entry    Produto a ter a sua entrada alterada
     * @param quantity Quantidade a respeito do seu tipo de volume
     * @param raw      Se precisamos realizar uma escalação pros valores serem armazenados corretamente
     */
    private static void initBasicType(StorageEntry entry, Long quantity, boolean raw) {
        if (entry.getVType().isSpecialType()) {
            throw new IllegalArgumentException(
                    "Entrada de armazenamento deve ser de tipo simples para ser iniciado desta forma " +
                            "id: [ " +
                            entry.getProduct().getId().toString() +
                            " ] : " +
                            entry.getVType()

            );
        }

        if (raw) {
            entry.setUnits(quantity);
        } else {
            Long scale = switch (entry.getVType()) {
                case LITER -> LITERS.getScale();
                case KILOGRAM -> KILOGRAMS.getScale();
                default -> 1L;
            };

            entry.setUnits(Math.multiplyExact(quantity, scale));

        }
    }


    /**
     * Inicializa um StorageEntry de tipo especial (quilos por unidade, litros por unidade, unidade por unidade)
     * <p>
     * Lógica:
     * - 'units' = quantidade de unidades existentes (contagem simples)
     * - 'scaleToEnter' = quantidade mínima correspondente a 1 unidade (em ml, g ou 1 unidade)
     * - Se 'raw = true' → os valores já estão no mínimo (não multiplica nada)
     * - Se 'raw = false' → multiplica pelo valor mínimo correspondente (ex: 1 kg = 1000 g)
     * - 'subUnits' = total em valores mínimos (units * scaleToEnter)
     *
     * @param entry    Produto a ser afetado
     * @param quantity Quantidade de unidades
     * @param scale    Quantidade por unidade (ex: 2,5 kg por unidade, 1000 ml por unidade)
     * @param raw      Indica se os valores recebidos já estão no formato mínimo
     */
    private static void initSpecialType(StorageEntry entry, Long quantity, Long scale, boolean raw) {
        if (!entry.vType.isSpecialType()) {
            throw new IllegalArgumentException(
                    "Entrada de armazenamento deve ser de tipo de volume especial para ser iniciado desta forma " +
                            "id: [ " + entry.getProduct().getId().toString() + " ] : " + entry.getVType()
            );
        }

        Long scaleToEnter;   // Quantidade mínima que representa 1 unidade
        long subUnits;       // Total em valores mínimos (g/ml/unidade)
        Long units;          // Contagem de unidades

        if (raw) {
            // Valores já estão em mínimos (não precisa multiplicar por 1000, por exemplo)
            scaleToEnter = scale;
        } else {
            // Para valores "humanos", converte para valor mínimo
            Long multi = switch (entry.getVType()) {
                case LITER_PER_UNITY -> LITERS.getScale();         // 1 L = 1000 ml
                case KILOGRAM_PER_UNIT -> KILOGRAMS.getScale();   // 1 kg = 1000 g
                default -> 1L;                                   // UNITY_PER_UNITY ou outros = 1
            };
            scaleToEnter = Math.multiplyExact(scale, multi); // Converte quantidade por unidade para valor mínimo
        }

        // A quantidade de unidades permanece a contagem pura
        units = quantity;

        // Total de subunidades = quantidade de unidades * escala mínima por unidade
        subUnits = Math.multiplyExact(quantity, scaleToEnter);

        // Atualiza o StorageEntry
        entry.setQuantityPerUnit(scaleToEnter); // escala em valor mínimo
        entry.setUnits(units);                  // contagem de unidades
        entry.setSubUnits(subUnits);            // total em valores mínimos
    }


    private void resetValues() {
        this.units = null;
        this.quantityPerUnit = null;
    }

    /**
     * Obtemos a quantidade disponível de um produto.
     * Se for um produto com tipos básicos, ainda iremos precisar realizar conversão para os tipos de quilo e litro.
     * Se for um tipo especial, a conversão se torna um pouco problemática, pois precisaríamos obter das subunidades,
     * porém não é nada muito complexo
     */
    public BigDecimal getAmountAvailable() {

        return switch (vType) {
            //Caso estejamos a lidar com um retorno de litros ou quilos, retornamos com a escala de litro e quilo
            case KILOGRAM, LITER -> BigDecimal.valueOf(units, 3);
            //Caso estejamos a lidar com o retorno de unidades, não há necessidade de realizar conversão
            case UNIT -> BigDecimal.valueOf(units);
            //Caso unidade por quilo/litro, ainda é preciso realizar uma conversão
            case KILOGRAM_PER_UNIT, LITER_PER_UNITY -> BigDecimal.valueOf(subUnits, 3);
            //Unidade por unidade já é normal então está de boa
            case UNITY_PER_UNITY -> BigDecimal.valueOf(subUnits);
        };

    }

    /// Obtemos os valores raw, ou seja, que são interpretados diretamente pelo sistema do jeito do sistema
    public BigDecimal getAmountAvailableRaw() {
        return switch (vType) {
            //Ao passar tipos comuns, podemos obter direto das unidades
            case KILOGRAM, LITER, UNIT -> BigDecimal.valueOf(units);
            //Ao passar tipos especiais precisamos pegar das subunidades
            case KILOGRAM_PER_UNIT, LITER_PER_UNITY, UNITY_PER_UNITY -> BigDecimal.valueOf(subUnits);
        };

    }

    /// Obtemos a quantidade por unidade caso exista dentro da lógica existente,
    /// e realizamos uma conversão para melhor interpretação
    public BigDecimal getQuantityPerUnit() {
        Long multi = switch (vType) {
            case KILOGRAM_PER_UNIT -> KILOGRAMS.getScale();
            case UNITY_PER_UNITY -> 1L;
            case LITER_PER_UNITY -> LITERS.getScale();
            default -> 0L;
        };

        return BigDecimal.valueOf(quantityPerUnit * multi);
    }

    /// Obtemos de forma direta a quantidade de um produto por unidade
    public Long getQuantityPerUnitRaw() {
        return quantityPerUnit;
    }
}
