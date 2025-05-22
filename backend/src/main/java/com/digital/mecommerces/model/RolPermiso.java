package com.digital.mecommerces.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "rolpermiso")
@Data
@NoArgsConstructor
@IdClass(RolPermisoId.class)
public class RolPermiso implements Serializable {

    @Id
    @Column(name = "rol_id")
    private Long rolId;

    @Id
    @Column(name = "permiso_id")
    private Long permisoId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", insertable = false, updatable = false)
    private RolUsuario rol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permiso_id", insertable = false, updatable = false)
    private Permiso permiso;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "createdby", length = 50)
    private String createdby;

    public RolPermiso(RolUsuario rol, Permiso permiso) {
        this.rol = rol;
        this.permiso = permiso;
        this.rolId = rol.getRolId();
        this.permisoId = permiso.getPermisoId();
        this.createdat = LocalDateTime.now();
    }

    public RolPermiso(Long rolId, Long permisoId) {
        this.rolId = rolId;
        this.permisoId = permisoId;
        this.createdat = LocalDateTime.now();
    }
}
