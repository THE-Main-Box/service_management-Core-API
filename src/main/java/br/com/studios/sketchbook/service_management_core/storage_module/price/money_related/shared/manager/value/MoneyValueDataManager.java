package br.com.studios.sketchbook.service_management_core.storage_module.price.money_related.shared.manager.value;

import br.com.studios.sketchbook.service_management_core.storage_module.price.money_related.domain.model.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static br.com.studios.sketchbook.service_management_core.storage_module.price.money_related.shared.manager.validation.MoneyValueValidationDataManager.*;

@Component
public class MoneyValueDataManager {


    /// Realiza uma soma entre dois objetos do tipo Money e retorna o resultado
    public BigDecimal add(Money valueA, Money valueB) {
        validateCurrencyCompatibility(valueA, valueB);
        return valueA.getPrice().add(valueB.getPrice());
    }

    /**
     * Realiza uma subtração entre dois objetos do tipo Money e retorna o resultado
     *
     * @param minuend    Valor a ser subtraído, precisa ser o primeiro dos parâmetros
     * @param subtrahend Valor que irá subtrair
     */
    public BigDecimal subtract(Money minuend, Money subtrahend) {
        validateCurrencyCompatibility(minuend, subtrahend);
        validateSubtractionAvailable(minuend, subtrahend);

        return minuend.getPrice().subtract(subtrahend.getPrice());
    }

    /**
     * Realiza a multiplicação de um valor Money por um fator positivo.
     *
     * @param value  Money a ser multiplicado
     * @param factor BigDecimal fator de multiplicação
     * @return BigDecimal resultado da multiplicação
     */
    public BigDecimal multiply(Money value, BigDecimal factor) {
        validateMultiplicationFactor(factor);
        if (isValidCurrency(value.getCurrency())) {
            return value.getPrice().multiply(factor);
        } else {
            throw new IllegalArgumentException("Campo do tipo de moeda está incorreta");
        }
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

    public BigDecimal convertoCurrency(BigDecimal amount, String from, String to) {
        if (from.equals(to)) {
            return amount; // por enquanto só BRL
        }
        throw new UnsupportedOperationException("Currency conversion not implemented yet");
    }

}
