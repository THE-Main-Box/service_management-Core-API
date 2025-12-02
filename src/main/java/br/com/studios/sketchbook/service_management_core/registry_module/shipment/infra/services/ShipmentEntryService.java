package br.com.studios.sketchbook.service_management_core.registry_module.shipment.infra.services;

import br.com.studios.sketchbook.service_management_core.application.api_utils.util.ApiUtils;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.req.AddressEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.model.AddressEntry;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req.ShipmentEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model.ShipmentEntry;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.infra.repositories.ShipmentEntryRepository;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.shared.util.manager.core.ShipmentEntryDataManagementCore;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.ConfigRefNames.AddressConfig.address_transaction_manager_ref;

@Service
@Transactional(address_transaction_manager_ref)
public class ShipmentEntryService {

    private final ShipmentEntryRepository repository;
    private final ShipmentEntryDataManagementCore manager;

    @Autowired
    public ShipmentEntryService(ShipmentEntryRepository repository) {
        this.repository = repository;
        this.manager = new ShipmentEntryDataManagementCore();
    }

    public ShipmentEntry createAndSave(ShipmentEntryCreationDTO dto) {
        return repository.save(
                manager.createByDTO(dto)
        );
    }

    public Page<ShipmentEntry> getAllInstances(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    public Page<ShipmentEntry> getByDescription(int page, int size, String description) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.listByDescription(description, pageable);
    }

    public boolean delete(UUID id) {
        repository.deleteById(id);

        Optional<ShipmentEntry> model = repository.findById(id);

        return model.isEmpty();
    }

    public ShipmentEntry getInstanceById(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Entrada com id [" + id + "] não encontrado")
        );
    }

    public URI getUriForPersistedObject(ShipmentEntry model) {
        return ApiUtils.getUriForPersistedObject(model.getId().toString(), "/entry/shipment/id/{id}");
    }

}
