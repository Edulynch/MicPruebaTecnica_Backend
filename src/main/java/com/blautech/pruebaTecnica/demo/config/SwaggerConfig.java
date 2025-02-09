package com.blautech.pruebaTecnica.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API con JWT")
                        .description("Esta API permite gestionar usuarios, autenticación, catálogos, carretillas de compras y órdenes.\n\n"
                                + "Ejemplo de respuesta para error de validación:\n"
                                + "{\n"
                                + "  \"title\": \"Bad Request\",\n"
                                + "  \"status\": 400,\n"
                                + "  \"detail\": \"El campo email es obligatorio; El email debe tener un formato válido\",\n"
                                + "  \"instance\": \"/api/users\",\n"
                                + "  \"errorCode\": \"VALIDATION_ERROR\"\n"
                                + "}")
                        .version("1.0.0")
                        .contact(new Contact().name("Soporte Técnico").email("soporte@example.com"))
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                .components(new Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
    }
}
