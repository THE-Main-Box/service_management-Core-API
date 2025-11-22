package br.com.studios.sketchbook.service_management_core.storage.shared.util.manager.validation_related;

import br.com.studios.sketchbook.service_management_core.storage.domain.model.StorageEntry;

public class StorageEntryValidateDataManager {

    public static void validateSpecialType(StorageEntry entry) {
        if (!entry.getVolumeType().isCompostType()) {
            throw new IllegalStateException("Operação válida apenas para tipos especiais: " + entry.getId());
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
        if (entry.getVolumeType().isCompostType()) {
            if (qpu != null && qpu <= 0)
                throw new IllegalStateException("quantityPerUnit deve ser > 0 para tipos especiais");
        }
    }

}
