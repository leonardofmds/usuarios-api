package br.com.cotiinformatica.configurations;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Usuarios API - Treinamento Java Avançado: Formação Arquiteto")
                        .version("v1")
                        .description("Documentação da API do projeto")
                        .contact(new Contact()
                                .name("COTI Informática")
                                .email("contato@cotiinformatica.com.br")));
    }
}