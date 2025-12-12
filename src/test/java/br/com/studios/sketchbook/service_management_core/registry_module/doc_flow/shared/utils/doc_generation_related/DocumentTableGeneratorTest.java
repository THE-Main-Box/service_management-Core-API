package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.GeneratedTableData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DocumentTableGeneratorTest {

    private static final DocumentTableGenerator docGen = new DocumentTableGenerator();

    private static final List<List<Object>> tableMapping = new ArrayList<>();

    @Test
    public void testDocumentCreation() {

        tableMapping.add(Arrays.asList(
                1010111L,
                "testando_a_table",
                true
        ));

        GeneratedTableData result = docGen.generateTable(tableMapping);

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


}
