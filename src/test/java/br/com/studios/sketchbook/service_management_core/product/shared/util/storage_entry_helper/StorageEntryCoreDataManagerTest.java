package br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper;

import br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.StorageEntryCoreDataManager;
import br.com.studios.sketchbook.service_management_core.product.domain.dto.storage_entry.StorageEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.product.domain.model.Product;
import br.com.studios.sketchbook.service_management_core.product.domain.model.aux_model.StorageEntry;
import br.com.studios.sketchbook.service_management_core.product.shared.enums.VolumeType;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes de unidade para o StorageEntryCoreDataManager.
 * Cada teste cobre um tipo de VolumeType e garante que as operações de
 * inicialização, adição, remoção e conversão funcionem corretamente.
 */
public class StorageEntryCoreDataManagerTest {

    private static StorageEntryCoreDataManager dataManager;
    private static Product currentProduct;
    private static StorageEntry entry;

    @BeforeAll
    static void setup() {
        dataManager = new StorageEntryCoreDataManager();
    }

    @AfterEach
    void end() {
        System.out.println("// // // // // // // // // //");
        System.out.println(currentProduct.toString());
        System.out.println(entry.toString());
    }

    /// Testa comportamento para itens em KILOGRAM.
    @Test
    public void testKiloGramEntry() {
        currentProduct = new Product("Areia", VolumeType.KILOGRAM);
        entry = new StorageEntry(currentProduct);

        // Inicializa com 1 kg
        dataManager.initEntry(entry, 1L, 0L, false);

        // Converte para gramas (1 kg = 1000g)
        assertEquals(1_000L, dataManager.getAmountAvailableRaw(entry));

        // Adiciona 500g
        dataManager.addUnit(entry, 500L, true);
        assertEquals(1_500L, dataManager.getAmountAvailableRaw(entry));

        // Adiciona 5 kg
        dataManager.addUnit(entry, 5L, false);
        assertEquals(6_500L, dataManager.getAmountAvailableRaw(entry));
    }

    /// Testa comportamento para itens UNIT (por unidade).
    @Test
    public void testUnitEntry() {
        currentProduct = new Product("arroz", VolumeType.UNIT);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 50L, 0L, false);

        // Remove 5 unidades
        dataManager.removeUnit(entry, 5L, true);
        assertEquals(45L, dataManager.getAmountAvailableRaw(entry));

