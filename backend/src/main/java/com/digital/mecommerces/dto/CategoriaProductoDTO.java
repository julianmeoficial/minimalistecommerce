package com.digital.mecommerces.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoriaProductoDTO {
    private Long categoriaId;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;

    private String descripcion;

    private Long categoriaPadreId;

    // Constructor vacío
    public CategoriaProductoDTO() {}

    // Constructor con parámetros
    public CategoriaProductoDTO(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Constructor con categoría padre
    public CategoriaProductoDTO(String nombre, String descripcion, Long categoriaPadreId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoriaPadreId = categoriaPadreId;
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

    public Long getCategoriaPadreId() {
        return categoriaPadreId;
    }

    public void setCategoriaPadreId(Long categoriaPadreId) {
        this.categoriaPadreId = categoriaPadreId;
    }
}
