package br.com.studios.sketchbook.service_management_core.product.shared.util.price_entry_helper;

import br.com.studios.sketchbook.service_management_core.price.domain.Money;
import br.com.studios.sketchbook.service_management_core.product.domain.model.aux_model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.product.domain.model.aux_model.PriceModifier;
import br.com.studios.sketchbook.service_management_core.product.infra.enums.AdjustmentTrigger;
import br.com.studios.sketchbook.service_management_core.product.infra.enums.AdjustmentType;
import br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.value_related.StorageEntryConverterDataManager;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.value_related.StorageEntryConverterDataManager.getScaleByVolumeType;
import static br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.value_related.StorageEntryConverterDataManager.toHumanReadable;

@Component
public class PriceEntryDataManagementCore {

    private final PriceEntryInitDataManager initManager;
    private final PriceEntryValueDataManager valueManager;

    public PriceEntryDataManagementCore() {
        this.initManager = new PriceEntryInitDataManager();
        this.valueManager = new PriceEntryValueDataManager();
    }

    /// Inicia a entrada de preço do produto
    public void initEntry(PriceEntry entry, double price, String currency) {
        initManager.initEntry(entry, price, currency);
    }

    /**
     * Inicia um modificador de preço
     *
     * @param entry          Entrada de preço de um produto
     * @param quantity       Quantidade do produto
     *                       que será usada de acordo com um contexto
     *                       para determinar a aplicação do modificador de preço
     * @param raw            Se a quantidade é raw ou não, se não escala conforme o tipo de volume
     * @param trigger        Contexto do uso da quantidade do produto
     * @param percentage     Porcentagem do preço do produto a ser modificado, se deve ser aumentada ou não
     * @param adjustmentType Diz se devemos somar ou remover a porcentagem como "juros" ou "desconto"
     */
    public void initPriceMod(
            PriceEntry entry,
            long quantity,
            boolean raw,
            AdjustmentTrigger trigger,
            double percentage,
            AdjustmentType adjustmentType
    ) {
        initManager.initPriceMod(
                entry,
                quantity,
                raw,
                trigger,
                new BigDecimal(percentage),
                adjustmentType
        );
    }

    /// Retorna o dinheiro já aplicado com juros ou desconto de acordo com o modificador interno
    public Money getTotalSumModApplied(PriceEntry entry, long amount) {
        if (entry.getModifier() == null) throw new IllegalArgumentException(
                "Precisa de modificador para usar essa funcionalidade"
        );

        return getPriceModifyingApplied(                //Obtém o preço modificado com os juros ou desconto
                entry,                                  //Entrada do preço do produto
                amount,                                 //quantidade para contexto
                new Money(                              //Converte para tipo correto para interpretação
                        getTotalSum(                    //Obtemos a quantidade total que queremos calcular
                                entry,
                                amount
                        ),
                        entry.getPrice().getCurrency()  //Tipo de moeda para garantir tipagem interna correta
                )
        );

    }

    /**
     * Retorna o preço total de um produto com base na quantidade passada
     *
     * @param entry  Entrada de preço do produto
     * @param amount Quantidade em tipo raw da quantidade a ser calculada
     */
    public BigDecimal getTotalSum(PriceEntry entry, long amount) {
        return valueManager.getTotalSum(entry, amount);
    }

    /// Obtém o dinheiro com o desconto ou juros já aplicado
    public Money getPriceModifyingApplied(PriceEntry entry, long amount, Money price) {

        return valueManager.applyPriceMod(
                entry.getModifier(),
                amount,
                price
        );
    }

}
