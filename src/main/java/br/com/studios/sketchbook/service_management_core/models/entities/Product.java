package br.com.studios.sketchbook.service_management_core.models.entities;

import br.com.studios.sketchbook.service_management_core.models.enumerators.VolumeType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/// Classe base para todos os produtos que poderão existir dentro do meu sistema
@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Product implements Serializable {

    //TODO: Reavaliar o hash code dos produtos
    // para impedir a adição de produtos semelhantes ou repetidos de forma indevida

    /// Id geral do produto
    @Id
    @Getter
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    protected UUID id;

    /// Nome com o qual o produto será referenciado
    @Getter
    @Setter
    @Column(name = "name", nullable = false)
    protected String name;

    /// Valor do produto
    @Getter
    @Column(name = "value", nullable = false, precision = 38, scale = 2)
    protected BigDecimal value;

    @Getter
    @Enumerated(EnumType.STRING)
    protected VolumeType volumeType;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Getter
    protected StorageEntry entry;

    public Product(String name, double value) {
        this.name = name;
        this.value = BigDecimal.valueOf(value);
    }

    public void setValue(String value) {
        this.value = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }


    public void setValue(double value) {
        this.value = BigDecimal.valueOf(value);
    }

    public String getValueAsString() {
        return value.toPlainString();
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", volumeType=" + volumeType +
                '}';
    }
}
