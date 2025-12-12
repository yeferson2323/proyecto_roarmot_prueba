package com.roarmot.roarmot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration // Indica que esta clase define Beans
public class AppConfig {

    /**
     * Define el Bean de WebClient.Builder. Necesario para que MapServiceImpl
     * pueda crear el WebClient y realizar llamadas HTTP.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     * Define el Bean de ObjectMapper. Necesario para convertir
     * la respuesta JSON de Google a objetos Java.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}