package br.com.studios.sketchbook.service_management_core.infra.util.dataManager;

import br.com.studios.sketchbook.service_management_core.models.entities.StorageEntry;
import br.com.studios.sketchbook.service_management_core.models.enumerators.VolumeType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static br.com.studios.sketchbook.service_management_core.models.enumerators.ValuesScaleConstants.KILOGRAMS;
import static br.com.studios.sketchbook.service_management_core.models.enumerators.ValuesScaleConstants.LITERS;

@Component
public class StorageEntryDataManager {

    /**
     * Remove valor das subunidades (apenas para tipos especiais).
     * <p>
     * Contrato:
     * - raw == true  → 'quantity' já está em subunidades (ex: gramas / ml / unidades internas)
     * - raw == false → 'quantity' está no formato humano (ex: kg / L / unidades) e será convertido
     * para subunidades usando a escala apropriada (kg -> g, L -> ml, etc).
     * <p>
     * Nota: se o que você quer remover são unidades inteiras, use removeUnit(...).
     */
    public void removeSubQuantity(StorageEntry entry, Long quantity, boolean raw) {
        validateSpecialType(entry);

        if (quantity == null) throw new IllegalArgumentException("quantity não pode ser nulo");

        // garante que não trabalhemos com null internamente
        long current = entry.getSubUnits() == null ? 0L : entry.getSubUnits();

        long toSubtract;
        try {
            if (raw) {
                // quantidade já está em subunidades
                toSubtract = Math.subtractExact(current, quantity);
            } else {
                long scale = getScaleByVolumeType(entry.getVType()); // 1000 para kg/l, 1 para unidade
                long converted = Math.multiplyExact(quantity, scale);
                toSubtract = Math.subtractExact(current, converted);
            }
        } catch (ArithmeticException ex) {
            throw new ArithmeticException("Overflow ao calcular subunidades para remoção: " + ex.getMessage());
        }

        entry.setSubUnits(toSubtract);
        syncUnitsOnSubUnits(entry);
    }

    /**
     * Adiciona valor nas subunidades (apenas para tipos especiais).
     * <p>
     * Contrato:
     * - raw == true  → 'quantity' já está em subunidades (ex: gramas / ml / unidades internas)
     * - raw == false → 'quantity' está no formato humano (ex: kg / L / unidades) e será convertido
     * para subunidades usando a escala apropriada (kg -> g, L -> ml, etc).
     * <p>
     * Nota: se o que você quer adicionar são unidades inteiras, use addUnit(...).
     */
    public void addSubQuantity(StorageEntry entry, Long quantity, boolean raw) {
        validateSpecialType(entry);

        if (quantity == null) throw new IllegalArgumentException("quantity não pode ser nulo");

        long current = entry.getSubUnits() == null ? 0L : entry.getSubUnits();

        long toAdd;
        try {
            if (raw) {
                // 'quantity' já é o valor em subunidades
                toAdd = Math.addExact(current, quantity);
            } else {
                long scale = getScaleByVolumeType(entry.getVType());
                long converted = Math.multiplyExact(quantity, scale);
                toAdd = Math.addExact(current, converted);
            }
        } catch (ArithmeticException ex) {
            throw new ArithmeticException("Overflow ao calcular subunidades para adição: " + ex.getMessage());
        }

        entry.setSubUnits(toAdd);
        syncUnitsOnSubUnits(entry);
    }

    /**
     * Sincroniza unidades inteiras a partir das subunidades.
     * Rotina:
     * - exige quantityPerUnit definido (>0) (quantas subunidades compõem 1 unidade)
     * - faz floorDiv(subUnits, quantityPerUnit) para garantir unidades inteiras sem arredondamento
     */
    private void syncUnitsOnSubUnits(StorageEntry entry) {
        validateSpecialType(entry);

        Long qpu = entry.getQuantityPerUnit();
        if (qpu == null || qpu <= 0L) {
            throw new IllegalStateException("quantityPerUnit inválido ou não configurado para: " + entry.getProduct());
        }

        long sub = entry.getSubUnits() == null ? 0L : entry.getSubUnits();

        // floorDiv protege contra valores negativos e faz divisão inteira consistente
        long newUnits = Math.floorDiv(sub, qpu);
        entry.setUnits(newUnits);
    }

