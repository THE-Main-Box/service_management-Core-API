package br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.util.manager.objectifying_related;

import br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.dto.StorageEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.storage_module.storage.domain.model.StorageEntry;
import br.com.studios.sketchbook.service_management_core.storage_module.product.shared.enums.VolumeType;

import static br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.util.manager.validation_related.StorageEntryValidateDataManager.validateEntryPostUpdate;
import static br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.util.manager.validation_related.StorageEntryValidateDataManager.validateSpecialType;
import static br.com.studios.sketchbook.service_management_core.storage_module.storage.shared.util.manager.value_related.StorageEntryConverterDataManager.*;

public class StorageEntryUpdateDataManager {
    /**
     * Atualiza uma StorageEntry com base nos dados do DTO.
     *
     * @param entry A entrada de estoque a ser atualizada
     * @param dto   DTO contendo os novos valores (campos opcionais)
     * @param raw   Se true, valores já estão na escala interna; se false, converte valores humanos
     *
     * Funcionamento:
     * 1. Valida se tipo composto tem quantityPerUnit (quando aplicável)
     * 2. Atualiza tipo de volume se fornecido
     * 3. Converte valores para escala interna (skip se null)
     * 4. Aplica atualização baseada no tipo
     * 5. Valida estado final
     */
    public void editEntry(StorageEntry entry, StorageEntryUpdateDTO dto, boolean raw) {

        // === 1. Determinar tipo alvo ===
        VolumeType targetType = dto.type() != null ? dto.type() : entry.getVolumeType();

        // === 2. Validar tipo composto (ANTES de qualquer operação) ===
        if (targetType.isCompostType()) {
            // Se está mudando para tipo composto ou atualizando tipo composto
            Long newQpu = dto.quantityPerUnit();
            Long existingQpu = entry.getQuantityPerUnit();

            // Precisa ter qpu (novo ou existente) e não pode ser zero
            if ((newQpu == null || newQpu == 0) && (existingQpu == null || existingQpu == 0)) {
                throw new IllegalArgumentException(
                        "Tipo composto requer quantityPerUnit definido e diferente de zero"
                );
            }
        }

        // === 3. Atualizar tipo se fornecido ===
        if (dto.type() != null && dto.type() != entry.getVolumeType()) {
            entry.setVolumeType(dto.type());
        }

        // === 4. Converter valores (null-safe) ===
        long scale = getScaleByVolumeType(targetType);

        Long unitsInternal = dto.units() != null
                ? convertUnitsField(dto.units(), targetType, scale, raw)
                : null;

        Long subUnitsInternal = dto.subUnits() != null
                ? convertSubUnitsField(dto.subUnits(), scale, raw)
                : null;

        Long qpuInternal = dto.quantityPerUnit() != null
                ? convertQpuField(dto.quantityPerUnit(), scale, raw)
                : null;

        // === 5. Aplicar atualização ===
        if (!targetType.isCompostType()) {
            applySimpleTypeUpdate(entry, unitsInternal);
        } else {
            applySpecialTypeUpdate(entry, unitsInternal, subUnitsInternal, qpuInternal);
        }

        // === 6. Validar estado final ===
        validateEntryPostUpdate(entry);
    }


    /**
     * Aplica atualização para tipos simples (KILOGRAM, LITER, UNIT).
     * - Só mudamos entry.units (se vier no DTO convertido).
     */
    private void applySimpleTypeUpdate(StorageEntry entry, Long unitsInternal) {
        if (unitsInternal != null) {
            entry.setUnits(unitsInternal);
        }
        // NOTA: não tocamos subUnits/quantityPerUnit para tipos simples.
    }

    /**
     * Aplica atualização para tipos compostos (KILOGRAM_PER_UNIT, LITER_PER_UNITY, UNITY_PER_UNITY).
     *
     * Regras de prioridade e consistência:
     * 1. subUnits tem prioridade absoluta - sempre recalcula units quando atualizado
     * 2. quantityPerUnit (QPU) é atualizado primeiro, depois recalcula units se houver subUnits
     * 3. units só é usado diretamente quando subUnits NÃO foi fornecido (recalcula subUnits)
     */
    private void applySpecialTypeUpdate(StorageEntry entry, Long unitsInternal, Long subUnitsInternal, Long qpuInternal) {

        // === 1. Atualizar QPU primeiro (se fornecido) ===
        if (qpuInternal != null) {
            entry.setQuantityPerUnit(qpuInternal);
        }

        // Garante que QPU está definido
        if (entry.getQuantityPerUnit() == null || entry.getQuantityPerUnit() <= 0) {
            throw new IllegalStateException("quantityPerUnit inválido para tipo composto");
        }

        // === 2. SubUnits tem PRIORIDADE (fonte da verdade) ===
        if (subUnitsInternal != null) {
            entry.setSubUnits(subUnitsInternal);
            syncUnitsOnSubUnits(entry);  // ← Recalcula units baseado em subUnits
            return; // Termina aqui - subUnits define tudo
        }

        // === 3. Se QPU foi atualizado mas subUnits não, recalcula units ===
        if (qpuInternal != null && entry.getSubUnits() != null) {
            syncUnitsOnSubUnits(entry);  // ← Recalcula units com novo QPU
            return;
        }

        // === 4. Units fornecido (quando subUnits NÃO foi fornecido) ===
        if (unitsInternal != null) {
            entry.setUnits(unitsInternal);

            // Recalcula subUnits para manter consistência
            long qpu = entry.getQuantityPerUnit();
            long newSubUnits = safeMultiply(unitsInternal, qpu, "units * quantityPerUnit");
            entry.setSubUnits(newSubUnits);
        }
    }


    /**
     * Sincroniza unidades inteiras a partir das subunidades.
     * Rotina:
     * - exige quantityPerUnit definido (>0) (quantas subunidades compõem 1 unidade)
     * - faz floorDiv(subUnits, quantityPerUnit) para garantir unidades inteiras sem arredondamento
     */
    public void syncUnitsOnSubUnits(StorageEntry entry) {
        validateSpecialType(entry);

        Long qpu = entry.getQuantityPerUnit();
        if (qpu == null || qpu <= 0L) {
            throw new IllegalStateException("quantityPerUnit inválido ou não configurado para: " + entry.getId());
        }

        long sub = entry.getSubUnits() == null ? 0L : entry.getSubUnits();

        // floorDiv protege contra valores negativos e faz divisão inteira consistente
        long newUnits = Math.floorDiv(sub, qpu);
        entry.setUnits(newUnits);
    }
}
