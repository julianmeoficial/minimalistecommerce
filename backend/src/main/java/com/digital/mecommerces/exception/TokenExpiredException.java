package com.digital.mecommerces.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

/**
 * Excepción para tokens expirados
 * Optimizada para el sistema medbcommerce 3.0
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends AuthenticationException {

    private final String tokenType;
    private final LocalDateTime expirationTime;
    private final String tokenIdentifier;

    // Constructor básico
    public TokenExpiredException(String message) {
        super(message);
        this.tokenType = "JWT";
        this.expirationTime = null;
        this.tokenIdentifier = null;
    }

    // Constructor con tipo de token
    public TokenExpiredException(String message, String tokenType) {
        super(message);
        this.tokenType = tokenType;
        this.expirationTime = null;
        this.tokenIdentifier = null;
    }

    // Constructor completo
    public TokenExpiredException(String message, String tokenType, LocalDateTime expirationTime, String tokenIdentifier) {
        super(message);
        this.tokenType = tokenType;
        this.expirationTime = expirationTime;
        this.tokenIdentifier = tokenIdentifier;
    }

    // === MÉTODOS ESTÁTICOS PARA CREAR EXCEPCIONES ESPECÍFICAS ===

    public static TokenExpiredException jwtToken() {
        return new TokenExpiredException(
                "El token JWT ha expirado. Por favor, inicia sesión nuevamente",
                "JWT"
        );
    }

    public static TokenExpiredException jwtToken(LocalDateTime expirationTime) {
        return new TokenExpiredException(
                "El token JWT expiró el " + expirationTime + ". Por favor, inicia sesión nuevamente",
                "JWT",
                expirationTime,
                null
        );
    }

    public static TokenExpiredException refreshToken() {
        return new TokenExpiredException(
                "El token de actualización ha expirado. Por favor, inicia sesión nuevamente",
                "REFRESH"
        );
    }

    public static TokenExpiredException refreshToken(LocalDateTime expirationTime) {
        return new TokenExpiredException(
                "El token de actualización expiró el " + expirationTime + ". Por favor, inicia sesión nuevamente",
                "REFRESH",
                expirationTime,
                null
        );
    }

    public static TokenExpiredException resetPasswordToken() {
        return new TokenExpiredException(
                "El token para restablecer contraseña ha expirado. Solicita uno nuevo",
                "PASSWORD_RESET"
        );
    }

    public static TokenExpiredException resetPasswordToken(LocalDateTime expirationTime, String tokenId) {
        return new TokenExpiredException(
                "El token para restablecer contraseña expiró el " + expirationTime + ". Solicita uno nuevo",
                "PASSWORD_RESET",
                expirationTime,
                tokenId
        );
    }

    public static TokenExpiredException verificationToken() {
        return new TokenExpiredException(
                "El token de verificación ha expirado. Solicita un nuevo enlace de verificación",
                "EMAIL_VERIFICATION"
        );
    }

    public static TokenExpiredException sessionToken() {
        return new TokenExpiredException(
                "Tu sesión ha expirado por inactividad. Por favor, inicia sesión nuevamente",
                "SESSION"
        );
    }

    public static TokenExpiredException apiToken() {
        return new TokenExpiredException(
                "El token de API ha expirado. Genera un nuevo token",
                "API"
        );
    }

    public static TokenExpiredException customToken(String tokenType, String message) {
        return new TokenExpiredException(message, tokenType);
    }

    // === MÉTODOS DE UTILIDAD ===

    public boolean isJwtToken() {
        return "JWT".equals(tokenType);
    }

    public boolean isRefreshToken() {
        return "REFRESH".equals(tokenType);
    }

    public boolean isPasswordResetToken() {
        return "PASSWORD_RESET".equals(tokenType);
    }

    public boolean hasExpirationTime() {
        return expirationTime != null;
    }

    public long getMinutesSinceExpiration() {
        if (expirationTime == null) {
            return 0;
        }
        return java.time.Duration.between(expirationTime, LocalDateTime.now()).toMinutes();
    }

    // === GETTERS ===

    public String getTokenType() {
        return tokenType;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public String getTokenIdentifier() {
        return tokenIdentifier;
    }

    @Override
    public String toString() {
        return "TokenExpiredException{" +
                "message='" + getMessage() + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expirationTime=" + expirationTime +
                ", tokenIdentifier='" + tokenIdentifier + '\'' +
                '}';
    }
}
