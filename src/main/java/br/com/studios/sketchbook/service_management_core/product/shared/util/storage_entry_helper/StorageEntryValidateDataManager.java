package br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper;

import br.com.studios.sketchbook.service_management_core.product.domain.model.aux_model.StorageEntry;

public class StorageEntryValidateDataManager {

    public static void validateSpecialType(StorageEntry entry) {
        if (!entry.isInit()) throw new IllegalStateException("Produto não iniciado: " + entry.getProduct());
        if (!entry.getVType().isSpecialType()) {
            throw new IllegalStateException("Operação válida apenas para tipos especiais: " + entry.getProduct());
        }
    }

    /**
     * Valida invariantes mínimas após update.
     */
    public static void validateEntryPostUpdate(StorageEntry entry) {
        Long units = entry.getUnits();
        Long sub = entry.getSubUnits();
        Long qpu = entry.getQuantityPerUnit();

        if (units != null && units < 0) throw new IllegalStateException("units negativo");
        if (sub != null && sub < 0) throw new IllegalStateException("subUnits negativo");
        if (entry.getVType().isSpecialType()) {
            if (qpu != null && qpu <= 0)
                throw new IllegalStateException("quantityPerUnit deve ser > 0 para tipos especiais");
        }
    }

}
