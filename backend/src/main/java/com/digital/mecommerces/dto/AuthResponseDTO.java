package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para respuestas de autenticación del sistema
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("token")
    private String token;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("tokenType")
    private String tokenType = "Bearer";

    @JsonProperty("expiresIn")
    private Long expiresIn; // Tiempo de expiración en segundos

    @JsonProperty("usuario")
    private UsuarioSimpleDTO usuario;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("authorities")
    private List<String> authorities;

    // Constructor para respuestas exitosas con token
    public AuthResponseDTO(Boolean success, String message, String token, UsuarioSimpleDTO usuario) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.usuario = usuario;
        this.timestamp = LocalDateTime.now();
        this.tokenType = "Bearer";
    }

    // Constructor para respuestas de error
    public AuthResponseDTO(Boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor para respuestas exitosas sin token (registro)
    public AuthResponseDTO(String message, Boolean success, UsuarioSimpleDTO usuario) {
        this.message = message;
        this.success = success;
        this.usuario = usuario;
        this.timestamp = LocalDateTime.now();
    }

    // Métodos de utilidad para crear respuestas estándar
    public static AuthResponseDTO success(String message) {
        return new AuthResponseDTO(true, message);
    }

    public static AuthResponseDTO success(String message, String token, UsuarioSimpleDTO usuario) {
        return new AuthResponseDTO(true, message, token, usuario);
    }

    public static AuthResponseDTO error(String message) {
        return new AuthResponseDTO(false, message);
    }

    public static AuthResponseDTO loginSuccess(String token, String refreshToken, UsuarioSimpleDTO usuario, List<String> authorities, Long expiresIn) {
        AuthResponseDTO response = new AuthResponseDTO();
        response.setSuccess(true);
        response.setMessage("Autenticación exitosa");
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUsuario(usuario);
        response.setAuthorities(authorities);
        response.setExpiresIn(expiresIn);
        response.setTimestamp(LocalDateTime.now());
        response.setTokenType("Bearer");
        return response;
    }

    public static AuthResponseDTO registrationSuccess(UsuarioSimpleDTO usuario) {
        AuthResponseDTO response = new AuthResponseDTO();
        response.setSuccess(true);
        response.setMessage("Usuario registrado exitosamente");
        response.setUsuario(usuario);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    // Método para verificar si la respuesta contiene información completa
    public boolean isComplete() {
        return success != null && message != null && timestamp != null;
    }

    // Método para verificar si es una respuesta de autenticación exitosa
    public boolean isAuthenticationSuccess() {
        return Boolean.TRUE.equals(success) && token != null && usuario != null;
    }

    // DTO interno para información básica del usuario
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioSimpleDTO {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("nombre")
        private String nombre;

        @JsonProperty("email")
        private String email;

        @JsonProperty("rol")
        private String rol;

        @JsonProperty("activo")
        private Boolean activo;

        @JsonProperty("ultimoLogin")
        private LocalDateTime ultimoLogin;

        // Constructor básico
        public UsuarioSimpleDTO(Long id, String nombre, String email, String rol) {
            this.id = id;
            this.nombre = nombre;
            this.email = email;
            this.rol = rol;
            this.activo = true;
        }
    }
}
