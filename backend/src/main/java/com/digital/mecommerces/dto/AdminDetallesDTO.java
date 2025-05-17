package com.digital.mecommerces.dto;

public class AdminDetallesDTO {
    private Long usuarioId;
    private String region;
    private String nivelAcceso;

    // Constructor vacío
    public AdminDetallesDTO() {}

    // Constructor con parámetros
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
}
