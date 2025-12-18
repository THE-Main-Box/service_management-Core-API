package br.com.studios.sketchbook.service_management_core.registry_module.shipment.infra.services;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentGenerator;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentIO;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model.ShipmentEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ShipmentEntryDocProjectionService {

    private final ShipmentEntryService shipmentEntryService;

    private final DocumentIO docIO;
    private final DocumentGenerator docGen;

    @Autowired
    public ShipmentEntryDocProjectionService(ShipmentEntryService shipmentEntryService, ObjectMapper mapper) {

        this.docIO = new DocumentIO(mapper);
        this.docGen = new DocumentGenerator();

        this.shipmentEntryService = shipmentEntryService;
    }

    public Integer createDocumentByIdList(String documentName, List<UUID> entryIdList) {
        List<List<Object>> currentTableData = new ArrayList<>();
        List<String> currentTableColumnNames = new ArrayList<>();
        ShipmentEntry currentEntryToDocument;

        //Gera o nome das colunas
        this.generateColumnNamesToBaseDocumentFromEntry(
                currentTableColumnNames
        );

        //Percorre a lista de IDs para gerar um documento contendo todos os objetos de id
        for (UUID id : entryIdList) {

            currentEntryToDocument = this.shipmentEntryService.getInstanceById(id);  //Tentamos pegar uma instancia

            if (currentEntryToDocument == null) continue;        //Se não encontrarmos o id continuamos

            //Geramos a table
            generateTableDataFromShipmentEntry(
                    currentEntryToDocument,
                    currentTableData
            );
        }

        //Obtem o documento completo
        DocumentData currentDocument = docGen.generateDocument(
                currentTableData,
                currentTableColumnNames,
                documentName
        );

        //Atualiza a permição de sobrescrita
        currentDocument.table().setCanBeOverridden(false);

        docIO.saveDocument(currentDocument);

        return currentDocument.table().getId();
    }

    /// Gera os nomes das colunas que serão passadas para as cell específico para a entry
    private void generateColumnNamesToBaseDocumentFromEntry(List<String> columnNameList) {
        //Limpa para caso a lista não esteja vazia
        columnNameList.clear();

        columnNameList.addAll(List.of(
                "origin_address_description",              // Descrição do endereço de origem
                "destination_address_description",         // Descrição do endereço de destino
                "trip_date",                               // Data da viagem
                "item_name",                               // Nome do item transportado
                "item_units",                              // Quantidade transportada
                "item_quantity_per_unit",                     // Quantidade por unidade
                "item_volume_type",                        // Tipo de volume
                "issue_date"                               // Data de emissão do documento
        ));

    }

    /// Gera uma lista de lista contendo as informações da entry
    private void generateTableDataFromShipmentEntry(
            ShipmentEntry entry,
            List<List<Object>> tableData
    ) {
        tableData.add(Arrays.asList(
                entry.getOriginAddressRef().description(),                 //Descrição do endereço de origem
                entry.getDestinationAddressRef().description(),            //Descrição do endereço de destino
                entry.getTripDate().toString(),                            //Data da viagem
                entry.getItemShipped().itemName(),                         //Nome do item da viagem
                entry.getItemShipped().units(),                            //Quantidade de item passada
                entry.getItemShipped().quantityPerUnit(),                     //Quantidade de item por subunidade
                entry.getItemShipped().volumeType().name(),                //Tipo de volume passado
                entry.getIssueDate().toString()                            //Data da viagem
        ));

    }

}
