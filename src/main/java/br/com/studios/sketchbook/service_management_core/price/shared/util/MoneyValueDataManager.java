package br.com.studios.sketchbook.service_management_core.price.shared.util;

import br.com.studios.sketchbook.service_management_core.price.domain.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MoneyValueDataManager {

    private final MoneyValueValidationDataManager validationManager;

    public MoneyValueDataManager() {
        validationManager = new MoneyValueValidationDataManager();
    }

    /// Realiza uma soma entre dois objetos do tipo Money e retorna o resultado
    public BigDecimal add(Money valueA, Money valueB) {
        validationManager.validateCurrencyCompatibility(valueA, valueB);
        return valueA.getValue().add(valueB.getValue());
    }

    /**
     * Realiza uma subtração entre dois objetos do tipo Money e retorna o resultado
     * <p>
     * Diferente de uma soma, onde a ordem dos fatores não importa, aqui é de extrema importância.
     * Isso porque a ordém irá ajudar a prevenir um resultado negativo erróneo
     *
     * @param minuend    Valor a ser subtraído, precisa ser o primeiro dos parâmetros
     * @param subtrahend Valor que irá subtrair, é como de costume o segundo parâmetro na matemática,
     *                   assim como nesta função
     */
    public BigDecimal subtract(Money minuend, Money subtrahend) {
        validationManager.validateCurrencyCompatibility(minuend, subtrahend);
        validationManager.validateSubtractionAvailable(minuend, subtrahend);

        return minuend.getValue().subtract(subtrahend.getValue());
    }

    /**
     * Realiza a multiplicação de um valor Money por um fator positivo.
     *
     * @param value Money a ser multiplicado
     * @param factor BigDecimal fator de multiplicação
     * @return BigDecimal resultado da multiplicação
     */
    public BigDecimal multiply(Money value, BigDecimal factor) {
        validationManager.validateMultiplicationFactor(factor);
        return value.getValue().multiply(factor);
    }

}