package br.com.studios.sketchbook.service_management_core.storage.domain.model;

import br.com.studios.sketchbook.service_management_core.aplication.api_utils.converters.ClassToStringConverter;
import br.com.studios.sketchbook.service_management_core.product.shared.enums.VolumeType;
import br.com.studios.sketchbook.service_management_core.storage.shared.interfaces.StorageAble;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "TB_STORAGE_ENTRY",
        indexes = {
                @Index(name = "idx_storage_entry_owner_id", columnList = "ownerId")
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class StorageEntry implements Serializable {

    @Serial
    @Column(name = "version")
    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Getter
    @Column(updatable = false, nullable = false)
    /// Id do dono
    private UUID ownerId;

    @Getter
    @Convert(converter = ClassToStringConverter.class)
    @Column(updatable = false, nullable = false)
    /// Tipo da classe do dono
    private Class<? extends StorageAble> ownerType;

    @Getter
    @Setter
    /// Tipo de controle de armazenamento
    private VolumeType volumeType;

    @Getter
    @Setter
    /// Unidades completas existentes
    private Long units;

    @Getter
    @Setter
    /// Sub-unidades existentes
    private Long subUnits;

    /**
     * Escala para produtos de tipo de volume especial
     * 1 > 10 de unidades, significa que para cada unidade teríamos 10 unidades menores,
     * ou seja, é uma escala contando objetos reais, e não de um pra outro, como 0,5, ou 1,9,
     * é um valor de produto real, como 10 pra uma unidade ou coisa parecida
     */
    @Setter
    @Getter
    private Long quantityPerUnit;

    @Getter
    @Setter
    private boolean init;

    public StorageEntry(StorageAble owner, VolumeType volumeType) {
        this.ownerId = owner.getId();
        this.ownerType = owner.getClass();
        this.volumeType = volumeType;
    }

    @Override
    public String toString() {
        return "StorageEntry{" +
                "id=" + id +
                ", vType=" + volumeType +
                ", units=" + units +
                ", subUnits=" + subUnits +
                ", quantityPerUnit=" + quantityPerUnit +
                '}';
    }
}
