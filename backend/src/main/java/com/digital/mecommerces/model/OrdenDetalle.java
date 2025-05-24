package com.digital.mecommerces.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ordendetalle")
public class OrdenDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalleid") // CORREGIDO: era "detalle_id"
    private Long detalleId;

    @Column(name = "ordenid", nullable = false) // CORREGIDO: era "orden_id"
    private Long ordenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordenid", insertable = false, updatable = false)
    private Orden orden;

    @Column(name = "productoid", nullable = false) // CORREGIDO: era "producto_id"
    private Long productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productoid", insertable = false, updatable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "preciounitario", nullable = false) // CORREGIDO: era "precio_unitario"
    private Double precioUnitario;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    @Column(name = "notas", length = 255)
    private String notas;

    @Column(name = "personalizado")
    private Boolean personalizado = false;

    @Column(name = "opcionespersonalizacion", columnDefinition = "TEXT") // CORREGIDO: era "opciones_personalizacion"
    private String opcionesPersonalizacion;

    // Constructor vacío
    public OrdenDetalle() {}

    // Constructor con parámetros
    public OrdenDetalle(Producto producto, Integer cantidad, Double precioUnitario) {
        this.producto = producto;
        this.productoId = producto.getProductoId();
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }

    // Getters y Setters
    public Long getDetalleId() {
        return detalleId;
    }

    public void setDetalleId(Long detalleId) {
        this.detalleId = detalleId;
    }

    public Long getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(Long ordenId) {
        this.ordenId = ordenId;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
        if (orden != null) {
            this.ordenId = orden.getOrdenId();
        }
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        if (producto != null) {
            this.productoId = producto.getProductoId();
        }
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        this.subtotal = cantidad * this.precioUnitario;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.subtotal = this.cantidad * precioUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Boolean getPersonalizado() {
        return personalizado;
    }

    public void setPersonalizado(Boolean personalizado) {
        this.personalizado = personalizado;
    }

    public String getOpcionesPersonalizacion() {
        return opcionesPersonalizacion;
    }

    public void setOpcionesPersonalizacion(String opcionesPersonalizacion) {
        this.opcionesPersonalizacion = opcionesPersonalizacion;
    }
}
