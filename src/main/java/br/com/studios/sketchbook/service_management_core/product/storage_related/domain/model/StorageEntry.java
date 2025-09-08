package br.com.studios.sketchbook.service_management_core.product.storage_related.domain.model;

import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;
import br.com.studios.sketchbook.service_management_core.product.product_related.domain.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "TB_STORAGE_ENTRY")
@NoArgsConstructor
@AllArgsConstructor
public class StorageEntry implements Serializable {

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
    private VolumeType vType;

    @Getter
    @Setter
    /// Unidades completas existentes
    private Long units;

    @Getter
    @Setter
    private Long subUnits;

    /**
     * Escala para produtos de tipo de volume especial
     * 1 > 10 de unidades, significa que para cada unidade teríamos 10 unidades menores,
     * ou seja, é uma escala contando objetos reais, e não de um pra outro, como 0,5, ou 1,9,
     * é um valor de produto real, como 10 pra uma unidade ou coisa parecida
     */
    @Setter @Getter
    private Long quantityPerUnit;

    @Getter
    @Setter
    private boolean init;

    public StorageEntry(Product product) {
        this.product = product;
        this.product.setStorageEntry(this);
        this.vType = product.getVolumeType();
    }

    public void resetValues(){
        this.units = null;
        this.subUnits = null;
        this.quantityPerUnit = null;

        this.init = false;
    }

    @Override
    public String toString() {
        return "StorageEntry{" +
                "id=" + id +
                ", vType=" + vType +
                ", units=" + units +
                ", subUnits=" + subUnits +
                ", quantityPerUnit=" + quantityPerUnit +
                '}';
    }
}
