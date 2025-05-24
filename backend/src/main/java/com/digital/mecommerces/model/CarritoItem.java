package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "carritoitem")
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itemid", nullable = false)
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "carritoid", nullable = false)
    private CarritoCompra carritoCompra;

    @ManyToOne
    @JoinColumn(name = "productoid", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "preciounitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "fechaagregado")
    private LocalDateTime fechaAgregado;

    @Column(name = "guardadodespues")
    private Boolean guardadoDespues = false;

    // Constructor vacío
    public CarritoItem() {
        this.fechaAgregado = LocalDateTime.now();
    }

    // Constructor con parámetros
    public CarritoItem(Producto producto, Integer cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
        this.fechaAgregado = LocalDateTime.now();
        this.guardadoDespues = false;
    }

    // Getters y Setters
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
            this.precioUnitario = producto.getPrecio();
        }
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }

    public Boolean getGuardadoDespues() {
        return guardadoDespues;
    }

    public void setGuardadoDespues(Boolean guardadoDespues) {
        this.guardadoDespues = guardadoDespues;
    }

    // Método para calcular el subtotal
    public Double calcularSubtotal() {
        return cantidad * precioUnitario;
    }
}
