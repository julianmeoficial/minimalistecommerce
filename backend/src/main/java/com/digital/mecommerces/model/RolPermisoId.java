package com.digital.mecommerces.model;

import java.io.Serializable;
import java.util.Objects;

public class RolPermisoId implements Serializable {

    private Long rolId;
    private Long permisoId;

    // Constructor vacío
    public RolPermisoId() {}

    // Constructor con parámetros
    public RolPermisoId(Long rolId, Long permisoId) {
        this.rolId = rolId;
        this.permisoId = permisoId;
    }

    // Getters y Setters
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolPermisoId that = (RolPermisoId) o;
        return Objects.equals(rolId, that.rolId) &&
                Objects.equals(permisoId, that.permisoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rolId, permisoId);
    }

    @Override
    public String toString() {
        return "RolPermisoId{" +
                "rolId=" + rolId +
                ", permisoId=" + permisoId +
                '}';
    }
}
