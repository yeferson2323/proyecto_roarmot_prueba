package com.roarmot.roarmot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig2 implements WebMvcConfigurer {
    
    @Value("${file.upload-dir}")  // ← ESTO inyecta el valor
    private String uploadDir;      // ← Ahora uploadDir SÍ existe
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("[WEBCONFIG2] Configurando...");
        
        // 1. Static (CSS, JS, imágenes fijas) - CORREGIDO
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");  // ← "classpath" minúscula

        // 2. Productos - CORREGIDO
        registry.addResourceHandler("/products/**")  // ← Comillas cerradas
                .addResourceLocations("file:" + uploadDir + "/products/");  // ← Sin #

        // 3. Motos RutaDinámica...
        registry.addResourceHandler("/motos/**") 
        .addResourceLocations("file:" + uploadDir + "/motos/"); //  Ruta dinámica

        // 4. Perfiles - CORREGIDO
        registry.addResourceHandler("/perfiles/**")
                .addResourceLocations("file:" + uploadDir + "/perfiles/");  // ← Sin #

        System.out.println("🟢 [WEBCONFIG2] Todo configurado");
    }
}