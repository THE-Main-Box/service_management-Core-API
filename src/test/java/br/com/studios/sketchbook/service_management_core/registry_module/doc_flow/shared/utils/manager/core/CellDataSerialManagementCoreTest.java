package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CellDataSerialManagementCoreTest {

    private static CellDataSerialManagementCore manager;
    private static Cell currentCell;

    public CellDataSerialManagementCoreTest() {
        manager = new CellDataSerialManagementCore(new ObjectMapper());
    }

    public void createDummyCell(
            Integer id,
            Integer tableId,
            Integer rowId,
            Object value
    ) {
        currentCell = new Cell(id, tableId, rowId, value);
    }


    @AfterEach
    void end() {
        if (currentCell == null) return;

        System.out.println("// // // // // // // // // //");
        System.out.println(
                "cell________: " + currentCell.getId()
        );
        System.out.println(
                "tabela______: " + currentCell.getTableId()
        );
        System.out.println(
                "linha_______: " + currentCell.getRowId()
        );
        System.out.println(
                "valor_______: " + currentCell.getValue()
        );
        System.out.println(
                "tipo_de_dado: " + currentCell.getValueType().getSimpleName()
        );

        if (manager.isCellJsonPresent(
                currentCell.getTableId(),
                currentCell.getRowId(),
                currentCell.getId()
        ))
            manager.deleteCellJsonIfPresent(
                    currentCell.getTableId(),
                    currentCell.getRowId(),
                    currentCell.getId()
            );

    }

    @Test
    public void CreateCellInJsonTest() {
        createDummyCell(
                1,
                1,
                1,
                "testando"
        );

        manager.saveCellInJson(currentCell);

        Cell tmpCell = manager.loadCellFromJson(
                currentCell.getTableId(),
                currentCell.getRowId(),
                currentCell.getId()
        );

        assertEquals(
                currentCell.getValue().toString(),
                tmpCell.getValue().toString()
        );
        assertEquals(
                String.class,
                tmpCell.getValueType()
        );
        assertEquals(
                String.class,
                currentCell.getValueType()
        );
    }


    @Test
    public void saveCellListInJsonTest() {
        Cell c1 = new Cell(
                10,
                1,
                2,
                "A"
        );
        Cell c2 = new Cell(
                11,
                1,
                2,
                false
        );
        Cell c3 = new Cell(
                12,
                1,
                2,
                555
        );

        List<Cell> list = List.of(c1, c2, c3);

        manager.saveCellListInJson(list);

        Cell l1 = manager.loadCellFromJson(
                1,
                2,
                10
        );
        Cell l2 = manager.loadCellFromJson(
                1,
                2,
                11
        );
        Cell l3 = manager.loadCellFromJson(
                1,
                2,
                12
        );

        assertEquals(
                "A",
                l1.getValue()
        );
        assertEquals(
                false,
                l2.getValue()
        );
        assertEquals(
                555,
                l3.getValue()
        );

        manager.deleteCellJsonIfPresent(
                1,
                2,
                10
        );
        manager.deleteCellJsonIfPresent(
                1,
                2,
                11
        );
        manager.deleteCellJsonIfPresent(
                1,
                2,
                12
        );
    }


    @Test
    public void loadMultipleCellsInJsonTest() {
        Integer tableId = 1;
        Integer rowId = 1;

        Cell cell1 = new Cell(
                1,
                tableId,
                rowId,
                "abc"
        );
        Cell cell2 = new Cell(
                2,
                tableId,
                rowId,
                999
        );
        Cell cell3 = new Cell(
                3,
                tableId,
                rowId,
                true
        );

        manager.saveCellInJson(cell1);
        manager.saveCellInJson(cell2);
        manager.saveCellInJson(cell3);

        List<Integer> tableIds = List.of(tableId);
        List<Integer> rowIds = List.of(rowId);
        List<Integer> cellIds = List.of(
                1,
                2,
                3
        );

        List<Cell> loaded = manager.loadCellListFromJson(
                tableIds,
                rowIds,
                cellIds
        );

        assertEquals(
                3,
                loaded.size()
        );
        assertEquals(
                "abc",
                loaded.get(0).getValue()
        );
        assertEquals(
                999,
                loaded.get(1).getValue()
        );
        assertEquals(
                true,
                loaded.get(2).getValue()
        );

        assertEquals(
                String.class,
                loaded.get(0).getValueType()
        );
        assertEquals(
                Integer.class,
                loaded.get(1).getValueType()
        );
        assertEquals(
                Boolean.class,
                loaded.get(2).getValueType()
        );

        manager.deleteCellJsonIfPresent(
                tableId,
                rowId,
                1
        );
        manager.deleteCellJsonIfPresent(
                tableId,
                rowId,
                2
        );
        manager.deleteCellJsonIfPresent(
                tableId,
                rowId,
                3
        );
    }


    @Test
    public void deleteCellInJsonTest() {
        createDummyCell(
                1,
                1,
                1,
                123L
        );

        manager.saveCellInJson(currentCell);

        manager.deleteCellJsonIfPresent(
                currentCell.getTableId(),
                currentCell.getRowId(),
                currentCell.getId()
        );

        assertFalse(
                manager.isCellJsonPresent(
                        currentCell.getTableId(),
                        currentCell.getRowId(),
                        currentCell.getId()
                )
        );
    }


    @Test
    public void deleteCellListInJsonTest() {
        Integer tableId = 5;
        Integer rowId = 5;

        Cell c1 = new Cell(
                1,
                tableId,
                rowId,
                1
        );

        Cell c2 = new Cell(
                2,
                tableId,
                rowId,
                2
        );

        Cell c3 = new Cell(
                3,
                tableId,
                rowId,
                3
        );

        manager.saveCellInJson(c1);
        manager.saveCellInJson(c2);
        manager.saveCellInJson(c3);

        List<Integer> tableIds = List.of(tableId);

        List<Integer> rowIds = List.of(rowId);

        List<Integer> cells = List.of(
                1,
                2,
                3
        );

        manager.deleteCellListJsonIfPresent(
                tableIds,
                rowIds,
                cells
        );

        assertFalse(
                manager.isCellJsonPresent(
                        tableId,
                        rowId,
                        1
                )
        );
        assertFalse(
                manager.isCellJsonPresent(
                        tableId,
                        rowId,
                        2
                )
        );
        assertFalse(
                manager.isCellJsonPresent(
                        tableId,
                        rowId,
                        3
                )
        );
    }


}
