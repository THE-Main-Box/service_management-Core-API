package br.com.studios.sketchbook.service_management_core.storage.shared.util.manager.value_related;

import br.com.studios.sketchbook.service_management_core.storage.domain.model.StorageEntry;

import static br.com.studios.sketchbook.service_management_core.storage.shared.util.manager.value_related.StorageEntryConverterDataManager.getScaleByVolumeType;
import static br.com.studios.sketchbook.service_management_core.storage.shared.util.manager.validation_related.StorageEntryValidateDataManager.validateSpecialType;

public class StorageEntryValueDataManager {

    /**
     * Remove valor das subunidades (apenas para tipos especiais).
     * <p>
     * Contrato:
     * - raw == true  → 'quantity' já está em subunidades (ex: gramas / ml / unidades internas)
     * - raw == false → 'quantity' está no formato humano (ex: kg / L / unidades) e será convertido
     * para subunidades usando a escala apropriada (kg -> g, L -> ml, etc).
     * <p>
     * Nota: se o que você quer remover são unidades inteiras, use removeUnit(...).
     */
    public void subtractSubQuantity(StorageEntry entry, Long quantity, boolean raw) {
        validateSpecialType(entry);

        if (quantity == null) throw new IllegalArgumentException("quantity não pode ser nulo");

        // garante que não trabalhemos com null internamente
        long current = entry.getSubUnits() == null ? 0L : entry.getSubUnits();

        long toSubtract;
        try {
            if (raw) {
                // quantidade já está em subunidades
                toSubtract = Math.subtractExact(current, quantity);
            } else {
                long scale = getScaleByVolumeType(entry.getVolumeType()); // 1000 para kg/l, 1 para unidade
                long converted = Math.multiplyExact(quantity, scale);
                toSubtract = Math.subtractExact(current, converted);
            }
        } catch (ArithmeticException ex) {
            throw new ArithmeticException("Overflow ao calcular subunidades para remoção: " + ex.getMessage());
        }

        entry.setSubUnits(toSubtract);
    }

    /**
     * Adiciona valor nas subunidades (apenas para tipos especiais).
     * <p>
     * Contrato:
     * - raw == true  → 'quantity' já está em subunidades (ex: gramas / ml / unidades internas)
     * - raw == false → 'quantity' está no formato humano (ex: kg / L / unidades) e será convertido
     * para subunidades usando a escala apropriada (kg -> g, L -> ml, etc).
     * <p>
     * Nota: se o que você quer adicionar são unidades inteiras, use addUnit(...).
     */
    public void addSubQuantity(StorageEntry entry, Long quantity, boolean raw) {
        validateSpecialType(entry);

        if (quantity == null) throw new IllegalArgumentException("quantity não pode ser nulo");

        long current = entry.getSubUnits() == null ? 0L : entry.getSubUnits();

        long toAdd;
        try {
            if (raw) {
                // 'quantity' já é o valor em subunidades
                toAdd = Math.addExact(current, quantity);
            } else {
                long scale = getScaleByVolumeType(entry.getVolumeType());
                long converted = Math.multiplyExact(quantity, scale);
                toAdd = Math.addExact(current, converted);
            }
        } catch (ArithmeticException ex) {
            throw new ArithmeticException("Overflow ao calcular subunidades para adição: " + ex.getMessage());
        }

        entry.setSubUnits(toAdd);
    }

    /**
     * Adiciona unidades ao produto. Lida com tipos normais e tipos especiais.
     *
     * @param quantity quantidade de unidades a adicionar
     * @param raw      (true) usamos o valor sem conversão, ou seja usamos o valor minimo, como sub-unidades
     *                 (false) usamos o valor com a necessidade de conversão, ou seja valor maior para converter
     */
    public void addUnit(StorageEntry entry, Long quantity, boolean raw) {
        if (!entry.getVolumeType().isCompostType()) {
            long toAdd = raw
                    ? quantity
                    : Math.multiplyExact(quantity, getScaleByVolumeType(entry.getVolumeType()));

            entry.setUnits(Math.addExact(entry.getUnits(), toAdd));
        } else {
            long subQuantity = Math.multiplyExact(
                    quantity,
                    entry.getQuantityPerUnit()
            );

            addSubQuantity(
                    entry,
                    subQuantity,
                    true
            );
        }
    }

    /**
     * Remove unidades do produto, mantendo a mesma lógica de addUnit.
     *
     * @param quantity quantidade de unidades a remover
     * @param raw      se true, a quantidade já está no formato interno (subunidades),
     *                 se false, é um valor "human-readable" que precisa ser convertido
     */
    public void subtractUnit(StorageEntry entry, Long quantity, boolean raw) {

        if (!entry.getVolumeType().isCompostType()) {
            long toSubtract = raw ? quantity : Math.multiplyExact(quantity, getScaleByVolumeType(entry.getVolumeType()));
            entry.setUnits(Math.subtractExact(entry.getUnits(), toSubtract));
        } else {
            long subQuantity = Math.multiplyExact(quantity, entry.getQuantityPerUnit());

            subtractSubQuantity(entry, subQuantity, true);
        }
    }


}
