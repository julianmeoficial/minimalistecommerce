package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "listadeseositem")
public class ListaDeseosItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itemid", nullable = false)
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "listaid", nullable = false)
    private ListaDeseos lista;

    @ManyToOne
    @JoinColumn(name = "productoid", nullable = false)
    private Producto producto;

    @Column(name = "fechaagregado")
    private LocalDateTime fechaAgregado;

    @Column(name = "prioridad")
    private Integer prioridad = 0;

    @Column(name = "notas")
    private String notas;

    // Constructor vacío
    public ListaDeseosItem() {
        this.fechaAgregado = LocalDateTime.now();
        this.prioridad = 0;
    }

    // Constructor con parámetros
    public ListaDeseosItem(ListaDeseos lista, Producto producto) {
        this.lista = lista;
        this.producto = producto;
        this.fechaAgregado = LocalDateTime.now();
        this.prioridad = 0;
    }

    // Constructor completo
    public ListaDeseosItem(ListaDeseos lista, Producto producto, Integer prioridad, String notas) {
        this.lista = lista;
        this.producto = producto;
        this.prioridad = prioridad;
        this.notas = notas;
        this.fechaAgregado = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public ListaDeseos getLista() {
        return lista;
    }

    public void setLista(ListaDeseos lista) {
        this.lista = lista;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
