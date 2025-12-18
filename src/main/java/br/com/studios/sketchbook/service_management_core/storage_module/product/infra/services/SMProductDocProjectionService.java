package br.com.studios.sketchbook.service_management_core.storage_module.product.infra.services;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.enumerators.DocumentPrefix;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentGenerator;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentIO;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model.Product;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model.SuperMarketProduct;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.model.StorageEntry;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.infra.services.StorageEntryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class SMProductDocProjectionService {

    private final DocumentIO docIO;
    private final DocumentGenerator docGen;

    private final SMProductService productService;
    private final StorageEntryService storageService;

    @Autowired
    public SMProductDocProjectionService(
            ObjectMapper mapper,
            SMProductService productService,
            StorageEntryService storageService
    ) {

        this.docIO = new DocumentIO(mapper);
        this.docGen = new DocumentGenerator();

        this.storageService = storageService;
        this.productService = productService;
    }

    public Integer createDocumentForStorageByIdList(
            String documentTableName,
            List<UUID> entryIdList
    ) {
        List<List<Object>> currentTableData = new ArrayList<>();
        List<String> currentTableColumnNames = new ArrayList<>();
        SuperMarketProduct currenProductModelToDocument;
        StorageEntry currentStorageModelToDocument;
        DocumentPrefix prefix = DocumentPrefix.SUPER_MARKET_PRODUCT_STORAGE;

        //Gera o nome das colunas
        this.generateColumnNamesToStorageDocumentFromEntry(
                currentTableColumnNames
        );

        //Percorre a lista de IDs para gerar um documento contendo todos os objetos de id
        for (UUID id : entryIdList) {

            //Tentamos pegar uma instancia do modelo de product
            currenProductModelToDocument = this.productService.getInstanceById(id);
            //Tentamos pegar uma instancia do modelo de armazenamento pelo id do dono
            currentStorageModelToDocument = this.storageService.getInstanceByOwnerId(id);

            //Se não encontrarmos os modelos continuamos
            if (currenProductModelToDocument == null || currentStorageModelToDocument == null) continue;

            //Geramos a table
            generateTableDataToProductStorage(
                    currenProductModelToDocument,
                    currentStorageModelToDocument,
                    currentTableData
            );
        }

        //Obtem o documento completo
        DocumentData currentDocument = docGen.generateDocument(
                currentTableData,
                currentTableColumnNames,
                documentTableName,
                false,
                prefix
        );

        docIO.saveDocument(currentDocument);

        return currentDocument.table().getId();
    }

    private void generateTableDataToProductStorage(
            SuperMarketProduct productModel,
            StorageEntry storageModel,
            List<List<Object>> tableDataToInsert
    ) {

        tableDataToInsert.add(Arrays.asList(
                productModel.getName(),                     //Nome do produto
                productModel.getBarcode(),                  //Código de barras
                storageModel.getVolumeType().name(),        //Tipo de volume
                storageModel.getUnits(),                    //Quantidade em unidades inteiras
                storageModel.getSubUnits(),                 //Quantidade em unidades quebradas(caso exista)
                storageModel.getQuantityPerUnit()           //Quantidade por unidades(Caso volume composto)
        ));

    }

    private void generateColumnNamesToStorageDocumentFromEntry(List<String> columnNameList) {
        columnNameList.clear();

        columnNameList.addAll(List.of(
                "super_market_product_name",                    //Nome do produto
                "bar_code",                                     //Código de barras
                "volume_type",                                  //Tipo de volume do produto
                "units",                                        //Quantidade em valor raw
                "sub_units",                                    //Quantidade interna por unidade em raw
                "quantity_per_unit"                             /*Constante de quantidade por unidade
                 *(Importante para saber quantas unidades temos por unidade)
                 */
        ));
    }

}
