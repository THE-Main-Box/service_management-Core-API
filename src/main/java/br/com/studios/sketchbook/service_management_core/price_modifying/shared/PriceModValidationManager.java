package br.com.studios.sketchbook.service_management_core.price_modifying.shared;

import br.com.studios.sketchbook.service_management_core.price_modifying.model.PriceModifier;
import br.com.studios.sketchbook.service_management_core.price_modifying.shared.enums.AdjustmentTrigger;
import org.springframework.stereotype.Component;

@Component
public class PriceModValidationManager {

    /**
     * Verifica se podemos aplicar um desconto
     *
     * @param modifier modificador de preço contendo parâmetros importantes
     * @param amount Quantidade presente que iremos considerar
     * @param amountNecessary Quantidade necessária relativa para aplicar o desconto
     * */
    public static boolean validatePriceModifyingAppliance(PriceModifier modifier, long amount, long amountNecessary) {
        if (modifier.getTrigger() == AdjustmentTrigger.APPLY_ON_MINIMUM) {
            //Se a quantidade que estamos a passar for menor que a quantidade minima
            return amountNecessary <= amount;

        } else if (modifier.getTrigger() == AdjustmentTrigger.APPLY_UNTIL) {
            //Se a quantidade que estamos a passar ultrapassar a quantidade máxima
            return amount <= amountNecessary;

        } else if (modifier.getTrigger() == AdjustmentTrigger.APPLY_WHEN_MULTIPLE) {
            //Se a quantidade que estamos a passar for maior ou igual um multiplo do necessário
            return amount % amountNecessary == 0;
        } else if (modifier.getTrigger() == AdjustmentTrigger.APPLY_ALWAYS) {
            return true;
        }


        return false;

    }

}
