package br.com.studios.sketchbook.service_management_core.infra.util.StorageEntry;

import br.com.studios.sketchbook.service_management_core.models.data_transfer_objects.StorageEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.models.entities.StorageEntry;

import java.math.BigDecimal;

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
    public Long getRemainder(StorageEntry entry) {
        return entry.getSubUnits() % entry.getQuantityPerUnit();
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
