package br.com.studios.sketchbook.service_management_core.price.shared.util;

import br.com.studios.sketchbook.service_management_core.price.domain.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MoneyDataManagementCore {

    private final MoneyValueDataManager valueManager;

    private BigDecimal valueBuffer;
    private Money toReturnBuffer;

    public MoneyDataManagementCore() {
        valueManager = new MoneyValueDataManager();
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
            throw new RuntimeException(
                    "Moedas de tipos diferentes," +
                            " sugiro adicionar uma camada de abstração para garantir o mesmo tipo de moeda " +
                            "usando uma conversão de moedas simples"
            );
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

            if(values.size() > 1 && i == 0) continue;

            try {
                //Adicionamos usando o primeiro valor como referência
                sum = valueManager.add(
                        ref, //Usamos a referencia como base para o campo
                        values.get(i) //Passamos o valor atual como soma
                );

                ref.setValue(sum); //Atualizamos a referencia para conter a soma dos números corretos

            } catch (IllegalArgumentException e) {

                throw new RuntimeException(
                        "Moedas de tipos diferentes," +
                                " sugiro adicionar uma camada de abstração para garantir o mesmo tipo de moeda " +
                                "usando uma conversão de moedas simples"
                );

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
                throw new RuntimeException(
                        "Moedas de tipos diferentes," +
                                " sugiro adicionar uma camada de abstração para garantir o mesmo tipo de moeda " +
                                "usando uma conversão de moedas simples"
                );
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
    public Money subtractAll(Money toSubtract, List<Money> values) {
        if (values == null || values.isEmpty() || values.size() == 1) {
            throw new IllegalArgumentException("A lista de valores é imprópria para execução.");
        }

        // Copiamos o primeiro valor como referência (minuendo inicial)
        Money ref = toSubtract.cpy();
        BigDecimal result;

        // Percorremos a lista
        for (int i = 0; i < values.size(); i++) {
            try {
                // Realizamos a subtração usando a referência como base
                result = valueManager.subtract(ref, values.get(i));

                // Atualizamos a referência com o valor resultante
                ref.setValue(result);

            } catch (Exception e) {
                if (e instanceof IllegalArgumentException) {
                    throw new RuntimeException(
                            "Moedas de tipos diferentes," +
                                    " sugiro adicionar uma camada de abstração para garantir o mesmo tipo de moeda " +
                                    "usando uma conversão de moedas simples"
                    );
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

}
