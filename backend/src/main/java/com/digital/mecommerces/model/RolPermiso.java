package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "rolpermiso")
@IdClass(RolPermisoId.class)
public class RolPermiso implements Serializable {

    @Id
    @Column(name = "rolid")
    private Long rolId;

    @Id
    @Column(name = "permisoid")
    private Long permisoId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rolid", insertable = false, updatable = false)
    private RolUsuario rol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permisoid", insertable = false, updatable = false)
    private Permiso permiso;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "createdby", length = 50)
    private String createdby;

    // Constructor vacío
    public RolPermiso() {
        this.createdat = LocalDateTime.now();
    }

    // Constructor con entidades
    public RolPermiso(RolUsuario rol, Permiso permiso) {
        this.rol = rol;
        this.permiso = permiso;
        this.rolId = rol.getRolId();
        this.permisoId = permiso.getPermisoId();
        this.createdat = LocalDateTime.now();
    }

    // Constructor con IDs
    public RolPermiso(Long rolId, Long permisoId) {
        this.rolId = rolId;
        this.permisoId = permisoId;
        this.createdat = LocalDateTime.now();
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

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
        if (rol != null) {
            this.rolId = rol.getRolId();
        }
    }

    public Permiso getPermiso() {
        return permiso;
    }

    public void setPermiso(Permiso permiso) {
        this.permiso = permiso;
        if (permiso != null) {
            this.permisoId = permiso.getPermisoId();
        }
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolPermiso)) return false;
        RolPermiso that = (RolPermiso) o;
        return rolId != null && permisoId != null &&
                rolId.equals(that.rolId) && permisoId.equals(that.permisoId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(rolId, permisoId);
    }

    @Override
    public String toString() {
        return "RolPermiso{" +
                "rolId=" + rolId +
                ", permisoId=" + permisoId +
                ", createdat=" + createdat +
                '}';
    }
}
