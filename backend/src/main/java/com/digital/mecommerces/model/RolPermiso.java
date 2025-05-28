package com.digital.mecommerces.model;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "rolpermiso")
@IdClass(RolPermisoId.class)
@Slf4j
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
    private String createdby = "SYSTEM";

    // Constructor vacío requerido por JPA
    public RolPermiso() {
        this.createdat = LocalDateTime.now();
        this.createdby = "SYSTEM";
    }

    // Constructor optimizado con objetos
    public RolPermiso(RolUsuario rol, Permiso permiso) {
        this();
        this.rol = rol;
        this.permiso = permiso;

        if (rol != null) {
            this.rolId = rol.getRolId();
        }

        if (permiso != null) {
            this.permisoId = permiso.getPermisoId();
        }

        log.debug("✅ RolPermiso creado: {} -> {}",
                rol != null ? rol.getNombre() : "null",
                permiso != null ? permiso.getCodigo() : "null");
    }

    // Constructor con IDs
    public RolPermiso(Long rolId, Long permisoId) {
        this();
        this.rolId = rolId;
        this.permisoId = permisoId;
    }

    // Constructor completo optimizado
    public RolPermiso(RolUsuario rol, Permiso permiso, String createdby) {
        this(rol, permiso);
        this.createdby = createdby != null ? createdby : "SYSTEM";
    }

    // Métodos de ciclo de vida optimizados
    @PrePersist
    public void prePersist() {
        if (this.createdat == null) {
            this.createdat = LocalDateTime.now();
        }

        if (this.createdby == null || this.createdby.isEmpty()) {
            this.createdby = "SYSTEM";
        }

        // Sincronizar IDs con objetos
        if (this.rol != null && this.rolId == null) {
            this.rolId = this.rol.getRolId();
        }

        if (this.permiso != null && this.permisoId == null) {
            this.permisoId = this.permiso.getPermisoId();
        }

        log.debug("✅ RolPermiso preparado para persistir: Rol ID: {}, Permiso ID: {}",
                this.rolId, this.permisoId);
    }

    @PreUpdate
    public void preUpdate() {
        log.debug("🔄 RolPermiso actualizado: Rol ID: {}, Permiso ID: {}",
                this.rolId, this.permisoId);
    }

    // Métodos de validación optimizados
    public boolean esValido() {
        return this.rolId != null &&
                this.permisoId != null &&
                this.rol != null &&
                this.permiso != null;
    }

    public boolean perteneceARolDelSistema() {
        return this.rol != null && this.rol.esRolDelSistema();
    }

    public boolean esPermisoDelSistema() {
        return this.permiso != null && this.permiso.esPermisoDelSistema();
    }

    // Métodos de utilidad optimizados
    public String getRolNombre() {
        return this.rol != null ? this.rol.getNombre() : null;
    }

    public String getPermisoCodigo() {
        return this.permiso != null ? this.permiso.getCodigo() : null;
    }

    public String getPermisoDescripcion() {
        return this.permiso != null ? this.permiso.getDescripcion() : null;
    }

    public String getRolDescripcion() {
        return this.rol != null ? this.rol.getDescripcion() : null;
    }

    public boolean esAsignacionCritica() {
        // Verificar si es una asignación crítica del sistema
        if (this.rol == null || this.permiso == null) return false;

        return this.rol.esAdministrador() &&
                "ADMIN_TOTAL".equals(this.permiso.getCodigo());
    }

    public boolean esCompatible() {
        if (this.rol == null || this.permiso == null) return false;

        // Lógica de compatibilidad entre roles y permisos
        String rolNombre = this.rol.getNombre();
        String permisoCodigo = this.permiso.getCodigo();

        switch (rolNombre) {
            case "ADMINISTRADOR":
                return true; // Admin puede tener cualquier permiso

            case "VENDEDOR":
                return "VENDER_PRODUCTOS".equals(permisoCodigo) ||
                        "GESTIONAR_CATEGORIAS".equals(permisoCodigo);

            case "COMPRADOR":
                return "COMPRAR_PRODUCTOS".equals(permisoCodigo);

            default:
                return true; // Roles personalizados pueden tener cualquier permiso
        }
    }

    public int getNivelImportancia() {
        if (this.permiso == null) return 999;

        // Asignar nivel de importancia basado en el permiso
        String codigo = this.permiso.getCodigo();
        switch (codigo) {
            case "ADMIN_TOTAL":
                return 1; // Máxima importancia
            case "GESTIONAR_USUARIOS":
                return 2;
            case "GESTIONAR_CATEGORIAS":
                return 3;
            case "VENDER_PRODUCTOS":
                return 4;
            case "COMPRAR_PRODUCTOS":
                return 5;
            default:
                return 999; // Menor importancia para permisos personalizados
        }
    }

    // Getters y Setters optimizados
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
    public String toString() {
        return "RolPermiso{" +
                "rolId=" + rolId +
                ", permisoId=" + permisoId +
                ", rolNombre='" + getRolNombre() + '\'' +
                ", permisoCodigo='" + getPermisoCodigo() + '\'' +
                ", esValido=" + esValido() +
                ", esCompatible=" + esCompatible() +
                ", nivelImportancia=" + getNivelImportancia() +
                ", createdat=" + createdat +
                ", createdby='" + createdby + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolPermiso)) return false;
        RolPermiso that = (RolPermiso) o;
        return rolId != null && rolId.equals(that.rolId) &&
                permisoId != null && permisoId.equals(that.permisoId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(rolId, permisoId);
    }
}
