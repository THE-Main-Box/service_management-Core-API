package br.com.studios.sketchbook.service_management_core.price.shared.util;

import br.com.studios.sketchbook.service_management_core.price.domain.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
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

        moneyC = moneyManager.subtractAll(moneyA, moneyList);//Armazenamos para verificar depois

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

        moneyC = moneyManager.multiplyAll(
                moneyA,
                factorList
        );//Armazenamos para verificar depois

        assertEquals(160d, moneyC.getValue().doubleValue());

    }

}
