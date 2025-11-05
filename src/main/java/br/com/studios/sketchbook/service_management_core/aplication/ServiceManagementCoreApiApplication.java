package br.com.studios.sketchbook.service_management_core.aplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "br.com.studios.sketchbook.service_management_core") // Pacote base de Services, Controllers, etc.
@EnableJpaRepositories(basePackages = "br.com.studios.sketchbook.service_management_core") // Pacote base de Repositories
@EntityScan(basePackages = "br.com.studios.sketchbook.service_management_core") // Pacote base de Repositories
public class ServiceManagementCoreApiApplication {

    public static void main(String[] args) {
        //Iniciamos o aplicativo, e permitimos que o spring gerencie sua inicialização
        SpringApplication app = new SpringApplication(ServiceManagementCoreApiApplication.class);

        //Adicionamos o profile padrão a ser utilizado
        app.setAdditionalProfiles("prod");

        //Rodamos o aplicativo passando os argumentos da inicialização
        ConfigurableApplicationContext context = app.run(args);

        printH2ConsoleUrl(context.getEnvironment());

    }

    private static void printH2ConsoleUrl(Environment env) {
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
