package br.com.rts.eventmanager.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EventManager API")
                        .version("1.0.0")
                        .description("API para gerenciamento completo de eventos, catálogo, financeiro e gestão no EventManager.")
                        .contact(new Contact()
                                .name("Suporte RTS")
                                .email("suporte@rts.com.br")
                                .url("https://rts.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor Local de Desenvolvimento")
                ));
    }
}
