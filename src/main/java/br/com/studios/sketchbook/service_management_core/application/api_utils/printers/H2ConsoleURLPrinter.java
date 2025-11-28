package br.com.studios.sketchbook.service_management_core.application.api_utils.printers;

import org.springframework.core.env.Environment;

public class H2ConsoleURLPrinter {

    public static void printH2ConsoleUrl(Environment env) {
        // Obtém o host. server.address será o endereço configurado (padrão: null ou localhost)
        // Usamos 127.0.0.1 como fallback seguro para evitar problemas de resolução de nome.
        String host = env.getProperty("server.address", "127.0.0.1");

        // Em um aplicativo Spring Boot, o 'local.server.port' é a porta real que o servidor está usando
        // se ele for iniciado (por exemplo, dentro de um evento ApplicationReadyEvent).
        String port = env.getProperty("server.port", "8080");

        String consolePath = env.getProperty("spring.h2.console.path", "/h2-console");
        boolean enabled = Boolean.parseBoolean(env.getProperty("spring.h2.console.enabled", "false"));

        if (enabled) {
            String url = "http://" + host + ":" + port + consolePath;
            System.out.println("\n" + "=".repeat(80));
            System.out.println("🗄️  H2 Console: " + url);

            // CORREÇÃO CRUCIAL: As chaves JDBC corretas são `jdbc-url`, não `url`.
            // As chaves que você usou antes (e que funcionaram) eram `spring.datasource.storage.jdbc-url`.
            System.out.println("📋 JDBC URL (Storage): " + env.getProperty("spring.datasource.storage.url"));
            System.out.println("📋 JDBC URL (Shipment): " + env.getProperty("spring.datasource.shipment.url"));
            // Se houver módulo Audit:
            System.out.println("📋 JDBC URL (Audit): " + env.getProperty("spring.datasource.audit.url"));

            System.out.println("=".repeat(80) + "\n");
        }
    }

}