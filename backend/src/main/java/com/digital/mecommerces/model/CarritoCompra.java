package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritocompra")
public class CarritoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carritoid", nullable = false)
    private Long carritoId;

    @ManyToOne
    @JoinColumn(name = "usuarioid", nullable = false)
    private Usuario usuario;

    @Column(name = "fechacreacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @OneToMany(mappedBy = "carritoCompra", cascade = CascadeType.ALL)
    private List<CarritoItem> items = new ArrayList<>();

    // Constructor vacío
    public CarritoCompra() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    // Constructor con parámetros
    public CarritoCompra(Usuario usuario) {
        this.usuario = usuario;
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    // Getters y Setters
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
    }

    public List<CarritoItem> getItems() {
        return items;
    }

    public void setItems(List<CarritoItem> items) {
        this.items = items;
    }

    // Método para agregar un item al carrito
    public void addItem(CarritoItem item) {
        items.add(item);
        item.setCarritoCompra(this);
    }

    // Método para eliminar un item del carrito
    public void removeItem(CarritoItem item) {
        items.remove(item);
        item.setCarritoCompra(null);
    }

    // Método para calcular el total del carrito
    public Double calcularTotal() {
        return items.stream()
                .mapToDouble(item -> item.getCantidad() * item.getPrecioUnitario())
                .sum();
    }
}
