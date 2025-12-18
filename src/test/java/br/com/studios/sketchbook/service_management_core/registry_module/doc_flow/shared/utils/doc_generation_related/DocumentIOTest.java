package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.enumerators.DocumentPrefix;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para DocumentTableIO.
 *
 * Estes testes validam o comportamento real de persistência,
 * carregamento e sobrescrita de documentos, sem mocks,
 * respeitando o fluxo completo do sistema.
 */
public class DocumentIOTest {

    // Gerador de documentos (responsável apenas por criar dados em memória)
    private static final DocumentGenerator docGen = new DocumentGenerator();

    // IO responsável por persistência
    private final DocumentIO docIO;

    // Documento atual utilizado nos testes
    private DocumentData currentDocument;

    // Estrutura mutável de entrada usada para gerar tabelas
    private static final List<List<Object>> tableMapping = new ArrayList<>();

    /**
     * Configuração mínima necessária para serialização correta.
     * Mantida explícita para evitar dependência implícita do Spring Context.
     */
    public DocumentIOTest() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        this.docIO = new DocumentIO(mapper);
    }

    /**
     * Limpeza após cada teste.
     *
     * Garante isolamento entre testes e evita efeitos colaterais
     * no filesystem persistente.
     */
    @AfterEach
    void afterEach() {
        tableMapping.clear();

        if (currentDocument != null) {
            docIO.deleteAllTableComponentsIfPresentAsDocument(currentDocument);
            currentDocument = null;
        }
    }

    /**
     * Responsabilidade: validar que um documento é persistido corretamente.
     *
     * Não valida conteúdo interno — apenas a existência do artefato persistido.
     */
    @Test
    public void testDocumentSave() {

        tableMapping.add(Arrays.asList(
                1010111L,
                "testando_a_table-linha",
                true
        ));

        tableMapping.add(Arrays.asList(
                1000L,
                "testando_a_table-linha 2",
                'A'
        ));

        tableMapping.add(Arrays.asList(
                1L,
                "testando_a_table-linha 3",
                'B'
        ));

        List<String> cellNames = Arrays.asList(
                "id",
                "descricao",
                "ativo"
        );

        currentDocument = docGen.generateDocument(
                tableMapping,
                cellNames,
                "teste de tabelação",
                true,
                DocumentPrefix.NON_DEFINED
        );

        docIO.saveDocument(currentDocument);

        assertTrue(
                docIO.isTablePresent(currentDocument.table().getId()),
                "A tabela deveria estar presente após o save"
        );
    }


    /**
     * Responsabilidade: validar que um documento salvo pode ser carregado.
     *
     * Aqui validamos apenas consistência básica entre save e load,
     * não o conteúdo completo.
     */
    @Test
    public void testDocumentLoad() {

        tableMapping.add(Arrays.asList(
                "Teste_10-9",
                false
        ));

        List<String> cellNames = Arrays.asList(
                "codigo",
                "ativo"
        );

        currentDocument = docGen.generateDocument(
                tableMapping,
                cellNames,
                "teste de save",
                true,
                DocumentPrefix.NON_DEFINED
        );

        docIO.saveDocument(currentDocument);

        DocumentData loadedDocument = docIO.loadDocumentIfPresent(
                currentDocument.table().getId()
        );

        assertNotNull(
                loadedDocument,
                "O documento carregado não deveria ser null"
        );

        assertEquals(
                currentDocument.table().getName(),
                loadedDocument.table().getName(),
                "O nome da tabela deve permanecer consistente após load"
        );
    }


    /**
     * Responsabilidade: validar o fluxo completo de sobrescrita de documentos.
     *
     * Este teste cobre:
     * - persistência inicial
     * - carregamento
     * - sobrescrita (delete + save)
     * - recarregamento
     * - validação do novo conteúdo
     */
    @Test
    public void testUpdateDocumentOverridesPersistentFiles() {

        // ---------- Arrange (estado inicial) ----------
        tableMapping.add(Arrays.asList(
                true,
                "valor inicial == true"
        ));

        List<String> initialCellNames = Arrays.asList(
                "ativo",
                "descricao"
        );

        currentDocument = docGen.generateDocument(
                tableMapping,
                initialCellNames,
                "teste de tabelação",
                true,
                DocumentPrefix.NON_DEFINED
        );

        docIO.saveDocument(currentDocument);

        assertEquals(
                true,
                currentDocument.rowCellListMap()
                        .get(0)
                        .get(0)
                        .getValue(),
                "O valor inicial da célula deve ser true"
        );

        assertTrue(
                docIO.isTablePresent(currentDocument.table().getId()),
                "A tabela inicial deve existir"
        );

        // ---------- Act (carrega e sobrescreve) ----------
        currentDocument = docIO.loadDocumentIfPresent(
                currentDocument.table().getId()
        );

        tableMapping.clear();
        tableMapping.add(Arrays.asList(
                "true",
                "valor final == true"
        ));

        List<String> overrideCellNames = Arrays.asList(
                "ativo",
                "descricao"
        );

        DocumentData overriddenData = docGen.overrideDocumentData(
                currentDocument.table().getId(),
                currentDocument.table().getName(),
                currentDocument.table().getDocumentPrefix(),
                currentDocument.table().getCreatedAt(),
                tableMapping,
                overrideCellNames
        );

        docIO.updateDocument(
                currentDocument,
                overriddenData
        );

        // ---------- Assert (estado final) ----------
        DocumentData reloadedData = docIO.loadDocumentIfPresent(
                currentDocument.table().getId()
        );

        assertNotNull(
                reloadedData,
                "O documento sobrescrito deve ser recarregável"
        );

        assertTrue(
                docIO.isTablePresent(reloadedData.table().getId()),
                "A tabela deve existir após o update"
        );

        assertEquals(
                "true",
                reloadedData.rowCellListMap()
                        .get(0)
                        .get(0)
                        .getValue(),
                "O valor da célula deve refletir a sobrescrita"
        );
    }

}
