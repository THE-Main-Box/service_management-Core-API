package br.com.studios.sketchbook.service_management_core.models.entities;


import br.com.studios.sketchbook.service_management_core.models.data_transfer_objects.SMProductCreationDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

/// Modelo de produtos de supermercado
@Entity
@Table(name = "TB_SUPER_MARKET-PRODUCTS")
@NoArgsConstructor
public class SMProductModel extends Product {
    /// Número de série da entidade
    @Serial
    private static final long serialVersionUID = 1L;

    /// Código de barras do produto
    @Column(name = "bar_code")
    @Getter
    @Setter
    private String barcode;

    public SMProductModel(String name, double value, String barcode) {
        super(name, value);
        this.barcode = barcode;
    }

    public SMProductModel(SMProductCreationDTO dto) {
        this.barcode = dto.barCode();
        this.name = dto.name();
        this.setValue(dto.value());
    }
}
