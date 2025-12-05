package br.com.studios.sketchbook.service_management_core.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import static br.com.studios.sketchbook.service_management_core.application.api_utils.printers.H2ConsoleURLPrinter.printH2ConsoleUrl;
import static br.com.studios.sketchbook.service_management_core.application.api_utils.references.PathDirection.*;

@SpringBootApplication
@ComponentScan(basePackages = {
        base_package_path,
        storage_module_path,
        registry_module_path
}) // Pacote base de Services, Controllers, etc.
public class ServiceManagementCoreApiApplication {

    public static void main(String[] args) {
        //Iniciamos o aplicativo, e permitimos que o spring gerencie sua inicialização
        SpringApplication app = new SpringApplication(ServiceManagementCoreApiApplication.class);

        //Adicionamos o profile padrão a ser utilizado
        app.setAdditionalProfiles("prod", "storage", "registry");

        //Rodamos o aplicativo passando os argumentos da inicialização
        ConfigurableApplicationContext context = app.run(args);

        printH2ConsoleUrl(context.getEnvironment());

    }


}
