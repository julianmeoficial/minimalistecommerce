package com.digital.mecommerces.model;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritocompra")
@Slf4j
public class CarritoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carritoid", nullable = false)
    private Long carritoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioid", nullable = false)
    private Usuario usuario;

    @Column(name = "fechacreacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fechamodificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "estado", length = 50)
    private String estado = "ACTIVO";

    @Column(name = "totalitems")
    private Integer totalItems = 0;

    @Column(name = "totalestimado")
    private Double totalEstimado = 0.0;

    @OneToMany(mappedBy = "carritoCompra", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CarritoItem> items = new ArrayList<>();

    // Constructor vacío requerido por JPA
    public CarritoCompra() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
        this.activo = true;
        this.estado = "ACTIVO";
        this.totalItems = 0;
        this.totalEstimado = 0.0;
    }

    // Constructor optimizado con usuario
    public CarritoCompra(Usuario usuario) {
        this();
        this.usuario = usuario;

        if (usuario != null && usuario.getRol() != null) {
            try {
                TipoUsuario tipo = TipoUsuario.fromCodigo(usuario.getRol().getNombre());
                log.debug("✅ Carrito creado para usuario tipo: {}", tipo.getDescripcion());

                // Solo compradores y administradores pueden tener carrito
                if (tipo != TipoUsuario.COMPRADOR && tipo != TipoUsuario.ADMINISTRADOR) {
                    log.warn("⚠️ Usuario tipo {} no debería tener carrito de compras", tipo.getCodigo());
                }
            } catch (IllegalArgumentException e) {
                log.warn("⚠️ Rol no reconocido para carrito: {}", usuario.getRol().getNombre());
            }
        }
    }

    // Métodos de gestión del carrito optimizados
    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
        recalcularTotales();
        log.debug("🔄 Carrito actualizado - ID: {}, Items: {}", this.carritoId, this.totalItems);
    }

    public void addItem(CarritoItem item) {
        if (item == null) {
            log.warn("⚠️ Intento de agregar item null al carrito {}", this.carritoId);
            return;
        }

        // Verificar si el producto ya existe en el carrito
        CarritoItem existente = findItemByProducto(item.getProducto());

        if (existente != null) {
            // Actualizar cantidad del item existente
            existente.setCantidad(existente.getCantidad() + item.getCantidad());
            log.debug("🔄 Cantidad actualizada para producto {} en carrito {}",
                    item.getProducto().getProductoId(), this.carritoId);
        } else {
            // Agregar nuevo item
            items.add(item);
            item.setCarritoCompra(this);
            log.debug("➕ Nuevo item agregado al carrito {}: {}",
                    this.carritoId, item.getProducto().getProductoNombre());
        }

        recalcularTotales();
        this.fechaModificacion = LocalDateTime.now();
    }

    public void removeItem(CarritoItem item) {
        if (item != null && items.remove(item)) {
            item.setCarritoCompra(null);
            recalcularTotales();
            this.fechaModificacion = LocalDateTime.now();
            log.debug("➖ Item eliminado del carrito {}: {}",
                    this.carritoId, item.getProducto().getProductoNombre());
        }
    }

    public void removeItemByProducto(Producto producto) {
        CarritoItem item = findItemByProducto(producto);
        if (item != null) {
            removeItem(item);
        }
    }

    public CarritoItem findItemByProducto(Producto producto) {
        if (producto == null || producto.getProductoId() == null) {
            return null;
        }

        return items.stream()
                .filter(item -> item.getProducto() != null &&
                        producto.getProductoId().equals(item.getProducto().getProductoId()))
                .findFirst()
                .orElse(null);
    }

    public void actualizarCantidadProducto(Producto producto, Integer nuevaCantidad) {
        CarritoItem item = findItemByProducto(producto);

        if (item != null) {
            if (nuevaCantidad <= 0) {
                removeItem(item);
            } else {
                item.setCantidad(nuevaCantidad);
                recalcularTotales();
                this.fechaModificacion = LocalDateTime.now();
                log.debug("🔄 Cantidad actualizada para producto {} a {}",
                        producto.getProductoId(), nuevaCantidad);
            }
        }
    }

    public void vaciarCarrito() {
        items.forEach(item -> item.setCarritoCompra(null));
        items.clear();
        recalcularTotales();
        this.fechaModificacion = LocalDateTime.now();
        log.info("🗑️ Carrito {} vaciado completamente", this.carritoId);
    }

    public void recalcularTotales() {
        this.totalItems = items.size();
        this.totalEstimado = items.stream()
                .mapToDouble(CarritoItem::calcularSubtotal)
                .sum();

        log.debug("📊 Totales recalculados para carrito {}: {} items, ${}",
                this.carritoId, this.totalItems, this.totalEstimado);
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }

    public boolean tieneProducto(Producto producto) {
        return findItemByProducto(producto) != null;
    }

    public boolean puedeComprar() {
        return this.activo && !estaVacio() &&
                this.usuario != null && this.usuario.getActivo() &&
                items.stream().allMatch(item ->
                        item.getProducto().getActivo() &&
                                item.getProducto().getStock() >= item.getCantidad());
    }

    public void marcarComoInactivo() {
        this.activo = false;
        this.estado = "INACTIVO";
        this.fechaModificacion = LocalDateTime.now();
        log.info("❌ Carrito {} marcado como inactivo", this.carritoId);
    }

    public void convertirAOrden() {
        this.estado = "CONVERTIDO";
        this.activo = false;
        this.fechaModificacion = LocalDateTime.now();
        log.info("🛒➡️📦 Carrito {} convertido a orden", this.carritoId);
    }

    // Getters y Setters optimizados
    public Long getCarritoId() {
        return carritoId;
    }

    public void setCarritoId(Long carritoId) {
        this.carritoId = carritoId;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
        if (!activo) {
            this.estado = "INACTIVO";
        }
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Double getTotalEstimado() {
        return totalEstimado;
    }

    public void setTotalEstimado(Double totalEstimado) {
        this.totalEstimado = totalEstimado;
    }

    public List<CarritoItem> getItems() {
        return items;
    }

    public void setItems(List<CarritoItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        recalcularTotales();
    }

    @Override
    public String toString() {
        return "CarritoCompra{" +
                "carritoId=" + carritoId +
                ", usuario=" + (usuario != null ? usuario.getEmail() : "null") +
                ", activo=" + activo +
                ", estado='" + estado + '\'' +
                ", totalItems=" + totalItems +
                ", totalEstimado=" + totalEstimado +
                '}';
    }
}
