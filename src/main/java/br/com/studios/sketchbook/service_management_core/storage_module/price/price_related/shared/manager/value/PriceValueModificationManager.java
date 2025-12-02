package br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.manager.value;

import br.com.studios.sketchbook.service_management_core.storage_module.price.money_related.domain.model.Money;
import br.com.studios.sketchbook.service_management_core.storage_module.price.money_related.shared.manager.core.MoneyDataManagementCore;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.enums.AdjustmentType;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.model.PriceModifier;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.manager.validation.PriceModValidationManager;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;

import java.math.BigDecimal;

import static br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.util.manager.value_related.StorageEntryConverterDataManager.getScaleByVolumeType;
import static br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.util.manager.value_related.StorageEntryConverterDataManager.toHumanReadable;

public class PriceValueModificationManager {

    private final MoneyDataManagementCore moneyManager;
    private final PriceModValidationManager validationManager;

    public PriceValueModificationManager() {
        moneyManager = new MoneyDataManagementCore();
        validationManager = new PriceModValidationManager();
    }


    /// Retorna o dinheiro já aplicado com juros ou desconto de acordo com o modificador interno
    public BigDecimal getTotalSumModApplied(PriceEntry entry, PriceModifier modifier, VolumeType volumeType,long amount) {

        return getPriceModifyingApplied(                //Obtém o preço modificado com os juros ou desconto
                modifier,                                  //Entrada do preço do produto
                amount,                                 //quantidade para contexto
                new Money(                              //Converte para tipo correto para interpretação
                        getTotalSum(                    //Obtemos a quantidade total que queremos calcular
                                entry,
                                volumeType,
                                amount
                        ),
                        entry.getPrice().getCurrency()  //Tipo de moeda para garantir tipagem interna correta
                )
        );

    }

    /// Obtém o dinheiro com o desconto ou juros já aplicado
    public BigDecimal getPriceModifyingApplied(PriceModifier modifier, long amount, Money price) {

        return applyPriceMod(
                modifier,
                amount,
                price
        );
    }

    /**
     * Retorna o preço total de um produto com base na quantidade passada.
     * <p>
     * Multiplica a quantidade passada com o preço para encontrar o preço a ser retornado
     */
    public BigDecimal getTotalSum(PriceEntry entry, VolumeType volumeType, long quantity) {

        //Realizamos uma multiplicação com o valor money do produto
        return moneyManager.multiply(
                entry.getPrice(),
                toHumanReadable(                    //Convertemos para centesimal ex:1550Litros vira 1.55
                        quantity,                   //Quantidade raw
                        getScaleByVolumeType(       //Passamos a escala para a conversão
                                volumeType
                        )
                )
        ).getPrice();//Retornamos apenas o valor

    }

    /**
     * Aplica a modificação de preço no preço de um produto
     *
     * @param amount Quantidade de produto que estamos a considerar
     * @param price  preço do produto a ter a modificação feita
     * @return Retornamos o preço já modificado
     */
    public BigDecimal applyPriceMod(PriceModifier modifier, long amount, Money price) {

        //cópia de preço, para evitar sobre-escrita incorreta de valores
        Money modifiedPrice = price.cpy();
        //usa um valor fora de escopo local para garantir um retorno correto
        BigDecimal toReturn = null;

        //Quantidade necessaria para executar um desconto
        long amountNecessary = modifier.getQuantityOfVolumeNecessary();

        //Se podemos atualizar o preço
        if (validationManager.validatePriceModifyingAppliance(modifier, amount, amountNecessary)) {

            //Se for para aplicar desconto aplicamos um desconto
            if (modifier.getType() == AdjustmentType.DISCOUNT) {
                //Atualizamos o valor do preço
                toReturn = applyDiscount(modifier.getPercentage(), modifiedPrice);

            } else if (modifier.getType() == AdjustmentType.INTEREST) {
                //Atualizamos o valor do preço
                toReturn = applyInterest(modifier.getPercentage(), modifiedPrice);
            }

        } else {//caso não possamos retornamos o preço inalterado
            return toReturn;
        }

        return toReturn;
    }

    /// Aplica juros e retorna o valor já convertido
    private BigDecimal applyInterest(BigDecimal interest, Money money) {
        return moneyManager.calculateInterest(money, interest);
    }

    /// Aplica desconto e retorna o valor correto do desconto
    private BigDecimal applyDiscount(BigDecimal discount, Money money) {
        return moneyManager.calculateDiscount(money, discount);//retornamos apenas o valor do desconto
    }

}
