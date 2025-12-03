package br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model;

import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.interfaces.PriceOwner;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.product.req.ProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.interfaces.StorageAble;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/// Classe base para todos os produtos que poderão existir dentro do meu sistema
@Entity
@Table(
        name = "TB_PRODUCT"
)
@NoArgsConstructor
public class Product implements Serializable, PriceOwner, StorageAble, Item {

    /// Número de série da entidade
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Getter
    @Setter
    @Column(nullable = false, unique = true)
    private String name;

    public Product(String name) {
        this.name = name;
    }

    public Product(ProductCreationDTO dto) {
        this(dto.name());
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public String getItemType() {
        return this.getClass().getSimpleName();
    }
}
