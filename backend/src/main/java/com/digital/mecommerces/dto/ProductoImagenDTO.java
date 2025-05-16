package com.digital.mecommerces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductoImagenDTO {

    private Long imagenId;

    @NotBlank(message = "La URL de la imagen es obligatoria")
    private String url;

    private String descripcion;

    private Boolean esPrincipal;

    // Constructor vacío
    public ProductoImagenDTO() {
    }

    // Getters y Setters
    public Long getImagenId() {
        return imagenId;
    }

    public void setImagenId(Long imagenId) {
        this.imagenId = imagenId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}