package com.digital.mecommerces.dto;

public class AuthResponseDTO {

    private String mensaje;
    private boolean success;
    private String token;

    // Constructor vacío
    public AuthResponseDTO() {
    }

    // Constructor sin token (para mensajes de error o éxito sin token)
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
}