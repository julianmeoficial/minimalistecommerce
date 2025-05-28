package com.digital.mecommerces.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuración de caché optimizada para el sistema
 * Sistema medbcommerce 3.0
 */
@Configuration
@EnableCaching
@Slf4j
public class CachingConfig {

    /**
     * Define el gestor de caché para la aplicación.
     * Incluye todos los cachés necesarios para optimizar el rendimiento.
     */
    @Bean
    public CacheManager cacheManager() {
        log.info("🚀 Configurando Cache Manager para medbcommerce 3.0");

        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();

        // Definir todos los cachés necesarios
        cacheManager.setCacheNames(Arrays.asList(
                // Cachés de productos
                "productos",
                "productosPorCategoria",
                "productosPorVendedor",
                "productosDestacados",
                "productosRecientes",
                "productosPorSlug",

                // Cachés de categorías
                "categorias",
                "categoriasPrincipales",
                "categoriasActivas",
                "categoriasPorSlug",
                "jerarquiaCategorias",

                // Cachés de usuarios
                "usuarios",
                "usuariosPorRol",
                "usuariosActivos",
                "perfilesUsuario",

                // Cachés de roles y permisos
                "roles",
                "permisos",
                "rolesPermisos",
                "permisosDelSistema",

                // Cachés de imágenes
                "imagenesProducto",
                "imagenesPrincipales",

                // Cachés de estadísticas
                "estadisticasGenerales",
                "estadisticasVendedor",
                "dashboardAdmin",

                // Cachés de carritos
                "carritosActivos",
                "itemsCarrito",

                // Cachés de configuración
                "configuracionSistema",
                "rolesDelSistema"
        ));

        // Permitir la creación dinámica de cachés
        cacheManager.setAllowNullValues(true);

        log.info("✅ Cache Manager configurado con {} cachés predefinidos",
                cacheManager.getCacheNames().size());

        log.info("📋 Cachés configurados: {}", cacheManager.getCacheNames());

        return cacheManager;
    }
}
