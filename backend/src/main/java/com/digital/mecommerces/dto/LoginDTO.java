package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el proceso de autenticación/login de usuarios
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email es inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    @JsonProperty("password")
    private String password;

    // Campo opcional para recordar sesión
    @JsonProperty("rememberMe")
    private Boolean rememberMe = false;

    // Campo opcional para información del dispositivo
    @JsonProperty("deviceInfo")
    private String deviceInfo;

    // Constructor principal para casos comunes
    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
        this.rememberMe = false;
    }

    // Método de validación personalizada
    public boolean isValid() {
        return email != null && !email.trim().isEmpty() &&
                password != null && !password.trim().isEmpty() &&
                email.contains("@");
    }

    // Método para limpiar datos sensibles en logs
    public LoginDTO toSafeLog() {
        LoginDTO safeDto = new LoginDTO();
        safeDto.setEmail(this.email);
        safeDto.setPassword("***"); // Ocultar contraseña
        safeDto.setRememberMe(this.rememberMe);
        safeDto.setDeviceInfo(this.deviceInfo);
        return safeDto;
    }

    @Override
    public String toString() {
        return "LoginDTO{" +
                "email='" + email + '\'' +
                ", password='***'" + // Nunca mostrar la contraseña
                ", rememberMe=" + rememberMe +
                ", deviceInfo='" + deviceInfo + '\'' +
                '}';
    }
}
