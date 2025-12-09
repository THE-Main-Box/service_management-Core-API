package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RowDataManagementCoreTest {

    private static RowDataManagementCore manager;
    private static Row currentRow;

    @BeforeAll
    static void setup() {
        manager = new RowDataManagementCore();
    }

    public void createDummyRow(Integer id, List<Integer> cellIdList) {
        currentRow = new Row(id, cellIdList);
    }

    @AfterEach
    void end() {
        if (currentRow == null) return;

        System.out.println("// // // // // // // // // //");
        System.out.println("row_________: " + currentRow.getId());
        System.out.println("cell_ids____: " + currentRow.getCellIdList());

        if (manager.isRowJsonPresent(currentRow.getId())) {
            manager.deleteRowJsonIfPresent(currentRow.getId());
        }

        currentRow = null;
    }

    @Test
    public void createRowJsonTest() {
        createDummyRow(1, List.of(0, 10, 50));

        manager.saveRowToJson(currentRow);

        Row tmpRow = manager.loadRowFromJson(currentRow.getId());

        assertEquals(currentRow.getId(), tmpRow.getId());
        assertEquals(currentRow.getCellIdList(), tmpRow.getCellIdList());
    }

    @Test
    public void saveRowListJsonTest() {
        Row row1 = new Row(11, List.of(5, 6));
        Row row2 = new Row(12, List.of(7, 8, 9));

        manager.saveRowListToJson(List.of(row1, row2));

        Row loaded1 = manager.loadRowFromJson(11);
        Row loaded2 = manager.loadRowFromJson(12);


        assertEquals(row1.getCellIdList(), loaded1.getCellIdList());
        assertEquals(row2.getCellIdList(), loaded2.getCellIdList());

        manager.deleteRowJsonIfPresent(11);
        manager.deleteRowJsonIfPresent(12);
    }

    @Test
    public void deleteRowListJsonIfPresentTest() {
        Row r1 = new Row(30, List.of(11));
        Row r2 = new Row(31, List.of(110));

        manager.saveRowToJson(r1);
        manager.saveRowToJson(r2);

        manager.deleteRowListJsonIfPresent(List.of(30, 31));

        assertFalse(manager.isRowJsonPresent(30));
        assertFalse(manager.isRowJsonPresent(31));
    }

    @Test
    public void deleteNonExistentRowJsonThrowsTest() {
        assertThrows(RuntimeException.class, () -> manager.deleteRowJsonIfPresent(999));
    }

    @Test
    public void loadMultipleRowsJsonTest() {
        Row row1 = new Row(1, List.of(1, 2));
        Row row2 = new Row(2, List.of(3, 4, 5));
        Row row3 = new Row(3, List.of());

        manager.saveRowToJson(row1);
        manager.saveRowToJson(row2);
        manager.saveRowToJson(row3);

        List<Integer> idList = List.of(1, 2, 3);

        List<Row> loaded = manager.loadRowListFromJson(idList);

        assertEquals(3, loaded.size());

        assertEquals(List.of(1, 2), loaded.get(0).getCellIdList());
        assertEquals(List.of(3, 4, 5), loaded.get(1).getCellIdList());
        assertEquals(List.of(), loaded.get(2).getCellIdList());

        manager.deleteRowJsonIfPresent(1);
        manager.deleteRowJsonIfPresent(2);
        manager.deleteRowJsonIfPresent(3);
    }

    @Test
    public void deleteRowJsonTest() {
        createDummyRow(10, List.of(7, 8, 9));

        manager.saveRowToJson(currentRow);

        manager.deleteRowJsonIfPresent(currentRow.getId());

        assertFalse(manager.isRowJsonPresent(currentRow.getId()));
    }

}
