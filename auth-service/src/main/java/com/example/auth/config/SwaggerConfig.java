package com.example.auth.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@OpenAPIDefinition
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(
        @Value("${openapi.service.title}") String serviceTitle,
        @Value("${openapi.service.version}") String serviceVersion,
        @Value("${openapi.service.url}") String url,
        @Value("${openapi.service.description}") String description) {
        Info info = new Info()
            .title(serviceTitle)
            .version(serviceVersion)
            .description(description);
        return new OpenAPI()
            .servers(List.of(new Server().url(url)))
            .info(info);
    }

}
