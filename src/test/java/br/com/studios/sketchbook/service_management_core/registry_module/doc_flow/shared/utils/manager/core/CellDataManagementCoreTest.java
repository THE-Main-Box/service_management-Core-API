package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Cell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CellDataManagementCoreTest {

    private static CellDataManagementCore manager;
    private static Cell currentCell;

    @BeforeAll
    static void setup() {
        manager = new CellDataManagementCore();
    }

    public void createDummyCell(Integer id, Integer rowId, Object value) {
        currentCell = new Cell(id, rowId, value);
    }

    @AfterEach
    void end() {
        System.out.println("// // // // // // // // // //");
        System.out.println(
                "cell________: " + currentCell.getId()
        );
        System.out.println(
                "coluna______: " + currentCell.getRowId()
        );
        System.out.println(
                "valor_______: " + currentCell.getValue()
        );
        System.out.println(
                "tipo_de_dado: " + currentCell.getValueType().getSimpleName()
        );

        if (manager.isCellJsonPresent(
                currentCell.getRowId(),
                currentCell.getId()
        ))
            manager.deleteCellJsonIfPresent(
                    currentCell.getRowId(),
                    currentCell.getId()
            );

    }

    @Test
    public void CreateCellTest() {
        createDummyCell(1, 1, "testando");

        manager.saveCellFromJson(currentCell);

        Cell tmpCell = manager.loadCellFromJson(currentCell.getRowId(), currentCell.getId());

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
    public void deleteCellTest() {
        createDummyCell(1, 1, 123L);

        manager.saveCellFromJson(currentCell);

        manager.deleteCellJsonIfPresent(
                currentCell.getRowId(),
                currentCell.getId()
        );

        assertFalse(
                manager.isCellJsonPresent(
                        currentCell.getRowId(),
                        currentCell.getId()
                )
        );

    }

}
