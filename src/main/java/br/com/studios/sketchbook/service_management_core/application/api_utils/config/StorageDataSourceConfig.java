package br.com.studios.sketchbook.service_management_core.application.api_utils.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.ConfigRefNames.StorageConfig.*;
import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.PathDirection.storage_module_path;

@Configuration
@EnableTransactionManagement
@Profile({"prod", "storage"})
@EnableJpaRepositories(
        basePackages = storage_module_path,
        entityManagerFactoryRef = storage_entity_manager_factory_ref,
        transactionManagerRef = storage_transaction_manager_ref
)
@EntityScan(storage_module_path)
public class StorageDataSourceConfig {


    /// Configura as propriedades do dataSource para o storage
    @Primary
    @Bean(name = storage_data_source_properties_ref)
    @ConfigurationProperties(prefix = "spring.datasource.storage")
    public DataSourceProperties storageDataSourceProperties() {
        return new DataSourceProperties();
    }

    /// Inicializa o dataSource com as configurações passadas para o storage
    @Primary
    @Bean(name = storage_data_source_ref)
    public DataSource storageDataSource(
            @Qualifier(storage_data_source_properties_ref) DataSourceProperties properties
    ) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    /// Configura e cria o sistema de gerenciamento de entidades
    @Primary
    @Bean(name = storage_entity_manager_factory_ref)
    public LocalContainerEntityManagerFactoryBean storageEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(storage_data_source_ref) DataSource dataSource
    ) {

        HashMap<String, Object> properties = new HashMap<>();
        properties.put(
                "hibernate.hbm2ddl.auto",
                "update"
        );
        properties.put(
                "hibernate.dialect",
                "org.hibernate.dialect.H2Dialect"
        );
        properties.put(
                "hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl"
        );

        return builder
                .dataSource(dataSource)
                .packages(storage_module_path)
                .persistenceUnit("storage")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean(name = storage_transaction_manager_ref)
    public PlatformTransactionManager storageTransactionManager(
            @Qualifier(storage_entity_manager_factory_ref) LocalContainerEntityManagerFactoryBean storageEntityManagerFactory
    ) {
        assert storageEntityManagerFactory.getObject() != null;
        return new JpaTransactionManager(storageEntityManagerFactory.getObject());
    }

}
