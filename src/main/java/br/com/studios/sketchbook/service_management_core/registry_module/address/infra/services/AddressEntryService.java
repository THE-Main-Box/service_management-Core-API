package br.com.studios.sketchbook.service_management_core.registry_module.address.infra.services;

import br.com.studios.sketchbook.service_management_core.application.api_utils.util.ApiUtils;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.req.AddressEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.req.AddressEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.model.AddressEntry;
import br.com.studios.sketchbook.service_management_core.registry_module.address.infra.repositories.AddressEntryRepository;
import br.com.studios.sketchbook.service_management_core.registry_module.address.shared.manager.core.AddressEntryDataManagementCore;
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
public class AddressEntryService {

    private final AddressEntryDataManagementCore manager;
    private final AddressEntryRepository repository;

    @Autowired
    public AddressEntryService(
            AddressEntryRepository repository
    ) {
        this.repository = repository;
        this.manager = new AddressEntryDataManagementCore();
    }


    public AddressEntry createAndSave(AddressEntryCreationDTO dto) {
        return repository.save(
                manager.createByDTO(dto)
        );
    }

    public Page<AddressEntry> getAllInstances(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    public Page<AddressEntry> getByDescription(int page, int size, String description) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.listByDescription(description, pageable);
    }

    public boolean delete(UUID id) {
        repository.deleteById(id);

        Optional<AddressEntry> model = repository.findById(id);

        return model.isEmpty();
    }

    public AddressEntry update(AddressEntry toUpdate, AddressEntryUpdateDTO dto) {

        manager.editEntryByDTO(
                toUpdate,
                dto
        );

        return toUpdate;
    }

    public AddressEntry getInstanceById(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Entrada com id [" + id + "] não encontrado")
        );
    }

    public URI getUriForPersistedObject(AddressEntry model) {
        return ApiUtils.getUriForPersistedObject(model.getId().toString(), "/entry/address/id/{id}");
    }
}
