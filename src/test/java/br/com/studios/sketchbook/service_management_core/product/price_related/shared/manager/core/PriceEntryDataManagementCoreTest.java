package br.com.studios.sketchbook.service_management_core.product.price_related.shared.manager.core;

import br.com.studios.sketchbook.service_management_core.price.money_related.domain.model.Money;
import br.com.studios.sketchbook.service_management_core.price.price_related.shared.manager.core.PriceEntryDataManagementCore;
import br.com.studios.sketchbook.service_management_core.price.money_related.shared.util.money_helper.core.MoneyDataManagementCore;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceModifier;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.Product;
import br.com.studios.sketchbook.service_management_core.price.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.product.storage_related.domain.model.StorageEntry;
import br.com.studios.sketchbook.service_management_core.price.price_related.shared.enums.AdjustmentTrigger;
import br.com.studios.sketchbook.service_management_core.price.price_related.shared.enums.AdjustmentType;
import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;
import br.com.studios.sketchbook.service_management_core.product.storage_related.shared.util.manager.core.StorageEntryDataManagementCore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceEntryDataManagementCoreTest {

    private static Product prod;
    private static StorageEntry storageEntry;
    private static PriceEntry priceEntry;
    private static PriceModifier modifier;

    private static final String currency = "BRL";

    private static PriceEntryDataManagementCore priceManager;
    private static StorageEntryDataManagementCore storageManager;
    private static MoneyDataManagementCore moneyManager;

    @BeforeAll
    public static void setup() {
        priceManager = new PriceEntryDataManagementCore();
        storageManager = new StorageEntryDataManagementCore();
        moneyManager = new MoneyDataManagementCore();
    }

    @Test
    public void initTest() {
        prod = new Product("arroz");
        priceEntry = new PriceEntry();

        priceManager.initEntry(
                priceEntry,
                5.00,
                currency
        );

        assertEquals(
                5.00,
                priceEntry
                        .getPrice()
                        .getValue()
                        .doubleValue()
        );

    }

    @Test
    public void sumTest() {
        prod = new Product("arroz");
        priceEntry = new PriceEntry();

        priceManager.initEntry(
                priceEntry,
                5.00,
                currency
        );

        Money price = priceEntry.getPrice();

        //Determinamos o valor com base no retorno de outro valor
        price.setValue(
                moneyManager.add(
                        price,
                        (5d)
                ).getValue()
        );

        assertEquals(10.00, price.getValue().doubleValue());
    }

    @Test
    public void totalStorageSumTest() {
        prod = new Product("arroz");
        storageEntry = new StorageEntry(prod, VolumeType.KILOGRAM_PER_UNIT);
        priceEntry = new PriceEntry();

        //Iniciamos o preço
        priceManager.initEntry(
                priceEntry,
                5.00,
                currency
        );

        //Iniciamos a quantidade
        storageManager.initEntry(
                storageEntry,
                10L,//10 unidades
                5L,//5 sub-unidades por unidade inteira
                false
        );

        //Atualizamos o preço do produto
        priceEntry.getPrice().setValue(
                priceManager.getTotalSum(//descobrir o preço pela quantidade de produtos no armazém
                        priceEntry,
                        storageEntry.getVolumeType(),
                        storageManager.getAmountAvailableRaw(storageEntry)//Obtemos a quantidade raw do produto
                )
        );

        Money price = priceEntry.getPrice();

        /*Como pegamos a quantidade geral interna de produtos dentro do armazém
         * estamos fazendo 10 * 5, que é 50,
         * porém o preço por sub-unidade é 5 reais
         * é 50 * 5, que é 250.
         *
         * Para descobrir o preço pela unidade inteira é só colocar a quantidad de 1 unidade para vender
         */
        assertEquals(250.00, price.getValue().doubleValue());
    }

    @Test
    public void wholeUnitSumTest() {
        prod = new Product("arroz");
        storageEntry = new StorageEntry(prod, VolumeType.KILOGRAM_PER_UNIT);
        priceEntry = new PriceEntry();

        //Iniciamos o preço
        priceManager.initEntry(
                priceEntry,
                5.00,
                currency
        );

        //Iniciamos a quantidade
        storageManager.initEntry(
                storageEntry,
                10L,//10 unidades
                5L,//5 sub-unidades por unidade inteira
                false
        );

        //Atualizamos o preço do produto
        priceEntry.getPrice().setValue(
                priceManager.getTotalSum(                //descobrir o preço pela quantidade de produtos no armazém
                        priceEntry,
                        storageEntry.getVolumeType(),
                        storageEntry.getQuantityPerUnit()//Obtemos a quantidade raw do produto por unidade,
                        // já que queremos saber quanto é o preço da unidade inteira
                )
        );

        Money price = priceEntry.getPrice();

        /*Como estamos tentando descobrir o preço do produto como unidade inteira
         * é só multiplicar pela quantidade por unidade que é 5, então seria 5*5, que é o mesmo que 25.00 reais
         * Para descobrir o preço pela unidade inteira é só colocar a quantidad de 1 unidade para vender
         */
        assertEquals(25.00, price.getValue().doubleValue());
    }

    @Test
    public void discountTest() {
        prod = new Product("arroz");
        storageEntry = new StorageEntry(prod, VolumeType.KILOGRAM_PER_UNIT);
        priceEntry = new PriceEntry();

        //Iniciamos o preço
        priceManager.initEntry(
                priceEntry,
                5.00,
                currency
        );

        modifier = priceManager.initPriceMod(
                storageEntry.getVolumeType(),
                20,
                true,
                AdjustmentTrigger.APPLY_ON_MINIMUM,
                20,
                AdjustmentType.DISCOUNT
        );


        //Iniciamos a quantidade
        storageManager.initEntry(
                storageEntry,
                10L,//10 unidades
                5L,//5 sub-unidades por unidade inteira
                false
        );

        //Atualizamos o preço do produto
        priceEntry.getPrice().setValue(
                priceManager.getTotalSumModApplied(
                        priceEntry,
                        modifier,
                        storageEntry.getVolumeType(),
                        storageEntry.getQuantityPerUnit()
                )
        );

        Money price = priceEntry.getPrice();

        /*Como estamos tentando descobrir o preço do produto como unidade inteira
         * é só multiplicar pela quantidade por unidade que é 5, então seria 5*5, que é o mesmo que 25.00 reais
         * Para descobrir o preço pela unidade inteira é só colocar a quantidad de 1 unidade para vender
         */
        assertEquals(20.00, price.getValue().doubleValue());
    }

    @Test
    public void interestTest() {
        prod = new Product("arroz");
        storageEntry = new StorageEntry(prod, VolumeType.KILOGRAM_PER_UNIT);
        priceEntry = new PriceEntry();

        //Iniciamos o preço
        priceManager.initEntry(
                priceEntry,
                5.00,
                currency
        );

        modifier = priceManager.initPriceMod(
                storageEntry.getVolumeType(),
                20,
                true,
                AdjustmentTrigger.APPLY_ALWAYS,
                20,
                AdjustmentType.INTEREST
        );


        //Iniciamos a quantidade
        storageManager.initEntry(
                storageEntry,
                10L,//10 unidades
                5L,//5 sub-unidades por unidade inteira
                false
        );

        //Atualizamos o preço do produto
        priceEntry.getPrice().setValue(
                priceManager.getTotalSumModApplied(
                        priceEntry,
                        modifier,
                        storageEntry.getVolumeType(),
                        storageEntry.getQuantityPerUnit()
                )
        );

        Money price = priceEntry.getPrice();

        /*Como estamos tentando descobrir o preço do produto como unidade inteira
         * é só multiplicar pela quantidade por unidade que é 5, então seria 5*5, que é o mesmo que 25.00 reais
         * Para descobrir o preço pela unidade inteira é só colocar a quantidad de 1 unidade para vender
         */
        assertEquals(30.00, price.getValue().doubleValue());
    }

}
