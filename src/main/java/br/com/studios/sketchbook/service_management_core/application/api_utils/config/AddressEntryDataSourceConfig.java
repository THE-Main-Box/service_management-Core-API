package br.com.studios.sketchbook.service_management_core.application.api_utils.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.ConfigRefNames.AddressConfig.*;
import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.PathDirection.registry_module_path;

@Configuration
@EnableTransactionManagement
@Profile({"prod", "registry"})
@EnableJpaRepositories(
        basePackages = registry_module_path,
        entityManagerFactoryRef = address_entity_manager_factory_ref,
        transactionManagerRef = address_transaction_manager_ref
)
@EntityScan(registry_module_path)
public class AddressEntryDataSourceConfig {

    /// Configura as propriedades do dataSource para o address
    @Bean(name = address_data_source_properties_ref)//nome do bean
    @ConfigurationProperties("spring.datasource.registry")//carrega as config de address
    public DataSourceProperties addressDataSourceProperties() {
        return new DataSourceProperties();
    }

    /// Inicializa o dataSource com as configurações passadas para o storage
    @Bean(name = address_data_source_ref)
    public DataSource addressDataSource(@Qualifier(address_data_source_properties_ref) DataSourceProperties properties) {

        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    /// Configura e cria o sistema de gerenciamento de entidades
    @Bean(name = address_entity_manager_factory_ref)
    public LocalContainerEntityManagerFactoryBean addressEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(address_data_source_ref) DataSource dataSource
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
                .packages(registry_module_path)
                .persistenceUnit("address")
                .properties(properties)
                .build();
    }

    @Bean(name = address_transaction_manager_ref)
    public PlatformTransactionManager addressTransactionManager(
            @Qualifier(address_entity_manager_factory_ref) LocalContainerEntityManagerFactoryBean addressEntityManagerFactory
    ) {
        assert addressEntityManagerFactory.getObject() != null;
        return new JpaTransactionManager(addressEntityManagerFactory.getObject());
    }
}
