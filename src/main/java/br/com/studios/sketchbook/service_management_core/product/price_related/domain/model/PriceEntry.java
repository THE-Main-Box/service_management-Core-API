package br.com.studios.sketchbook.service_management_core.product.price_related.domain.model;

import br.com.studios.sketchbook.service_management_core.price.domain.model.Money;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.Product;
import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;
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

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    @Getter
    /// Referência ao produto a quem isso daqui pertence
    private Product product;

    @Getter
    @Setter
    /// Referência ao tipo de volume do produto
    private VolumeType vType;

    @Getter
    @Setter
    @Embedded
    private Money price;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "priceEntry")
    @Getter @Setter
    /// Modificador de preço, realiza a referência do ajuste
    private PriceModifier modifier;

    public PriceEntry(Product product) {
        this.product = product;
        this.vType = product.getVolumeType();
    }
}
