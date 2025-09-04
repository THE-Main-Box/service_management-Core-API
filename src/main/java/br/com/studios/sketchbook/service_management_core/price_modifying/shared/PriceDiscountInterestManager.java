package br.com.studios.sketchbook.service_management_core.price_modifying.shared;

import br.com.studios.sketchbook.service_management_core.price.domain.Money;
import br.com.studios.sketchbook.service_management_core.price.shared.dto.MoneyPercentDTO;
import br.com.studios.sketchbook.service_management_core.price.shared.util.money_helper.MoneyDataManagementCore;
import br.com.studios.sketchbook.service_management_core.price_modifying.model.PriceModifier;
import br.com.studios.sketchbook.service_management_core.price_modifying.shared.enums.AdjustmentType;
import br.com.studios.sketchbook.service_management_core.product.domain.model.aux_model.PriceEntry;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static br.com.studios.sketchbook.service_management_core.price_modifying.shared.PriceModValidationManager.validatePriceModifyingAppliance;
import static br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.value_related.StorageEntryConverterDataManager.getScaleByVolumeType;
import static br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.value_related.StorageEntryConverterDataManager.toHumanReadable;

@Component
public class PriceDiscountInterestManager {

    private final MoneyDataManagementCore moneyManager;

    public PriceDiscountInterestManager() {
        moneyManager = new MoneyDataManagementCore();
    }

    /**
     * Retorna o preço total de um produto com base na quantidade passada.
     * <p>
     * Multiplica a quantidade passada com o preço para encontrar o preço a ser retornado
     */
    public BigDecimal getTotalSum(PriceEntry entry, long quantity) {

        //Realizamos uma multiplicação com o valor money do produto
        return moneyManager.multiply(
                entry.getPrice(),
                toHumanReadable(                    //Convertemos para centesimal ex:1550Litros vira 1.55
                        quantity,                   //Quantidade raw
                        getScaleByVolumeType(       //Passamos a escala para a conversão
                                entry.getVType()
                        )
                )
        ).getValue();//Retornamos apenas o valor

    }

    /**
     * Aplica a modificação de preço no preço de um produto
     *
     * @param amount Quantidade de produto que estamos a considerar
     * @param price preço do produto a ter a modificação feita
     *
     * @return Retornamos o preço já modificado
     */
    public Money applyPriceMod(PriceModifier modifier, long amount, Money price) {

        //cópia de preço, para evitar sobre-escrita incorreta de valores
        Money modifiedPrice = price.cpy();

        //Quantidade necessaria para executar um desconto
        long amountNecessary = modifier.getQuantityOfVolumeNecessary();

        //Se podemos atualizar o preço
        if (validatePriceModifyingAppliance(modifier, amount, amountNecessary)) {

            //Se for para aplicar desconto aplicamos um desconto
            if (modifier.getType() == AdjustmentType.DISCOUNT) {
                //Atualizamos o valor do preço
                modifiedPrice = applyDiscount(modifier.getPercentage(), modifiedPrice);

            } else if (modifier.getType() == AdjustmentType.INTEREST) {
                //Atualizamos o valor do preço
                modifiedPrice = applyInterest(modifier.getPercentage(), modifiedPrice);
            }

        } else {//caso não possamos retornamos o preço inalterado
            return modifiedPrice;
        }

        return modifiedPrice;
    }

    /// Aplica juros e retorna um novo objeto contendo o valor já convertido
    private Money applyInterest(BigDecimal interest, Money money) {
        return moneyManager.calculateInterest(  //calcula os juros
                new MoneyPercentDTO(            //converte para tipo interpretável
                        money,
                        interest
                )
        );
    }

    /// Aplica desconto e retorna um novo objeto contendo o valor já convertido
    private Money applyDiscount(BigDecimal discount, Money money) {

        return moneyManager.calculateDiscount( //calcula o desconto
                new MoneyPercentDTO(    //converte para um tipo de dado que conseguimos interpretar
                        money,
                        discount
                )
        );//retornamos apenas o valor do desconto

    }

}
