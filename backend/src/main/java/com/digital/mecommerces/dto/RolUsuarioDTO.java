package com.digital.mecommerces.dto;

import jakarta.validation.constraints.NotBlank;

public class RolUsuarioDTO {
    private Long rolId;

    @NotBlank(message = "El nombre del rol es obligatorio")
    private String nombre;

    private String descripcion;

    // Constructor vacío
    public RolUsuarioDTO() {}

    // Constructor con parámetros
    public RolUsuarioDTO(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

