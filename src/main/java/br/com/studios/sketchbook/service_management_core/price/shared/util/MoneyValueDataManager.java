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
     *
     * @param minuend    Valor a ser subtraído, precisa ser o primeiro dos parâmetros
     * @param subtrahend Valor que irá subtrair
     */
    public BigDecimal subtract(Money minuend, Money subtrahend) {
        validationManager.validateCurrencyCompatibility(minuend, subtrahend);
        validationManager.validateSubtractionAvailable(minuend, subtrahend);

        return minuend.getValue().subtract(subtrahend.getValue());
    }

    /**
     * Realiza a multiplicação de um valor Money por um fator positivo.
     *
     * @param value  Money a ser multiplicado
     * @param factor BigDecimal fator de multiplicação
     * @return BigDecimal resultado da multiplicação
     */
    public BigDecimal multiply(Money value, BigDecimal factor) {
        validationManager.validateMultiplicationFactor(factor);
        return value.getValue().multiply(factor);
    }

    /**
     * Aplica uma porcentagem a um valor Money.
     *
     * @param value      Money base
     * @param percentage BigDecimal representando a porcentagem (ex: 10 → 10%)
     * @return BigDecimal resultado da aplicação da porcentagem
     */
    public BigDecimal applyPercentage(Money value, BigDecimal percentage) {

        // Transformamos a porcentagem em fator usando movePointLeft(2) — evita arredondamento/exceção
        BigDecimal factor = percentage.movePointLeft(2); // 10 -> 0.10

        // Reaproveita multiply (que já valida)
        return multiply(value, factor);
    }

}
