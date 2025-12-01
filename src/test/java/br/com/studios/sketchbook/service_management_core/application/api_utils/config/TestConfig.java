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
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.ConfigRefNames.AddressConfig.*;
import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.ConfigRefNames.StorageConfig.*;
import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.ConfigRefNames.TestConfig.*;
import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.PackageNames.*;

@Configuration
@EnableTransactionManagement
@Profile("test")
@EnableJpaRepositories(
        basePackages = {
                storage_module_path,
                registry_module_path
        },
        entityManagerFactoryRef = test_entity_manager_factory_ref,
        transactionManagerRef = test_transaction_manager_ref
)
@EntityScan(basePackages = {
        storage_module_path,
        registry_module_path
})
public class TestConfig {

    // =========================================================
    // 1. DATASOURCE ÚNICO (PRIMARY) PARA TESTES
    // =========================================================

    @Primary
    @Bean(test_data_source_properties_ref)
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties testDSProp() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(test_data_source_ref)
    public DataSource testDS(
            @Qualifier(test_data_source_properties_ref) DataSourceProperties properties
    ) {
        System.out.println("=== CONFIGURAÇÃO DE TESTE ===");
        System.out.println("URL: " + properties.getUrl());
        System.out.println("Usando H2 em memória para todos os módulos");

        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    // =========================================================
    // 2. APENAS UM ENTITY MANAGER FACTORY PARA TODOS OS MÓDULOS
    // =========================================================

    @Primary
    @Bean(test_entity_manager_factory_ref)
    public LocalContainerEntityManagerFactoryBean testEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(test_data_source_ref) DataSource dataSource
    ) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");

        return builder
                .dataSource(dataSource)
                .packages(storage_module_path, registry_module_path)  // TODAS AS ENTIDADES
                .persistenceUnit("testPersistenceUnit")
                .properties(properties)
                .build();
    }

    // =========================================================
    // 3. APENAS UM TRANSACTION MANAGER PARA TODOS OS MÓDULOS
    // =========================================================

    @Primary
    @Bean(test_transaction_manager_ref)
    public PlatformTransactionManager testTransactionManager(
            @Qualifier(test_entity_manager_factory_ref) LocalContainerEntityManagerFactoryBean testEntityManagerFactory
    ) {
        assert testEntityManagerFactory.getObject() != null;
        return new JpaTransactionManager(testEntityManagerFactory.getObject());
    }

    // =========================================================
    // 4. BEANS DE COMPATIBILIDADE SIMPLIFICADOS
    // =========================================================

    @Bean(storage_data_source_ref)
    public DataSource storageDataSource() {
        return testDS(testDSProp());
    }

    @Bean(address_data_source_ref)
    public DataSource addressDataSource() {
        return testDS(testDSProp());
    }

    // IMPORTANTE: Estes beans DEVEM retornar os mesmos objetos do PRIMARY
    @Bean(storage_entity_manager_factory_ref)
    public LocalContainerEntityManagerFactoryBean storageEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(test_data_source_ref) DataSource dataSource
    ) {
        return testEntityManagerFactory(builder, dataSource);  // MESMO BEAN!
    }

    @Bean(storage_transaction_manager_ref)
    public PlatformTransactionManager storageTransactionManager() {
        return testTransactionManager(testEntityManagerFactory(
                new EntityManagerFactoryBuilder((JpaVendorAdapter) null, (Function<DataSource, Map<String, ?>>) null, null),
                testDS(testDSProp())
        ));
    }

    @Bean(address_entity_manager_factory_ref)
    public LocalContainerEntityManagerFactoryBean addressEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(test_data_source_ref) DataSource dataSource
    ) {
        return testEntityManagerFactory(builder, dataSource);  // MESMO BEAN!
    }

    @Bean(address_transaction_manager_ref)
    public PlatformTransactionManager addressTransactionManager() {
        return testTransactionManager(testEntityManagerFactory(
                new EntityManagerFactoryBuilder((JpaVendorAdapter) null, (Function<DataSource, Map<String, ?>>) null, null),
                testDS(testDSProp())
        ));
    }
}