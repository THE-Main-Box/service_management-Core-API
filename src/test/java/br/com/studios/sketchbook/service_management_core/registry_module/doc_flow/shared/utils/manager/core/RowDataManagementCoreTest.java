package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
        System.out.println("// // // // // // // // // //");
        System.out.println("row_________: " + currentRow.getId());
        System.out.println("cell_ids____: " + currentRow.getCellIdList());

        if (manager.isRowJsonPresent(currentRow.getId())) {
            manager.deleteRowJsonIfPresent(currentRow.getId());
        }
    }

    @Test
    public void createRowTest() {
        createDummyRow(1, List.of(10, 20, 30));

        manager.saveRowFromJson(currentRow);

        Row tmpRow = manager.loadRowFromJson(currentRow.getId());

        assertEquals(currentRow.getId(), tmpRow.getId());
        assertEquals(currentRow.getCellIdList(), tmpRow.getCellIdList());
    }

    @Test
    public void loadMultipleRowsTest() {
        Row row1 = new Row(1, List.of(1, 2));
        Row row2 = new Row(2, List.of(3, 4, 5));
        Row row3 = new Row(3, List.of());

        manager.saveRowFromJson(row1);
        manager.saveRowFromJson(row2);
        manager.saveRowFromJson(row3);

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
    public void deleteRowTest() {
        createDummyRow(10, List.of(7, 8, 9));

        manager.saveRowFromJson(currentRow);

        manager.deleteRowJsonIfPresent(currentRow.getId());

        assertFalse(manager.isRowJsonPresent(currentRow.getId()));
    }

}
