package br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.objectfying_related;

import br.com.studios.sketchbook.service_management_core.product.domain.dto.storage_entry.StorageEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.product.domain.model.aux_model.StorageEntry;
import br.com.studios.sketchbook.service_management_core.product.shared.enums.VolumeType;

import static br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.value_related.StorageEntryConverterDataManager.*;
import static br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.validation_related.StorageEntryValidateDataManager.validateEntryPostUpdate;
import static br.com.studios.sketchbook.service_management_core.product.shared.util.storage_entry_helper.validation_related.StorageEntryValidateDataManager.validateSpecialType;

public class StorageEntryUpdateDataManager {
    /**
     * Atualiza uma StorageEntry a partir de um DTO.
     * <p>
     * Contrato:
     * - dto.type(), se presente, define o tipo alvo (interpreta-se o DTO como referente ao tipo alvo).
     * - raw == true  -> valores no DTO já estão na escala interna (subunidades / internal units).
     * - raw == false -> valores no DTO estão em "forma humana" e precisam ser convertidos por getScaleByVolumeType(...)
     * <p>
     * Regras resumidas:
     * - tipos simples: apenas units é utilizado (convertido se raw==false).
     * - tipos especiais:
     * * subUnits tem prioridade (se enviado sobrescreve).
     * * quantityPerUnit (qpu) atualiza qpu e, se já houver subUnits, recalcula units.
     * * units: se veio junto com qpu -> recalcula subUnits = units * qpu; caso contrário, se qpu existir -> subUnits = units * qpu;
     * se subUnits também veio no mesmo request priorizamos subUnits enviado.
     */
    public void editEntry(StorageEntry entry, StorageEntryUpdateDTO dto, boolean raw) {
        // 1) decide tipo alvo (DTO tem prioridade)
        VolumeType oldType = entry.getVType();
        if (dto.type() != null && dto.type() != oldType) {
            entry.setVType(dto.type());
        }
        VolumeType targetType = entry.getVType();

        if (dto.type() != null
                && dto.type().isCompostType()
                && dto.quantityPerUnit() == null
                || dto.quantityPerUnit() == 0
        ) {
            throw new IllegalArgumentException(
                    "É importante a existencia da quantidade por unidade," +
                            " na edição de um objeto de tipo de volume que nao seja simples"
            );
        }

        // 2) converte os campos do DTO para a escala interna (aplica scale somente onde faz sentido)
        long scale = getScaleByVolumeType(targetType);
        final Long unitsInternal = convertUnitsField(dto.units(), targetType, scale, raw);
        final Long subUnitsInternal = convertSubUnitsField(dto.subUnits(), scale, raw);
        final Long qpuInternal = convertQpuField(dto.quantityPerUnit(), scale, raw);

        // 3) aplica por categoria
        if (!targetType.isCompostType()) {
            applySimpleTypeUpdate(entry, unitsInternal);
        } else {
            applySpecialTypeUpdate(entry, unitsInternal, subUnitsInternal, qpuInternal);
        }

        // 4) valida invariantes mínimas após a atualização
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
     * Aplica atualização para tipos especiais (KILOGRAM_PER_UNIT, LITER_PER_UNITY, UNITY_PER_UNITY).
     * Ordem/prioridade aplicada:
     * 1) subUnits enviado -> sobrescreve (tem prioridade).
     * 2) quantityPerUnit enviado -> sobrescreve; se já há subUnits, recalcula units via floorDiv.
     * 3) units enviado:
     * - se veio também qpu no mesmo request -> recalcula subUnits = units * qpuAtual (qpu já atualizado acima).
     * - se não veio subUnits e entry.qpu existe -> recalcula subUnits = units * entry.qpu.
     * - se subUnits foi enviado no mesmo request -> mantemos subUnits e apenas setUnits (não reescrevemos subUnits).
     */
    private void applySpecialTypeUpdate(StorageEntry entry, Long unitsInternal, Long subUnitsInternal, Long qpuInternal) {
        boolean subUpdated = false;

        // 1) subUnits (prioridade)
        if (subUnitsInternal != null) {
            entry.setSubUnits(subUnitsInternal);
            // sincroniza units com o novo subUnits => garante consistência imediata
            // usa syncUnitsOnSubUnits, que valida qpu existente
            // Só sincroniza se quantityPerUnit já estiver definido (caso contrário deixamos para quando qpu for definido)
            if (entry.getQuantityPerUnit() != null) {
                // syncUnitsOnSubUnits já verifica qpu válido
                syncUnitsOnSubUnits(entry);
            }
            subUpdated = true;
        }

        // 2) quantityPerUnit (qpu)
        if (qpuInternal != null) {
            entry.setQuantityPerUnit(qpuInternal);
            // após atualizar qpu, se já há subUnits devemos sincronizar units usando a nova qpu.
            if (entry.getSubUnits() != null) {
                syncUnitsOnSubUnits(entry);
            }
        }

        // 3) units
        if (unitsInternal != null) {
            if (qpuInternal != null) {
                // veio units + qpu no mesmo request -> use qpu atualizado para calcular subUnits
                long qpu = entry.getQuantityPerUnit();
                long newSub = safeMultiply(unitsInternal, qpu, "units * quantityPerUnit");
                entry.setUnits(unitsInternal);
                entry.setSubUnits(newSub);
                // units já consistente com subUnits; exit
                return;
            }

            if (!subUpdated && entry.getQuantityPerUnit() != null) {
                // veio só units: derive subUnits usando qpu existente
                long qpu = entry.getQuantityPerUnit();
                long newSub = safeMultiply(unitsInternal, qpu, "units * quantityPerUnit");
                entry.setUnits(unitsInternal);
                entry.setSubUnits(newSub);
                return;
            }

            // fallback: subUnits foi atualizado neste request => preferimos manter subUnits,
            // mas atualizamos units para o valor explícito do DTO (se fornecido).
            entry.setUnits(unitsInternal);
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
            throw new IllegalStateException("quantityPerUnit inválido ou não configurado para: " + entry.getProduct());
        }

        long sub = entry.getSubUnits() == null ? 0L : entry.getSubUnits();

        // floorDiv protege contra valores negativos e faz divisão inteira consistente
        long newUnits = Math.floorDiv(sub, qpu);
        entry.setUnits(newUnits);
    }
}