    /**
     * Retorna o valor restante em subunidades que não formam uma unidade inteira.
     * Este valor permanece no armazenamento, sem alterar a contagem de unidades.
     */
    public Long getRemainder(StorageEntry entry) {
        return entry.getSubUnits() % entry.getQuantityPerUnit();
    }

    /**
     * Retorna o multiplicador de escala para o tipo de volume do produto.
     * Garante que a conversão entre unidades e subunidades seja correta.
     */
    public Long getScaleByVolumeType(VolumeType vType) {
        return switch (vType) {
            case LITER, LITER_PER_UNITY -> LITERS.getScale();
            case KILOGRAM, KILOGRAM_PER_UNIT -> KILOGRAMS.getScale();
            case UNIT, UNITY_PER_UNITY -> 1L;
        };
    }

    /**
     * Obtemos a quantidade disponível de um produto.
     * Se for um produto com tipos básicos, ainda iremos precisar realizar conversão para os tipos de quilo e litro.
     * Se for um tipo especial, a conversão se torna um pouco problemática, pois precisaríamos obter das subunidades,
     * porém não é nada muito complexo
     */
    public BigDecimal getAmountAvailable(StorageEntry entry) {

        return switch (entry.getVType()) {
            //Caso estejamos a lidar com um retorno de litros ou quilos, retornamos com a escala de litro e quilo
            case KILOGRAM, LITER -> BigDecimal.valueOf(entry.getUnits(), 3);
            //Caso estejamos a lidar com o retorno de unidades, não há necessidade de realizar conversão
            case UNIT -> BigDecimal.valueOf(entry.getUnits());
            //Caso unidade por quilo/litro, ainda é preciso realizar uma conversão
            case KILOGRAM_PER_UNIT, LITER_PER_UNITY -> BigDecimal.valueOf(entry.getSubUnits(), 3);
            //Unidade por unidade já é normal então está de boa
            case UNITY_PER_UNITY -> BigDecimal.valueOf(entry.getSubUnits());
        };

    }

    /// Obtemos os valores raw, ou seja, que são interpretados diretamente pelo sistema do jeito do sistema
    public BigDecimal getAmountAvailableRaw(StorageEntry entry) {
        return switch (entry.getVType()) {
            //Ao passar tipos comuns, podemos obter direto das unidades
            case KILOGRAM, LITER, UNIT -> BigDecimal.valueOf(entry.getUnits());
            //Ao passar tipos especiais precisamos pegar das subunidades
            case KILOGRAM_PER_UNIT, LITER_PER_UNITY, UNITY_PER_UNITY -> BigDecimal.valueOf(entry.getSubUnits());
        };

    }

    /**
     * Adiciona unidades ao produto. Lida com tipos normais e tipos especiais.
     *
     * @param quantity quantidade de unidades a adicionar
     * @param raw      (true) usamos o valor sem conversão, ou seja usamos o valor minimo, como sub-unidades
     *                 (false) usamos o valor com a necessidade de conversão, ou seja valor maior para converter
     */
    public void addUnit(StorageEntry entry, Long quantity, boolean raw) {
        if (!entry.isInit()) throw new IllegalStateException("Produto não iniciado: " + entry.getProduct());

        if (!entry.getVType().isSpecialType()) {
            long toAdd = raw
                    ? quantity
                    : Math.multiplyExact(quantity, getScaleByVolumeType(entry.getVType()));

            entry.setUnits(Math.addExact(entry.getUnits(), toAdd));
        } else {
            long subQuantity = Math.multiplyExact(
                    quantity,
                    entry.getQuantityPerUnit()
            );

            addSubQuantity(
                    entry,
                    subQuantity,
                    true
            );
        }
    }

