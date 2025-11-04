package com.kiwisha.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapea las URLs que comienzan con /files/** a la carpeta "uploads" en el sistema de archivos
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:uploads/");
    }
}
