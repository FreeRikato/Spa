package com.example.spas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**") // Applies to all routes (context path is already /api)
            .allowedOrigins("http://localhost:4200") // Trusts your Angular app
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // Allows all methods
            .allowedHeaders("*") // Allows all headers
            .allowCredentials(true) // Allows session cookies
            .maxAge(3600); // Cache preflight for 1 hour
    }
}
