package br.com.studios.sketchbook.service_management_core.address.shared.interfaces;

import br.com.studios.sketchbook.service_management_core.address.shared.enumerators.AddressOwnerType;

import java.util.UUID;

public interface AddressOwner {
    UUID getId();
    UUID getAddressId();
    AddressOwnerType getOwnerType();
}
