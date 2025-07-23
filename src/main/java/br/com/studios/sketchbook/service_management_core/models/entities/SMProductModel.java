package br.com.studios.sketchbook.service_management_core.models.entities;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/// Modelo de produtos de supermercado
@Entity
@Table(name = "TB_PRODUCTS")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class SMProductModel implements Serializable {
    /// Número de série da entidade
    @Serial
    private static final long serialVersionUID = 1L;

    /// Id geral do produto
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @Getter
    @Id
    private UUID id;

    /// Nome do produto
    @Column(name = "name", nullable = false)
    @Getter
    private String name;

    /// Valor do produto
    @Column(name = "value", nullable = false, precision = 38, scale = 2)
    @Getter
    private BigDecimal value;

    /// Código de barras do produto
    @Column(name = "bar_code")
    @Getter
    private String barcode;

    public SMProductModel(String name, double value, String barcode) {
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
