package com.digital.mecommerces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public class ProductoDTO {
    private Long productoId;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String productoNombre;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock debe ser mayor o igual a cero")
    private Integer stock;

    @NotNull(message = "La categoría del producto es obligatoria")
    private Long categoriaId;

    @NotNull(message = "El vendedor es obligatorio")
    private Long vendedorId;

    private List<ProductoImagenDTO> imagenes;

    // Constructor vacío
    public ProductoDTO() {}

    // Getters y Setters
    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Long vendedorId) {
        this.vendedorId = vendedorId;
    }

    public List<ProductoImagenDTO> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ProductoImagenDTO> imagenes) {
        this.imagenes = imagenes;
    }
}
