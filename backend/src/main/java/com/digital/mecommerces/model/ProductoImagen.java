package com.digital.mecommerces.model;

import jakarta.persistence.*;

@Entity
@Table(name = "producto_imagen")
public class ProductoImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imagen_id", nullable = false)
    private Long imagenId;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "es_principal")
    private Boolean esPrincipal;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Constructor vacío
    public ProductoImagen() {
    }

    // Constructor con parámetros
    public ProductoImagen(String url, String descripcion, Boolean esPrincipal, Producto producto) {
        this.url = url;
        this.descripcion = descripcion;
        this.esPrincipal = esPrincipal;
        this.producto = producto;
    }

    // Getters y Setters
    public Long getImagenId() {
        return imagenId;
    }

    public void setImagenId(Long imagenId) {
        this.imagenId = imagenId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}