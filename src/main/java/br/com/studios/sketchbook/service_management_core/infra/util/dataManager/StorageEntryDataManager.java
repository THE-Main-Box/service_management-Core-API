package br.com.studios.sketchbook.service_management_core.infra.util.dataManager;

import br.com.studios.sketchbook.service_management_core.models.data_transfer_objects.StorageEntryUpdateDTO;
import br.com.studios.sketchbook.service_management_core.models.entities.StorageEntry;
import br.com.studios.sketchbook.service_management_core.models.enumerators.VolumeType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static br.com.studios.sketchbook.service_management_core.models.enumerators.ValuesScaleConstants.KILOGRAMS;
import static br.com.studios.sketchbook.service_management_core.models.enumerators.ValuesScaleConstants.LITERS;

@Component
public class StorageEntryDataManager {

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
                && dto.type().isSpecialType()
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
        if (!targetType.isSpecialType()) {
            applySimpleTypeUpdate(entry, unitsInternal);
        } else {
            applySpecialTypeUpdate(entry, unitsInternal, subUnitsInternal, qpuInternal);
        }

        // 4) valida invariantes mínimas após a atualização
        validateEntryPostUpdate(entry);
    }

    /* ----------------------- Helpers extraídos para legibilidade ----------------------- */

    /**
     * Conversão do campo units conforme o tipo alvo.
     * - Para tipos simples, units é convertido por scale (se raw==false).
     * - Para tipos especiais, units é contagem e NÃO é escalado (raw é ignorado).
     */
    private Long convertUnitsField(Long dtoUnits, VolumeType targetType, long scale, boolean raw) {
        if (dtoUnits == null) return null;
        if (!targetType.isSpecialType()) {
            // tipos simples: armazenamos em escala interna (ex: kg -> g)
            return raw ? dtoUnits : safeMultiply(dtoUnits, scale, "units");
        } else {
            // tipos especiais: units é contagem (embalagens) — não escala
            return dtoUnits;
        }
    }

    /**
     * Conversão do campo subUnits (sempre escala para a unidade interna quando raw == false).
     */
    private Long convertSubUnitsField(Long dtoSubUnits, long scale, boolean raw) {
        if (dtoSubUnits == null) return null;
        return raw ? dtoSubUnits : safeMultiply(dtoSubUnits, scale, "subUnits");
    }

    /**
     * Conversão do campo quantityPerUnit (scale aplicado quando raw == false)
     */
    private Long convertQpuField(Long dtoQpu, long scale, boolean raw) {
        if (dtoQpu == null) return null;
        return raw ? dtoQpu : safeMultiply(dtoQpu, scale, "quantityPerUnit");
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
     * Valida invariantes mínimas após update.
     */
    private void validateEntryPostUpdate(StorageEntry entry) {
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

    /**
     * Multiplicação segura com tratamento de overflow e mensagem contextual.
     */
    private long safeMultiply(long a, long b, String context) {
        try {
            return Math.multiplyExact(a, b);
        } catch (ArithmeticException ex) {
            throw new ArithmeticException("Overflow ao multiplicar (" + context + "): " + ex.getMessage());
        }
    }

    /**
     * overloads para Long nullable -> delega a safeMultiply
     */
    private Long safeMultiply(Long a, long b, String context) {
        if (a == null) return null;
        return safeMultiply(a.longValue(), b, context);
    }


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
    public void removeSubQuantity(StorageEntry entry, Long quantity, boolean raw) {
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
                long scale = getScaleByVolumeType(entry.getVType()); // 1000 para kg/l, 1 para unidade
                long converted = Math.multiplyExact(quantity, scale);
                toSubtract = Math.subtractExact(current, converted);
            }
        } catch (ArithmeticException ex) {
            throw new ArithmeticException("Overflow ao calcular subunidades para remoção: " + ex.getMessage());
        }

        entry.setSubUnits(toSubtract);
        syncUnitsOnSubUnits(entry);
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
                long scale = getScaleByVolumeType(entry.getVType());
                long converted = Math.multiplyExact(quantity, scale);
                toAdd = Math.addExact(current, converted);
            }
        } catch (ArithmeticException ex) {
            throw new ArithmeticException("Overflow ao calcular subunidades para adição: " + ex.getMessage());
        }

        entry.setSubUnits(toAdd);
        syncUnitsOnSubUnits(entry);
    }

    /**
     * Sincroniza unidades inteiras a partir das subunidades.
     * Rotina:
     * - exige quantityPerUnit definido (>0) (quantas subunidades compõem 1 unidade)
     * - faz floorDiv(subUnits, quantityPerUnit) para garantir unidades inteiras sem arredondamento
     */
    private void syncUnitsOnSubUnits(StorageEntry entry) {
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

    /**
     * Retorna o valor restante em subunidades que não formam uma unidade inteira.
     * Este valor permanece no armazenamento, sem alterar a contagem de unidades.
     */
    public Long getRemainder(StorageEntry entry) {
        return entry.getSubUnits() % entry.getQuantityPerUnit();
    }

    /**
     * Retorna o multiplicador de escala para o tipo de volume do produto.
     * Garante que a conversão entre unidades e subunidades seja correta.
     */
    public Long getScaleByVolumeType(VolumeType vType) {
        return switch (vType) {
            case LITER, LITER_PER_UNITY -> LITERS.getScale();
            case KILOGRAM, KILOGRAM_PER_UNIT -> KILOGRAMS.getScale();
            case UNIT, UNITY_PER_UNITY -> 1L;
        };
    }

    /**
     * Obtemos a quantidade disponível de um produto.
     * Se for um produto com tipos básicos, ainda iremos precisar realizar conversão para os tipos de quilo e litro.
     * Se for um tipo especial, a conversão se torna um pouco problemática, pois precisaríamos obter das subunidades,
     * porém não é nada muito complexo
     */
    public BigDecimal getAmountAvailable(StorageEntry entry) {

        return switch (entry.getVType()) {
            //Caso estejamos a lidar com um retorno de litros ou quilos, retornamos com a escala de litro e quilo
            case KILOGRAM, LITER -> BigDecimal.valueOf(entry.getUnits(), 3);
            //Caso estejamos a lidar com o retorno de unidades, não há necessidade de realizar conversão
            case UNIT -> BigDecimal.valueOf(entry.getUnits());
            //Caso unidade por quilo/litro, ainda é preciso realizar uma conversão
            case KILOGRAM_PER_UNIT, LITER_PER_UNITY -> BigDecimal.valueOf(entry.getSubUnits(), 3);
            //Unidade por unidade já é normal então está de boa
            case UNITY_PER_UNITY -> BigDecimal.valueOf(entry.getSubUnits());
        };

    }

    /// Obtemos os valores raw, ou seja, que são interpretados diretamente pelo sistema do jeito do sistema
    public BigDecimal getAmountAvailableRaw(StorageEntry entry) {
        return switch (entry.getVType()) {
            //Ao passar tipos comuns, podemos obter direto das unidades
            case KILOGRAM, LITER, UNIT -> BigDecimal.valueOf(entry.getUnits());
            //Ao passar tipos especiais precisamos pegar das subunidades
            case KILOGRAM_PER_UNIT, LITER_PER_UNITY, UNITY_PER_UNITY -> BigDecimal.valueOf(entry.getSubUnits());
        };

    }

    /**
     * Adiciona unidades ao produto. Lida com tipos normais e tipos especiais.
     *
     * @param quantity quantidade de unidades a adicionar
     * @param raw      (true) usamos o valor sem conversão, ou seja usamos o valor minimo, como sub-unidades
     *                 (false) usamos o valor com a necessidade de conversão, ou seja valor maior para converter
     */
    public void addUnit(StorageEntry entry, Long quantity, boolean raw) {
        if (!entry.isInit()) throw new IllegalStateException("Produto não iniciado: " + entry.getProduct());

        if (!entry.getVType().isSpecialType()) {
            long toAdd = raw
                    ? quantity
                    : Math.multiplyExact(quantity, getScaleByVolumeType(entry.getVType()));

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
    public void removeUnit(StorageEntry entry, Long quantity, boolean raw) {
        if (!entry.isInit()) throw new IllegalStateException("Produto não iniciado: " + entry.getProduct());

        if (!entry.getVType().isSpecialType()) {
            long toSubtract = raw ? quantity : Math.multiplyExact(quantity, getScaleByVolumeType(entry.getVType()));
            entry.setUnits(Math.subtractExact(entry.getUnits(), toSubtract));
        } else {
            long subQuantity = Math.multiplyExact(quantity, entry.getQuantityPerUnit());

            removeSubQuantity(entry, subQuantity, true);
        }
    }

    /**
     * Inicializa a entrada de armazenamento para tipos básicos.
     * Se necessário, aplica a conversão da quantidade para o valor mínimo interno.
     *
     * @param entry    Produto a ter a sua entrada alterada
     * @param quantity Quantidade a respeito do seu tipo de volume
     * @param raw      Se precisamos realizar uma escalação pros valores serem armazenados corretamente
     */
    private void initBasicType(StorageEntry entry, Long quantity, boolean raw) {
        if (entry.getVType().isSpecialType()) {
            throw new IllegalArgumentException("Tipo incorreto para init básico: " + entry.getProduct());
        }

        long units = raw
                ? quantity
                : Math.multiplyExact(quantity, getScaleByVolumeType(entry.getVType()));

        entry.setUnits(units);
        entry.setInit(true);
    }

    /**
     * Inicializa a entrada de armazenamento para tipos especiais.
     * Calcula subunidades e unidades inteiras, garantindo a consistência do armazenamento.
     *
     * @param entry    Produto a ser afetado
     * @param quantity Quantidade de unidades
     * @param raw      Indica se os valores recebidos já estão no formato mínimo
     */
    private void initSpecialType(StorageEntry entry, Long quantity, Long quantityPerUnit, boolean raw) {
        if (!entry.getVType().isSpecialType()) {
            throw new IllegalArgumentException(
                    "Tipo incorreto para init especial: "
                            + entry.getId()
            );
        }
        if (quantityPerUnit == null || quantityPerUnit == 0) {
            throw new IllegalArgumentException(
                    "Tipos especiais precisam ter quantidade por unidade como valor obrigatório: "
                            + entry.getId()
            );
        }


        long scaleToEnter = raw
                ? quantityPerUnit
                : Math.multiplyExact(quantityPerUnit, getScaleByVolumeType(entry.getVType()));

        entry.setQuantityPerUnit(scaleToEnter);
        entry.setUnits(quantity);
        entry.setSubUnits(Math.multiplyExact(quantity, scaleToEnter));
        entry.setInit(true);
    }

    /**
     * Inicia a entrada, já atribuída a um produto
     *
     * @param entry           objeto a ser iniciado
     * @param quantity        quantidade do produto dentro do armazém, pode variar dependendo do tipo
     * @param quantityPerUnit quantidade por unidade caso seja de tipo especial, pode ser ignorado para tipos simples
     * @param raw             Se precisamos realizar uma conversão para os tipos internos,
     *                        ou se já estão convertidos de entrada
     */
    public void initEntry(StorageEntry entry, Long quantity, Long quantityPerUnit, boolean raw) {
        if (entry.getVType().isSpecialType()) {
            initSpecialType(entry, quantity, quantityPerUnit, raw);
        } else {
            initBasicType(entry, quantity, raw);
        }
    }

    private void validateSpecialType(StorageEntry entry) {
        if (!entry.isInit()) throw new IllegalStateException("Produto não iniciado: " + entry.getProduct());
        if (!entry.getVType().isSpecialType()) {
            throw new IllegalStateException("Operação válida apenas para tipos especiais: " + entry.getProduct());
        }
    }

}
