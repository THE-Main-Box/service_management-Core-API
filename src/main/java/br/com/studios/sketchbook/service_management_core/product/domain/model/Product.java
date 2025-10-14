package br.com.studios.sketchbook.service_management_core.product.domain.model;

import br.com.studios.sketchbook.service_management_core.price.price_related.shared.interfaces.PriceOwner;
import br.com.studios.sketchbook.service_management_core.product.domain.dto.product.ProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage.shared.interfaces.StorageAble;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/// Classe base para todos os produtos que poderão existir dentro do meu sistema
@Entity
@Table(name = "TB_PRODUCT")
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Product implements Serializable, PriceOwner, StorageAble {

    //TODO: Reavaliar o hash code dos produtos
    // para impedir a adição de produtos semelhantes ou repetidos de forma indevida

    /// Número de série da entidade
    @Serial
    @Column(name = "version")
    private static final long serialVersionUID = 1L;

    /// Id geral do produto
    @Id
    @Getter
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected UUID id;

    /// Nome com o qual o produto será referenciado
    @Getter
    @Setter
    @Column(name = "name", nullable = false)
    protected String name;

    public Product(String name) {
        this.name = name;
    }

    public Product(ProductCreationDTO dto){
        this(dto.name());
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
