package br.com.studios.sketchbook.service_management_core.registry_module.address.shared.interfaces;

import br.com.studios.sketchbook.service_management_core.registry_module.address.shared.enumerators.AddressOwnerType;

import java.util.UUID;

public interface AddressOwnerTypes {
    UUID getId();
    AddressOwnerType getAddressOwnerType();
}
