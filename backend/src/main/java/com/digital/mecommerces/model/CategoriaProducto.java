package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categoria_producto")
public class CategoriaProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @Column(name = "nombre", nullable = false, length = 50, unique = true)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "categoria_padre_id")
    private Long categoriapadreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_padre_id", insertable = false, updatable = false)
    private CategoriaProducto categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategoriaProducto> subcategorias = new ArrayList<>();

    @Column(name = "slug", length = 100, unique = true)
    private String slug;

    @Column(name = "imagen", length = 255)
    private String imagen;

    @Column(name = "activo")
    private Boolean activo = true;

    // Constructor vacío
    public CategoriaProducto() {}

    // Constructor con parámetros
    public CategoriaProducto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = true;
    }

    // Constructor con categoría padre
    public CategoriaProducto(String nombre, String descripcion, CategoriaProducto categoriaPadre) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoriaPadre = categoriaPadre;
        if (categoriaPadre != null) {
            this.categoriapadreId = categoriaPadre.getCategoriaId();
        }
        this.activo = true;
    }

    // Getters y Setters
    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getCategoriapadreId() {
        return categoriapadreId;
    }

    public void setCategoriapadreId(Long categoriapadreId) {
        this.categoriapadreId = categoriapadreId;
    }

    public CategoriaProducto getCategoriaPadre() {
        return categoriaPadre;
    }

    public void setCategoriaPadre(CategoriaProducto categoriaPadre) {
        this.categoriaPadre = categoriaPadre;
        if (categoriaPadre != null) {
            this.categoriapadreId = categoriaPadre.getCategoriaId();
        }
    }

    public List<CategoriaProducto> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<CategoriaProducto> subcategorias) {
        this.subcategorias = subcategorias;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    // Métodos de utilidad
    public boolean isActivo() {
        return activo != null && activo;
    }

    public boolean tienePadre() {
        return categoriaPadre != null;
    }

    public boolean tieneSubcategorias() {
        return subcategorias != null && !subcategorias.isEmpty();
    }

    @Override
    public String toString() {
        return "CategoriaProducto{" +
                "categoriaId=" + categoriaId +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                ", slug='" + slug + '\'' +
                '}';
    }
}
