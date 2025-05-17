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

                    // Rutas de autenticación
                    auth.requestMatchers("/api/auth/**").permitAll();

                    // Rutas públicas de productos y categorías
                    auth.requestMatchers("/api/productos").permitAll();
                    auth.requestMatchers("/api/categorias/**").permitAll();

                    // Rutas para administradores
                    auth.requestMatchers("/api/admin/**").hasAuthority("ADMINISTRADOR");
                    auth.requestMatchers("/api/usuarios/**").hasAuthority("ADMINISTRADOR");
                    auth.requestMatchers("/api/permisos/**").hasAuthority("ADMINISTRADOR");
                    auth.requestMatchers("/api/roles/**").hasAuthority("ADMINISTRADOR");
                    auth.requestMatchers("/api/roles-permisos/**").hasAuthority("ADMINISTRADOR");

                    // Rutas para vendedores
                    auth.requestMatchers("/api/vendedor/**").hasAuthority("VENDEDOR");

                    // Rutas para compradores
                    auth.requestMatchers("/api/carrito/**").hasAuthority("COMPRADOR");

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
