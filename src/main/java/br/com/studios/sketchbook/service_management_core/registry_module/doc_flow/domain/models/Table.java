package br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.models;

import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.enumerators.DocumentPrefix;
import br.com.studios.sketchbook.service_management_core.registry_module.doc_flow.domain.serial_models.json.TableJsonSerialModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Table {

    /// Id da tabela
    @Getter
    private final Integer id;

    /// Prefixo para auxiliar o rastreamento do documento
    @Getter
    @Setter
    private DocumentPrefix documentPrefix = DocumentPrefix.NON_DEFINED;

    /// Nome com o qual poderemos identificar a table
    @Getter
    @Setter
    private String name;

    /// Valor que determina se podemos sobrescrever os dados da tabela
    @Getter
    @Setter
    private boolean canBeOverridden = true;

    /// Lista de linhas que pertencem a essa tabela
    @Getter
    private final List<Integer> rowIdList;

    @Getter
    private final LocalDateTime createdAt;

    @Getter
    private LocalDateTime updatedAt;

    public Table(Integer id) {
        this.id = id;
        this.rowIdList = new ArrayList<>();

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Table(Integer id, LocalDateTime createdAt) {
        this.id = id;
        this.rowIdList = new ArrayList<>();


        this.createdAt = createdAt;
    }

    public Table(Integer id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.rowIdList = new ArrayList<>();


        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Table(Integer id, List<Integer> rowIdList) {
        this.id = id;
        this.rowIdList = rowIdList;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Table(
            Integer id,
            String name,
            List<Integer> rowIdList,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;

        this.rowIdList = rowIdList;

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Table(TableJsonSerialModel model){
        this(
                model.id(),
                model.name(),
                model.rowIdList(),
                model.createdAt(),
                model.updatedAt()
        );

        this.canBeOverridden = model.canBeOverridden();
        this.documentPrefix = DocumentPrefix.valueOf(
                model.documentPrefix()
        );
    }

    public void updateUpdateAtValue() {
        this.updatedAt = LocalDateTime.now();
    }


}
