package com.digital.mecommerces.model;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rolusuario")
@Slf4j
public class RolUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rolid", nullable = false)
    private Long rolId;

    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<>();

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RolPermiso> rolPermisos = new ArrayList<>();

    // Constructor vacío requerido por JPA
    public RolUsuario() {}

    // Constructor optimizado con validación de enum
    public RolUsuario(String nombre) {
        this.nombre = nombre;

        // Validar si es un rol del sistema usando enum
        try {
            TipoUsuario tipo = TipoUsuario.fromCodigo(nombre.toUpperCase());
            this.descripcion = tipo.getDescripcion();
            log.debug("✅ Rol del sistema creado: {} - {}", tipo.getCodigo(), tipo.getDescripcion());
        } catch (IllegalArgumentException e) {
            log.debug("📝 Rol personalizado creado: {}", nombre);
        }
    }

    // Constructor completo optimizado
    public RolUsuario(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;

        try {
            TipoUsuario tipo = TipoUsuario.fromCodigo(nombre.toUpperCase());
            log.debug("✅ Rol del sistema creado con descripción personalizada: {}", tipo.getCodigo());
        } catch (IllegalArgumentException e) {
            log.debug("📝 Rol personalizado creado con descripción: {}", nombre);
        }
    }

    // Métodos de validación optimizados
    @PrePersist
    public void prePersist() {
        // Verificar si es un rol del sistema y asignar descripción automática si no tiene
        if (this.descripcion == null || this.descripcion.isEmpty()) {
            try {
                TipoUsuario tipo = TipoUsuario.fromCodigo(this.nombre.toUpperCase());
                this.descripcion = tipo.getDescripcion();
                log.debug("✅ Descripción automática asignada al rol del sistema: {}", this.nombre);
            } catch (IllegalArgumentException e) {
                this.descripcion = "Rol personalizado";
                log.debug("📝 Descripción por defecto asignada al rol personalizado: {}", this.nombre);
            }
        }

        log.debug("✅ RolUsuario preparado para persistir: {} - {}", this.nombre, this.descripcion);
    }

    @PreUpdate
    public void preUpdate() {
        log.debug("🔄 RolUsuario actualizado: {}", this.nombre);
    }

    // Métodos de utilidad optimizados
    public boolean esRolDelSistema() {
        try {
            TipoUsuario.fromCodigo(this.nombre.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public TipoUsuario getTipoUsuario() {
        try {
            return TipoUsuario.fromCodigo(this.nombre.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean esAdministrador() {
        return RoleConstants.ROLE_ADMINISTRADOR.equals(this.nombre);
    }

    public boolean esVendedor() {
        return RoleConstants.ROLE_VENDEDOR.equals(this.nombre);
    }

    public boolean esComprador() {
        return RoleConstants.ROLE_COMPRADOR.equals(this.nombre);
    }

    public boolean tieneUsuarios() {
        return this.usuarios != null && !this.usuarios.isEmpty();
    }

    public boolean tienePermisos() {
        return this.rolPermisos != null && !this.rolPermisos.isEmpty();
    }

    public int getNumeroUsuarios() {
        return this.usuarios != null ? this.usuarios.size() : 0;
    }

    public int getNumeroPermisos() {
        return this.rolPermisos != null ? this.rolPermisos.size() : 0;
    }

    // Métodos de gestión de permisos optimizados
    public void agregarPermiso(RolPermiso rolPermiso) {
        if (rolPermiso != null) {
            this.rolPermisos.add(rolPermiso);
            rolPermiso.setRol(this);
            log.debug("➕ Permiso agregado al rol {}: {}", this.nombre,
                    rolPermiso.getPermiso() != null ? rolPermiso.getPermiso().getCodigo() : "null");
        }
    }

    public void removerPermiso(RolPermiso rolPermiso) {
        if (rolPermiso != null && this.rolPermisos.remove(rolPermiso)) {
            rolPermiso.setRol(null);
            log.debug("➖ Permiso removido del rol {}", this.nombre);
        }
    }

    public boolean tienePermiso(String codigoPermiso) {
        if (this.rolPermisos == null || codigoPermiso == null) {
            return false;
        }

        return this.rolPermisos.stream()
                .anyMatch(rp -> rp.getPermiso() != null &&
                        codigoPermiso.equals(rp.getPermiso().getCodigo()));
    }

    public List<String> getCodigosPermisos() {
        if (this.rolPermisos == null) {
            return new ArrayList<>();
        }

        return this.rolPermisos.stream()
                .filter(rp -> rp.getPermiso() != null)
                .map(rp -> rp.getPermiso().getCodigo())
                .toList();
    }

    // Métodos de gestión de usuarios optimizados
    public void agregarUsuario(Usuario usuario) {
        if (usuario != null) {
            this.usuarios.add(usuario);
            usuario.setRol(this);
            log.debug("➕ Usuario agregado al rol {}: {}", this.nombre,
                    usuario.getEmail() != null ? usuario.getEmail() : "sin email");
        }
    }

    public void removerUsuario(Usuario usuario) {
        if (usuario != null && this.usuarios.remove(usuario)) {
            usuario.setRol(null);
            log.debug("➖ Usuario removido del rol {}", this.nombre);
        }
    }

    // Métodos de comparación optimizados
    public int getJerarquia() {
        if (esAdministrador()) {
            return 1; // Máxima jerarquía
        } else if (esVendedor()) {
            return 2; // Jerarquía media
        } else if (esComprador()) {
            return 3; // Jerarquía básica
        } else {
            return 999; // Roles personalizados - jerarquía más baja
        }
    }

    public boolean tieneMayorJerarquiaQue(RolUsuario otroRol) {
        if (otroRol == null) return true;
        return this.getJerarquia() < otroRol.getJerarquia();
    }

    public boolean puedeGestionar(RolUsuario otroRol) {
        // Un rol puede gestionar roles de jerarquía igual o menor
        return this.getJerarquia() <= otroRol.getJerarquia();
    }

    // Getters y Setters optimizados
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

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios != null ? usuarios : new ArrayList<>();
    }

    public List<RolPermiso> getRolPermisos() {
        return rolPermisos;
    }

    public void setRolPermisos(List<RolPermiso> rolPermisos) {
        this.rolPermisos = rolPermisos != null ? rolPermisos : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "RolUsuario{" +
                "rolId=" + rolId +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", numeroUsuarios=" + getNumeroUsuarios() +
                ", numeroPermisos=" + getNumeroPermisos() +
                ", esRolSistema=" + esRolDelSistema() +
                ", jerarquia=" + getJerarquia() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolUsuario)) return false;
        RolUsuario that = (RolUsuario) o;
        return rolId != null && rolId.equals(that.rolId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
