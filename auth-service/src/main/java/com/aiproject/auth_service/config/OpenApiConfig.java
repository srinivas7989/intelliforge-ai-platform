package com.aiproject.auth_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IntelliForge Auth Service API")
                        .version("1.0.0")
                        .description("Authentication, users, roles, and admin APIs")
                        .contact(new Contact()
                                .name("IntelliForge AI Platform")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes(
                                "Bearer Authentication",
                                new SecurityScheme()
                                        .name("Bearer Authentication")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}