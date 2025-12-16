package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models.Row;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para DocumentTableGenerator.
 *
 * Estes testes validam exclusivamente a geração de estruturas em memória
 * (Table, Row, Cell), sem envolver persistência ou IO.
 */
public class DocumentGeneratorTest {

    // Gerador sob teste
    private static final DocumentGenerator docGen = new DocumentGenerator();

    // Estrutura mutável usada como entrada para geração
    private static final List<List<Object>> tableMapping = new ArrayList<>();

    /**
     * Limpeza após cada teste para garantir isolamento.
     */
    @AfterEach
    void afterEach() {
        tableMapping.clear();
    }

    /**
     * Responsabilidade: validar a criação básica de um documento.
     *
     * Este teste garante que:
     * - uma Row é criada
     * - o valor da primeira Cell corresponde ao valor de entrada
     */
    @Test
    public void testDocumentCreation() {

        // ---------- Arrange ----------
        tableMapping.add(Arrays.asList(
                1010111L,
                "testando_a_table",
                true
        ));

        List<String> cellNames = Arrays.asList(
                "id",
                "nome",
                "ativo"
        );

        // ---------- Act ----------
        DocumentData result = docGen.generateTable(
                tableMapping,
                cellNames,
                ""
        );

        Object expectedFirstCellValue = tableMapping.get(0).get(0);

        Object actualFirstCellValue = result
                .rowCellListMap()
                .get(0)
                .get(0)
                .getValue();

        Row firstRow = result.rowList().get(0);

        // ---------- Assert ----------
        assertNotNull(
                firstRow,
                "A primeira row deve ser criada"
        );

        assertEquals(
                expectedFirstCellValue,
                actualFirstCellValue,
                "O valor da primeira cell deve corresponder ao valor de entrada"
        );
    }


    /**
     * Responsabilidade: validar sobrescrita lógica de dados mantendo identidade.
     *
     * Este teste garante que:
     * - o conteúdo muda
     * - o ID da tabela permanece
     * - o nome da tabela permanece
     */
    @Test
    public void testDocumentOverride() {

        // ---------- Arrange (criação inicial) ----------
        tableMapping.add(Arrays.asList(
                1010111L,
                "testando_a_table",
                true
        ));

        List<String> cellNames = Arrays.asList(
                "id",
                "nome",
                "ativo"
        );

        DocumentData original = docGen.generateTable(
                tableMapping,
                cellNames,
                "tabela de testes"
        );

        Object originalFirstCellValue = original
                .rowCellListMap()
                .get(0)
                .get(0)
                .getValue();

        assertEquals(
                tableMapping.get(0).get(0),
                originalFirstCellValue,
                "O valor inicial da célula deve estar correto"
        );

        // ---------- Act (override) ----------
        tableMapping.clear();

        tableMapping.add(Arrays.asList(
                2025_12_15L,
                "finalizando teste de override",
                'a'
        ));

        DocumentData overrideResult = docGen.overrideTableData(
                original.table().getId(),
                original.table().getName(),
                original.table().getCreatedAt(),
                tableMapping,
                cellNames
        );

        // ---------- Assert ----------
        assertEquals(
                original.table().getName(),
                overrideResult.table().getName(),
                "O nome da tabela deve ser preservado no override"
        );

        assertEquals(
                original.table().getId(),
                overrideResult.table().getId(),
                "O ID da tabela deve ser preservado no override"
        );
    }


    /**
     * Responsabilidade: validar falha na geração com dados inválidos.
     *
     * Este teste garante que o gerador rejeita tipos não suportados,
     * preservando o contrato de segurança do domínio.
     */
    @Test
    public void testDocumentInvalidDataCreation() {

        // ---------- Arrange ----------
        tableMapping.add(Arrays.asList(
                new ArrayList<>(), // Tipo inválido proposital
                "testando_a_table",
                true
        ));

        List<String> cellNames = Arrays.asList(
                "campo_invalido",
                "nome",
                "ativo"
        );

        // ---------- Assert ----------
        assertThrows(
                IllegalArgumentException.class,
                () -> docGen.generateTable(
                        tableMapping,
                        cellNames,
                        "tabela de testes"
                ),
                "Tipos inválidos devem gerar IllegalArgumentException"
        );
    }

}
