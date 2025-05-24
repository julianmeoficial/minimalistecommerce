package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tipo")
public class Tipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipoid", nullable = false) // CORREGIDO: era "tipo_id"
    private Long tipoId;

    @Column(name = "tiponombre", nullable = false, length = 50) // CORREGIDO: era "tipo_nombre"
    private String tipoNombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipocategoria") // CORREGIDO: era "tipo_categoria"
    private TipoCategoria tipoCategoria;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "updatedat")
    private LocalDateTime updatedat;

    @Column(name = "status")
    private Boolean status = true;

    @Column(name = "slug", length = 50, unique = true)
    private String slug;

    // Enum para categorías de tipo
    public enum TipoCategoria {
        USUARIO, PRODUCTO, ADMINISTRADOR, COMPRADOR, VENDEDOR,
        ELECTRONICA, ROPA, HOGAR
    }

    // Constructor vacío
    public Tipo() {
        this.createdat = LocalDateTime.now();
        this.updatedat = LocalDateTime.now();
        this.status = true;
    }

    // Constructor con parámetros
    public Tipo(String tipoNombre, TipoCategoria tipoCategoria) {
        this.tipoNombre = tipoNombre;
        this.tipoCategoria = tipoCategoria;
        this.createdat = LocalDateTime.now();
        this.updatedat = LocalDateTime.now();
        this.status = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedat = LocalDateTime.now();
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

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public LocalDateTime getUpdatedat() {
        return updatedat;
    }

    public void setUpdatedat(LocalDateTime updatedat) {
        this.updatedat = updatedat;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
