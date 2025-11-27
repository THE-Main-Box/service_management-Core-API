package br.com.studios.sketchbook.service_management_core.address.infra.service;

import br.com.studios.sketchbook.service_management_core.address.domain.AddressEntry;
import br.com.studios.sketchbook.service_management_core.address.domain.dto.AddressEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.address.domain.dto.AddressEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.address.infra.repository.AddressEntryRepository;
import br.com.studios.sketchbook.service_management_core.address.shared.managers.core.AddressEntryDataManagementCore;
import br.com.studios.sketchbook.service_management_core.aplication.api_utils.util.ApiUtils;
import br.com.studios.sketchbook.service_management_core.product.domain.model.Product;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static br.com.studios.sketchbook.service_management_core.aplication.api_utils.references.ConfigRefNames.AddressConfig.address_transaction_manager_ref;

@Service
@Transactional(address_transaction_manager_ref)
public class AddressEntryService {

    private final AddressEntryRepository repository;
    private final AddressEntryDataManagementCore manager;

    @Autowired
    public AddressEntryService(AddressEntryRepository repository) {
        this.repository = repository;
        this.manager = new AddressEntryDataManagementCore();
    }

    public AddressEntry getInstanceById(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Endereço com id [" + id + "] não encontrado")
        );
    }

    /// Configura o pageable e retorna todas as instancias em uma pagina
    public Page<AddressEntry> getAllInstances(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("description").ascending());
        return repository.findAll(pageable);
    }

    /// Obtém uma Page de instâncias de modelos do banco de dados conforme o nome
    public Page<AddressEntry> getInstancesByDescription(String description, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("description").ascending());
        return repository.listByDescription(description, pageable);
    }

    /// Utiliza o manager para criar uma entidade, e salva no banco
    public AddressEntry createAndSave(AddressEntryCreationDTO creationDTO) {
        return repository.save(
                manager.createByDTO(creationDTO)
        );
    }

    public void updateByDTO(AddressEntry toUpdate, AddressEntryUpdateDTO dto){
        manager.updateByDTO(toUpdate, dto);
    }

    public boolean delete(UUID id) {
        Optional<AddressEntry> model = repository.findById(id);
        if (model.isEmpty()) return false;
        repository.delete(model.get());
        return true;
    }

    /// TODO: Adicionar um path coerente com o caminho correto para leitura de modelo

    public URI getUriForPersistedObject(Product model) {
        return ApiUtils.getUriForPersistedObject(model.getId().toString(), "...");
    }


}
