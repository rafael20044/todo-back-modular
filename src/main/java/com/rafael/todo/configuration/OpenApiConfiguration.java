package com.rafael.todo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfiguration {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Todo Backend Modular API")
                        .version("1.0.0")
                        .description("API RESTful modular en Java 25 y Spring Boot 4.1.1 para clientes Angular y Flutter. "
                                + "Soporta autenticación JWT Stateless (Bearer Token) y rotación de Refresh Tokens mediante cookie HttpOnly ('token').")
                        .contact(new Contact()
                                .name("Rafael")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH_SCHEME, new SecurityScheme()
                                .name(BEARER_AUTH_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa el accessToken JWT obtenido en POST /api/auth/login o POST /api/auth/refresh")
                        )
                );
    }

}
