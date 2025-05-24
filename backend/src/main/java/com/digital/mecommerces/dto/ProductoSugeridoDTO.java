package com.digital.mecommerces.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDateTime;

public class ProductoSugeridoDTO {

    private Long sugerenciaId;

    @NotNull(message = "El ID del producto base es obligatorio")
    private Long productoBaseId;

    @NotNull(message = "El ID del producto sugerido es obligatorio")
    private Long productoSugeridoId;

    private String tipoRelacion = "COMPLEMENTO";

    @Min(value = 0, message = "La prioridad no puede ser negativa")
    @Max(value = 100, message = "La prioridad no puede ser mayor a 100")
    private Integer prioridad = 0;

    private LocalDateTime createdat;

    private Boolean activo = true;

    private String descripcionRelacion;

    // Información del producto base para mostrar
    private String productoBaseNombre;
    private Double productoBasePrecio;
    private String productoBaseImagen;

    // Información del producto sugerido para mostrar
    private String productoSugeridoNombre;
    private Double productoSugeridoPrecio;
    private String productoSugeridoImagen;
    private Boolean productoSugeridoActivo;
    private Integer productoSugeridoStock;

    // Constructor vacío
    public ProductoSugeridoDTO() {}

    // Constructor básico
    public ProductoSugeridoDTO(Long productoBaseId, Long productoSugeridoId) {
        this.productoBaseId = productoBaseId;
        this.productoSugeridoId = productoSugeridoId;
        this.tipoRelacion = "COMPLEMENTO";
        this.prioridad = 0;
        this.activo = true;
        this.createdat = LocalDateTime.now();
    }

    // Constructor completo
    public ProductoSugeridoDTO(Long sugerenciaId, Long productoBaseId, Long productoSugeridoId,
                               String tipoRelacion, Integer prioridad, Boolean activo, String descripcionRelacion) {
        this.sugerenciaId = sugerenciaId;
        this.productoBaseId = productoBaseId;
        this.productoSugeridoId = productoSugeridoId;
        this.tipoRelacion = tipoRelacion;
        this.prioridad = prioridad;
        this.activo = activo;
        this.descripcionRelacion = descripcionRelacion;
        this.createdat = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getSugerenciaId() {
        return sugerenciaId;
    }

    public void setSugerenciaId(Long sugerenciaId) {
        this.sugerenciaId = sugerenciaId;
    }

    public Long getProductoBaseId() {
        return productoBaseId;
    }

    public void setProductoBaseId(Long productoBaseId) {
        this.productoBaseId = productoBaseId;
    }

    public Long getProductoSugeridoId() {
        return productoSugeridoId;
    }

    public void setProductoSugeridoId(Long productoSugeridoId) {
        this.productoSugeridoId = productoSugeridoId;
    }

    public String getTipoRelacion() {
        return tipoRelacion;
    }

    public void setTipoRelacion(String tipoRelacion) {
        this.tipoRelacion = tipoRelacion;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getDescripcionRelacion() {
        return descripcionRelacion;
    }

    public void setDescripcionRelacion(String descripcionRelacion) {
        this.descripcionRelacion = descripcionRelacion;
    }

    public String getProductoBaseNombre() {
        return productoBaseNombre;
    }

    public void setProductoBaseNombre(String productoBaseNombre) {
        this.productoBaseNombre = productoBaseNombre;
    }

    public Double getProductoBasePrecio() {
        return productoBasePrecio;
    }

    public void setProductoBasePrecio(Double productoBasePrecio) {
        this.productoBasePrecio = productoBasePrecio;
    }

    public String getProductoBaseImagen() {
        return productoBaseImagen;
    }

    public void setProductoBaseImagen(String productoBaseImagen) {
        this.productoBaseImagen = productoBaseImagen;
    }

    public String getProductoSugeridoNombre() {
        return productoSugeridoNombre;
    }

    public void setProductoSugeridoNombre(String productoSugeridoNombre) {
        this.productoSugeridoNombre = productoSugeridoNombre;
    }

    public Double getProductoSugeridoPrecio() {
        return productoSugeridoPrecio;
    }

    public void setProductoSugeridoPrecio(Double productoSugeridoPrecio) {
        this.productoSugeridoPrecio = productoSugeridoPrecio;
    }

    public String getProductoSugeridoImagen() {
        return productoSugeridoImagen;
    }

    public void setProductoSugeridoImagen(String productoSugeridoImagen) {
        this.productoSugeridoImagen = productoSugeridoImagen;
    }

    public Boolean getProductoSugeridoActivo() {
        return productoSugeridoActivo;
    }

    public void setProductoSugeridoActivo(Boolean productoSugeridoActivo) {
        this.productoSugeridoActivo = productoSugeridoActivo;
    }

    public Integer getProductoSugeridoStock() {
        return productoSugeridoStock;
    }

    public void setProductoSugeridoStock(Integer productoSugeridoStock) {
        this.productoSugeridoStock = productoSugeridoStock;
    }

    @Override
    public String toString() {
        return "ProductoSugeridoDTO{" +
                "sugerenciaId=" + sugerenciaId +
                ", productoBaseId=" + productoBaseId +
                ", productoSugeridoId=" + productoSugeridoId +
                ", tipoRelacion='" + tipoRelacion + '\'' +
                ", prioridad=" + prioridad +
                ", activo=" + activo +
                ", descripcionRelacion='" + descripcionRelacion + '\'' +
                '}';
    }
}
