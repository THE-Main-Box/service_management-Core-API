package br.com.studios.sketchbook.service_management_core.product.infra.util.dataManager;

import br.com.studios.sketchbook.service_management_core.infra.util.StorageEntry.StorageEntryCoreDataManager;
import br.com.studios.sketchbook.service_management_core.models.data_transfer_objects.StorageEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.models.entities.Product;
import br.com.studios.sketchbook.service_management_core.models.entities.StorageEntry;
import br.com.studios.sketchbook.service_management_core.models.enumerators.VolumeType;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StorageEntryCoreDataManagerTest {

    /// Manager para inicialização e gestão da StorageEntry
    private static StorageEntryCoreDataManager dataManager;
    /// Produto atual usado
    private static Product currentProduct;
    /// Entrada atual sendo usada
    private static StorageEntry entry;

    @BeforeAll
    static void setup(){
        dataManager = new StorageEntryCoreDataManager();
    }

    @AfterEach
    void end() {
        System.out.println("// // // // // // // // // //");
        System.out.println(currentProduct.toString());
        System.out.println(entry.toString());
    }

    @Test
    public void testKiloGramEntry() {
        currentProduct = new Product("Areia", VolumeType.KILOGRAM);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 1L, 0L, false);

        assertEquals(1_000L, entry.getUnits());

        /*
         *  Adicionamos 500 miligramas na conta,
         *  usamos (raw = true) para garantir que estamos adicionando gramas e não quilos
         *  pois estamos lidando com valor menor
         */
        dataManager.addUnit(entry, 500L, true);
        assertEquals(1_500L, entry.getUnits());

        /*
         *  Adicionamos 5 litros na conta,
         *  usamos (raw = false) para garantir que estamos adicionando quilos,
         *  pois o valor é inteiro sem conversão, pois estamos lidando com o valor maior
         */
        dataManager.addUnit(entry, 5L, false);
        assertEquals(6_500L, entry.getUnits());
    }

    @Test
    public void testUnitEntry() {
        currentProduct = new Product("arroz",  VolumeType.UNIT);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 50L, 0L, false);

        //Removemos 5 unidades e validamos para ver se temos removido corretamente
        dataManager.removeUnit(entry, 5L, true);
        assertEquals(45L, entry.getUnits());

        //Adicionamos 5 unidades e validamos para ver se temos removido corretamente
        dataManager.addUnit(entry, 5L, false);
        assertEquals(50L, entry.getUnits());
    }

    @Test
    public void testLiterEntry() {
        currentProduct = new Product("Suco_refil", VolumeType.LITER);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 10L, 0L, false);

        /*
         *  Adicionamos 500 miligramas na conta,
         *  usamos (raw = true) para garantir que estamos adicionando gramas e não quilos
         *  pois estamos lidando com valor menor
         */
        dataManager.addUnit(entry, 500L, true);
        assertEquals(10_500L, entry.getUnits());

        /*
         *  Adicionamos 5 litros na conta,
         *  usamos (raw = false) para garantir que estamos adicionando quilos,
         *  pois o valor é inteiro sem conversão, pois estamos lidando com o valor maior
         */
        dataManager.addUnit(entry, 5L, false);
        assertEquals(15_500L, entry.getUnits());
    }

    @Test
    public void testKiloGramPerUnitEntry() {
        currentProduct = new Product("Peito de frango", VolumeType.KILOGRAM_PER_UNIT);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 20L, 1L, false);


        assertTrue(entry.isInit());

        //Adicionamos 1 quilo
        dataManager.addSubQuantity(entry, 1L, false);
        assertEquals(21_000L, entry.getSubUnits());

        // Removemos 10 ml
        dataManager.removeSubQuantity(entry, 10L, true);
        assertEquals(20_990L, entry.getSubUnits());

        //Garantimos que houve um calculo correto
        assertEquals(990, dataManager.getRemainderRaw(entry));

        dataManager.removeUnit(entry, 1L, false);
        assertEquals(19_990L, entry.getSubUnits());
    }

    @Test
    public void testLiterPerUnitEntry() {
        currentProduct = new Product("Petróleo", VolumeType.LITER_PER_UNITY);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 100L, 10L, false);

        assertTrue(entry.isInit());

        //Adicionamos 1 litro
        dataManager.addSubQuantity(entry, 1L, false);
        assertEquals(1001_000L, entry.getSubUnits());

        // Removemos 10 ml
        dataManager.removeSubQuantity(entry, 10L, true);
        assertEquals(1000_990, entry.getSubUnits());

        //Garantimos que houve um calculo correto
        assertEquals(990, dataManager.getRemainderRaw(entry));

        dataManager.removeUnit(entry, 1L, false);
        assertEquals(990_990L, entry.getSubUnits());
    }

    @Test
    public void testUnitPerUnitEntry() {
        currentProduct = new Product("caixa_camisa", VolumeType.UNITY_PER_UNITY);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 10L, 10L, false);

        assertTrue(entry.isInit());
        assertEquals(10L, entry.getUnits());
        assertEquals(100, entry.getSubUnits());

        //Adicionamos 1 unidade
        dataManager.addSubQuantity(entry, 1L, false);
        assertEquals(101, entry.getSubUnits());

        // Removemos 10 sub-unidades
        dataManager.removeSubQuantity(entry, 10L, true);
        assertEquals(91, entry.getSubUnits());

    }

    @Test
    public void testEditEntry(){
        currentProduct = new Product("arroz", VolumeType.UNIT);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 10L, 0L, true);
        assertEquals(10L, entry.getUnits());

        StorageEntryUpdateDTO dto = new StorageEntryUpdateDTO(
                VolumeType.KILOGRAM_PER_UNIT,
                10L,
                null,
                10L,
                false
        );

        dataManager.editEntry(entry, dto);

        assertEquals(dto.type(), entry.getVType());
        assertEquals(100_000L, entry.getSubUnits());
        assertEquals(10_000L, entry.getQuantityPerUnit());

    }

    @Test
    public void testAmountAvailable(){
        currentProduct = new Product("arroz", VolumeType.LITER_PER_UNITY);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 10L, 500L, true);
        assertEquals(10L, entry.getUnits());
        assertEquals(5000L, entry.getSubUnits());

        dataManager.removeSubQuantity(entry, 200L, true);

        assertEquals(4800, dataManager.getAmountAvailableRaw(entry));
        assertEquals(4.8, dataManager.getAmountAvailable(entry).doubleValue());

    }

    @Test
    public void testRemainderAvailable(){
        currentProduct = new Product("arroz", VolumeType.LITER_PER_UNITY);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 10L, 500L, true);
        assertEquals(10L, entry.getUnits());
        assertEquals(5000L, entry.getSubUnits());

        dataManager.removeSubQuantity(entry, 200L, true);

        assertEquals(300, dataManager.getRemainderRaw(entry));
        assertEquals(0.3, dataManager.getRemainder(entry).doubleValue());

    }

}
