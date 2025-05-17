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

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "categoria_padre_id")
    private CategoriaProducto categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre")
    private List<CategoriaProducto> subcategorias = new ArrayList<>();

    // Constructor vacío
    public CategoriaProducto() {}

    // Constructor con parámetros
    public CategoriaProducto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Constructor con categoría padre
    public CategoriaProducto(String nombre, String descripcion, CategoriaProducto categoriaPadre) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoriaPadre = categoriaPadre;
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

    public CategoriaProducto getCategoriaPadre() {
        return categoriaPadre;
    }

    public void setCategoriaPadre(CategoriaProducto categoriaPadre) {
        this.categoriaPadre = categoriaPadre;
    }

    public List<CategoriaProducto> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<CategoriaProducto> subcategorias) {
        this.subcategorias = subcategorias;
    }
}
