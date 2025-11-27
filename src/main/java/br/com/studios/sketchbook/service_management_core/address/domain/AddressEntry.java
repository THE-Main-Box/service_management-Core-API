package br.com.studios.sketchbook.service_management_core.address.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "TB_ADDRESS")
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntry implements Serializable {

    /// Número de série da entidade
    @Serial
    @Column(name = "version")
    private static final long serialVersionUID = 1L;

    /// Id geral do produto
    @Id
    @Getter
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /// Descrição breve a respeito do endereço
    @Getter
    @Setter
    @Column(length = 100)
    private String description;

    /// Rua em que se encontra
    @Setter
    @Getter
    private String street;

    /// Numero de identificação, n- da casa, prédio, depende d ocontexto
    @Setter
    @Getter
    @Column(name = "number", length = 10)
    private String number;

    /// Complemento
    @Setter
    @Getter
    @Column(name = "complement", length = 100)
    private String complement;

    /// Bairro
    @Setter
    @Getter
    @Column(name = "neighborhood", length = 100)
    private String neighborhood;

    /// Cidade
    @Setter
    @Getter
    @Column(name = "city", length = 100)
    private String city;

    /// Estado em que se encontra
    @Setter
    @Getter
    @Column(name = "state", length = 100)
    private String state;

    /// CEP
    @Setter
    @Getter
    @Column(name = "zip_code", length = 20)
    private String zipCode;

    public AddressEntry(
            String description,
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            String state,
            String zipCode
    ) {
        this.description = description;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

}
