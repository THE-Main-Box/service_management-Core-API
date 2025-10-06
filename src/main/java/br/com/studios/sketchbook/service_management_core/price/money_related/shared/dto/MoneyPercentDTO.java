package br.com.studios.sketchbook.service_management_core.price.money_related.shared.dto;

import br.com.studios.sketchbook.service_management_core.price.money_related.domain.model.Money;

import java.math.BigDecimal;

public record MoneyPercentDTO(
        Money money,
        BigDecimal percent
) {
}
