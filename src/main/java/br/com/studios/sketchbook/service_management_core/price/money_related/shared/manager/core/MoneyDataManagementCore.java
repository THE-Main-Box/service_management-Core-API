package br.com.studios.sketchbook.service_management_core.price.money_related.shared.manager.core;

import br.com.studios.sketchbook.service_management_core.price.money_related.domain.model.Money;
import br.com.studios.sketchbook.service_management_core.price.money_related.shared.dto.MoneyPercentDTO;
import br.com.studios.sketchbook.service_management_core.price.money_related.shared.manager.value.MoneyValueDataManager;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MoneyDataManagementCore {

    private final MoneyValueDataManager valueManager;

    private Money toReturnBuffer;

    public MoneyDataManagementCore() {
        valueManager = new MoneyValueDataManager();
    }

    public Money add(Money valueA, double valueB) {
        return add(
                valueA,
                new Money(
                        valueB,
                        valueA.getCurrency()
                )
        );
    }

    /**
     * Retorna um novo objeto do tipo Money caso as moedas sejam do mesmo tipo
     *
     * @param valueA primeiro valor, é também aquele que irá determinar o tipo de moeda
     * @param valueB segundo valor, é o que irá somar
     */
    public Money add(Money valueA, Money valueB) {
        try {
            //Atualizamos o buffer de retorno
            toReturnBuffer = new Money(
                    valueManager.add(//Realizamos a soma, validação e implementamos logo em seguida no buffer
                            valueA,
                            valueB
                    ),
                    valueA.getCurrency()
                    //Como está garantido que o tipo de moeda é o mesmo,
                    // não precisamos nos preocupar e simplesmente escolher uma delas,
                    // o valueA me parece o mais intuitivo para isso
            );

        } catch (IllegalArgumentException e) {
            toReturnBuffer = null;
            throw new RuntimeException("Moedas de tipos incorretos, sugiro verificação");
        }

        return toReturnBuffer;
    }

    /// Realizamos uma soma com multiplos valores
    public Money addAll(List<Money> values) {
        if (values == null || values.isEmpty() || values.size() == 1) {
            throw new IllegalArgumentException("A lista de valores imprópria para execução");
        }

        //Copiamos o primeiro valor para termos uma referencia dele
        Money ref = values.get(0).cpy();
        //Soma de todos os objetos
        BigDecimal sum;

        //Percorremos a lista
        for (int i = 0; i < values.size(); i++) {

            if (values.size() > 1 && i == 0) continue;

            try {
                //Adicionamos usando o primeiro valor como referência
                sum = valueManager.add(
                        ref, //Usamos a referencia como base para o campo
                        values.get(i) //Passamos o valor atual como soma
                );

                ref.setValue(sum); //Atualizamos a referencia para conter a soma dos números corretos

            } catch (IllegalArgumentException e) {

                throw new RuntimeException("Moedas de tipos incorretos, sugiro verificação");

            }

        }

        return ref;
    }

    /**
     * Subtrai dois valores em ordem, desde que sejam do mesmo tipo de moeda
     *
     * @param minuend    Valor a ser subtraído, precisa ser o primeiro dos parâmetros
     * @param subtrahend Valor que irá subtrair, é como de costume o segundo parâmetro na matemática,
     *                   assim como nesta função
     * @return retornamos um novo objeto que é referente a subtração correta dos dois valores
     */
    public Money subtract(Money minuend, Money subtrahend) {

        try {

            toReturnBuffer = new Money(//Realizamos a subtração, validação e implementamos logo em seguida no buffer
                    valueManager.subtract(
                            minuend,
                            subtrahend
                    ),
                    minuend.getCurrency()
                    //Como está garantido que o tipo de moeda é o mesmo,
                    // não precisamos nos preocupar e simplesmente escolher uma delas,
                    // o minuendo me parece o mais intuitivo para isso
            );

        } catch (Exception e) {
            toReturnBuffer = null;

            if (e instanceof IllegalArgumentException) {
                throw new RuntimeException("Moedas de tipos incorretos, sugiro verificação");
            }

            if (e instanceof ArithmeticException) {
                throw new RuntimeException(
                        "Houve um erro na subtração de dinheiro," +
                                " com um valor incorreto," +
                                " obtivemos um resultado negativo"
                );
            }

        }

        return toReturnBuffer;
    }

    /// Subtrai múltiplos valores do tipo Money em sequência,
    /// desde que todos sejam da mesma moeda e o resultado não seja negativo.
    public Money subtractSeq(Money toSubtract, List<Money> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("A lista de valores é imprópria para execução.");
        }

        // Copiamos o primeiro valor como referência (minuendo inicial)
        Money ref = toSubtract.cpy();
        BigDecimal result;

        // Percorremos a lista
        for (Money value : values) {
            try {
                // Realizamos a subtração usando a referência como base
                result = valueManager.subtract(ref, value);

                // Atualizamos a referência com o valor resultante
                ref.setValue(result);

            } catch (Exception e) {
                if (e instanceof IllegalArgumentException) {
                    throw new RuntimeException("Moedas de tipos incorretos, sugiro verificação");

                }
                if (e instanceof ArithmeticException) {
                    throw new RuntimeException(
                            "Houve um erro na subtração de dinheiro," +
                                    " com um valor incorreto," +
                                    " obtivemos um resultado negativo"
                    );
                }
                throw e;
            }
        }

        return ref;
    }


    /**
     * Multiplica um valor Money por um fator positivo,
     * retornando um novo objeto Money com o resultado.
     *
     * @param base   valor Money que será multiplicado
     * @param factor BigDecimal fator de multiplicação (positivo)
     * @return novo Money com o valor multiplicado
     */
    public Money multiply(Money base, BigDecimal factor) {
        try {

            // Atualiza o buffer de retorno
            toReturnBuffer = new Money(
                    valueManager.multiply(
                            base,
                            factor
                    ),
                    base.getCurrency()
            );

        } catch (IllegalArgumentException e) {

            toReturnBuffer = null;

            throw new RuntimeException("Fator de multiplicação inválido: " + factor, e);

        }

        return toReturnBuffer;
    }

    /**
     * Multiplica um valor Money por múltiplos fatores positivos em sequência,
     * retornando o resultado final como um novo objeto Money.
     *
     * @param base    valor Money que será multiplicado
     * @param factors lista de fatores (BigDecimal) a serem aplicados
     * @return novo objeto Money com o valor multiplicado
     */
    public Money multiplySeq(Money base, List<BigDecimal> factors) {
        if (base == null || factors == null || factors.isEmpty()) {
            throw new IllegalArgumentException("Parâmetros inválidos para multiplicação.");
        }
        // Copiamos o valor base para não alterar o original
        Money ref = base.cpy();
        BigDecimal result;

        try {
            result = ref.getValue();

            // Multiplicação sequencial de cada fator
            for (BigDecimal factor : factors) {

                // Atualizamos o resultado a cada multiplicação
                result = valueManager.multiply(
                        new Money(
                                result,
                                ref.getCurrency()
                        ),
                        factor
                );
            }

            // Atualizamos o buffer de retorno
            toReturnBuffer = new Money(result, base.getCurrency());

        } catch (IllegalArgumentException e) {

            toReturnBuffer = null;

            throw new RuntimeException("Erro na multiplicação com múltiplos fatores.", e);

        }

        return toReturnBuffer;

    }

    /**
     * Aplica uma porcentagem usando o DTO (delegando ao valueManager).
     */
    public BigDecimal applyPercentage(Money money, BigDecimal percent) {
        try {
            return valueManager.applyPercentage(money, percent);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao aplicar porcentagem em: " + money, e);
        }
    }

    /**
     * Aplica múltiplas porcentagens sequenciais usando DTOs (delegando ao valueManager).
     */
    public Money applyPercentageSeq(List<MoneyPercentDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            throw new IllegalArgumentException("Lista de DTOs inválida para aplicação de porcentagem.");
        }

        Money ref = dtoList.get(0).money().cpy();
        BigDecimal result;

        try {
            for (MoneyPercentDTO dto : dtoList) {
                result = valueManager.applyPercentage(
                        ref,
                        dto.percent()
                );

                ref.setValue(result);
            }

            toReturnBuffer = ref;

        } catch (IllegalArgumentException e) {

            toReturnBuffer = null;

            throw new RuntimeException("Erro ao aplicar múltiplas porcentagens.", e);
        }

        return toReturnBuffer;

    }

    /**
     * Calcula juros simples delegando para applyPercentage.
     * Juros de X% = aplicar (100 + X)% sobre o valor.
     */
    public BigDecimal calculateInterest(Money money, BigDecimal percent) {
        return applyPercentage(
                money,
                BigDecimal.valueOf(100).add(percent) // 100 + X
        );
    }

    /**
     * Calcula desconto delegando para applyPercentage.
     * Desconto de X% = aplicar (100 - X)% sobre o valor.
     */
    public BigDecimal calculateDiscount(Money money, BigDecimal percent) {
        return applyPercentage(
                money,
                BigDecimal.valueOf(100).subtract(percent)
        ); // 100 - X
    }

}
