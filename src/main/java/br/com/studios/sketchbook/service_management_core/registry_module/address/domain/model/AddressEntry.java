package br.com.studios.sketchbook.service_management_core.registry_module.address.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "TB_ADDRESS_ENTRY")
@AllArgsConstructor
@NoArgsConstructor
public class AddressEntry implements Serializable {

    @Serial
    @Column(name = "version")
    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Getter
    @Setter
    @Column(unique = true, length = 100)
    /// Descrição para identificação do endereço
    private String description;

    @Getter
    @Setter
    @Column(name = "zip_code", length = 10)
    /// CEP do local
    private String zipCode;

    @Getter
    @Setter
    @Column(name = "street_name" ,length = 150)
    /// Nome da rua
    private String streetName;

    @Getter
    @Setter
    @Column(name = "number", length = 20)
    /// Número
    private String number;

    @Getter
    @Setter
    @Column(name = "complement", length = 150)
    /// Complemento
    private String complement;

    @Getter
    @Setter
    @Column(name = "district", length = 100)
    /// Bairro
    private String district;

    @Getter
    @Setter
    @Column(name = "city", length = 100)
    /// Cidade
    private String city;

    @Getter
    @Setter
    @Column(name = "state", length = 2)
    /// Estado
    private String state;

    public AddressEntry(
            String description,
            String zipCode,
            String number,
            String streetName,
            String complement,
            String district,
            String city,
            String state
    ) {
        this.description = description;
        this.zipCode = zipCode;
        this.number = number;
        this.streetName = streetName;
        this.complement = complement;
        this.district = district;
        this.city = city;
        this.state = state;
    }
}
