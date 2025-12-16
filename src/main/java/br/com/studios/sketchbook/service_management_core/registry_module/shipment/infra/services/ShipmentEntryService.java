package br.com.studios.sketchbook.service_management_core.registry_module.shipment.infra.services;

import br.com.studios.sketchbook.service_management_core.application.api_utils.util.ApiUtils;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentIO;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.doc_generation_related.DocumentGenerator;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.shared.utils.dto.DocumentData;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req.ShipmentEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model.ShipmentEntry;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.infra.repositories.ShipmentEntryRepository;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.shared.util.manager.core.ShipmentEntryDataManagementCore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.ConfigRefNames.AddressConfig.address_transaction_manager_ref;

@Service
@Transactional(address_transaction_manager_ref)
public class ShipmentEntryService {

    /// Repositório responsável pelo acesso aos dados persistidos
    private final ShipmentEntryRepository repository;

    /// Core responsável por montar a entidade a partir de DTOs
    private final ShipmentEntryDataManagementCore manager;

    @Autowired
    public ShipmentEntryService(ShipmentEntryRepository repository) {
        this.repository = repository;

        this.manager = new ShipmentEntryDataManagementCore();
    }

    /**
     * Cria uma nova entrada de remessa a partir de um DTO e persiste no banco
     */
    public ShipmentEntry createAndSave(ShipmentEntryCreationDTO dto) {
        // Converte o DTO em entidade de domínio
        ShipmentEntry entry = manager.createByDTO(dto);

        // Persiste a entidade
        return repository.save(entry);
    }

    /**
     * Retorna todas as entradas de remessa de forma paginada
     */
    public Page<ShipmentEntry> getAllInstances(int page, int size) {
        // Cria o objeto de paginação
        Pageable pageable = PageRequest.of(page, size);

        // Busca os registros paginados
        return repository.findAll(pageable);
    }

    /**
     * Busca entradas pela descrição do endereço de origem
     */
    public Page<ShipmentEntry> getByOriginAddressDescription(String description, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByOriginAddressDescription(description, pageable);
    }

    /**
     * Busca entradas pela descrição do endereço de destino
     */
    public Page<ShipmentEntry> getByDestinationAddressDescription(String description, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByDestinationAddressDescription(description, pageable);
    }

    /**
     * Busca entradas pelo nome do item enviado
     */
    public Page<ShipmentEntry> getByItemName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByItemName(name, pageable);
    }

    /**
     * Busca entradas pelo ID do endereço de origem
     */
    public Page<ShipmentEntry> getByOriginAddressId(UUID addressId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByOriginAddressId(addressId, pageable);
    }

    /**
     * Busca entradas pelo ID do endereço de destino
     */
    public Page<ShipmentEntry> getByDestinationAddressId(UUID addressId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByDestinationAddressId(addressId, pageable);
    }

    /**
     * Busca entradas pelo ID do item enviado
     */
    public Page<ShipmentEntry> getByItemId(UUID itemId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByItemId(itemId, pageable);
    }

    /**
     * Remove uma entrada de remessa pelo seu ID
     */
    public boolean delete(UUID id) {
        // Solicita a exclusão do registro
        repository.deleteById(id);

        // Verifica se o registro ainda existe
        Optional<ShipmentEntry> model = repository.findById(id);

        // Retorna true se o registro não foi encontrado
        return model.isEmpty();
    }

    /**
     * Busca uma entrada de remessa pelo ID ou lança exceção caso não exista
     */
    public ShipmentEntry getInstanceById(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Entrada com id [" + id + "] não encontrado")
        );
    }

    /**
     * Busca entradas pela data de emissão do documento
     */
    public Page<ShipmentEntry> findByIssueDate(
            LocalDate issueDate,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByIssueDate(issueDate, pageable);
    }

    /**
     * Busca entradas pela data da viagem
     */
    public Page<ShipmentEntry> findByTripDate(
            LocalDate tripDate,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByTripDate(tripDate, pageable);
    }

    /**
     * Gera a URI de acesso para uma entrada já persistida
     */
    public URI getUriForPersistedObject(ShipmentEntry model) {
        // Monta a URI baseada no ID persistido
        return ApiUtils.getUriForPersistedObject(
                model.getId().toString(),
                "/entry/shipment/id/{id}"
        );
    }
}
