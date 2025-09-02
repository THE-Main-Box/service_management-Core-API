package br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.objectfying_related;

import br.com.studios.sketchbook.service_management_core.product.domain.model.aux_model.StorageEntry;
import org.springframework.stereotype.Component;

import static br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.value_related.StorageEntryConverterDataManager.getScaleByVolumeType;

@Component
public class StorageEntryInitializerDataManager {

    /**
     * Inicializa a entrada de armazenamento para tipos especiais.
     * Calcula subunidades e unidades inteiras, garantindo a consistência do armazenamento.
     *
     * @param entry    Produto a ser afetado
     * @param quantity Quantidade de unidades
     * @param raw      Indica se os valores recebidos já estão no formato mínimo
     */
    public void initSpecialType(StorageEntry entry, Long quantity, Long quantityPerUnit, boolean raw) {
        if (!entry.getVType().isSpecialType()) {
            throw new IllegalArgumentException(
                    "Tipo incorreto para init especial: "
                            + entry.getId()
            );
        }
        if (quantityPerUnit == null || quantityPerUnit == 0) {
            throw new IllegalArgumentException(
                    "Tipos especiais precisam ter quantidade por unidade como valor obrigatório: "
                            + entry.getId()
            );
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



}
