package br.com.studios.sketchbook.service_management_core.application.api_utils.references;

public class ConfigRefNames {

    public static class StorageConfig {
        /// Referencia a configuração da dataSource
        public static final String storage_data_source_properties_ref = "storageDataSourceProperties";
        /// Referencia a criação do datasource
        public static final String storage_data_source_ref = "storageDataSource";

        /// Referencia ao manager de entidades
        public static final String storage_entity_manager_factory_ref = "storageEntityManagerFactory";
        /// Referencia ao manager de transações
        public static final String storage_transaction_manager_ref = "storageTransactionManager";
    }

    public static class AddressConfig{
        /// Referencia a configuração da dataSource
        public static final String address_data_source_properties_ref = "addressDataSourceProperties";
        /// Referencia a criação do datasource
        public static final String address_data_source_ref = "addressDataSource";

        /// Referencia ao manager de entidades
        public static final String address_entity_manager_factory_ref = "addressEntityManagerFactory";
        /// Referencia ao manager de transações
        public static final String address_transaction_manager_ref = "addressTransactionManager";
    }

}
