package br.com.studios.sketchbook.service_management_core.price.price_related.domain.model;

import br.com.studios.sketchbook.service_management_core.price.price_related.shared.interfaces.PriceOwner;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "TB_PRICE_ASSIGNMENT")
@NoArgsConstructor
@AllArgsConstructor
public class PriceEntryAssignment implements Serializable {
    @Id
    @Getter
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /// Número de série da entidade
    @Serial
    @Column(name = "version")
    private static final long serialVersionUID = 1L;

    /// Referência a entry
    @Getter
    private UUID entryId;

    /// Entidade a quem pertence a entry
    @Getter
    private UUID ownerId;

    /// Classe a quem é o dono do assignment
    @Getter
    private Class<? extends PriceOwner> ownerClass;

    public PriceEntryAssignment(PriceEntry entry, PriceOwner owner){
        this.entryId = entry.getId();
        this.ownerId = owner.getId();

        this.ownerClass = owner.getClass();
    }
}
