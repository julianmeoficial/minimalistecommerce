package com.digital.mecommerces.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@Table(name = "tipo")
public class Tipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipo_id", nullable = false, columnDefinition = "BIGINT")
    private Long tipoId;

    @Column(name = "tipo_nombre", nullable = false, length = 50)
    private String tipoNombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_categoria", nullable = false)
    private TipoCategoria tipoCategoria;

    // Constructor vacío requerido para JPA
    public Tipo() {
    }

    // Constructor con parámetros
    public Tipo(String tipoNombre, TipoCategoria tipoCategoria) {
        this.tipoNombre = tipoNombre;
        this.tipoCategoria = tipoCategoria;
    }

    // Getters y Setters
    public Long getTipoId() {
        return tipoId;
    }

    public void setTipoId(Long tipoId) {
        this.tipoId = tipoId;
    }

    public String getTipoNombre() {
        return tipoNombre;
    }

    public void setTipoNombre(String tipoNombre) {
        this.tipoNombre = tipoNombre;
    }

    public TipoCategoria getTipoCategoria() {
        return tipoCategoria;
    }

    public void setTipoCategoria(TipoCategoria tipoCategoria) {
        this.tipoCategoria = tipoCategoria;
    }
}
