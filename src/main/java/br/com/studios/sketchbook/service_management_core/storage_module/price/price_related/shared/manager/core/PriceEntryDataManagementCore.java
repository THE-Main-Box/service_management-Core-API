package br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.manager.core;

import br.com.studios.sketchbook.service_management_core.storage_module.price.money_related.domain.model.Money;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.model.PriceModifier;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.manager.objectify.PriceEntryInitDataManager;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.enums.AdjustmentTrigger;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.enums.AdjustmentType;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.storage_module.product.shared.enums.VolumeType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

public class PriceEntryDataManagementCore {

    private final PriceEntryInitDataManager initManager;
    private final PriceModManagementCore priceManager;


    public PriceEntryDataManagementCore() {
        this.initManager = new PriceEntryInitDataManager();
        this.priceManager = new PriceModManagementCore();
    }

    /// Inicia a entrada de preço do produto
    public void initEntry(PriceEntry entry, double price, String currency) {
        initManager.initEntry(entry, price, currency);
    }

    /**
     * Inicia um modificador de preço
     *
     * @param quantity       Quantidade do produto
     *                       que será usada de acordo com um contexto
     *                       para determinar a aplicação do modificador de preço
     * @param raw            Se a quantidade é raw ou não, se não escala conforme o tipo de volume
     * @param trigger        Contexto do uso da quantidade do produto
     * @param percentage     Porcentagem do preço do produto a ser modificado, se deve ser aumentada ou não
     * @param adjustmentType Diz se devemos somar ou remover a porcentagem como "juros" ou "desconto"
     */
    public PriceModifier initPriceMod(
            VolumeType volumeType,
            long quantity,
            boolean raw,
            AdjustmentTrigger trigger,
            double percentage,
            AdjustmentType adjustmentType
    ) {
        return priceManager.initPriceMod(
                volumeType,
                quantity,
                raw,
                trigger,
                new BigDecimal(percentage),
                adjustmentType
        );
    }

    /// Retorna o dinheiro já aplicado com juros ou desconto de acordo com o modificador interno
    public BigDecimal getTotalSumModApplied(PriceEntry entry, PriceModifier mod, VolumeType volumeType, long amount) {
        return priceManager.getTotalSumModApplied(entry, mod, volumeType, amount);

    }

    /**
     * Retorna o preço total de um produto com base na quantidade passada
     *
     * @param entry  Entrada de preço do produto
     * @param amount Quantidade em tipo raw da quantidade a ser calculada
     */
    public BigDecimal getTotalSum(PriceEntry entry, VolumeType volumeType, long amount) {
        return priceManager.getTotalSum(entry, volumeType, amount);
    }

    /// Obtém o dinheiro com o desconto ou juros já aplicado
    public BigDecimal getPriceModifyingApplied(PriceModifier modifier, long amount, Money price) {
        return priceManager.getPriceModifyingApplied(
                modifier,
                amount,
                price
        );
    }

}
