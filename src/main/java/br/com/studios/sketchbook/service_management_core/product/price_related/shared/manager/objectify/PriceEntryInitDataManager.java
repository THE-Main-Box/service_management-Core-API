package br.com.studios.sketchbook.service_management_core.product.price_related.shared.manager.objectify;

import br.com.studios.sketchbook.service_management_core.price.domain.model.Money;
import br.com.studios.sketchbook.service_management_core.product.price_related.domain.model.PriceEntry;
import org.springframework.stereotype.Component;

@Component
public class PriceEntryInitDataManager {

    public void initEntry(PriceEntry entry, double price, String currency){
        //Seta o preço enquanto cria um objeto do tipo "Money" com o tipo de moeda desejado
        entry.setPrice(
                new Money(
                        price,
                        currency
                )
        );
    }



}
