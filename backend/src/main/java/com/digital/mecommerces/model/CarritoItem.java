package com.digital.mecommerces.model;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "carritoitem")
@Slf4j
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itemid", nullable = false)
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carritoid", nullable = false)
    private CarritoCompra carritoCompra;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productoid", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "preciounitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "fechaagregado")
    private LocalDateTime fechaAgregado;

    @Column(name = "fechamodificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "guardadodespues")
    private Boolean guardadoDespues = false;

    @Column(name = "disponible")
    private Boolean disponible = true;

    // Constructor vacío requerido por JPA
    public CarritoItem() {
        this.fechaAgregado = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
        this.guardadoDespues = false;
        this.disponible = true;
    }

    // Constructor optimizado con producto y cantidad
    public CarritoItem(Producto producto, Integer cantidad) {
        this();
        this.producto = producto;
        this.cantidad = cantidad;

        if (producto != null) {
            this.precioUnitario = producto.getPrecio();
            this.disponible = producto.getActivo() && producto.getStock() >= cantidad;

            log.debug("✅ CarritoItem creado: {} x{} - ${}",
                    producto.getProductoNombre(), cantidad, this.precioUnitario);
        } else {
            log.warn("⚠️ CarritoItem creado con producto null");
            this.disponible = false;
        }
    }

    // Constructor completo optimizado
    public CarritoItem(Producto producto, Integer cantidad, CarritoCompra carritoCompra) {
        this(producto, cantidad);
        this.carritoCompra = carritoCompra;
    }

    // Métodos de gestión optimizados
    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();

        // Verificar disponibilidad del producto
        if (producto != null) {
            this.disponible = producto.getActivo() && producto.getStock() >= this.cantidad;

            // Actualizar precio si es necesario (opcional)
            if (!this.precioUnitario.equals(producto.getPrecio())) {
                log.debug("💰 Precio actualizado para item {}: {} -> {}",
                        this.itemId, this.precioUnitario, producto.getPrecio());
                this.precioUnitario = producto.getPrecio();
            }
        }

        log.debug("🔄 CarritoItem actualizado - ID: {}, Disponible: {}", this.itemId, this.disponible);
    }

    // Métodos de utilidad optimizados
    public Double calcularSubtotal() {
        if (cantidad == null || precioUnitario == null) {
            return 0.0;
        }
        return cantidad * precioUnitario;
    }

    public boolean puedeComprar() {
        return this.disponible &&
                this.producto != null &&
                this.producto.getActivo() &&
                this.producto.getStock() >= this.cantidad &&
                this.cantidad > 0;
    }

    public boolean necesitaActualizacionPrecio() {
        return this.producto != null &&
                !this.precioUnitario.equals(this.producto.getPrecio());
    }

    public void actualizarPrecioDesdeProducto() {
        if (this.producto != null) {
            Double precioAnterior = this.precioUnitario;
            this.precioUnitario = this.producto.getPrecio();
            this.fechaModificacion = LocalDateTime.now();

            log.debug("💰 Precio actualizado para item {}: {} -> {}",
                    this.itemId, precioAnterior, this.precioUnitario);
        }
    }

    public void verificarDisponibilidad() {
        if (this.producto != null) {
            boolean disponibleAnterior = this.disponible;
            this.disponible = this.producto.getActivo() && this.producto.getStock() >= this.cantidad;

            if (disponibleAnterior != this.disponible) {
                log.debug("📦 Disponibilidad cambiada para item {}: {} -> {}",
                        this.itemId, disponibleAnterior, this.disponible);
            }
        }
    }

    public void marcarComoGuardadoDespues() {
        this.guardadoDespues = true;
        this.fechaModificacion = LocalDateTime.now();
        log.debug("💾 Item {} marcado como guardado para después", this.itemId);
    }

    public void quitarDeGuardadoDespues() {
        this.guardadoDespues = false;
        this.fechaModificacion = LocalDateTime.now();
        log.debug("🔄 Item {} quitado de guardado para después", this.itemId);
    }

    public boolean esValidoParaCompra() {
        return puedeComprar() &&
                this.carritoCompra != null &&
                this.carritoCompra.getActivo();
    }

    public String getEstadoItem() {
        if (!this.disponible) {
            return "NO_DISPONIBLE";
        } else if (this.guardadoDespues) {
            return "GUARDADO_DESPUES";
        } else if (puedeComprar()) {
            return "DISPONIBLE";
        } else {
            return "PROBLEMA";
        }
    }

    // Getters y Setters optimizados
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public CarritoCompra getCarritoCompra() {
        return carritoCompra;
    }

    public void setCarritoCompra(CarritoCompra carritoCompra) {
        this.carritoCompra = carritoCompra;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;

        if (producto != null) {
            // Actualizar precio automáticamente
            this.precioUnitario = producto.getPrecio();
            // Verificar disponibilidad
            verificarDisponibilidad();
        }

        this.fechaModificacion = LocalDateTime.now();
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        if (cantidad != null && cantidad > 0) {
            this.cantidad = cantidad;
            verificarDisponibilidad();
            this.fechaModificacion = LocalDateTime.now();

            log.debug("🔢 Cantidad actualizada para item {}: {}", this.itemId, cantidad);
        } else {
            log.warn("⚠️ Intento de establecer cantidad inválida: {}", cantidad);
        }
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.fechaModificacion = LocalDateTime.now();
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Boolean getGuardadoDespues() {
        return guardadoDespues;
    }

    public void setGuardadoDespues(Boolean guardadoDespues) {
        this.guardadoDespues = guardadoDespues;
        this.fechaModificacion = LocalDateTime.now();
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "CarritoItem{" +
                "itemId=" + itemId +
                ", producto=" + (producto != null ? producto.getProductoNombre() : "null") +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + calcularSubtotal() +
                ", disponible=" + disponible +
                ", guardadoDespues=" + guardadoDespues +
                '}';
    }
}
