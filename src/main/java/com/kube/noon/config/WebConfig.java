package com.kube.noon.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@OpenAPIDefinition(
        info = @Info(title = "NOON API 명세서",
                description = "NOON Main API 명세서",
                version = "0.0.1-SNAPSHOP")
)
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${management.endpoints.web.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("Cookie","Content-Type","Content-Length","Host","Accept","Accept-Encoding")
                .allowCredentials(true)
                .exposedHeaders("Set-Cookie")
                .maxAge(3600);
    }
}
