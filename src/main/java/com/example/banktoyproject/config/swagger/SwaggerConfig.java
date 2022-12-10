package com.example.banktoyproject.config.swagger;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("com.example.janghj")
                .pathsToMatch("/**")
                .packagesToScan("com.example.janghj.web")
                .build();
//        http://localhost:8080/swagger-ui.html
    }
}