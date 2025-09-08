package br.com.studios.sketchbook.service_management_core.product.price_related.domain.model;

import br.com.studios.sketchbook.service_management_core.product.price_related.shared.enums.AdjustmentTrigger;
import br.com.studios.sketchbook.service_management_core.product.price_related.shared.enums.AdjustmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "TB_PRICE_MODIFIER")
@NoArgsConstructor
@AllArgsConstructor
public class PriceModifier implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "price_entry_id")
    @Getter
    @Setter
    private PriceEntry priceEntry;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    /// Tipo de ajuste de preço, desconto ou juros
    private AdjustmentType type; // DISCOUNT ou INTEREST

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    /// Gatilho para ativar o modificador de preço
    private AdjustmentTrigger trigger;

    @Getter
    @Setter
    /// Porcentagem do modificador, quer seja juros ou desconto
    private BigDecimal percentage;

    @Getter
    @Setter
    /// Quantidade raw usada como referência para ativar o modificador
    private long quantityOfVolumeNecessary;

    public PriceModifier(
            PriceEntry priceEntry,
            long quantityOfVolumeNecessary,
            AdjustmentTrigger trigger,
            BigDecimal percentage,
            AdjustmentType adjustmentType
    ) {
        this.priceEntry = priceEntry;
        this.quantityOfVolumeNecessary = quantityOfVolumeNecessary;
        this.trigger = trigger;
        this.percentage = percentage;
        this.type = adjustmentType;
    }
}
