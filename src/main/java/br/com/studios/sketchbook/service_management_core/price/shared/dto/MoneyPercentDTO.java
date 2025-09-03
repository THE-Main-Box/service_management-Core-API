package br.com.studios.sketchbook.service_management_core.price.shared.dto;

import br.com.studios.sketchbook.service_management_core.price.domain.Money;

import java.math.BigDecimal;

public record MoneyPercentDTO(
        Money money,
        BigDecimal percent
) {
}
