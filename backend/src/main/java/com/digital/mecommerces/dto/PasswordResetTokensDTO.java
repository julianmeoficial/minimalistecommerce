package com.digital.mecommerces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class PasswordResetTokensDTO {

    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    private String token;

    private LocalDateTime fechaExpiracion;

    private Boolean usado = false;

    private LocalDateTime createdat;

    // Para solicitud de reset
    @Email(message = "Email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    // Para confirmación de reset
    @NotBlank(message = "El token es obligatorio")
    private String resetToken;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String nuevaPassword;

    // Información del usuario para mostrar
    private String usuarioNombre;
    private String usuarioEmail;

    // Constructor vacío
    public PasswordResetTokensDTO() {}

    // Constructor para solicitud
    public PasswordResetTokensDTO(String email) {
        this.email = email;
    }

    // Constructor para reset
    public PasswordResetTokensDTO(String resetToken, String nuevaPassword) {
        this.resetToken = resetToken;
        this.nuevaPassword = nuevaPassword;
    }

    // Constructor completo
    public PasswordResetTokensDTO(Long id, Long usuarioId, String token,
                                  LocalDateTime fechaExpiracion, Boolean usado, LocalDateTime createdat) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.token = token;
        this.fechaExpiracion = fechaExpiracion;
        this.usado = usado;
        this.createdat = createdat;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Boolean getUsado() {
        return usado;
    }

    public void setUsado(Boolean usado) {
        this.usado = usado;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    // Métodos de utilidad
    public boolean isExpired() {
        return fechaExpiracion != null && LocalDateTime.now().isAfter(fechaExpiracion);
    }

    public boolean isValid() {
        return !usado && !isExpired();
    }
}
