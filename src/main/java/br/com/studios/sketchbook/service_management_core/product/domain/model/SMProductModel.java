package br.com.studios.sketchbook.service_management_core.product.domain.model;


import br.com.studios.sketchbook.service_management_core.product.domain.dto.super_market.SMProductCreationDTO;
import br.com.studios.sketchbook.service_management_core.product.shared.enums.VolumeType;
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
public class SMProductModel extends Product {
    /// Número de série da entidade
    @Serial
    @Column(name = "version")
    private static final long serialVersionUID = 1L;

    /// Código de barras do produto
    @Column(name = "bar_code")
    @Getter
    @Setter
    private String barcode;

    public SMProductModel(String name, String barcode, VolumeType type) {
        super(name, type);
        this.barcode = barcode;
    }

    public SMProductModel(SMProductCreationDTO dto) {
        this.barcode = dto.barCode();
        this.name = dto.name();
    }
}
