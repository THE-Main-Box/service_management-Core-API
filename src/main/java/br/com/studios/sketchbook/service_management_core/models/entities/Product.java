package br.com.studios.sketchbook.service_management_core.models.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/// Classe base para todos os produtos que poderão existir dentro do meu sistema
@MappedSuperclass
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Product implements Serializable {

    /// Id geral do produto
    @Id
    @Getter
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    protected UUID id;

    /// Nome com o qual o produto será referenciado
    @Getter
    @Column(name = "name", nullable = false)
    protected String name;

    /// Valor do produto
    @Getter
    @Column(name = "value", nullable = false, precision = 38, scale = 2)
    protected BigDecimal value;

    public Product(String name, double value) {
        this.name = name;
        this.value = BigDecimal.valueOf(value);
    }

    public void setValue(double value) {
        this.value = BigDecimal.valueOf(value);
    }

    public String getProductValueAsString(){
        return value.toPlainString();
    }
}
