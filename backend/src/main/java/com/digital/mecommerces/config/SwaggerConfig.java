package com.digital.mecommerces.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuración de Swagger/OpenAPI 3 optimizada
 * Sistema medbcommerce 3.0
 */
@Configuration
@Slf4j
public class SwaggerConfig {

    @Value("${server.port:8585}")
    private String serverPort;

    @Value("${app.swagger.enabled:true}")
    private boolean swaggerEnabled;

    @Bean
    public OpenAPI customOpenAPI() {
        if (!swaggerEnabled) {
            log.info("📚 Swagger está deshabilitado en esta configuración");
            return new OpenAPI();
        }

        log.info("📚 Configurando Swagger/OpenAPI 3 para medbcommerce 3.0");

        final String securitySchemeName = "bearerAuth";

        OpenAPI openAPI = new OpenAPI()
                .info(createApiInfo())
                .servers(Arrays.asList(
                        createServer("http://localhost:" + serverPort, "Servidor de Desarrollo"),
                        createServer("https://api.mecommerces.com", "Servidor de Producción"),
                        createServer("https://staging-api.mecommerces.com", "Servidor de Staging")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, createSecurityScheme())
                );

        log.info("✅ Swagger/OpenAPI configurado exitosamente");
        log.info("📖 Documentación disponible en: http://localhost:{}/swagger-ui.html", serverPort);

        return openAPI;
    }

    private Info createApiInfo() {
        return new Info()
                .title("MeCommerces API 3.0")
                .description("""
                API REST completa para el sistema de e-commerce modular MeCommerces.
                
                ## Características principales:
                
                - 🔐 **Autenticación JWT** con refresh tokens
                - 👥 **Sistema de roles granular** (Administrador, Vendedor, Comprador)
                - 🛍️ **Gestión completa de productos** con imágenes y categorías
                - 🛒 **Carrito de compras** avanzado
                - 📊 **Dashboard administrativo** con estadísticas
                - 🔒 **Seguridad robusta** con permisos específicos
                - 📱 **Optimizado para frontend React**
                
                ## Autenticación:
                
                1. Registrate o inicia sesión en `/api/auth/login`
                2. Usa el token JWT en el header `Authorization: Bearer <token>`
                3. El token expira en 24 horas
                
                ## Usuarios de prueba:
                
                - **Admin**: admin@mecommerces.com / admin123
                - **Vendedor**: vendedor@mecommerces.com / vendedor123  
                - **Comprador**: comprador@mecommerces.com / comprador123
                """)
                .version("3.0.0")
                .contact(new Contact()
                        .name("Equipo de Desarrollo MeCommerces")
                        .email("admin@mecommerces.com")
                        .url("https://mecommerces.com")
                )
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")
                );
    }

    private Server createServer(String url, String description) {
        return new Server()
                .url(url)
                .description(description);
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name("bearerAuth")
                .description("JWT Authentication - Ingresa tu token JWT")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);
    }
}
