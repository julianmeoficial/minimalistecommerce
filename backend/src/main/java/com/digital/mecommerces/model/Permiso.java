package com.digital.mecommerces.model;

import com.digital.mecommerces.constants.RoleConstants;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permiso")
@Slf4j
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permisoid", nullable = false)
    private Long permisoId;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "nivel")
    private Integer nivel = 0;

    @Column(name = "permisopadreid")
    private Long permisopadreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permisopadreid", insertable = false, updatable = false)
    private Permiso permisoPadre;

    @OneToMany(mappedBy = "permisoPadre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Permiso> subpermisos = new ArrayList<>();

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "categoria", length = 50)
    private String categoria = "GENERAL";

    // Constructor vacío requerido por JPA
    public Permiso() {
        this.nivel = 0;
        this.activo = true;
        this.categoria = "GENERAL";
    }

    // Constructor optimizado con código
    public Permiso(String codigo) {
        this();
        this.codigo = codigo.toUpperCase();

        // Asignar descripción automática si es un permiso del sistema
        if (esPermisoDelSistema(this.codigo)) {
            this.descripcion = obtenerDescripcionSistema(this.codigo);
            this.nivel = obtenerNivelSistema(this.codigo);
            this.categoria = obtenerCategoriaSistema(this.codigo);
            log.debug("✅ Permiso del sistema creado: {}", this.codigo);
        } else {
            log.debug("📝 Permiso personalizado creado: {}", this.codigo);
        }
    }

    // Constructor completo optimizado
    public Permiso(String codigo, String descripcion) {
        this(codigo);
        if (descripcion != null && !descripcion.isEmpty()) {
            this.descripcion = descripcion;
        }
    }

    // Constructor con nivel jerárquico
    public Permiso(String codigo, String descripcion, Integer nivel) {
        this(codigo, descripcion);
        this.nivel = nivel;
    }

    // Métodos de validación optimizados
    @PrePersist
    public void prePersist() {
        if (this.codigo != null) {
            this.codigo = this.codigo.toUpperCase();
        }

        if (this.activo == null) {
            this.activo = true;
        }

        if (this.nivel == null) {
            this.nivel = 0;
        }

        if (this.categoria == null || this.categoria.isEmpty()) {
            this.categoria = "GENERAL";
        }

        // Asignar valores del sistema si es aplicable
        if (esPermisoDelSistema(this.codigo)) {
            if (this.descripcion == null || this.descripcion.isEmpty()) {
                this.descripcion = obtenerDescripcionSistema(this.codigo);
            }
            this.nivel = obtenerNivelSistema(this.codigo);
            this.categoria = obtenerCategoriaSistema(this.codigo);
        }

        log.debug("✅ Permiso preparado para persistir: {} - Nivel: {}", this.codigo, this.nivel);
    }

    @PreUpdate
    public void preUpdate() {
        if (this.codigo != null) {
            this.codigo = this.codigo.toUpperCase();
        }
        log.debug("🔄 Permiso actualizado: {}", this.codigo);
    }

    // Métodos de utilidad optimizados
    public boolean esPermisoDelSistema() {
        return esPermisoDelSistema(this.codigo);
    }

    private boolean esPermisoDelSistema(String codigo) {
        return codigo != null && (
                codigo.equals(RoleConstants.PERM_ADMIN_TOTAL) ||
                        codigo.equals(RoleConstants.PERM_VENDER_PRODUCTOS) ||
                        codigo.equals(RoleConstants.PERM_COMPRAR_PRODUCTOS) ||
                        codigo.equals(RoleConstants.PERM_GESTIONAR_USUARIOS) ||
                        codigo.equals(RoleConstants.PERM_GESTIONAR_CATEGORIAS)
        );
    }

    private String obtenerDescripcionSistema(String codigo) {
        switch (codigo) {
            case "ADMIN_TOTAL":
                return "Acceso total de administrador al sistema medbcommerce";
            case "VENDER_PRODUCTOS":
                return "Crear, editar y gestionar productos para venta";
            case "COMPRAR_PRODUCTOS":
                return "Realizar compras y gestionar órdenes";
            case "GESTIONAR_USUARIOS":
                return "Gestionar usuarios del sistema";
            case "GESTIONAR_CATEGORIAS":
                return "Gestionar categorías de productos";
            default:
                return "Permiso personalizado";
        }
    }

    private Integer obtenerNivelSistema(String codigo) {
        switch (codigo) {
            case "ADMIN_TOTAL":
                return 1; // Nivel más alto
            case "GESTIONAR_USUARIOS":
            case "GESTIONAR_CATEGORIAS":
                return 2; // Nivel administrativo
            case "VENDER_PRODUCTOS":
                return 3; // Nivel de vendedor
            case "COMPRAR_PRODUCTOS":
                return 4; // Nivel básico
            default:
                return 999; // Nivel más bajo para personalizados
        }
    }

    private String obtenerCategoriaSistema(String codigo) {
        switch (codigo) {
            case "ADMIN_TOTAL":
            case "GESTIONAR_USUARIOS":
                return "ADMINISTRACION";
            case "GESTIONAR_CATEGORIAS":
                return "GESTION";
            case "VENDER_PRODUCTOS":
                return "VENTAS";
            case "COMPRAR_PRODUCTOS":
                return "COMPRAS";
            default:
                return "GENERAL";
        }
    }

    public boolean esPermisoJerarquico() {
        return this.permisoPadre != null || !this.subpermisos.isEmpty();
    }

    public boolean esPermisoRaiz() {
        return this.permisoPadre == null && this.permisopadreId == null;
    }

    public boolean tieneSubpermisos() {
        return this.subpermisos != null && !this.subpermisos.isEmpty();
    }

    public int getNivelJerarquia() {
        if (esPermisoRaiz()) {
            return 0;
        } else if (permisoPadre != null) {
            return permisoPadre.getNivelJerarquia() + 1;
        }
        return 1; // Fallback
    }

    public void agregarSubpermiso(Permiso subpermiso) {
        if (subpermiso != null) {
            subpermisos.add(subpermiso);
            subpermiso.setPermisoPadre(this);
            subpermiso.setPermisopadreId(this.permisoId);
            log.debug("➕ Subpermiso agregado: {} -> {}", this.codigo, subpermiso.getCodigo());
        }
    }

    public void removerSubpermiso(Permiso subpermiso) {
        if (subpermiso != null && subpermisos.remove(subpermiso)) {
            subpermiso.setPermisoPadre(null);
            subpermiso.setPermisopadreId(null);
            log.debug("➖ Subpermiso removido: {} <- {}", this.codigo, subpermiso.getCodigo());
        }
    }

    public boolean implica(Permiso otroPermiso) {
        if (otroPermiso == null) return false;

        // Un permiso de nivel más alto implica permisos de nivel más bajo
        return this.nivel <= otroPermiso.nivel;
    }

    public boolean esCompatibleCon(Permiso otroPermiso) {
        if (otroPermiso == null) return false;

        // Permisos de la misma categoría son compatibles
        return this.categoria.equals(otroPermiso.categoria);
    }

    // Getters y Setters optimizados
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
        this.codigo = codigo != null ? codigo.toUpperCase() : null;
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
        } else {
            this.permisopadreId = null;
        }
    }

    public List<Permiso> getSubpermisos() {
        return subpermisos;
    }

    public void setSubpermisos(List<Permiso> subpermisos) {
        this.subpermisos = subpermisos != null ? subpermisos : new ArrayList<>();
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Permiso{" +
                "permisoId=" + permisoId +
                ", codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", nivel=" + nivel +
                ", categoria='" + categoria + '\'' +
                ", activo=" + activo +
                ", esPermisoSistema=" + esPermisoDelSistema() +
                ", esPermisoRaiz=" + esPermisoRaiz() +
                '}';
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
}
