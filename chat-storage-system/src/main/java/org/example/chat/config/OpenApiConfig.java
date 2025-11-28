package org.example.chat.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI chatStorageOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Chat Storage Service API").version("v1").description("API for storing and retrieving chat messages"));
    }
}