    /**
     * Remove unidades do produto, mantendo a mesma lógica de addUnit.
     *
     * @param quantity quantidade de unidades a remover
     * @param raw      se true, a quantidade já está no formato interno (subunidades),
     *                 se false, é um valor "human-readable" que precisa ser convertido
     */
    public void removeUnit(StorageEntry entry, Long quantity, boolean raw) {
        if (!entry.isInit()) throw new IllegalStateException("Produto não iniciado: " + entry.getProduct());

        if (!entry.getVType().isSpecialType()) {
            long toSubtract = raw ? quantity : Math.multiplyExact(quantity, getScaleByVolumeType(entry.getVType()));
            entry.setUnits(Math.subtractExact(entry.getUnits(), toSubtract));
        } else {
            long subQuantity = Math.multiplyExact(quantity, entry.getQuantityPerUnit());

            removeSubQuantity(entry, subQuantity, true);
        }
    }

    /**
     * Inicializa a entrada de armazenamento para tipos básicos.
     * Se necessário, aplica a conversão da quantidade para o valor mínimo interno.
     *
     * @param entry    Produto a ter a sua entrada alterada
     * @param quantity Quantidade a respeito do seu tipo de volume
     * @param raw      Se precisamos realizar uma escalação pros valores serem armazenados corretamente
     */
    private void initBasicType(StorageEntry entry, Long quantity, boolean raw) {
        if (entry.getVType().isSpecialType()) {
            throw new IllegalArgumentException("Tipo incorreto para init básico: " + entry.getProduct());
        }

        long units = raw
                ? quantity
                : Math.multiplyExact(quantity, getScaleByVolumeType(entry.getVType()));

        entry.setUnits(units);
        entry.setInit(true);
    }

    /**
     * Inicializa a entrada de armazenamento para tipos especiais.
     * Calcula subunidades e unidades inteiras, garantindo a consistência do armazenamento.
     *
     * @param entry    Produto a ser afetado
     * @param quantity Quantidade de unidades
     * @param raw      Indica se os valores recebidos já estão no formato mínimo
     */
    private void initSpecialType(StorageEntry entry, Long quantity, Long quantityPerUnit, boolean raw) {
        if (!entry.getVType().isSpecialType()) {
            throw new IllegalArgumentException("Tipo incorreto para init especial: " + entry.getProduct());
        }

        long scaleToEnter = raw
                ? quantityPerUnit
                : Math.multiplyExact(quantityPerUnit, getScaleByVolumeType(entry.getVType()));

        entry.setQuantityPerUnit(scaleToEnter);
        entry.setUnits(quantity);
        entry.setSubUnits(Math.multiplyExact(quantity, scaleToEnter));
        entry.setInit(true);
    }

    /**
     * Inicia a entrada, já atribuída a um produto
     *
     * @param entry           objeto a ser iniciado
     * @param quantity        quantidade do produto dentro do armazém, pode variar dependendo do tipo
     * @param quantityPerUnit quantidade por unidade caso seja de tipo especial, pode ser ignorado para tipos simples
     * @param raw             Se precisamos realizar uma conversão para os tipos internos,
     *                        ou se já estão convertidos de entrada
     */
    public void initEntry(StorageEntry entry, Long quantity, Long quantityPerUnit, boolean raw) {
        if (entry.getVType().isSpecialType()) {
            initSpecialType(entry, quantity, quantityPerUnit, raw);
        } else {
            initBasicType(entry, quantity, raw);
        }
    }

    private void validateSpecialType(StorageEntry entry) {
        if (!entry.isInit()) throw new IllegalStateException("Produto não iniciado: " + entry.getProduct());
        if (!entry.getVType().isSpecialType()) {
            throw new IllegalStateException("Operação válida apenas para tipos especiais: " + entry.getProduct());
        }
    }

}
