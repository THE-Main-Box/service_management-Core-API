package br.com.studios.sketchbook.service_management_core.price.price_related.domain.model;

import br.com.studios.sketchbook.service_management_core.price.money_related.domain.model.Money;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "TB_PRICE_ENTRY")
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


}
