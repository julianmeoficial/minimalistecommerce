package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDTO {
    private String mensaje;
    private boolean success;
    private String token;
    private Object usuario; // Sin exponer password

    // Constructor vacío
    public AuthResponseDTO() {}

    // Constructor sin token
    public AuthResponseDTO(String mensaje, boolean success) {
        this.mensaje = mensaje;
        this.success = success;
    }

    // Constructor con todos los parámetros
    public AuthResponseDTO(String mensaje, boolean success, String token) {
        this.mensaje = mensaje;
        this.success = success;
        this.token = token;
    }

    // Getters y Setters
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getUsuario() {
        return usuario;
    }

    public void setUsuario(Object usuario) {
        this.usuario = usuario;
    }
}
