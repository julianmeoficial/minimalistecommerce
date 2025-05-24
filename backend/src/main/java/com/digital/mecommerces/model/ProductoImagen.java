package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "productoimagen")
public class ProductoImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imagenid", nullable = false)
    private Long imagenId;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "esprincipal")
    private Boolean esPrincipal = false;

    @ManyToOne
    @JoinColumn(name = "productoid", nullable = false)
    private Producto producto;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "tamanio")
    private Integer tamanio;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    // Constructor vacío
    public ProductoImagen() {
        this.createdat = LocalDateTime.now();
    }

    // Constructor con parámetros
    public ProductoImagen(String url, String descripcion, Boolean esPrincipal, Producto producto) {
        this.url = url;
        this.descripcion = descripcion;
        this.esPrincipal = esPrincipal;
        this.producto = producto;
        this.createdat = LocalDateTime.now();
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getTamanio() {
        return tamanio;
    }

    public void setTamanio(Integer tamanio) {
        this.tamanio = tamanio;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }
}
