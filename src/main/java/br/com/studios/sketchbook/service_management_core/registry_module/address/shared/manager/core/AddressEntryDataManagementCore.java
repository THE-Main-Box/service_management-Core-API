package br.com.studios.sketchbook.service_management_core.registry_module.address.shared.manager.core;

import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.req.AddressEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.dto.req.AddressEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.registry_module.address.domain.model.AddressEntry;

public class AddressEntryDataManagementCore {

    /**
     * Cria um objeto de endereço a partir de um dto
     *
     * @param dto dto de criação
     * @return um novo objeto contendo os dados do DTO, quer seja null ou não
     */
    public AddressEntry createByDTO(AddressEntryCreationDTO dto) {
        return new AddressEntry(
                dto.description(),
                dto.zipCode(),
                dto.number(),
                dto.streetName(),
                dto.complement(),
                dto.district(),
                dto.city(),
                dto.state()
        );
    }

    /**
     * Edita os dados de uma entry de endereço já existente com base num DTO
     *
     * @param entry referência ao endereço
     * @param dto dado de atualização
     */
    public void editEntryByDTO(AddressEntry entry, AddressEntryUpdateDTO dto) {
        if (dto.description() != null) {
            entry.setDescription(dto.description());
        }

        if (dto.zipCode() != null) {
            entry.setZipCode(dto.zipCode());
        }

        if (dto.streetName() != null) {
            entry.setStreetName(dto.streetName());
        }

        if (dto.number() != null) {
            entry.setNumber(dto.number());
        }

        if (dto.complement() != null) {
            entry.setComplement(dto.complement());
        }

        if (dto.district() != null) {
            entry.setDistrict(dto.district());
        }

        if (dto.city() != null) {
            entry.setCity(dto.city());
        }

        if (dto.state() != null) {
            entry.setState(dto.state());
        }
    }
}
