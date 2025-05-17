package com.digital.mecommerces.model;

import java.io.Serializable;
import java.util.Objects;

public class RolPermisoId implements Serializable {

    private Long rol; // Debe coincidir con el nombre del atributo en RolPermiso
    private Long permiso; // Debe coincidir con el nombre del atributo en RolPermiso

    // Constructor vacío requerido por JPA
    public RolPermisoId() {}

    public RolPermisoId(Long rol, Long permiso) {
        this.rol = rol;
        this.permiso = permiso;
    }

    public Long getRol() {
        return rol;
    }

    public void setRol(Long rol) {
        this.rol = rol;
    }

    public Long getPermiso() {
        return permiso;
    }

    public void setPermiso(Long permiso) {
        this.permiso = permiso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolPermisoId that = (RolPermisoId) o;
        return Objects.equals(rol, that.rol) &&
                Objects.equals(permiso, that.permiso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rol, permiso);
    }
}
