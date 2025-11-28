package br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.manager.objectify;

import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.model.PriceModifier;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.enums.AdjustmentTrigger;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.enums.AdjustmentType;
import br.com.studios.sketchbook.service_management_core.storage_module.product.shared.enums.VolumeType;

import java.math.BigDecimal;

import static br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.util.manager.value_related.StorageEntryConverterDataManager.getScaleByVolumeType;

public class PriceModInit {

    public PriceModifier initPriceMod(
            VolumeType volumeType,
            long quantity,
            boolean raw,
            AdjustmentTrigger trigger,
            BigDecimal percentage,
            AdjustmentType adjustmentType
    ) {

        //Verifica se estamos com o valor raw, se sim passamos de forma direta
        //Se não realizamos uma multiplicação para podermos saber o valor mínimo
        long rawQuantity = raw
                ? quantity
                : quantity * getScaleByVolumeType(volumeType);

        //Seta o modificador enquanto cria ele, garantindo uma relação bidirectional
        return new PriceModifier(
                rawQuantity,
                trigger,
                percentage,
                adjustmentType
        );

    }

}
