package br.com.studios.sketchbook.service_management_core.models.entities;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "TB_PRODUCTS")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class ProductModel implements Serializable {
    @Serial
    /// Número de série da entidade
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @Getter
    @Id
    /// Id geral do produto
    private UUID id;

    @Column(name = "name", nullable = false)
    @Getter
    /// Nome do produto
    private String name;

    @Column(name = "value", nullable = false, precision = 38, scale = 2)
    @Getter
    /// Valor do produto
    private BigDecimal value;

    @Column(name = "bar_code")
    @Getter
    /// Código de barras do produto
    private String barcode;

    public ProductModel(String name, double value, String barcode) {
        this.name = name;
        this.value = BigDecimal.valueOf(value);
        this.barcode = barcode;
    }

    public void setValue(double value) {
        this.value = BigDecimal.valueOf(value);
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", barcode='" + barcode + '\'' +
                '}';
    }
}
