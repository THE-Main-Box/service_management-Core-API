package br.com.studios.sketchbook.service_management_core.product.product_related.domain.model;


import br.com.studios.sketchbook.service_management_core.product.product_related.domain.dto.super_market.SMProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.product_related.shared.enums.VolumeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

/// Modelo de produtos de supermercado
@Entity
@Table(name = "TB_SUPER_MARKET_PRODUCTS")
@NoArgsConstructor
public class SuperMarketProduct extends Product {
    /// Número de série da entidade
    @Serial
    @Column(name = "version")
    private static final long serialVersionUID = 1L;

    /// Código de barras do produto
    @Column(name = "bar_code")
    @Getter
    @Setter
    private String barcode;

    public SuperMarketProduct(String name, String barcode, VolumeType type) {
        super(name);
        this.barcode = barcode;
    }

    public SuperMarketProduct(SMProductCreationDTO dto) {
        this(
                dto.name(),
                dto.barCode(),
                dto.volumeType()
        );
    }
}
