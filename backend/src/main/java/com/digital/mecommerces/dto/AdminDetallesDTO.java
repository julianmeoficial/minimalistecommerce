package com.digital.mecommerces.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class AdminDetallesDTO {

    private Long usuarioId;

    @Size(max = 100, message = "La región no puede tener más de 100 caracteres")
    private String region;

    @Size(max = 255, message = "El nivel de acceso no puede tener más de 255 caracteres")
    private String nivelAcceso;

    @Size(max = 255, message = "La última acción no puede tener más de 255 caracteres")
    private String ultimaAccion;

    private LocalDateTime ultimoLogin;

    @Size(max = 50, message = "La IP de acceso no puede tener más de 50 caracteres")
    private String ipAcceso;

    // Constructor vacío
    public AdminDetallesDTO() {}

    // Constructor con parámetros básicos
    public AdminDetallesDTO(String region, String nivelAcceso) {
        this.region = region;
        this.nivelAcceso = nivelAcceso;
    }

    // Getters y Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getNivelAcceso() {
        return nivelAcceso;
    }

    public void setNivelAcceso(String nivelAcceso) {
        this.nivelAcceso = nivelAcceso;
    }

    public String getUltimaAccion() {
        return ultimaAccion;
    }

    public void setUltimaAccion(String ultimaAccion) {
        this.ultimaAccion = ultimaAccion;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public String getIpAcceso() {
        return ipAcceso;
    }

    public void setIpAcceso(String ipAcceso) {
        this.ipAcceso = ipAcceso;
    }
}
