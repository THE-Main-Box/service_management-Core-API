package br.com.studios.sketchbook.service_management_core.product.product_related.domain.model;

import br.com.studios.sketchbook.service_management_core.product.price_related.domain.model.PriceEntry;
import br.com.studios.sketchbook.service_management_core.product.storage_related.domain.model.StorageEntry;
import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/// Classe base para todos os produtos que poderão existir dentro do meu sistema
@Entity(name = "TB_PRODUCT")
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    //TODO: Reavaliar o hash code dos produtos
    // para impedir a adição de produtos semelhantes ou repetidos de forma indevida

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

    //TODO:Quando expandir a funcionalidade do sistema,
    // talvez seja interessante separar as classes de modelo auxiliares para seus próprios pacotes

    @Getter
    @Enumerated(EnumType.STRING)
    protected VolumeType volumeType;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Getter
    @Setter
    protected StorageEntry storageEntry;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Getter
    @Setter
    protected PriceEntry priceEntry;

    public Product(String name, VolumeType type) {
        this.name = name;
        this.volumeType = type;
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
