package br.com.studios.sketchbook.service_management_core.product.infra.util.dataManager;

import br.com.studios.sketchbook.service_management_core.infra.util.dataManager.StorageEntryDataManager;
import br.com.studios.sketchbook.service_management_core.models.data_transfer_objects.StorageEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.models.entities.Product;
import br.com.studios.sketchbook.service_management_core.models.entities.StorageEntry;
import br.com.studios.sketchbook.service_management_core.models.enumerators.VolumeType;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StorageEntryDataManagerTest {

    /// Manager para inicialização e gestão da StorageEntry
    private StorageEntryDataManager dataManager;
    /// Produto atual usado
    private Product currentProduct;
    /// Entrada atual sendo usada
    private StorageEntry currentEntry;


    @BeforeEach
    void setup() {
        dataManager = new StorageEntryDataManager();
    }

    @AfterEach
    void end() {
        System.out.println("// // // // // // // // // //");
        System.out.println(currentProduct.toString());
        System.out.println(currentEntry.toString());
    }

    @Test
    public void testKiloGramEntry() {
        currentProduct = new Product("Areia", VolumeType.KILOGRAM);
        currentEntry = new StorageEntry(currentProduct);

        dataManager.initEntry(currentEntry, 1L, 0L, false);

        assertEquals(1_000L, currentEntry.getUnits());

        /*
         *  Adicionamos 500 miligramas na conta,
         *  usamos (raw = true) para garantir que estamos adicionando gramas e não quilos
         *  pois estamos lidando com valor menor
         */
        dataManager.addUnit(currentEntry, 500L, true);
        assertEquals(1_500L, currentEntry.getUnits());

        /*
         *  Adicionamos 5 litros na conta,
         *  usamos (raw = false) para garantir que estamos adicionando quilos,
         *  pois o valor é inteiro sem conversão, pois estamos lidando com o valor maior
         */
        dataManager.addUnit(currentEntry, 5L, false);
        assertEquals(6_500L, currentEntry.getUnits());
    }

    @Test
    public void testUnitEntry() {
        currentProduct = new Product("arroz",  VolumeType.UNIT);
        currentEntry = new StorageEntry(currentProduct);

        dataManager.initEntry(currentEntry, 50L, 0L, false);

        //Removemos 5 unidades e validamos para ver se temos removido corretamente
        dataManager.removeUnit(currentEntry, 5L, true);
        assertEquals(45L, currentEntry.getUnits());

        //Adicionamos 5 unidades e validamos para ver se temos removido corretamente
        dataManager.addUnit(currentEntry, 5L, false);
        assertEquals(50L, currentEntry.getUnits());
    }

    @Test
    public void testLiterEntry() {
        currentProduct = new Product("Suco_refil", VolumeType.LITER);
        currentEntry = new StorageEntry(currentProduct);

        dataManager.initEntry(currentEntry, 10L, 0L, false);

        /*
         *  Adicionamos 500 miligramas na conta,
         *  usamos (raw = true) para garantir que estamos adicionando gramas e não quilos
         *  pois estamos lidando com valor menor
         */
        dataManager.addUnit(currentEntry, 500L, true);
        assertEquals(10_500L, currentEntry.getUnits());

        /*
         *  Adicionamos 5 litros na conta,
         *  usamos (raw = false) para garantir que estamos adicionando quilos,
         *  pois o valor é inteiro sem conversão, pois estamos lidando com o valor maior
         */
        dataManager.addUnit(currentEntry, 5L, false);
        assertEquals(15_500L, currentEntry.getUnits());
    }

    @Test
    public void testKiloGramPerUnitEntry() {
        currentProduct = new Product("Peito de frango", VolumeType.KILOGRAM_PER_UNIT);
        currentEntry = new StorageEntry(currentProduct);

        dataManager.initEntry(currentEntry, 20L, 1L, false);


        assertTrue(currentEntry.isInit());

        //Adicionamos 1 quilo
        dataManager.addSubQuantity(currentEntry, 1L, false);
        assertEquals(21_000L, currentEntry.getSubUnits());

        // Removemos 10 ml
        dataManager.removeSubQuantity(currentEntry, 10L, true);
        assertEquals(20_990L, currentEntry.getSubUnits());

        //Garantimos que houve um calculo correto
        assertEquals(990, dataManager.getRemainder(currentEntry));

        dataManager.removeUnit(currentEntry, 1L, false);
        assertEquals(19_990L, currentEntry.getSubUnits());
    }

    @Test
    public void testLiterPerUnitEntry() {
        currentProduct = new Product("Petróleo", VolumeType.LITER_PER_UNITY);
        currentEntry = new StorageEntry(currentProduct);

        dataManager.initEntry(currentEntry, 100L, 10L, false);

        assertTrue(currentEntry.isInit());

        //Adicionamos 1 litro
        dataManager.addSubQuantity(currentEntry, 1L, false);
        assertEquals(1001_000L, currentEntry.getSubUnits());

        // Removemos 10 ml
        dataManager.removeSubQuantity(currentEntry, 10L, true);
        assertEquals(1000_990, currentEntry.getSubUnits());

        //Garantimos que houve um calculo correto
        assertEquals(990, dataManager.getRemainder(currentEntry));

        dataManager.removeUnit(currentEntry, 1L, false);
        assertEquals(990_990L, currentEntry.getSubUnits());
    }

    @Test
    public void testUnitPerUnitEntry() {
        currentProduct = new Product("caixa_camisa", VolumeType.UNITY_PER_UNITY);
        currentEntry = new StorageEntry(currentProduct);

        dataManager.initEntry(currentEntry, 10L, 10L, false);

        assertTrue(currentEntry.isInit());
        assertEquals(10L, currentEntry.getUnits());
        assertEquals(100, currentEntry.getSubUnits());

        //Adicionamos 1 unidade
        dataManager.addSubQuantity(currentEntry, 1L, false);
        assertEquals(101, currentEntry.getSubUnits());

        // Removemos 10 sub-unidades
        dataManager.removeSubQuantity(currentEntry, 10L, true);
        assertEquals(91, currentEntry.getSubUnits());

    }

    @Test
    public void testEditEntry(){
        currentProduct = new Product("arroz", VolumeType.UNIT);
        currentEntry = new StorageEntry(currentProduct);

        dataManager.initEntry(currentEntry, 10L, 0L, true);
        assertEquals(10L, currentEntry.getUnits());

        StorageEntryUpdateDTO dto = new StorageEntryUpdateDTO(
                VolumeType.KILOGRAM_PER_UNIT,
                10L,
                null,
                10L
        );

        dataManager.editEntry(currentEntry, dto, false);

        assertEquals(dto.type(), currentEntry.getVType());
        assertEquals(100_000L, currentEntry.getSubUnits());
        assertEquals(10_000L, currentEntry.getQuantityPerUnit());

    }


}
