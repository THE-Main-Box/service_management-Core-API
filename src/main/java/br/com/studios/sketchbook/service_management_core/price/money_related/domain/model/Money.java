package br.com.studios.sketchbook.service_management_core.price.money_related.domain.model;

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
    @Column(precision = 38, scale = 6, nullable = false)
    private BigDecimal price;

    @Getter
    @Setter
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    public Money(BigDecimal price, String currency) {
        this.price = price;
        this.currency = currency;
    }

    public Money(double price, String currency) {
        this.price = new BigDecimal(price);
        this.currency = currency;
    }

    public Money cpy() {
        return new Money(this.price, this.currency);
    }

    @Override
    public String toString() {
        return "Money{" +
                "value=" + price +
                ", currency='" + currency + '\'' +
                '}';
    }
}
