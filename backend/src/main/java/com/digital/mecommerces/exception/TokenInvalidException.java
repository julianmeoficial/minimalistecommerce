package com.digital.mecommerces.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para tokens inválidos
 * Optimizada para el sistema medbcommerce 3.0
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenInvalidException extends AuthenticationException {

    private final String tokenType;
    private final String reason;
    private final String tokenIdentifier;

    // Constructor básico
    public TokenInvalidException(String message) {
        super(message);
        this.tokenType = "JWT";
        this.reason = "INVALID_FORMAT";
        this.tokenIdentifier = null;
    }

    // Constructor con tipo y razón
    public TokenInvalidException(String message, String tokenType, String reason) {
        super(message);
        this.tokenType = tokenType;
        this.reason = reason;
        this.tokenIdentifier = null;
    }

    // Constructor completo
    public TokenInvalidException(String message, String tokenType, String reason, String tokenIdentifier) {
        super(message);
        this.tokenType = tokenType;
        this.reason = reason;
        this.tokenIdentifier = tokenIdentifier;
    }

    // === MÉTODOS ESTÁTICOS PARA CREAR EXCEPCIONES ESPECÍFICAS ===

    public static TokenInvalidException malformedJwt() {
        return new TokenInvalidException(
                "El token JWT tiene un formato inválido",
                "JWT",
                "MALFORMED"
        );
    }

    public static TokenInvalidException invalidSignature() {
        return new TokenInvalidException(
                "La firma del token JWT es inválida",
                "JWT",
                "INVALID_SIGNATURE"
        );
    }

    public static TokenInvalidException unsupportedJwt() {
        return new TokenInvalidException(
                "El token JWT no es compatible con este sistema",
                "JWT",
                "UNSUPPORTED"
        );
    }

    public static TokenInvalidException emptyJwt() {
        return new TokenInvalidException(
                "El token JWT está vacío o es nulo",
                "JWT",
                "EMPTY"
        );
    }

    public static TokenInvalidException invalidClaims() {
        return new TokenInvalidException(
                "Los claims del token JWT son inválidos",
                "JWT",
                "INVALID_CLAIMS"
        );
    }

    public static TokenInvalidException refreshTokenInvalid() {
        return new TokenInvalidException(
                "El token de actualización es inválido o ha sido revocado",
                "REFRESH",
                "INVALID_OR_REVOKED"
        );
    }

    public static TokenInvalidException passwordResetTokenInvalid() {
        return new TokenInvalidException(
                "El token para restablecer contraseña es inválido o ya fue utilizado",
                "PASSWORD_RESET",
                "INVALID_OR_USED"
        );
    }

    public static TokenInvalidException verificationTokenInvalid() {
        return new TokenInvalidException(
                "El token de verificación es inválido",
                "EMAIL_VERIFICATION",
                "INVALID"
        );
    }

    public static TokenInvalidException sessionTokenInvalid() {
        return new TokenInvalidException(
                "El token de sesión es inválido o ha sido comprometido",
                "SESSION",
                "INVALID_OR_COMPROMISED"
        );
    }

    public static TokenInvalidException apiTokenInvalid() {
        return new TokenInvalidException(
                "El token de API es inválido o ha sido revocado",
                "API",
                "INVALID_OR_REVOKED"
        );
    }

    public static TokenInvalidException blacklistedToken() {
        return new TokenInvalidException(
                "El token ha sido añadido a la lista negra",
                "JWT",
                "BLACKLISTED"
        );
    }

    public static TokenInvalidException tokenNotFound(String tokenId) {
        return new TokenInvalidException(
                "No se encontró el token con ID: " + tokenId,
                "UNKNOWN",
                "NOT_FOUND",
                tokenId
        );
    }

    public static TokenInvalidException customInvalidToken(String tokenType, String reason, String message) {
        return new TokenInvalidException(message, tokenType, reason);
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

    public boolean isMalformed() {
        return "MALFORMED".equals(reason);
    }

    public boolean isSignatureInvalid() {
        return "INVALID_SIGNATURE".equals(reason);
    }

    public boolean isBlacklisted() {
        return "BLACKLISTED".equals(reason);
    }

    public boolean isRevoked() {
        return reason != null && reason.contains("REVOKED");
    }

    public boolean isUsed() {
        return reason != null && reason.contains("USED");
    }

    // === GETTERS ===

    public String getTokenType() {
        return tokenType;
    }

    public String getReason() {
        return reason;
    }

    public String getTokenIdentifier() {
        return tokenIdentifier;
    }

    @Override
    public String toString() {
        return "TokenInvalidException{" +
                "message='" + getMessage() + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", reason='" + reason + '\'' +
                ", tokenIdentifier='" + tokenIdentifier + '\'' +
                '}';
    }
}
