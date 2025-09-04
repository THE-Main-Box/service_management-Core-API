package br.com.studios.sketchbook.service_management_core.product.shared.util.price_entry_helper;

import br.com.studios.sketchbook.service_management_core.price.domain.Money;
import br.com.studios.sketchbook.service_management_core.product.domain.model.aux_model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.product.domain.model.aux_model.PriceModifier;
import br.com.studios.sketchbook.service_management_core.product.infra.enums.AdjustmentTrigger;
import br.com.studios.sketchbook.service_management_core.product.infra.enums.AdjustmentType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.value_related.StorageEntryConverterDataManager.getScaleByVolumeType;

@Component
public class PriceEntryInitDataManager {

    public void initEntry(PriceEntry entry, double price, String currency){
        //Seta o preço enquanto cria um objeto do tipo "Money" com o tipo de moeda desejado
        entry.setPrice(
                new Money(
                        price,
                        currency
                )
        );
    }

    public void initPriceMod(
            PriceEntry entry,
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
                : quantity * getScaleByVolumeType(entry.getVType());

        //Seta o modificador enquanto cria ele, garantindo uma relação bidirectional
        entry.setModifier(
                new PriceModifier(
                        entry,
                        rawQuantity,
                        trigger,
                        percentage,
                        adjustmentType
                )
        );


    }

}
