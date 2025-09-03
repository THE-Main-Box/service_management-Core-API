package br.com.studios.sketchbook.service_management_core.price.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
@NoArgsConstructor
public class Money implements Serializable {

    /// Número de série da entidade
    @Serial
    @Column(name = "version")
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    @Column(name = "value", precision = 38, scale = 6, nullable = false)
    private BigDecimal value;

    @Getter
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    public Money(BigDecimal value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public Money(Double value, String currency) {
        this.value = new BigDecimal(value);
        this.currency = currency;
    }

    public Money cpy() {
        return new Money(this.value, this.currency);
    }

    @Override
    public String toString() {
        return "Money{" +
                "value=" + value +
                ", currency='" + currency + '\'' +
                '}';
    }
}
