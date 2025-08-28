package br.com.studios.sketchbook.service_management_core.infra.util.StorageEntry;

import br.com.studios.sketchbook.service_management_core.models.data_transfer_objects.StorageEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.models.entities.StorageEntry;

import java.math.BigDecimal;
import java.math.MathContext;

import static br.com.studios.sketchbook.service_management_core.infra.util.StorageEntry.StorageEntryConverterDataManager.getScaleByVolumeType;

public class StorageEntryCoreDataManager {

    private final StorageEntryInitializerDataManager initializer;
    private final StorageEntryValueDataManager valueManager;
    private final StorageEntryUpdateDataManager updateManager;

    public StorageEntryCoreDataManager() {
        initializer = new StorageEntryInitializerDataManager();
        valueManager = new StorageEntryValueDataManager();
        updateManager = new StorageEntryUpdateDataManager();
    }

    /// Através do dto atualizamos dados da entrada
    public void editEntry(StorageEntry entry, StorageEntryUpdateDTO dto) {
        updateManager.editEntry(entry, dto, dto.raw());
    }

    /**
     * Chama a função responsável pela subtração de subQuantidade
     *
     * @param entry    Objeto a ter o seu valor de estoque removido.
     *                 <p>
     * @param quantity Quantidade em questão, pode ser em "unidades", "gramas", "quilos", "mililitros", "litros".
     *                 Tudo dependente do valor passado, "VolumeType" e "raw".
     *                 <p>
     * @param raw      determina se devemos usar o valor sem conversão,
     *                 serve para podermos passar valores pequenos,
     *                 como gramas e ml,
     *                 em vez de quilos e litros.
     */
    public void removeSubQuantity(StorageEntry entry, Long quantity, boolean raw) {
        valueManager.removeSubQuantity(entry, quantity, raw);
        updateManager.syncUnitsOnSubUnits(entry);
    }

    /**
     * Chama a função responsável pela adição de subQuantidade
     *
     * @param entry    Objeto a ter o seu valor de estoque adicionado.
     *                 <p>
     * @param quantity Quantidade em questão, pode ser em "unidades", "gramas", "quilos", "mililitros", "litros".
     *                 Tudo dependente do valor passado, "VolumeType" e "raw".
     *                 <p>
     * @param raw      determina se devemos usar o valor sem conversão,
     *                 serve para podermos passar valores pequenos,
     *                 como gramas e ml,
     *                 em vez de quilos e litros.
     */
    public void addSubQuantity(StorageEntry entry, Long quantity, boolean raw) {
        valueManager.addSubQuantity(entry, quantity, raw);
        updateManager.syncUnitsOnSubUnits(entry);
    }


    /**
     * Retorna o valor restante em subunidades que não formam uma unidade inteira.
     * Este valor permanece no armazenamento, sem alterar a contagem de unidades.
     */
    public Long getRemainderRaw(StorageEntry entry) {
        return entry.getSubUnits() % entry.getQuantityPerUnit();
    }

    public BigDecimal getRemainder(StorageEntry entry) {
        long remainderRaw = getRemainderRaw(entry);
        long scaleValue = getScaleByVolumeType(entry.getVType());
        return StorageEntryConverterDataManager.toHumanReadable(remainderRaw, scaleValue);
    }


    /**
     * Obtemos a quantidade disponível de um produto.
     * Se for um produto com tipos básicos, ainda iremos precisar realizar conversão para os tipos de quilo e litro.
     * Se for um tipo especial, a conversão se torna um pouco problemática, pois precisaríamos obter das subunidades,
     * porém não é nada muito complexo
     */
    public BigDecimal getAmountAvailable(StorageEntry entry) {
        if (entry == null) return BigDecimal.ZERO;

        long rawValue = entry.getVType().isSpecialType()
                ? entry.getSubUnits()
                : entry.getUnits();

        long scaleValue = getScaleByVolumeType(entry.getVType());

        return StorageEntryConverterDataManager.toHumanReadable(rawValue, scaleValue);
    }


    /// Obtemos os valores raw no formato "Long",
    ///  ou seja, que são interpretados diretamente pelo sistema do jeito do sistema interpretar
    public Long getAmountAvailableRaw(StorageEntry entry) {

        //Se for de um tipo composto então obtemos pela subunidade
        if (entry.getVType().isSpecialType()) {
            return entry.getSubUnits();
        } else {//Se for de um tipo simples obtemos pelas unidades
            return entry.getUnits();
        }

    }


    /// Adiciona unidades inteiras à quantidade do produto
    public void addUnit(StorageEntry entry, Long quantity, boolean raw) {
        valueManager.addUnit(entry, quantity, raw);
    }

    /// Remove unidades inteiras à quantidade do produto
    public void removeUnit(StorageEntry entry, Long quantity, boolean raw) {
        valueManager.removeUnit(entry, quantity, raw);
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
            initializer.initSpecialType(
                    entry,
                    quantity,
                    quantityPerUnit,
                    raw
            );
        } else {
            initializer.initBasicType(
                    entry,
                    quantity,
                    raw
            );
        }
    }

}
