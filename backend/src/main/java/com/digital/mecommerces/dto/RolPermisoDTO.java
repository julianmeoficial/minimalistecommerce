package com.digital.mecommerces.dto;

import jakarta.validation.constraints.NotNull;

public class RolPermisoDTO {
    private Long id;

    @NotNull(message = "El ID del rol es obligatorio")
    private Long rolId;

    @NotNull(message = "El ID del permiso es obligatorio")
    private Long permisoId;

    // Constructor vacío
    public RolPermisoDTO() {}

    // Constructor con parámetros
    public RolPermisoDTO(Long rolId, Long permisoId) {
        this.rolId = rolId;
        this.permisoId = permisoId;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public Long getPermisoId() {
        return permisoId;
    }

    public void setPermisoId(Long permisoId) {
        this.permisoId = permisoId;
    }
}

