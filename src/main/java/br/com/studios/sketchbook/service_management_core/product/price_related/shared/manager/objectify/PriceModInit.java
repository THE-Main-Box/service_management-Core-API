package br.com.studios.sketchbook.service_management_core.product.price_related.shared.manager.objectify;

import br.com.studios.sketchbook.service_management_core.product.price_related.domain.model.PriceModifier;
import br.com.studios.sketchbook.service_management_core.product.price_related.shared.enums.AdjustmentTrigger;
import br.com.studios.sketchbook.service_management_core.product.price_related.shared.enums.AdjustmentType;
import br.com.studios.sketchbook.service_management_core.product.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static br.com.studios.sketchbook.service_management_core.product.storage_related.shared.util.manager.value_related.StorageEntryConverterDataManager.getScaleByVolumeType;

@Component
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
