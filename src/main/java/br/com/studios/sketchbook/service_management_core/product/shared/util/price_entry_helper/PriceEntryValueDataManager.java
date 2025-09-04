package br.com.studios.sketchbook.service_management_core.product.shared.util.price_entry_helper;

import br.com.studios.sketchbook.service_management_core.price.domain.Money;
import br.com.studios.sketchbook.service_management_core.price_modifying.shared.PriceDiscountInterestManager;
import br.com.studios.sketchbook.service_management_core.product.domain.model.aux_model.PriceEntry;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceEntryValueDataManager {

    private final PriceDiscountInterestManager valueManager;

    public PriceEntryValueDataManager() {
        valueManager = new PriceDiscountInterestManager();
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
