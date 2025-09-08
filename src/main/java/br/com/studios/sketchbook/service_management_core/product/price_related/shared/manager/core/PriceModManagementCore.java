package br.com.studios.sketchbook.service_management_core.product.price_related.shared.manager.core;

import br.com.studios.sketchbook.service_management_core.price.domain.model.Money;
import br.com.studios.sketchbook.service_management_core.product.price_related.shared.enums.AdjustmentTrigger;
import br.com.studios.sketchbook.service_management_core.product.price_related.shared.enums.AdjustmentType;
import br.com.studios.sketchbook.service_management_core.product.price_related.shared.manager.objectify.PriceModInit;
import br.com.studios.sketchbook.service_management_core.product.price_related.shared.manager.value.PriceValueModificationManager;
import br.com.studios.sketchbook.service_management_core.product.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.product.price_related.domain.model.PriceModifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceModManagementCore {
    private final PriceModInit initManager;
    private final PriceValueModificationManager valueManager;

    public PriceModManagementCore() {
        initManager = new PriceModInit();
        valueManager = new PriceValueModificationManager();
    }

    public void initPriceMod(
            PriceEntry entry,
            long quantity,
            boolean raw,
            AdjustmentTrigger trigger,
            BigDecimal percentage,
            AdjustmentType adjustmentType
    ) {
        initManager.initPriceMod(entry, quantity, raw, trigger, percentage, adjustmentType);
    }

    /// Obtém um preço de produto com a modificação de preço, juros e desconto, já aplicado
    public BigDecimal getPriceModifyingApplied(PriceEntry entry, long amount, Money price) {
        return valueManager.getPriceModifyingApplied(entry, amount, price);
    }

    /// Obtém o valor total de uma soma de produtos e aplica uma modificação, com desconto ou juros
    public BigDecimal getTotalSumModApplied(PriceEntry entry, long amount) {
        return valueManager.getTotalSumModApplied(entry, amount);
    }

    /// Obtém a soma total de um produto em relação a quantidade passada
    public BigDecimal getTotalSum(PriceEntry entry, long quantity) {
        return valueManager.getTotalSum(entry, quantity);
    }

    /// Obtém apenas o valor do desconto
    public BigDecimal applyPriceMod(PriceModifier modifier, long amount, Money price) {
        return valueManager.applyPriceMod(modifier, amount, price);
    }

}
