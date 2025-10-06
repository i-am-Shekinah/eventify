package com.codewithmike.eventify.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI eventifyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Eventify API")
                        .description("REST API for Event and Participant Management")
                        .version("1.0.0"));
    }
}
