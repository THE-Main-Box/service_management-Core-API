package br.com.studios.sketchbook.service_management_core.infra.services;

import br.com.studios.sketchbook.service_management_core.models.entities.StorageEntry;
import br.com.studios.sketchbook.service_management_core.models.enumerators.VolumeType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static br.com.studios.sketchbook.service_management_core.models.enumerators.ValuesScaleConstants.KILOGRAMS;
import static br.com.studios.sketchbook.service_management_core.models.enumerators.ValuesScaleConstants.LITERS;

@Component
public class StorageEntryDataManager {

    /**
     * Removemos na subQuantidade, isso é se estivermos a lidar com produtos de volume especial
     *
     * @param quantity Quantidade do volume a ser subtraído em questão
     * @param raw      Se estamos a lidar com o tipo já convertido para armazenamento interno,
     *                 ou se precisaremos realizar uma alteração nos valores
     */
    public void removeSubQuantity(StorageEntry entry, Long quantity, boolean raw) {
        validateSpecialType(entry);

        long toSubtract = raw
                ? quantity
                : Math.multiplyExact(quantity, getScaleByVolumeType(entry.getVType()));

        entry.setSubUnits(Math.subtractExact(entry.getSubUnits(), toSubtract));
        syncUnitsOnSubUnits(entry);
    }

    /**
     * Adiciona na subQuantidade, isso é se estivermos a lidar com produtos de volume especial
     *
     * @param quantity Quantidade do volume em questão
     * @param raw      Se estamos a lidar com o tipo já convertido para armazenamento interno,
     *                 ou se precisaremos realizar uma alteração nos valores
     */
    public void addSubQuantity(StorageEntry entry, Long quantity, boolean raw) {
        validateSpecialType(entry);

        long toAdd = raw
                ? quantity
                : Math.multiplyExact(quantity, getScaleByVolumeType(entry.getVType()));

        entry.setSubUnits(Math.addExact(entry.getSubUnits(), toAdd));
        syncUnitsOnSubUnits(entry);
    }

    /**
     * Sincroniza as unidades dos tipos especiais usando a quantidade de subunidades.
     * As unidades permanecem inteiras, enquanto a quantidade interna de subunidades é mantida.
     */
    private void syncUnitsOnSubUnits(StorageEntry entry) {
        validateSpecialType(entry);

        entry.setUnits(
                Math.floorDiv(entry.getSubUnits(), entry.getQuantityPerUnit())
        );
    }

    /**
     * Retorna o valor restante em subunidades que não formam uma unidade inteira.
     * Este valor permanece no armazenamento, sem alterar a contagem de unidades.
     */
    public Long getRemainderRaw(StorageEntry entry) {
        return entry.getSubUnits() % entry.getQuantityPerUnit();
    }


    /**
     * Retorna o valor restante convertido para a escala correspondente do tipo de volume.
     * Por exemplo, se for quilo ou litro, converte de volta para a unidade humana.
     */
    public Long getRemainder(StorageEntry entry) {
        return Math.floorDiv(
                getRemainderRaw(entry),
                getScaleByVolumeType(entry.getVType())
        );
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
     * @param raw      se true, a quantidade já está no formato interno (subunidades),
     *                 se false, é um valor "human-readable" que precisa ser convertido
     */
    public void addUnit(StorageEntry entry, Long quantity, boolean raw) {
        if (!entry.isInit()) throw new IllegalStateException("Produto não iniciado: " + entry.getProduct());

        if (!entry.getVType().isSpecialType()) {
            long toAdd = raw ? quantity : Math.multiplyExact(quantity, getScaleByVolumeType(entry.getVType()));
            entry.setUnits(Math.addExact(entry.getUnits(), toAdd));
        } else {
            long subQuantity = raw
                    ? Math.multiplyExact(quantity, entry.getQuantityPerUnit())
                    : Math.multiplyExact(quantity, entry.getQuantityPerUnit() * getScaleByVolumeType(entry.getVType()));

            addSubQuantity(entry, subQuantity, true);
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
            long subQuantity = raw
                    ? Math.multiplyExact(quantity, entry.getQuantityPerUnit())
                    : Math.multiplyExact(quantity, entry.getQuantityPerUnit() * getScaleByVolumeType(entry.getVType()));

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
    public void initBasicType(StorageEntry entry, Long quantity, boolean raw) {
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

     * @param entry    Produto a ser afetado
     * @param quantity Quantidade de unidades
     * @param raw      Indica se os valores recebidos já estão no formato mínimo
     */
    public void initSpecialType(StorageEntry entry, Long quantity, Long quantityPerUnit, boolean raw) {
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
