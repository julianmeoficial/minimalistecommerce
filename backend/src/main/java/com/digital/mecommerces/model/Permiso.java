package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permiso")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permisoid", nullable = false) // CORREGIDO: era "permiso_id"
    private Long permisoId;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "nivel")
    private Integer nivel = 0;

    @Column(name = "permisopadreid") // CORREGIDO: era "permiso_padre_id"
    private Long permisopadreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permisopadreid", insertable = false, updatable = false)
    private Permiso permisoPadre;

    @OneToMany(mappedBy = "permisoPadre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Permiso> permisosHijos = new ArrayList<>();

    @OneToMany(mappedBy = "permiso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RolPermiso> rolPermisos = new ArrayList<>();

    // Constructor vacío
    public Permiso() {}

    // Constructor con código y descripción
    public Permiso(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.nivel = 0;
    }

    // Constructor con código, descripción y nivel
    public Permiso(String codigo, String descripcion, Integer nivel) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.nivel = nivel;
    }

    // Constructor completo
    public Permiso(String codigo, String descripcion, Integer nivel, Permiso permisoPadre) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.nivel = nivel;
        this.permisoPadre = permisoPadre;
        if (permisoPadre != null) {
            this.permisopadreId = permisoPadre.getPermisoId();
        }
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

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Long getPermisopadreId() {
        return permisopadreId;
    }

    public void setPermisopadreId(Long permisopadreId) {
        this.permisopadreId = permisopadreId;
    }

    public Permiso getPermisoPadre() {
        return permisoPadre;
    }

    public void setPermisoPadre(Permiso permisoPadre) {
        this.permisoPadre = permisoPadre;
        if (permisoPadre != null) {
            this.permisopadreId = permisoPadre.getPermisoId();
        }
    }

    public List<Permiso> getPermisosHijos() {
        return permisosHijos;
    }

    public void setPermisosHijos(List<Permiso> permisosHijos) {
        this.permisosHijos = permisosHijos;
    }

    public List<RolPermiso> getRolPermisos() {
        return rolPermisos;
    }

    public void setRolPermisos(List<RolPermiso> rolPermisos) {
        this.rolPermisos = rolPermisos;
    }

    // Métodos de utilidad
    public void addPermisoHijo(Permiso hijo) {
        permisosHijos.add(hijo);
        hijo.setPermisoPadre(this);
    }

    public void removePermisoHijo(Permiso hijo) {
        permisosHijos.remove(hijo);
        hijo.setPermisoPadre(null);
    }

    public boolean tieneHijos() {
        return permisosHijos != null && !permisosHijos.isEmpty();
    }

    public boolean tienePadre() {
        return permisoPadre != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permiso)) return false;
        Permiso permiso = (Permiso) o;
        return permisoId != null && permisoId.equals(permiso.permisoId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Permiso{" +
                "permisoId=" + permisoId +
                ", codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", nivel=" + nivel +
                '}';
    }
}