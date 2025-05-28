package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para gestión de tokens de reset de contraseña
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetTokensDTO {

    // === REQUEST DTOs ===

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPasswordRequestDTO {
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato de email es inválido")
        @JsonProperty("email")
        private String email;

        @JsonProperty("ipSolicitante")
        private String ipSolicitante;

        @JsonProperty("userAgent")
        private String userAgent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPasswordConfirmDTO {
        @NotBlank(message = "El token es obligatorio")
        @JsonProperty("token")
        private String token;

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
        @JsonProperty("nuevaPassword")
        private String nuevaPassword;

        @NotBlank(message = "La confirmación de contraseña es obligatoria")
        @JsonProperty("confirmarPassword")
        private String confirmarPassword;

        // Validación de que las contraseñas coincidan
        public boolean passwordsMatch() {
            return nuevaPassword != null && nuevaPassword.equals(confirmarPassword);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidateTokenRequestDTO {
        @NotBlank(message = "El token es obligatorio")
        @JsonProperty("token")
        private String token;
    }

    // === RESPONSE DTOs ===

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPasswordResponseDTO {
        @JsonProperty("success")
        private Boolean success;

        @JsonProperty("message")
        private String message;

        @JsonProperty("timestamp")
        private LocalDateTime timestamp;

        @JsonProperty("expirationTime")
        private LocalDateTime expirationTime;

        // Constructor para respuestas exitosas
        public ResetPasswordResponseDTO(Boolean success, String message) {
            this.success = success;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }

        // Métodos de utilidad
        public static ResetPasswordResponseDTO success(String message) {
            return new ResetPasswordResponseDTO(true, message);
        }

        public static ResetPasswordResponseDTO success(String message, LocalDateTime expirationTime) {
            ResetPasswordResponseDTO response = new ResetPasswordResponseDTO(true, message);
            response.setExpirationTime(expirationTime);
            return response;
        }

        public static ResetPasswordResponseDTO error(String message) {
            return new ResetPasswordResponseDTO(false, message);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenValidationResponseDTO {
        @JsonProperty("valid")
        private Boolean valid;

        @JsonProperty("message")
        private String message;

        @JsonProperty("timeRemaining")
        private Long timeRemaining; // Segundos hasta expiración

        @JsonProperty("userEmail")
        private String userEmail;

        @JsonProperty("tokenType")
        private String tokenType;

        @JsonProperty("attempts")
        private Integer attempts;

        @JsonProperty("maxAttempts")
        private Integer maxAttempts;

        // Constructor para token válido
        public static TokenValidationResponseDTO valid(String userEmail, Long timeRemaining) {
            TokenValidationResponseDTO response = new TokenValidationResponseDTO();
            response.setValid(true);
            response.setMessage("Token válido");
            response.setUserEmail(userEmail);
            response.setTimeRemaining(timeRemaining);
            response.setTokenType("password_reset");
            return response;
        }

        // Constructor para token inválido
        public static TokenValidationResponseDTO invalid(String message) {
            TokenValidationResponseDTO response = new TokenValidationResponseDTO();
            response.setValid(false);
            response.setMessage(message);
            response.setTokenType("password_reset");
            return response;
        }

        // Constructor para token con intentos
        public static TokenValidationResponseDTO withAttempts(Boolean valid, String message,
                                                              Integer attempts, Integer maxAttempts) {
            TokenValidationResponseDTO response = new TokenValidationResponseDTO();
            response.setValid(valid);
            response.setMessage(message);
            response.setAttempts(attempts);
            response.setMaxAttempts(maxAttempts);
            response.setTokenType("password_reset");
            return response;
        }
    }

    // === INFO DTO para administración ===

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenInfoDTO {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("userEmail")
        private String userEmail;

        @JsonProperty("createdAt")
        private LocalDateTime createdAt;

        @JsonProperty("expirationDate")
        private LocalDateTime expirationDate;

        @JsonProperty("used")
        private Boolean used;

        @JsonProperty("active")
        private Boolean active;

        @JsonProperty("attempts")
        private Integer attempts;

        @JsonProperty("ipSolicitante")
        private String ipSolicitante;

        @JsonProperty("userAgent")
        private String userAgent;

        @JsonProperty("status")
        private String status; // VALID, EXPIRED, USED, BLOCKED

        @JsonProperty("timeUntilExpiration")
        private Long timeUntilExpiration;

        // Método para determinar el estado del token
        public void calculateStatus() {
            if (Boolean.TRUE.equals(used)) {
                this.status = "USED";
            } else if (Boolean.FALSE.equals(active)) {
                this.status = "BLOCKED";
            } else if (expirationDate != null && expirationDate.isBefore(LocalDateTime.now())) {
                this.status = "EXPIRED";
            } else {
                this.status = "VALID";
            }

            // Calcular tiempo restante
            if (expirationDate != null) {
                long seconds = java.time.Duration.between(LocalDateTime.now(), expirationDate).getSeconds();
                this.timeUntilExpiration = Math.max(0, seconds);
            }
        }

        // Método para crear desde entidad
        public static TokenInfoDTO fromEntity(com.digital.mecommerces.model.PasswordResetTokens token) {
            if (token == null) return null;

            TokenInfoDTO dto = new TokenInfoDTO();
            dto.setId(token.getId());
            dto.setCreatedAt(token.getCreatedAt());
            dto.setExpirationDate(token.getFechaExpiracion());
            dto.setUsed(token.getUsado());
            dto.setActive(token.getActivo());
            dto.setAttempts(token.getIntentos());
            dto.setIpSolicitante(token.getIpSolicitante());
            dto.setUserAgent(token.getUserAgent());

            if (token.getUsuario() != null) {
                dto.setUserEmail(token.getUsuario().getEmail());
            }

            dto.calculateStatus();
            return dto;
        }
    }
}
