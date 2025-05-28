package com.digital.mecommerces.config;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de seguridad optimizada para Spring Boot 3.x
 * Sistema medbcommerce 3.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Fuerza 12 para mejor seguridad
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("🔒 Configurando Security Filter Chain para medbcommerce 3.0");

        http
                // Deshabilitar CSRF ya que usamos JWT
                .csrf(AbstractHttpConfigurer::disable)

                // Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configurar sesiones como STATELESS para JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configurar autorización de rutas
                .authorizeHttpRequests(auth -> {
                    // === RUTAS PÚBLICAS COMPLETAMENTE ABIERTAS ===

                    // Swagger y documentación de API
                    auth.requestMatchers("/swagger-ui/**").permitAll();
                    auth.requestMatchers("/swagger-ui.html").permitAll();
                    auth.requestMatchers("/swagger-resources/**").permitAll();
                    auth.requestMatchers("/v3/api-docs/**").permitAll();
                    auth.requestMatchers("/v3/api-docs.yaml").permitAll();
                    auth.requestMatchers("/api-docs/**").permitAll();
                    auth.requestMatchers("/webjars/**").permitAll();

                    // Health checks y actuator
                    auth.requestMatchers("/actuator/**").permitAll();
                    auth.requestMatchers("/api/health/**").permitAll();

                    // Autenticación y registro
                    auth.requestMatchers("/api/auth/**").permitAll();

                    // Rutas públicas de productos y categorías (solo lectura)
                    auth.requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/imagenes/**").permitAll();

                    // Rutas públicas específicas
                    auth.requestMatchers("/api/public/**").permitAll();
                    auth.requestMatchers("/api/categorias/publicos").permitAll();
                    auth.requestMatchers("/api/roles/publicos").permitAll();

                    // Reset de contraseñas (público)
                    auth.requestMatchers("/api/password-reset/**").permitAll();

                    // === RUTAS PARA ADMINISTRADORES ===

                    auth.requestMatchers("/api/admin/**")
                            .hasAuthority(RoleConstants.PERM_ADMIN_TOTAL);

                    auth.requestMatchers("/api/usuarios/**")
                            .hasAnyAuthority(RoleConstants.PERM_ADMIN_TOTAL, RoleConstants.PERM_GESTIONAR_USUARIOS);

                    auth.requestMatchers("/api/permisos/**")
                            .hasAuthority(RoleConstants.PERM_ADMIN_TOTAL);

                    auth.requestMatchers("/api/roles/**")
                            .hasAuthority(RoleConstants.PERM_ADMIN_TOTAL);

                    auth.requestMatchers("/api/roles-permisos/**")
                            .hasAuthority(RoleConstants.PERM_ADMIN_TOTAL);

                    // === RUTAS PARA VENDEDORES ===

                    auth.requestMatchers("/api/vendedor/**")
                            .hasAnyAuthority(RoleConstants.PERM_VENDER_PRODUCTOS, RoleConstants.PERM_ADMIN_TOTAL);

                    auth.requestMatchers("/api/vendedor-detalles/**")
                            .hasAnyAuthority(RoleConstants.PERM_VENDER_PRODUCTOS, RoleConstants.PERM_ADMIN_TOTAL);

                    auth.requestMatchers(HttpMethod.POST, "/api/productos")
                            .hasAnyAuthority(RoleConstants.PERM_VENDER_PRODUCTOS, RoleConstants.PERM_ADMIN_TOTAL);

                    auth.requestMatchers(HttpMethod.PUT, "/api/productos/**")
                            .hasAnyAuthority(RoleConstants.PERM_VENDER_PRODUCTOS, RoleConstants.PERM_ADMIN_TOTAL);

                    auth.requestMatchers(HttpMethod.DELETE, "/api/productos/**")
                            .hasAnyAuthority(RoleConstants.PERM_VENDER_PRODUCTOS, RoleConstants.PERM_ADMIN_TOTAL);

                    // === RUTAS PARA COMPRADORES ===

                    auth.requestMatchers("/api/carrito/**")
                            .hasAnyAuthority(RoleConstants.PERM_COMPRAR_PRODUCTOS, RoleConstants.PERM_ADMIN_TOTAL);

                    auth.requestMatchers("/api/carrito-items/**")
                            .hasAnyAuthority(RoleConstants.PERM_COMPRAR_PRODUCTOS, RoleConstants.PERM_ADMIN_TOTAL);

                    auth.requestMatchers("/api/comprador-detalles/**")
                            .hasAnyAuthority(RoleConstants.PERM_COMPRAR_PRODUCTOS, RoleConstants.PERM_ADMIN_TOTAL);

                    // === RUTAS PARA GESTIÓN DE CATEGORÍAS ===

                    auth.requestMatchers(HttpMethod.POST, "/api/categorias")
                            .hasAnyAuthority(RoleConstants.PERM_ADMIN_TOTAL, RoleConstants.PERM_GESTIONAR_CATEGORIAS);

                    auth.requestMatchers(HttpMethod.PUT, "/api/categorias/**")
                            .hasAnyAuthority(RoleConstants.PERM_ADMIN_TOTAL, RoleConstants.PERM_GESTIONAR_CATEGORIAS);

                    auth.requestMatchers(HttpMethod.DELETE, "/api/categorias/**")
                            .hasAuthority(RoleConstants.PERM_ADMIN_TOTAL);

                    // === RUTAS PARA GESTIÓN DE IMÁGENES ===

                    auth.requestMatchers(HttpMethod.POST, "/api/imagenes/**")
                            .hasAnyAuthority(RoleConstants.PERM_ADMIN_TOTAL, RoleConstants.PERM_VENDER_PRODUCTOS);

                    auth.requestMatchers(HttpMethod.DELETE, "/api/imagenes/**")
                            .hasAnyAuthority(RoleConstants.PERM_ADMIN_TOTAL, RoleConstants.PERM_VENDER_PRODUCTOS);

                    auth.requestMatchers("/api/producto-imagenes/**")
                            .hasAnyAuthority(RoleConstants.PERM_ADMIN_TOTAL, RoleConstants.PERM_VENDER_PRODUCTOS);

                    // === RUTAS DE PERFIL (cualquier usuario autenticado) ===

                    auth.requestMatchers("/api/usuarios/perfil").authenticated();
                    auth.requestMatchers(HttpMethod.PUT, "/api/usuarios/perfil").authenticated();
                    auth.requestMatchers("/api/usuarios/cambiar-password").authenticated();

                    // === CUALQUIER OTRA RUTA REQUIERE AUTENTICACIÓN ===

                    auth.anyRequest().authenticated();
                })

                // Deshabilitar configuraciones por defecto que no necesitamos
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        // Agregar filtro JWT antes del filtro de autenticación estándar
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("✅ Security Filter Chain configurado exitosamente");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("🌐 Configurando CORS para desarrollo y producción");

        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (desarrollo y producción)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",      // React dev server
                "http://localhost:3001",      // React alternate port
                "http://localhost:5173",      // Vite dev server
                "http://localhost:8080",      // Frontend alternativo
                "https://mecommerces.com",    // Producción
                "https://*.mecommerces.com",  // Subdominios de producción
                "http://127.0.0.1:*",         // Local development
                "http://localhost:*"          // Cualquier puerto local
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Headers expuestos
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count",
                "X-Total-Pages",
                "Cache-Control",
                "Content-Disposition"
        ));

        // Permitir credenciales
        configuration.setAllowCredentials(true);

        // Tiempo de cache para preflight requests
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
