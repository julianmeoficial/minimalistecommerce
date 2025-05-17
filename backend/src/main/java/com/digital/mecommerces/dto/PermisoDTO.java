package com.digital.mecommerces.dto;

import jakarta.validation.constraints.NotBlank;

public class PermisoDTO {
    private Long permisoId;

    @NotBlank(message = "El código del permiso es obligatorio")
    private String codigo;

    private String descripcion;

    // Constructor vacío
    public PermisoDTO() {}

    // Constructor con parámetros
    public PermisoDTO(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getPermisoId() {
        return permisoId;
    }

    public void setPermisoId(Long permisoId) {
        this.permisoId = permisoId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

