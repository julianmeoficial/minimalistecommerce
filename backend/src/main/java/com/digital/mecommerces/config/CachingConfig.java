package com.digital.mecommerces.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de caché para la aplicación.
 * Esta clase habilita el uso de caché en la aplicación y define los caches disponibles.
 */
@Configuration
@EnableCaching
public class CachingConfig {

    /**
     * Define el gestor de caché para la aplicación.
     * Crea caches para productos, categorías y otros datos frecuentemente accedidos.
     * 
     * @return CacheManager configurado con los caches necesarios
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
            "productos", 
            "productosPorCategoria",
            "productosPorVendedor",
            "categorias"
        );
        return cacheManager;
    }
}