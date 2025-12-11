package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RowDataSerialManagementCoreTest {

    private static RowDataSerialManagementCore manager;
    private static Row currentRow;

    @BeforeAll
    static void setup() {
        manager = new RowDataSerialManagementCore();
    }

    public void createDummyRow(Integer id, Integer tableId, List<Integer> cellIdList) {
        currentRow = new Row(id, tableId,cellIdList);
    }

    @AfterEach
    void end() {
        if (currentRow == null) return;

        System.out.println("// // // // // // // // // //");
        System.out.println("table_______: " + currentRow.getTableId());
        System.out.println("row_________: " + currentRow.getId());
        System.out.println("cell_ids____: " + currentRow.getCellIdList());

        if (manager.isRowJsonPresent(currentRow.getTableId(), currentRow.getId())) {
            manager.deleteRowJsonIfPresent(currentRow.getTableId(), currentRow.getId());
        }

        currentRow = null;
    }

    @Test
    public void createRowJsonTest() {
        createDummyRow(1, 999, List.of(0, 10, 50)); // tabela fantasma 999

        manager.saveRowToJson(currentRow);

        Row tmpRow = manager.loadRowFromJson(currentRow.getTableId(), currentRow.getId());

        assertEquals(currentRow.getTableId(), tmpRow.getTableId());
        assertEquals(currentRow.getId(), tmpRow.getId());
        assertEquals(currentRow.getCellIdList(), tmpRow.getCellIdList());
    }

    @Test
    public void saveRowListJsonTest() {
        Row row1 = new Row(11, 900, List.of(5, 6));
        Row row2 = new Row(12, 901, List.of(7, 8, 9));

        manager.saveRowListToJson(List.of(row1, row2));

        Row loaded1 = manager.loadRowFromJson(900, 11);
        Row loaded2 = manager.loadRowFromJson(901, 12);

        assertEquals(row1.getCellIdList(), loaded1.getCellIdList());
        assertEquals(row2.getCellIdList(), loaded2.getCellIdList());

        manager.deleteRowJsonIfPresent(900, 11);
        manager.deleteRowJsonIfPresent(901, 12);
    }

    @Test
    public void deleteRowListJsonIfPresentTest() {
        Row r1 = new Row(30, 700, List.of(11));
        Row r2 = new Row(31, 701, List.of(110));

        manager.saveRowToJson(r1);
        manager.saveRowToJson(r2);

        manager.deleteRowListJsonIfPresent(
                List.of(700, 701),
                List.of(30, 31)
        );

        assertFalse(manager.isRowJsonPresent(700, 30));
        assertFalse(manager.isRowJsonPresent(701, 31));
    }

    @Test
    public void deleteNonExistentRowJsonDoesNotThrowTest() {
        assertDoesNotThrow(() -> manager.deleteRowJsonIfPresent(5000, 9999));
    }

    @Test
    public void loadMultipleRowsJsonTest() {
        Row row1 = new Row(1, 300, List.of(1, 2));
        Row row2 = new Row(2, 301, List.of(3, 4, 5));
        Row row3 = new Row(3, 302, List.of());

        manager.saveRowToJson(row1);
        manager.saveRowToJson(row2);
        manager.saveRowToJson(row3);

        List<Row> loaded = manager.loadRowListFromJson(
                List.of(300, 301, 302),
                List.of(1, 2, 3)
        );

        assertEquals(3, loaded.size());

        assertEquals(List.of(1, 2), loaded.get(0).getCellIdList());
        assertEquals(List.of(3, 4, 5), loaded.get(1).getCellIdList());
        assertEquals(List.of(), loaded.get(2).getCellIdList());

        manager.deleteRowJsonIfPresent(300, 1);
        manager.deleteRowJsonIfPresent(301, 2);
        manager.deleteRowJsonIfPresent(302, 3);
    }


    @Test
    public void deleteRowJsonTest() {
        createDummyRow(10, 400, List.of(7, 8, 9));

        manager.saveRowToJson(currentRow);

        manager.deleteRowJsonIfPresent(400, 10);

        assertFalse(manager.isRowJsonPresent(400, 10));
    }


}
