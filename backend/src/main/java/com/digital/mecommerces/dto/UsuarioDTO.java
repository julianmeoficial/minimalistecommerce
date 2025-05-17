package com.digital.mecommerces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsuarioDTO {
    private Long usuarioId;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String usuarioNombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email es inválido")
    private String email;

    private String password;

    @NotNull(message = "El rol de usuario es obligatorio")
    private Long rolId;

    // Constructor vacío
    public UsuarioDTO() {}

    // Constructor con parámetros
    public UsuarioDTO(String usuarioNombre, String email, Long rolId) {
        this.usuarioNombre = usuarioNombre;
        this.email = email;
        this.rolId = rolId;
    }

    // Getters y Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }
}
