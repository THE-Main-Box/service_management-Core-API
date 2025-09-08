package br.com.studios.sketchbook.service_management_core.price.shared.util.money_helper.core;

import br.com.studios.sketchbook.service_management_core.price.domain.model.Money;
import br.com.studios.sketchbook.service_management_core.price.shared.dto.MoneyPercentDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyDataManagementCoreTest {


    private static Money moneyA;

    private static Money moneyB;

    private static Money moneyC;

    /// Manager de dados da classe Money
    private static MoneyDataManagementCore moneyManager;

    @BeforeAll
    static void setup() {
        moneyManager = new MoneyDataManagementCore();
    }

    @AfterEach
    public void end() {
        System.out.println("//-//-//-//-//-//-//-//-//-//-//-");
        System.out.println(moneyA);
        System.out.println(moneyB);
        System.out.println(moneyC);
    }

    @Test
    public void testSub() {
        // Moedas do mesmo tipo
        moneyA = new Money(20d, "brl");//Moeda a ser subtraída precisa ter um valor maior
        moneyB = new Money(15d, "brl");//Moeda que subtrai precisa ser menor que a subtraída

        moneyC = moneyManager.subtract(moneyA, moneyB);//Armazenamos para verificar depois

        assertEquals(5d, moneyC.getValue().doubleValue());

    }

    @Test
    public void testSubAll() {
        // Moedas do mesmo tipo

        //Moeda a ser subtraída precisa ter um valor maior que a subtração
        moneyA = new Money(100d, "brl");

        List<Money> moneyList = new ArrayList<>(Arrays.asList(
                new Money(10d, "brl"),
                new Money(10d, "brl"),
                new Money(10d, "brl"),
                new Money(10d, "brl"),
                new Money(10d, "brl")
        ));

        moneyC = moneyManager.subtractSeq(moneyA, moneyList);//Armazenamos para verificar depois

        assertEquals(50d, moneyC.getValue().doubleValue());

    }

    @Test
    public void testSum() {
        // Moedas do mesmo tipo
        moneyA = new Money(20d, "brl");
        moneyB = new Money(15d, "brl");

        moneyC = moneyManager.add(moneyA, moneyB);//Armazenamos para verificar depois

        assertEquals(35d, moneyC.getValue().doubleValue());
    }

    @Test
    public void testSumAll() {
        // Moedas do mesmo tipo
        List<Money> moneyList = new ArrayList<>(Arrays.asList(
                new Money(10d, "brl"),
                new Money(10d, "brl"),
                new Money(10d, "brl"),
                new Money(10d, "brl"),
                new Money(10d, "brl")
        ));

        moneyC = moneyManager.addAll(moneyList);//Armazenamos para verificar depois

        assertEquals(50d, moneyC.getValue().doubleValue());

    }

    @Test
    public void testMulti() {
        // Moedas do mesmo tipo
        moneyA = new Money(20d, "brl");

        moneyC = moneyManager.multiply(
                moneyA,
                new BigDecimal(2)
        );//Armazenamos para verificar depois

        assertEquals(40d, moneyC.getValue().doubleValue());

    }

    @Test
    public void testMultiAll() {
        // Moedas do mesmo tipo
        moneyA = new Money(20d, "brl");

        List<BigDecimal> factorList = new ArrayList<>(
                Arrays.asList(
                        new BigDecimal(2),
                        new BigDecimal(2),
                        new BigDecimal(2)
                )
        );

        moneyC = moneyManager.multiplySeq(
                moneyA,
                factorList
        );//Armazenamos para verificar depois

        assertEquals(160d, moneyC.getValue().doubleValue());

    }

    @Test
    public void testApplyPercentage() {
        // applyPercentage retorna a parcela referente à porcentagem (value * percent/100)
        moneyA = new Money(100d, "brl");
        moneyC = new Money();
        moneyC.setCurrency("brl");

        moneyC.setValue(
                moneyManager.applyPercentage(
                        moneyA,
                        new BigDecimal(10)
                )
        );

        // 10% de 100 = 10
        assertEquals(10d, moneyC.getValue().doubleValue());
        assertEquals(100d, moneyA.getValue().doubleValue());
    }

    @Test
    public void testApplyPercentageSeq_sequentialBehavior() {
        // applyPercentageAll aplica porcentagens em sequência sobre o mesmo ref:
        // primeiro: 10% de 100 = 10 -> ref = 10
        // segundo: 20% de ref(10) = 2 -> resultado final = 2
        moneyA = new Money(100d, "brl");

        List<MoneyPercentDTO> dtos = new ArrayList<>(Arrays.asList(
                new MoneyPercentDTO(moneyA, new BigDecimal(10)), // 10% de 100 = 10
                new MoneyPercentDTO(moneyA, new BigDecimal(20))  // 20% de 10 = 2
        ));

        moneyC = moneyManager.applyPercentageSeq(dtos);

        assertEquals(2d, moneyC.getValue().doubleValue());
    }

    @Test
    public void testCalculateInterest() {
        // Juros simples: result = value * (1 + percent/100)
        moneyA = new Money(100d, "brl");
        moneyC = new Money();
        moneyC.setCurrency("brl");

        moneyC.setValue(
                moneyManager.calculateInterest(
                        moneyA,
                        new BigDecimal(10)
                )// 10%
        );

        // 100 * 1.10 = 110
        assertEquals(110d, moneyC.getValue().doubleValue());
    }

    @Test
    public void testCalculateDiscount() {
        // Desconto: result = value * (1 - percent/100)
        moneyA = new Money(100d, "brl");

        moneyC = new Money();
        moneyC.setCurrency("brl");

        moneyC.setValue(
                moneyManager.calculateDiscount(
                        moneyA,
                        new BigDecimal(10)
                )// 10%
        );

        // 100 * 0.90 = 90
        assertEquals(90d, moneyC.getValue().doubleValue());
    }


}
