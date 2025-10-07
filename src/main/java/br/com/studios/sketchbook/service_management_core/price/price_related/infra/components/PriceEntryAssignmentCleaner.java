package br.com.studios.sketchbook.service_management_core.price.price_related.infra.components;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PriceEntryAssignmentCleaner {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // tabelas que implementam PriceOwner
    private final List<String> ownerTables = List.of("tb_product", "tb_service");

    @PostConstruct
    public void cleanupOrphanAssignments() {
        // limpa owners órfãos
        for (String table : ownerTables) {
            String sql = """
                DELETE FROM tb_price_assignment
                WHERE owner_id NOT IN (SELECT id FROM %s)
            """.formatted(table);
            jdbcTemplate.update(sql);
        }

        // limpa entries órfãs
        String entrySql = """
            DELETE FROM tb_price_assignment
            WHERE entry_id NOT IN (SELECT id FROM tb_price_entry)
        """;
        jdbcTemplate.update(entrySql);
    }

}
