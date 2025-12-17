package br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.model;

import br.com.studios.sketchbook.service_management_core.registry_module.address.shared.enumerators.AddressOwnerType;
import br.com.studios.sketchbook.service_management_core.registry_module.address.shared.interfaces.AddressOwnerTypes;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.snapshot.AddressRef;
import br.com.studios.sketchbook.service_management_core.registry_module.shipment.domain.snapshot.ItemShipped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "TB_SHIPMENT_ENTRY")
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentEntry implements AddressOwnerTypes, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /// Data que a viagem foi feita
    @Getter
    @Column(name = "trip_date", updatable = false, nullable = false)
    private LocalDate tripDate;

    /// Referência em snapshot de endereço de origem
    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "addressId",
                    column = @Column(
                            name = "origin_address_id",
                            updatable = false
                    )
            ),
            @AttributeOverride(
                    name = "description",
                    column = @Column(
                            name = "origin_address_description",
                            nullable = false,
                            updatable = false
                    )
            )
    })
    private AddressRef originAddressRef;

    /// Referência em snapshot de endereço de destino
    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "addressId",
                    column = @Column(
                            name = "destination_address_id",
                            updatable = false
                    )
            ),
            @AttributeOverride(
                    name = "description",
                    column = @Column(
                            name = "destination_address_description",
                            nullable = false,
                            updatable = false
                    )
            )
    })
    private AddressRef destinationAddressRef;

    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "itemId",
                    column = @Column(
                            name = "item_id",
                            updatable = false
                    )
            ),
            @AttributeOverride(
                    name = "itemName",
                    column = @Column(
                            name = "name",
                            nullable = false,
                            updatable = false
                    )
            ),
            @AttributeOverride(
                    name = "units",
                    column = @Column(
                            name = "units",
                            nullable = false,
                            updatable = false
                    )
            ),
            @AttributeOverride(
                    name = "unitsPerUnit",
                    column = @Column(
                            name = "units_per_unit",
                            updatable = false
                    )
            ),
            @AttributeOverride(
                    name = "volumeType",
                    column = @Column(
                            name = "volume_type",
                            nullable = false,
                            updatable = false
                    )
            )

    })
    private ItemShipped itemShipped;

    /// Data de emissão de documento
    @Getter
    @Column(name = "issue_date", updatable = false, nullable = false)
    private LocalDate issueDate;

    public ShipmentEntry(
            LocalDate tripDate,
            AddressRef originAddressRef,
            AddressRef destinationAddressRef,
            ItemShipped itemShipped,
            LocalDate issueDate
    ) {
        this.tripDate = tripDate;
        this.originAddressRef = originAddressRef;
        this.destinationAddressRef = destinationAddressRef;
        this.itemShipped = itemShipped;
        this.issueDate = issueDate;
    }

    @Override
    public AddressOwnerType getAddressOwnerType() {
        return AddressOwnerType.TRIP;
    }


}
