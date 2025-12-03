package br.com.studios.sketchbook.service_management_core.storage_module.product.domain.model;


import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.interfaces.PriceOwner;
import br.com.studios.sketchbook.service_management_core.storage_module.product.domain.dto.super_market.req.SMProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.interfaces.StorageAble;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/// Modelo de produtos de supermercado
@Entity
@Table(
        name = "TB_SUPER_MARKET_PRODUCTS"
)
@NoArgsConstructor
public class SuperMarketProduct implements Serializable, PriceOwner, StorageAble, Item{
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
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /// Código de barras do produto
    @Column(name = "bar_code")
    @Getter
    @Setter
    private String barcode;

    public SuperMarketProduct(String name, String barcode) {
        this.name = name;
        this.barcode = barcode;
    }

    public SuperMarketProduct(SMProductCreationDTO dto) {
        this(
                dto.name(),
                dto.barCode()
        );
    }
}