        // Adiciona 5 unidades
        dataManager.addUnit(entry, 5L, false);
        assertEquals(50L, dataManager.getAmountAvailableRaw(entry));
    }

    /// Testa comportamento para LITER (litros).
    @Test
    public void testLiterEntry() {
        currentProduct = new Product("Suco_refil", VolumeType.LITER);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 1L, 0L, false);

        // Adiciona 500ml
        dataManager.addUnit(entry, 500L, true);

        // Deve resultar em 1.5 L
        assertEquals(1.5, dataManager.getAmountAvailable(entry).doubleValue());
        assertEquals(1500L, dataManager.getAmountAvailableRaw(entry));

        // Adiciona 14 L
        dataManager.addUnit(entry, 14L, false);

        // Deve resultar em 15.5 L
        assertEquals(15.5, dataManager.getAmountAvailable(entry).doubleValue());
        assertEquals(15_500L, dataManager.getAmountAvailableRaw(entry));
    }

    /// Testa comportamento para KILOGRAM_PER_UNIT (kg por unidade).
    @Test
    public void testKiloGramPerUnitEntry() {
        currentProduct = new Product("Peito de frango", VolumeType.KILOGRAM_PER_UNIT);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 20L, 1L, false);

        assertTrue(entry.isInit());

        // Adiciona 1g
        dataManager.addSubQuantity(entry, 1L, false);
        assertEquals(21_000L, dataManager.getAmountAvailableRaw(entry));

        // Remove 10g
        dataManager.removeSubQuantity(entry, 10L, true);
        assertEquals(20_990L, dataManager.getAmountAvailableRaw(entry));

        // Verifica resto de subunidades
        assertEquals(990, dataManager.getRemainderRaw(entry));

        // Remove 1 unidade (1kg)
        dataManager.removeUnit(entry, 1L, false);
        assertEquals(19_990L, dataManager.getAmountAvailableRaw(entry));
    }

    /// Testa comportamento para LITER_PER_UNITY (litros por unidade).
    @Test
    public void testLiterPerUnitEntry() {
        currentProduct = new Product("Petróleo", VolumeType.LITER_PER_UNITY);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 100L, 10L, false);

        assertTrue(entry.isInit());

        // Adiciona 1ml
        dataManager.addSubQuantity(entry, 1L, false);
        assertEquals(1001_000L, dataManager.getAmountAvailableRaw(entry));

        // Remove 10ml
        dataManager.removeSubQuantity(entry, 10L, true);
        assertEquals(1000_990L, dataManager.getAmountAvailableRaw(entry));

        // Verifica resto de subunidades
        assertEquals(990, dataManager.getRemainderRaw(entry));

        // Remove 1 unidade
        dataManager.removeUnit(entry, 1L, false);
        assertEquals(990_990L, dataManager.getAmountAvailableRaw(entry));
    }

    /// Testa comportamento para UNITY_PER_UNITY (unidades por unidade).
    @Test
    public void testUnitPerUnitEntry() {
        currentProduct = new Product("caixa_camisa", VolumeType.UNITY_PER_UNITY);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 10L, 10L, false);

        assertTrue(entry.isInit());
        assertEquals(100, dataManager.getAmountAvailableRaw(entry));

        dataManager.addSubQuantity(entry, 1L, false);
        assertEquals(101, dataManager.getAmountAvailableRaw(entry));

        dataManager.removeSubQuantity(entry, 10L, true);
        assertEquals(91, dataManager.getAmountAvailableRaw(entry));
    }

    /// Testa edição completa de entrada.
    @Test
    public void testEditEntry() {
        currentProduct = new Product("arroz", VolumeType.UNIT);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 10L, 0L, true);
        assertEquals(10L, dataManager.getAmountAvailableRaw(entry));

        // Edita para kg por unidade
        StorageEntryUpdateDTO dto = new StorageEntryUpdateDTO(
                VolumeType.KILOGRAM_PER_UNIT,
                10L,
                null,
                10L,
                false
        );

        dataManager.editEntry(entry, dto);

        assertEquals(dto.type(), entry.getVType());
        assertEquals(100_000L, dataManager.getAmountAvailableRaw(entry));
        assertEquals(10_000L, entry.getQuantityPerUnit());
    }

    /// Testa cálculo de quantidade disponível em litros.
    @Test
    public void testAmountAvailable() {
        currentProduct = new Product("arroz", VolumeType.LITER_PER_UNITY);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 10L, 500L, true);
        assertEquals(5000L, dataManager.getAmountAvailableRaw(entry));

        dataManager.removeSubQuantity(entry, 200L, true);

        assertEquals(4800, dataManager.getAmountAvailableRaw(entry));
        assertEquals(4.8, dataManager.getAmountAvailable(entry).doubleValue());
    }

    /// Testa cálculo de resto (subunidades que não completam uma unidade).
    @Test
    public void testRemainderAvailable() {
        currentProduct = new Product("arroz", VolumeType.LITER_PER_UNITY);
        entry = new StorageEntry(currentProduct);

        dataManager.initEntry(entry, 10L, 500L, true);
        assertEquals(5000L, dataManager.getAmountAvailableRaw(entry));

        dataManager.removeSubQuantity(entry, 200L, true);

        // Resto esperado: 300ml (0.3 L)
        assertEquals(300, dataManager.getRemainderRaw(entry));
        assertEquals(0.3, dataManager.getRemainder(entry).doubleValue());
    }
}
