package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "productosugerido")
public class ProductoSugerido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sugerenciaid", nullable = false)
    private Long sugerenciaId;

    @ManyToOne
    @JoinColumn(name = "productobaseid", nullable = false)
    private Producto productoBase;

    @ManyToOne
    @JoinColumn(name = "productosugeridoid", nullable = false)
    private Producto productoSugerido;

    @Column(name = "tiporelacion")
    private String tipoRelacion = "COMPLEMENTO";

    @Column(name = "prioridad")
    private Integer prioridad = 0;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "descripcionrelacion")
    private String descripcionRelacion;

    // Constructor vacío
    public ProductoSugerido() {
        this.createdat = LocalDateTime.now();
        this.tipoRelacion = "COMPLEMENTO";
        this.prioridad = 0;
        this.activo = true;
    }

    // Constructor con parámetros básicos
    public ProductoSugerido(Producto productoBase, Producto productoSugerido) {
        this.productoBase = productoBase;
        this.productoSugerido = productoSugerido;
        this.createdat = LocalDateTime.now();
        this.tipoRelacion = "COMPLEMENTO";
        this.prioridad = 0;
        this.activo = true;
    }

    // Constructor completo
    public ProductoSugerido(Producto productoBase, Producto productoSugerido, String tipoRelacion, Integer prioridad) {
        this.productoBase = productoBase;
        this.productoSugerido = productoSugerido;
        this.tipoRelacion = tipoRelacion;
        this.prioridad = prioridad;
        this.createdat = LocalDateTime.now();
        this.activo = true;
    }

    // Getters y Setters
    public Long getSugerenciaId() {
        return sugerenciaId;
    }

    public void setSugerenciaId(Long sugerenciaId) {
        this.sugerenciaId = sugerenciaId;
    }

    public Producto getProductoBase() {
        return productoBase;
    }

    public void setProductoBase(Producto productoBase) {
        this.productoBase = productoBase;
    }

    public Producto getProductoSugerido() {
        return productoSugerido;
    }

    public void setProductoSugerido(Producto productoSugerido) {
        this.productoSugerido = productoSugerido;
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

    // Métodos de utilidad
    public boolean isActivo() {
        return activo != null && activo;
    }

    // Enumeración para tipos de relación
    public enum TipoRelacion {
        COMPLEMENTO("COMPLEMENTO"),
        SIMILAR("SIMILAR"),
        ACCESORIO("ACCESORIO"),
        REEMPLAZO("REEMPLAZO"),
        UPGRADE("UPGRADE");

        private final String valor;

        TipoRelacion(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return valor;
        }
    }
}
