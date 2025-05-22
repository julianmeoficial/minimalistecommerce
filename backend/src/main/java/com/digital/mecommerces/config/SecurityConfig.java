package com.digital.mecommerces.config;

import com.digital.mecommerces.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // Rutas públicas - Swagger UI y documentación
                    auth.requestMatchers("/swagger-ui/**").permitAll();
                    auth.requestMatchers("/swagger-ui.html").permitAll();
                    auth.requestMatchers("/swagger-resources/**").permitAll();
                    auth.requestMatchers("/v3/api-docs/**").permitAll();
                    auth.requestMatchers("/v3/api-docs").permitAll();
                    auth.requestMatchers("/api-docs/**").permitAll();
                    auth.requestMatchers("/webjars/**").permitAll();

                    // Rutas de autenticación y registro
                    auth.requestMatchers("/api/auth/**").permitAll();

                    // Rutas públicas de productos y categorías
                    auth.requestMatchers("/api/productos").permitAll();
                    auth.requestMatchers("/api/productos/**").permitAll();
                    auth.requestMatchers("/api/categorias/**").permitAll();
                    auth.requestMatchers("/api/public/**").permitAll();

                    // Health check
                    auth.requestMatchers("/api/health").permitAll();

                    // Rutas de imágenes
                    auth.requestMatchers("/api/imagenes/upload").authenticated();

                    // Rutas para administradores
                    auth.requestMatchers("/api/admin/**").hasAuthority("ADMINISTRADOR");
                    auth.requestMatchers("/api/usuarios/**").hasAuthority("ADMINISTRADOR");
                    auth.requestMatchers("/api/permisos/**").hasAuthority("ADMINISTRADOR");
                    auth.requestMatchers("/api/roles/**").hasAuthority("ADMINISTRADOR");
                    auth.requestMatchers("/api/roles-permisos/**").hasAuthority("ADMINISTRADOR");

                    // Rutas específicas por rol
                    auth.requestMatchers("/api/vendedor/**").hasAuthority("VENDEDOR");
                    auth.requestMatchers("/api/carrito/**").hasAuthority("COMPRADOR");
                    auth.requestMatchers("/api/ordenes/**").hasAnyAuthority("COMPRADOR", "VENDEDOR", "ADMINISTRADOR");

                    // Cualquier otra ruta requiere autenticación
                    auth.anyRequest().authenticated();
                });

        // Agregar filtro JWT antes del filtro de autenticación
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
