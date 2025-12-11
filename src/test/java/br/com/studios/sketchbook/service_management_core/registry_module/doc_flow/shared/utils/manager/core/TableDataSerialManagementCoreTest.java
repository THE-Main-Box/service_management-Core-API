package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TableDataSerialManagementCoreTest {

    private static TableDataSerialManagementCore manager;
    private static Table currentTable;

    @BeforeAll
    static void setup() {
        manager = new TableDataSerialManagementCore();
    }

    private void createDummyTable(Integer id, List<Integer> rowIdList) {
        currentTable = new Table(id, rowIdList);
    }

    @AfterEach
    void cleanup() {
        if (currentTable == null) return;

        System.out.println("// // // // // // // // // //");
        System.out.println("table_id____: " + currentTable.getId());
        System.out.println("row_ids_____: " + currentTable.getRowIdList());

        if (manager.isTablePresentInJson(currentTable.getId())) {
            manager.deleteTableIfPresentInJson(currentTable.getId());
        }

        currentTable = null;
    }

    @Test
    public void createTableJsonTest() {
        createDummyTable(1001, List.of(10, 20, 30));

        manager.saveTableInJson(currentTable);

        Table loaded = manager.loadTableFromJson(currentTable.getId());

        assertEquals(currentTable.getId(), loaded.getId());
        assertEquals(currentTable.getRowIdList(), loaded.getRowIdList());
    }

    @Test
    public void saveTableListJsonTest() {
        Table t1 = new Table(2001, List.of(1, 2, 3));
        Table t2 = new Table(2002, List.of(4, 5));

        manager.saveTableListInJson(List.of(t1, t2));

        Table loaded1 = manager.loadTableFromJson(2001);
        Table loaded2 = manager.loadTableFromJson(2002);

        assertEquals(t1.getRowIdList(), loaded1.getRowIdList());
        assertEquals(t2.getRowIdList(), loaded2.getRowIdList());

        manager.deleteTableIfPresentInJson(2001);
        manager.deleteTableIfPresentInJson(2002);
    }

    @Test
    public void deleteTableListJsonIfPresentTest() {
        Table t1 = new Table(3001, List.of(99));
        Table t2 = new Table(3002, List.of(100));

        manager.saveTableInJson(t1);
        manager.saveTableInJson(t2);

        manager.deleteTableListIfPresentInJson(
                List.of(3001, 3002)
        );

        assertFalse(manager.isTablePresentInJson(3001));
        assertFalse(manager.isTablePresentInJson(3002));
    }

    @Test
    public void deleteNonExistentTableJsonDoesNotThrowTest() {
        assertDoesNotThrow(() -> manager.deleteTableIfPresentInJson(999999));
    }

    @Test
    public void loadMultipleTablesJsonTest() {
        Table t1 = new Table(4001, List.of(1, 2));
        Table t2 = new Table(4002, List.of(3, 4, 5));
        Table t3 = new Table(4003, List.of());

        manager.saveTableInJson(t1);
        manager.saveTableInJson(t2);
        manager.saveTableInJson(t3);

        List<Table> loadedTables = manager.loadTableListFromJson(
                List.of(4001, 4002, 4003)
        );

        assertEquals(3, loadedTables.size());

        assertEquals(List.of(1, 2), loadedTables.get(0).getRowIdList());
        assertEquals(List.of(3, 4, 5), loadedTables.get(1).getRowIdList());
        assertEquals(List.of(), loadedTables.get(2).getRowIdList());

        manager.deleteTableIfPresentInJson(4001);
        manager.deleteTableIfPresentInJson(4002);
        manager.deleteTableIfPresentInJson(4003);
    }

    @Test
    public void deleteTableJsonTest() {
        createDummyTable(5001, List.of(7, 8, 9));

        manager.saveTableInJson(currentTable);

        manager.deleteTableIfPresentInJson(5001);

        assertFalse(manager.isTablePresentInJson(5001));
    }
}
