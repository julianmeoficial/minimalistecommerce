package com.digital.mecommerces.model;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Slf4j
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuarioid", nullable = false)
    private Long usuarioId;

    @Column(name = "usuarionombre", nullable = false, length = 50)
    private String usuarioNombre;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rolid", nullable = false)
    private RolUsuario rol;

    @Column(name = "createdat")
    private LocalDateTime createdAt;

    @Column(name = "updatedat")
    private LocalDateTime updatedAt;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "ultimologin")
    private LocalDateTime ultimoLogin;

    // Relaciones con detalles específicos por rol
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private AdminDetalles adminDetalles;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private CompradorDetalles compradorDetalles;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private VendedorDetalles vendedorDetalles;

    // Relaciones con entidades del negocio
    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Producto> productos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CarritoCompra> carritos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Orden> ordenes = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PasswordResetTokens> passwordResetTokens = new ArrayList<>();

    // Constructor vacío requerido por JPA
    public Usuario() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.activo = true;
    }

    // Constructor optimizado con validación de rol
    public Usuario(String usuarioNombre, String email, String password, RolUsuario rol) {
        this();
        this.usuarioNombre = usuarioNombre;
        this.email = email;
        this.password = password;
        this.rol = rol;

        // Validar que el rol sea del sistema optimizado
        if (rol != null) {
            try {
                TipoUsuario tipo = TipoUsuario.fromCodigo(rol.getNombre());
                log.debug("✅ Usuario creado con rol del sistema: {} - {}",
                        tipo.getCodigo(), tipo.getDescripcion());
            } catch (IllegalArgumentException e) {
                log.debug("📝 Usuario creado con rol personalizado: {}", rol.getNombre());
            }
        }
    }

    // Métodos de ciclo de vida optimizados
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }

        if (this.activo == null) {
            this.activo = true;
        }

        log.debug("✅ Usuario preparado para persistir: {} - Rol: {}",
                this.email, this.rol != null ? this.rol.getNombre() : "null");
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        log.debug("🔄 Usuario actualizado: {}", this.email);
    }

    // Métodos de validación de roles optimizados
    public boolean esAdministrador() {
        return this.rol != null && RoleConstants.ROLE_ADMINISTRADOR.equals(this.rol.getNombre());
    }

    public boolean esVendedor() {
        return this.rol != null && RoleConstants.ROLE_VENDEDOR.equals(this.rol.getNombre());
    }

    public boolean esComprador() {
        return this.rol != null && RoleConstants.ROLE_COMPRADOR.equals(this.rol.getNombre());
    }

    public boolean tieneRolDelSistema() {
        if (this.rol == null) return false;

        try {
            TipoUsuario.fromCodigo(this.rol.getNombre());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public TipoUsuario getTipoUsuario() {
        if (this.rol == null) return null;

        try {
            return TipoUsuario.fromCodigo(this.rol.getNombre());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Métodos de validación de permisos optimizados
    public boolean tienePermiso(String codigoPermiso) {
        return this.rol != null && this.rol.tienePermiso(codigoPermiso);
    }

    public boolean puedeVenderProductos() {
        return tienePermiso(RoleConstants.PERM_VENDER_PRODUCTOS) || esAdministrador();
    }

    public boolean puedeComprarProductos() {
        return tienePermiso(RoleConstants.PERM_COMPRAR_PRODUCTOS) || esAdministrador();
    }

    public boolean puedeGestionarUsuarios() {
        return tienePermiso(RoleConstants.PERM_GESTIONAR_USUARIOS) || esAdministrador();
    }

    public boolean puedeGestionarCategorias() {
        return tienePermiso(RoleConstants.PERM_GESTIONAR_CATEGORIAS) || esAdministrador();
    }

    public boolean esAdminTotal() {
        return tienePermiso(RoleConstants.PERM_ADMIN_TOTAL);
    }

    // Métodos de gestión de estado optimizados
    public boolean estaActivo() {
        return this.activo != null && this.activo;
    }

    public void activar() {
        this.activo = true;
        this.updatedAt = LocalDateTime.now();
        log.debug("✅ Usuario activado: {}", this.email);
    }

    public void desactivar() {
        this.activo = false;
        this.updatedAt = LocalDateTime.now();
        log.debug("❌ Usuario desactivado: {}", this.email);
    }

    public void registrarLogin() {
        this.ultimoLogin = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        log.debug("🔐 Login registrado para usuario: {}", this.email);
    }

    // Métodos de gestión de detalles por rol optimizados
    public Object getDetallesEspecificos() {
        if (esAdministrador()) {
            return this.adminDetalles;
        } else if (esComprador()) {
            return this.compradorDetalles;
        } else if (esVendedor()) {
            return this.vendedorDetalles;
        }
        return null;
    }

    public boolean tieneDetallesCompletos() {
        if (esAdministrador()) {
            return this.adminDetalles != null;
        } else if (esComprador()) {
            return this.compradorDetalles != null &&
                    this.compradorDetalles.tieneInformacionCompleta();
        } else if (esVendedor()) {
            return this.vendedorDetalles != null;
        }
        return true; // Para roles personalizados
    }

    // Métodos de gestión de carritos optimizados
    public CarritoCompra getCarritoActivo() {
        return this.carritos.stream()
                .filter(carrito -> carrito.getActivo() != null && carrito.getActivo())
                .findFirst()
                .orElse(null);
    }

    public boolean tieneCarritoActivo() {
        return getCarritoActivo() != null;
    }

    public void agregarCarrito(CarritoCompra carrito) {
        if (carrito != null) {
            this.carritos.add(carrito);
            carrito.setUsuario(this);
            log.debug("🛒 Carrito agregado al usuario: {}", this.email);
        }
    }

    // Métodos de gestión de productos (para vendedores) optimizados
    public boolean puedeGestionarProducto(Producto producto) {
        if (!puedeVenderProductos()) {
            return false;
        }

        if (esAdministrador()) {
            return true; // Admin puede gestionar cualquier producto
        }

        return producto != null && this.equals(producto.getVendedor());
    }

    public void agregarProducto(Producto producto) {
        if (producto != null && puedeVenderProductos()) {
            this.productos.add(producto);
            producto.setVendedor(this);
            log.debug("📦 Producto agregado al vendedor: {}", this.email);
        }
    }

    public int getNumeroProductos() {
        return this.productos != null ? this.productos.size() : 0;
    }

    public int getNumeroProductosActivos() {
        if (this.productos == null) return 0;

        return (int) this.productos.stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .count();
    }

    // Métodos de gestión de órdenes optimizados
    public void agregarOrden(Orden orden) {
        if (orden != null) {
            this.ordenes.add(orden);
            orden.setUsuario(this);
            log.debug("📋 Orden agregada al usuario: {}", this.email);
        }
    }

    public int getNumeroOrdenes() {
        return this.ordenes != null ? this.ordenes.size() : 0;
    }

    public List<Orden> getOrdenesRecientes(int limite) {
        if (this.ordenes == null) return new ArrayList<>();

        return this.ordenes.stream()
                .sorted((o1, o2) -> o2.getFechaCreacion().compareTo(o1.getFechaCreacion()))
                .limit(limite)
                .toList();
    }

    // Métodos de utilidad optimizados
    public String getNombreCompleto() {
        return this.usuarioNombre;
    }

    public String getRolDescriptivo() {
        if (this.rol == null) return "Sin rol";

        TipoUsuario tipo = getTipoUsuario();
        return tipo != null ? tipo.getDescripcion() : this.rol.getNombre();
    }

    public boolean puedeRealizarAccion(String accion) {
        if (!estaActivo()) return false;

        switch (accion.toUpperCase()) {
            case "VENDER":
                return puedeVenderProductos();
            case "COMPRAR":
                return puedeComprarProductos();
            case "GESTIONAR_USUARIOS":
                return puedeGestionarUsuarios();
            case "GESTIONAR_CATEGORIAS":
                return puedeGestionarCategorias();
            case "ADMIN_TOTAL":
                return esAdminTotal();
            default:
                return false;
        }
    }

    // Getters y Setters optimizados
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public AdminDetalles getAdminDetalles() {
        return adminDetalles;
    }

    public void setAdminDetalles(AdminDetalles adminDetalles) {
        this.adminDetalles = adminDetalles;
    }

    public CompradorDetalles getCompradorDetalles() {
        return compradorDetalles;
    }

    public void setCompradorDetalles(CompradorDetalles compradorDetalles) {
        this.compradorDetalles = compradorDetalles;
    }

    public VendedorDetalles getVendedorDetalles() {
        return vendedorDetalles;
    }

    public void setVendedorDetalles(VendedorDetalles vendedorDetalles) {
        this.vendedorDetalles = vendedorDetalles;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos != null ? productos : new ArrayList<>();
    }

    public List<CarritoCompra> getCarritos() {
        return carritos;
    }

    public void setCarritos(List<CarritoCompra> carritos) {
        this.carritos = carritos != null ? carritos : new ArrayList<>();
    }

    public List<Orden> getOrdenes() {
        return ordenes;
    }

    public void setOrdenes(List<Orden> ordenes) {
        this.ordenes = ordenes != null ? ordenes : new ArrayList<>();
    }

    public List<PasswordResetTokens> getPasswordResetTokens() {
        return passwordResetTokens;
    }

    public void setPasswordResetTokens(List<PasswordResetTokens> passwordResetTokens) {
        this.passwordResetTokens = passwordResetTokens != null ? passwordResetTokens : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "usuarioId=" + usuarioId +
                ", usuarioNombre='" + usuarioNombre + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + (rol != null ? rol.getNombre() : "null") +
                ", tipoUsuario=" + (getTipoUsuario() != null ? getTipoUsuario().getDescripcion() : "personalizado") +
                ", activo=" + activo +
                ", numeroProductos=" + getNumeroProductos() +
                ", numeroOrdenes=" + getNumeroOrdenes() +
                ", tieneCarritoActivo=" + tieneCarritoActivo() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return usuarioId != null && usuarioId.equals(usuario.usuarioId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
