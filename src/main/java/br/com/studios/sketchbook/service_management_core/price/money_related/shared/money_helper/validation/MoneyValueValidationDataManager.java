package br.com.studios.sketchbook.service_management_core.price.money_related.shared.money_helper.validation;

import br.com.studios.sketchbook.service_management_core.price.money_related.domain.model.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MoneyValueValidationDataManager {

    /// Verifica se estamos usando tipos diferentes de moeda
    public static void validateCurrencyCompatibility(Money valueA, Money valueB) {
        if (
                !valueA.getCurrency().equals(valueB.getCurrency())
                        || !isValidCurrency(valueA.getCurrency())
                        || !isValidCurrency(valueB.getCurrency())
        ) {
            throw new IllegalArgumentException(
                    "Não é possível operar valores de moedas inválidas: "
                            + valueA.getCurrency() + " x " + valueB.getCurrency()
            );
        }
    }

    /**
     * Valida se o código da currency está no formato ISO 4217 (AAA).
     * Apenas verifica o padrão de 3 letras maiúsculas.
     */
    public static boolean isValidCurrency(String code) {
        if (code == null) return false;
        return code.matches("^[A-Z]{3}$");
    }

    /**
     * Verifica se podemos realizar uma subtração e haver um resto maior ou igual a 0
     *
     * @param minuend    Valor a ser subtraído, precisa ser o primeiro dos parâmetros
     * @param subtrahend Valor que irá subtrair, é como de costume o segundo parâmetro na matemática,
     *                   assim como nesta função
     */
    public static void validateSubtractionAvailable(Money minuend, Money subtrahend) {
        // Comparação mais rápida usando double
        if (minuend.getValue().doubleValue() < subtrahend.getValue().doubleValue()) {
            throw new ArithmeticException(
                    "O minuendo (valor a ser subtraído) precisa ser maior que o subtraendo " +
                            "para que o resultado seja maior ou igual a 0: " +
                            minuend.getValue() + " - " + subtrahend.getValue()
            );
        }
    }

    /**
     * Valida se o fator de multiplicação é válido (não nulo, não negativo).
     *
     * @param factor BigDecimal a ser validado
     */
    public static void validateMultiplicationFactor(BigDecimal factor) {
        if (factor == null) {
            throw new IllegalArgumentException("Fator de multiplicação não pode ser nulo.");
        }
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Fator de multiplicação não pode ser negativo.");
        }
    }

}
