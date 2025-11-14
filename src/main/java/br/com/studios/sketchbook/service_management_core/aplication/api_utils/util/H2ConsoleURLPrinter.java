package br.com.studios.sketchbook.service_management_core.aplication.api_utils.util;

import org.springframework.core.env.Environment;

public class H2ConsoleURLPrinter {

    public static void printH2ConsoleUrl(Environment env) {
        String port = env.getProperty("local.server.port", "8080");
        String consolePath = env.getProperty("spring.h2.console.path", "/h2-console");
        boolean enabled = Boolean.parseBoolean(env.getProperty("spring.h2.console.enabled", "false"));

        if (enabled) {
            String url = "http://localhost:" + port + consolePath;
            System.out.println("\n" + "=".repeat(80));
            System.out.println("🗄️  H2 Console: " + url);
            System.out.println("📋 JDBC URL: " + env.getProperty("spring.datasource.url"));
            System.out.println("=".repeat(80) + "\n");
        }
    }

}
