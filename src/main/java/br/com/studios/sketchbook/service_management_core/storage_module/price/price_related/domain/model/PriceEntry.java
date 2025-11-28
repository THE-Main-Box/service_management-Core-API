package br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.model;

import br.com.studios.sketchbook.service_management_core.aplication.api_utils.converters.ClassToStringConverter;
import br.com.studios.sketchbook.service_management_core.storage_module.price.money_related.domain.model.Money;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.interfaces.PriceOwner;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(
        name = "TB_PRICE_ENTRY",
        indexes = {
                @Index(name = "idx_price_entry_owner_id", columnList = "ownerId")
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class PriceEntry implements Serializable {

    /// Número de série da entidade
    @Serial
    @Column(name = "version")
    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Getter
    @Setter
    @Embedded
    private Money price;

    @Getter
    /// Id do dono da entry
    private UUID ownerId;

    @Convert(converter = ClassToStringConverter.class)
    @Getter
    /// Tipo da classe do dono da entry
    private Class<? extends PriceOwner> ownerType;

    public PriceEntry(PriceOwner owner){
        this.ownerId = owner.getId();
        this.ownerType = owner.getClass();
    }
}
