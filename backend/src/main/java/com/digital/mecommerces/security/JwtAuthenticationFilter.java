package com.digital.mecommerces.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    // Rutas que no requieren autenticación
    private final List<String> excludedPaths = Arrays.asList(
            "/api/auth/",
            "/api/public/",
            "/api/productos",
            "/api/categorias",
            "/api/health",
            "/api/imagenes/",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/swagger-resources/",
            "/webjars/"
    );

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        log.debug("🔍 Procesando solicitud: {} {}", method, requestURI);

        try {
            // Verificar si la ruta está excluida de autenticación
            if (isExcludedPath(requestURI)) {
                log.debug("⚡ Ruta pública, saltando autenticación: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            // Obtener token JWT de la solicitud HTTP
            String token = getJwtFromRequest(request);

            // Validar y procesar token si está presente
            if (StringUtils.hasText(token)) {
                processTokenAuthentication(token, request);
            } else {
                log.debug("🔒 No se encontró token JWT en la solicitud: {}", requestURI);
            }

        } catch (Exception e) {
            log.error("❌ Error en filtro de autenticación JWT: {}", e.getMessage());
            // Limpiar contexto de seguridad en caso de error
            SecurityContextHolder.clearContext();

            // Enviar respuesta de error personalizada
            sendErrorResponse(response, "Error de autenticación: " + e.getMessage());
            return;
        }

        // Continuar con el siguiente filtro
        filterChain.doFilter(request, response);
    }

    private void processTokenAuthentication(String token, HttpServletRequest request) {
        try {
            // Validar token
            if (jwtTokenProvider.validateToken(token)) {
                // Obtener username del token
                String username = jwtTokenProvider.getUsernameFromToken(token);
                log.debug("✅ Token válido para usuario: {}", username);

                // Verificar si ya hay autenticación en el contexto
                if (SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Cargar detalles del usuario
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Verificar que el usuario esté activo
                    if (userDetails.isEnabled() && userDetails.isAccountNonLocked()) {

                        // Crear token de autenticación
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        // Establecer detalles adicionales
                        authenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        // Establecer autenticación en el contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                        log.debug("🔐 Autenticación establecida para usuario: {} con {} autoridades",
                                username, userDetails.getAuthorities().size());

                    } else {
                        log.warn("⚠️ Usuario inactivo o bloqueado: {}", username);
                        SecurityContextHolder.clearContext();
                    }
                }
            } else {
                log.warn("❌ Token JWT inválido o expirado");
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            log.error("❌ Error procesando autenticación por token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            throw e;
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        // Intentar obtener token del header Authorization
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.debug("🎫 Token JWT extraído del header Authorization");
            return token;
        }

        // Intentar obtener token como parámetro de consulta (para casos especiales)
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            log.debug("🎫 Token JWT extraído de parámetro de consulta");
            return tokenParam;
        }

        // Intentar obtener token de cookie (para casos especiales)
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt-token".equals(cookie.getName())) {
                    log.debug("🎫 Token JWT extraído de cookie");
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private boolean isExcludedPath(String requestURI) {
        return excludedPaths.stream()
                .anyMatch(excludedPath -> requestURI.startsWith(excludedPath));
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"error\": \"Unauthorized\", \"message\": \"%s\", \"timestamp\": \"%s\"}",
                message.replace("\"", "\\\""),
                java.time.LocalDateTime.now()
        );

        response.getWriter().write(jsonResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Evitar filtrar OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("⚡ Saltando filtro para OPTIONS request: {}", path);
            return true;
        }

        // Evitar filtrar rutas de actuator si están presentes
        if (path.startsWith("/actuator/")) {
            log.debug("⚡ Saltando filtro para ruta de actuator: {}", path);
            return true;
        }

        // Evitar filtrar recursos estáticos
        if (path.matches(".*\\.(css|js|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$")) {
            log.debug("⚡ Saltando filtro para recurso estático: {}", path);
            return true;
        }

        return super.shouldNotFilter(request);
    }

    // Método para debug de headers de request
    private void debugRequestHeaders(HttpServletRequest request) {
        if (log.isTraceEnabled()) {
            log.trace("🔍 Headers de la solicitud:");
            request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                log.trace("  {}: {}", headerName, request.getHeader(headerName));
            });
        }
    }

    // Método para logging de información de autenticación
    private void logAuthenticationInfo(String username, UserDetails userDetails) {
        if (log.isDebugEnabled()) {
            log.debug("🔐 Información de autenticación:");
            log.debug("  Usuario: {}", username);
            log.debug("  Habilitado: {}", userDetails.isEnabled());
            log.debug("  Cuenta no expirada: {}", userDetails.isAccountNonExpired());
            log.debug("  Cuenta no bloqueada: {}", userDetails.isAccountNonLocked());
            log.debug("  Credenciales no expiradas: {}", userDetails.isCredentialsNonExpired());
            log.debug("  Autoridades: {}", userDetails.getAuthorities());
        }
    }
}
