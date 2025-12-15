package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentTableGeneratorTest {

    private static final DocumentTableGenerator docGen = new DocumentTableGenerator();

    private static final List<List<Object>> tableMapping = new ArrayList<>();

    @AfterEach
    void afterEach() {
        tableMapping.clear();
    }

    @Test
    public void testDocumentCreation() {

        tableMapping.add(Arrays.asList(
                1010111L,
                "testando_a_table",
                true
        ));

        DocumentData result = docGen.generateTable(tableMapping, "");

        Object tableMappingFirstCellValue = tableMapping.get(0).get(0);
        Object resultFirstCellValue = result
                .rowCellListMap()//Percorre o mapeamento de cell
                .get(0)
                .get(0)//Obtém a primeira cell
                .getValue();//Obtém o valor pedido

        Row firstRow = result.rowList().get(0);

        assertNotNull(firstRow);

        assertEquals(
                tableMappingFirstCellValue,
                resultFirstCellValue
        );

        System.out.println(result);
    }

    @Test
    public void testDocumentOverride() {
        //Testa criação
        tableMapping.add(Arrays.asList(
                1010111L,
                "testando_a_table",
                true
        ));

        DocumentData result = docGen.generateTable(tableMapping, "tabela de testes");

        Object tableMappingFirstCellValue = tableMapping.get(0).get(0);
        Object resultFirstCellValue = result
                .rowCellListMap()//Percorre o mapeamento de cell
                .get(0)
                .get(0)//Obtém a primeira cell
                .getValue();//Obtém o valor pedido

        assertEquals(
                tableMappingFirstCellValue,
                resultFirstCellValue
        );

        tableMapping.clear();

        tableMapping.add(Arrays.asList(
                2025_12_15L,
                "finalizando teste de override",
                'a'
        ));

        DocumentData overrideResult = docGen.overrideTableData(
                result.table().getId(),
                result.table().getName(),
                result.table().getCreatedAt(),
                tableMapping
        );

        assertEquals(
                result.table().getName(),
                overrideResult.table().getName()
        );

        assertEquals(
                result.table().getId(),
                overrideResult.table().getId()
        );

        System.out.println(result);
        System.out.println(overrideResult);
    }

    @Test
    public void testDocumentInvalidDataCreation() {
        tableMapping.add(Arrays.asList(
                new ArrayList<>(),
                "testando_a_table",
                true
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> docGen.generateTable(tableMapping, "tabela de testes")
        );

    }

}
