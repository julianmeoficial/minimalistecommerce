package com.digital.mecommerces.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDateTime;

public class ListaDeseosItemDTO {

    private Long itemId;

    @NotNull(message = "El ID de la lista es obligatorio")
    private Long listaId;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    private LocalDateTime fechaAgregado;

    @Min(value = 0, message = "La prioridad no puede ser negativa")
    @Max(value = 10, message = "La prioridad no puede ser mayor a 10")
    private Integer prioridad = 0;

    private String notas;

    // Información del producto para mostrar
    private String productoNombre;
    private Double productoPrecio;
    private String productoImagen;
    private Boolean productoActivo;
    private Integer productoStock;

    // Constructor vacío
    public ListaDeseosItemDTO() {}

    // Constructor básico
    public ListaDeseosItemDTO(Long listaId, Long productoId) {
        this.listaId = listaId;
        this.productoId = productoId;
        this.fechaAgregado = LocalDateTime.now();
        this.prioridad = 0;
    }

    // Constructor completo
    public ListaDeseosItemDTO(Long itemId, Long listaId, Long productoId, LocalDateTime fechaAgregado,
                              Integer prioridad, String notas) {
        this.itemId = itemId;
        this.listaId = listaId;
        this.productoId = productoId;
        this.fechaAgregado = fechaAgregado;
        this.prioridad = prioridad;
        this.notas = notas;
    }

    // Getters y Setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getListaId() {
        return listaId;
    }

    public void setListaId(Long listaId) {
        this.listaId = listaId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
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

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public Double getProductoPrecio() {
        return productoPrecio;
    }

    public void setProductoPrecio(Double productoPrecio) {
        this.productoPrecio = productoPrecio;
    }

    public String getProductoImagen() {
        return productoImagen;
    }

    public void setProductoImagen(String productoImagen) {
        this.productoImagen = productoImagen;
    }

    public Boolean getProductoActivo() {
        return productoActivo;
    }

    public void setProductoActivo(Boolean productoActivo) {
        this.productoActivo = productoActivo;
    }

    public Integer getProductoStock() {
        return productoStock;
    }

    public void setProductoStock(Integer productoStock) {
        this.productoStock = productoStock;
    }
}
