package br.com.studios.sketchbook.service_management_core.price;

import jakarta.persistence.Column;

import java.io.Serial;
import java.io.Serializable;

public class Money implements Serializable {

    /// Número de série da entidade
    @Serial
    @Column(name = "version")
    private static final long serialVersionUID = 1L;

}
