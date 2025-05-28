package com.digital.mecommerces.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.secret:mecommerces-super-secret-key-for-jwt-token-generation-2024}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24 horas en millisegundos
    private long jwtExpirationInMs;

    @Value("${app.jwt.refresh-expiration:604800000}") // 7 días en millisegundos
    private long jwtRefreshExpirationInMs;

    private final UserDetailsService userDetailsService;
    private Key key;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    public void init() {
        // Generar clave secreta para firma del token
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        log.info("🔑 JwtTokenProvider inicializado con expiración de {} ms", jwtExpirationInMs);
    }

    // Generar token JWT principal
    public String generateToken(String username) {
        log.info("🎫 Generando token JWT para usuario: {}", username);

        try {
            // Cargar detalles del usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Extraer autoridades
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

            // Crear claims adicionales
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", roles);
            claims.put("type", "access");
            claims.put("iat", now.getTime() / 1000);
            claims.put("enabled", userDetails.isEnabled());

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .setIssuer("mecommerces")
                    .setAudience("mecommerces-users")
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            log.info("✅ Token JWT generado exitosamente para usuario: {} (expira: {})",
                    username, expiryDate);

            return token;

        } catch (Exception e) {
            log.error("❌ Error generando token JWT para usuario {}: {}", username, e.getMessage());
            throw new RuntimeException("Error generando token JWT", e);
        }
    }

    // Generar refresh token
    public String generateRefreshToken(String username) {
        log.info("🔄 Generando refresh token para usuario: {}", username);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationInMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("iat", now.getTime() / 1000);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer("mecommerces")
                .setAudience("mecommerces-refresh")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("✅ Refresh token generado exitosamente para usuario: {}", username);
        return refreshToken;
    }

    // Obtener username del token
    public String getUsernameFromToken(String token) {
        try {
            String username = getClaimFromToken(token, Claims::getSubject);
            log.debug("👤 Username extraído del token: {}", username);
            return username;
        } catch (Exception e) {
            log.warn("⚠️ Error extrayendo username del token: {}", e.getMessage());
            return null;
        }
    }

    // Obtener fecha de expiración del token
    public Date getExpirationDateFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getExpiration);
        } catch (Exception e) {
            log.warn("⚠️ Error extrayendo fecha de expiración del token: {}", e.getMessage());
            return null;
        }
    }

    // Obtener roles del token
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            List<String> roles = (List<String>) claims.get("roles");
            return roles != null ? roles : new ArrayList<>();
        } catch (Exception e) {
            log.warn("⚠️ Error extrayendo roles del token: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // Verificar si es refresh token
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String type = (String) claims.get("type");
            return "refresh".equals(type);
        } catch (Exception e) {
            log.warn("⚠️ Error verificando tipo de token: {}", e.getMessage());
            return false;
        }
    }

    // Validar token JWT
    public boolean validateToken(String token) {
        try {
            log.debug("🔍 Validando token JWT...");

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Verificar expiración
            Date expiration = claims.getExpiration();
            boolean isExpired = expiration.before(new Date());

            if (isExpired) {
                log.warn("⏰ Token JWT expirado: {}", expiration);
                return false;
            }

            // Verificar issuer
            String issuer = claims.getIssuer();
            if (!"mecommerces".equals(issuer)) {
                log.warn("⚠️ Issuer inválido en token: {}", issuer);
                return false;
            }

            // Verificar que tenga username
            String username = claims.getSubject();
            if (username == null || username.trim().isEmpty()) {
                log.warn("⚠️ Token sin username válido");
                return false;
            }

            log.debug("✅ Token JWT válido para usuario: {}", username);
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("⏰ Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("❌ Token JWT no soportado: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("❌ Token JWT malformado: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.warn("🔒 Firma de token JWT inválida: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("❌ Token JWT vacío o inválido: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error inesperado validando token JWT: {}", e.getMessage());
        }

        return false;
    }

    // Refrescar token usando refresh token
    public String refreshToken(String refreshToken) {
        log.info("🔄 Refrescando token JWT...");

        try {
            if (!validateToken(refreshToken)) {
                throw new RuntimeException("Refresh token inválido");
            }

            if (!isRefreshToken(refreshToken)) {
                throw new RuntimeException("Token no es de tipo refresh");
            }

            String username = getUsernameFromToken(refreshToken);
            if (username == null) {
                throw new RuntimeException("No se pudo extraer username del refresh token");
            }

            // Generar nuevo access token
            String newToken = generateToken(username);
            log.info("✅ Token refrescado exitosamente para usuario: {}", username);

            return newToken;

        } catch (Exception e) {
            log.error("❌ Error refrescando token: {}", e.getMessage());
            throw new RuntimeException("Error refrescando token: " + e.getMessage());
        }
    }

    // Obtener tiempo restante de expiración en segundos
    public long getTimeUntilExpiration(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            if (expiration == null) {
                return 0;
            }

            long timeUntilExpiration = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            return Math.max(0, timeUntilExpiration);

        } catch (Exception e) {
            log.warn("⚠️ Error calculando tiempo de expiración: {}", e.getMessage());
            return 0;
        }
    }

    // Verificar si el token expira pronto (menos de 5 minutos)
    public boolean isTokenExpiringSoon(String token) {
        long timeUntilExpiration = getTimeUntilExpiration(token);
        return timeUntilExpiration > 0 && timeUntilExpiration < 300; // 5 minutos
    }

    // Métodos privados de utilidad

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Métodos para información y debugging

    public Map<String, Object> getTokenInfo(String token) {
        Map<String, Object> info = new HashMap<>();

        try {
            Claims claims = getAllClaimsFromToken(token);

            info.put("username", claims.getSubject());
            info.put("issuedAt", claims.getIssuedAt());
            info.put("expiration", claims.getExpiration());
            info.put("issuer", claims.getIssuer());
            info.put("audience", claims.getAudience());
            info.put("roles", claims.get("roles"));
            info.put("type", claims.get("type"));
            info.put("timeUntilExpiration", getTimeUntilExpiration(token));
            info.put("isValid", validateToken(token));
            info.put("isExpiringSoon", isTokenExpiringSoon(token));

        } catch (Exception e) {
            log.warn("⚠️ Error obteniendo información del token: {}", e.getMessage());
            info.put("error", e.getMessage());
        }

        return info;
    }

    // Generar token para testing (solo en desarrollo)
    public String generateTokenForTesting(String username, List<String> roles) {
        log.warn("⚠️ Generando token para testing - Solo usar en desarrollo!");

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("type", "access");
        claims.put("testing", true);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer("mecommerces-testing")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Getters para configuración
    public long getJwtExpirationInMs() {
        return jwtExpirationInMs;
    }

    public long getJwtRefreshExpirationInMs() {
        return jwtRefreshExpirationInMs;
    }
}
