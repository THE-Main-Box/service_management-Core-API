package br.com.studios.sketchbook.service_management_core.address.shared.managers.core;

import br.com.studios.sketchbook.service_management_core.address.domain.AddressEntry;
import br.com.studios.sketchbook.service_management_core.address.domain.dto.AddressEntryCreationDTO;
import br.com.studios.sketchbook.service_management_core.address.domain.dto.AddressEntryUpdateDTO;

public class AddressEntryDataManagementCore {


    /// Cria um objeto com base num dto
    public AddressEntry createByDTO(AddressEntryCreationDTO dto){
        return new AddressEntry(
                dto.description() != null ? dto.description() : null,
                dto.street() != null ? dto.street() : null,
                dto.number() != null ? dto.number() : null,
                dto.complement() != null ? dto.complement() : null,
                dto.neighborhood() != null ? dto.neighborhood() : null,
                dto.city() != null ? dto.city() : null,
                dto.state() != null ? dto.state() : null,
                dto.zipCode() != null ? dto.zipCode() : null
        );
    }

    /**
     * Atualização de um objeto relacionado ao endereço de algum lugar.
     * <p>
     * Importante ter em mente que os valores "null", serão ignorados
     *
     * @param toUpdate objeto já inicializado
     * @param dto dto dedicado a atualização
     */
    public void updateByDTO(AddressEntry toUpdate, AddressEntryUpdateDTO dto) {

        // Verifica se a descrição foi fornecida no DTO. Se não for nula, atualiza o campo.
        if (dto.description() != null) {
            toUpdate.setDescription(dto.description());
        }

        // Verifica se a rua foi fornecida.
        if (dto.street() != null) {
            toUpdate.setStreet(dto.street());
        }

        // Verifica se o número foi fornecido.
        if (dto.number() != null) {
            toUpdate.setNumber(dto.number());
        }

        // Verifica se o complemento foi fornecido.
        if (dto.complement() != null) {
            toUpdate.setComplement(dto.complement());
        }

        // Verifica se o bairro foi fornecido.
        if (dto.neighborhood() != null) {
            toUpdate.setNeighborhood(dto.neighborhood());
        }

        // Verifica se a cidade foi fornecida.
        if (dto.city() != null) {
            toUpdate.setCity(dto.city());
        }

        // Verifica se o estado foi fornecido.
        if (dto.state() != null) {
            toUpdate.setState(dto.state());
        }

        // Verifica se o CEP foi fornecido.
        if (dto.zipCode() != null) {
            toUpdate.setZipCode(dto.zipCode());
        }
    }
}
