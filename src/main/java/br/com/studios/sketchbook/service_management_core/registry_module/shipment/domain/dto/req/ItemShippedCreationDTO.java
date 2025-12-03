package br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.dto.req;

import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.enums.VolumeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ItemShippedCreationDTO (
        UUID itemId,
        @NotBlank(message = "Qual o tipo de item? por favor insira um existente")
        String itemType,
        @NotBlank(message = "Nome do que está sendo enviado é muito importante também")
        String name,
        @NotNull(message ="Precisamos saber a quantidade do que foi enviado")
        Long units,
        @NotNull(message = "Precisamos saber o tipo de volume")
        VolumeType volumeType
){
}
