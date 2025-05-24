package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listadeseos")
public class ListaDeseos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listaid", nullable = false)
    private Long listaId;

    @ManyToOne
    @JoinColumn(name = "usuarioid", nullable = false)
    private Usuario usuario;

    @Column(name = "nombre")
    private String nombre = "Mi Lista de Deseos";

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ListaDeseosItem> items = new ArrayList<>();

    // Constructor vacío
    public ListaDeseos() {
        this.createdat = LocalDateTime.now();
        this.nombre = "Mi Lista de Deseos";
    }

    // Constructor con usuario
    public ListaDeseos(Usuario usuario) {
        this.usuario = usuario;
        this.createdat = LocalDateTime.now();
        this.nombre = "Mi Lista de Deseos";
    }

    // Constructor con usuario y nombre
    public ListaDeseos(Usuario usuario, String nombre) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.createdat = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getListaId() {
        return listaId;
    }

    public void setListaId(Long listaId) {
        this.listaId = listaId;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public List<ListaDeseosItem> getItems() {
        return items;
    }

    public void setItems(List<ListaDeseosItem> items) {
        this.items = items;
    }

    // Métodos de utilidad
    public void addItem(ListaDeseosItem item) {
        items.add(item);
        item.setLista(this);
    }

    public void removeItem(ListaDeseosItem item) {
        items.remove(item);
        item.setLista(null);
    }

    public int getTotalItems() {
        return items.size();
    }

    public boolean containsProduct(Producto producto) {
        return items.stream()
                .anyMatch(item -> item.getProducto().equals(producto));
    }
}
