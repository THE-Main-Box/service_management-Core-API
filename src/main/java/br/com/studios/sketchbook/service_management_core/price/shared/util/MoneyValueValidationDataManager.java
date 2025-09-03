package br.com.studios.sketchbook.service_management_core.price.shared.util;

import br.com.studios.sketchbook.service_management_core.price.domain.Money;
import org.springframework.stereotype.Component;

@Component
public class MoneyValueValidationDataManager {

    /// Verifica se estamos usando tipos diferentes de moeda
    public void validateCurrencyCompatibility(Money valueA, Money valueB) {
        if (!valueA.getCurrency().equals(valueB.getCurrency())) {
            throw new IllegalArgumentException(
                    "Não é possível operar valores de moedas diferentes: "
                            + valueA.getCurrency() + " x " + valueB.getCurrency()
            );
        }
    }

    /**
     * Verifica se podemos realizar uma subtração e haver um resto maior ou igual a 0
     *
     * @param minuend    Valor a ser subtraído, precisa ser o primeiro dos parâmetros
     * @param subtrahend Valor que irá subtrair, é como de costume o segundo parâmetro na matemática,
     *                   assim como nesta função
     */
    public void validateSubtractionAvailable(Money minuend, Money subtrahend) {
        //Verifica se podemos sequer realizar o calculo
        this.validateCurrencyCompatibility(minuend, subtrahend);

        if (minuend.getValue().compareTo(subtrahend.getValue()) < 0) {
            throw new ArithmeticException(
                    "O minuendo (valor a ser subtraído)" +
                            " precisa ser maior que o subtraendo (valor que irá subtrair)" +
                            " para que o resultado seja maior ou igual a 0: "
                            + minuend.getValue()
                            + subtrahend.getValue()
            );
        }
    }

}
