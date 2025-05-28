package com.digital.mecommerces.model;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "ordendetalle")
@Slf4j
public class OrdenDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalleid", nullable = false)
    private Long detalleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordenid", nullable = false)
    private Orden orden;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productoid", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "preciounitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "fechacreacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fechamodificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "descuento")
    private Double descuento = 0.0;

    @Column(name = "impuesto")
    private Double impuesto = 0.0;

    // Constructor vacío requerido por JPA
    public OrdenDetalle() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
        this.descuento = 0.0;
        this.impuesto = 0.0;
    }

    // Constructor optimizado con producto y cantidad
    public OrdenDetalle(Producto producto, Integer cantidad) {
        this();
        this.producto = producto;
        this.cantidad = cantidad;

        if (producto != null) {
            this.precioUnitario = producto.getPrecio();
            log.debug("✅ OrdenDetalle creado: {} x{} - ${}",
                    producto.getProductoNombre(), cantidad, this.precioUnitario);
        } else {
            log.warn("⚠️ OrdenDetalle creado con producto null");
        }
    }

    // Constructor completo optimizado
    public OrdenDetalle(Producto producto, Integer cantidad, Orden orden) {
        this(producto, cantidad);
        this.orden = orden;
    }

    // Métodos de gestión optimizados
    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
        log.debug("🔄 OrdenDetalle actualizado - ID: {}", this.detalleId);
    }

    // Métodos de utilidad optimizados
    public Double getSubtotal() {
        if (cantidad == null || precioUnitario == null) {
            return 0.0;
        }
        Double subtotalSinDescuento = cantidad * precioUnitario;
        Double montoDescuento = (descuento != null) ? subtotalSinDescuento * (descuento / 100) : 0.0;
        Double subtotalConDescuento = subtotalSinDescuento - montoDescuento;
        Double montoImpuesto = (impuesto != null) ? subtotalConDescuento * (impuesto / 100) : 0.0;
        
        return subtotalConDescuento + montoImpuesto;
    }

    // Getters y Setters optimizados
    public Long getDetalleId() {
        return detalleId;
    }

    public void setDetalleId(Long detalleId) {
        this.detalleId = detalleId;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;

        if (producto != null) {
            // Actualizar precio automáticamente
            this.precioUnitario = producto.getPrecio();
        }

        this.fechaModificacion = LocalDateTime.now();
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        if (cantidad != null && cantidad > 0) {
            this.cantidad = cantidad;
            this.fechaModificacion = LocalDateTime.now();
            log.debug("🔢 Cantidad actualizada para detalle {}: {}", this.detalleId, cantidad);
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
        this.fechaModificacion = LocalDateTime.now();
    }

    public Double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Double impuesto) {
        this.impuesto = impuesto;
        this.fechaModificacion = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "OrdenDetalle{" +
                "detalleId=" + detalleId +
                ", producto=" + (producto != null ? producto.getProductoNombre() : "null") +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + getSubtotal() +
                ", descuento=" + descuento +
                ", impuesto=" + impuesto +
                '}';
    }
}