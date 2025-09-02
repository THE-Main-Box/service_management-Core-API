package br.com.studios.sketchbook.service_management_core.product.domain.model.aux_model;

import jakarta.persistence.Column;

import java.io.Serial;
import java.io.Serializable;

public class PriceEntry implements Serializable {

    /// Número de série da entidade
    @Serial
    @Column(name = "version")
    private static final long serialVersionUID = 1L;

}
