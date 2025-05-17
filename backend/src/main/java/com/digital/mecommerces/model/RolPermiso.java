package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "rol_permiso")
@IdClass(RolPermisoId.class)
public class RolPermiso implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private RolUsuario rol;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permiso_id", nullable = false)
    private Permiso permiso;

    // Constructor vacío requerido por JPA
    public RolPermiso() {}

    // Constructor con parámetros
    public RolPermiso(RolUsuario rol, Permiso permiso) {
        this.rol = rol;
        this.permiso = permiso;
    }

    // Getters y Setters
    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public Permiso getPermiso() {
        return permiso;
    }

    public void setPermiso(Permiso permiso) {
        this.permiso = permiso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RolPermiso that = (RolPermiso) o;

        if (rol != null ? !rol.equals(that.rol) : that.rol != null) return false;
        return permiso != null ? permiso.equals(that.permiso) : that.permiso == null;
    }

    @Override
    public int hashCode() {
        int result = rol != null ? rol.hashCode() : 0;
        result = 31 * result + (permiso != null ? permiso.hashCode() : 0);
        return result;
    }
}
