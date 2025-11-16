package br.com.studios.sketchbook.service_management_core.aplication.api_utils.config;

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

import static br.com.studios.sketchbook.service_management_core.aplication.api_utils.references.ConfigRefNames.StorageConfig.*;
import static br.com.studios.sketchbook.service_management_core.aplication.api_utils.references.PackageNames.base_package_path;

@Configuration
@EnableTransactionManagement
@Profile({"prod", "storage"})
@EnableJpaRepositories(
        basePackages = base_package_path,
        entityManagerFactoryRef = storage_entity_manager_factory_ref,
        transactionManagerRef = storage_transaction_manager_ref
)
@EntityScan(base_package_path)
public class StorageDataSourceConfig {

    @Primary
    @Bean(name = storage_data_source_properties_ref)
    @ConfigurationProperties("spring.datasource.storage")
    /// Configura as propriedades do dataSource para o storage
    public DataSourceProperties storageDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = storage_data_source_ref)
    /// Inicializa o dataSource com as configurações passadas para o storage
    public DataSource storageDataSource(@Qualifier(storage_data_source_properties_ref) DataSourceProperties properties) {
        System.out.println("========================================");
        System.out.println("🔧 Configurando DataSource:");
        System.out.println("   URL: " + properties.getUrl());
        System.out.println("   Driver: " + properties.getDriverClassName());
        System.out.println("   Username: " + properties.getUsername());
        System.out.println("========================================");

        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean(name = storage_entity_manager_factory_ref)
    /// Configura e cria o sistema de gerenciamento de entidades
    public LocalContainerEntityManagerFactoryBean storageEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(storage_data_source_ref) DataSource dataSource) {

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
                .packages(base_package_path)
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
