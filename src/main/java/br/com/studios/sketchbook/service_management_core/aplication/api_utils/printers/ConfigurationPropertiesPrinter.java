package br.com.studios.sketchbook.service_management_core.aplication.api_utils.printers;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class ConfigurationPropertiesPrinter {

    /**
     * Escreve num terminal os dados das configurações a serem carregadas,
     * (importante para depuração)
     *
     * @param context Contexto de referência ao qualificador de dados de propriedade
     * @param properties Dados de propriedades usados para identificar as informações importantes
     * */
    public static void printDSProperties(String context, DataSourceProperties properties){
        System.out.println("========================================");
        System.out.println("DataSource configuration context: " + context);
        System.out.println("   URL: " + properties.getUrl());
        System.out.println("   Driver: " + properties.getDriverClassName());
        System.out.println("   Username: " + properties.getUsername());
        System.out.println("========================================");
    }

}
