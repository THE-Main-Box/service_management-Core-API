package br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.shared.manager.objectify;

import br.com.studios.sketchbook.service_management_core.storage_module.price.money_related.domain.model.Money;
import br.com.studios.sketchbook.service_management_core.storage_module.price.price_related.domain.model.PriceEntry;
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
