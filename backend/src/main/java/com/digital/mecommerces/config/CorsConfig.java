package com.digital.mecommerces.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración CORS optimizada para desarrollo y producción
 * Sistema medbcommerce 3.0
 */
@Configuration
@Slf4j
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:3001}")
    private String[] allowedOrigins;

    @Value("${app.cors.max-age:3600}")
    private Long maxAge;

    @Bean
    public CorsFilter corsFilter() {
        log.info("🌐 Configurando CORS Filter para medbcommerce 3.0");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Configurar orígenes permitidos
        List<String> origins = Arrays.asList(allowedOrigins);
        log.info("🌐 Orígenes CORS permitidos: {}", origins);

        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://localhost:5173",
                "http://localhost:8080",
                "http://127.0.0.1:*",
                "http://localhost:*",
                "https://mecommerces.com",
                "https://*.mecommerces.com"
        ));

        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        // Headers permitidos - amplio para desarrollo
        config.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-Requested-With",
                "X-Auth-Token",
                "X-Xsrf-Token",
                "Cache-Control",
                "Id-Token",
                "X-Total-Count",
                "X-Total-Pages"
        ));

        // Headers expuestos al cliente
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition",
                "X-Total-Count",
                "X-Total-Pages",
                "Cache-Control",
                "Content-Range",
                "Accept-Ranges"
        ));

        // Permitir credenciales (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Tiempo de cache para preflight requests
        config.setMaxAge(maxAge);

        // Aplicar configuración a todas las rutas
        source.registerCorsConfiguration("/**", config);

        log.info("✅ CORS Filter configurado exitosamente");
        log.info("🔧 Max Age: {} segundos", maxAge);
        log.info("🔧 Credentials permitidas: true");

        return new CorsFilter(source);
    }
}
