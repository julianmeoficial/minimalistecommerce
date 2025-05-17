package com.digital.mecommerces.model;

import jakarta.persistence.*;

@Entity
@Table(name = "permiso")
public class Permiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permiso_id", nullable = false)
    private Long permisoId;

    @Column(name = "codigo", nullable = false, unique = true)
    private String codigo;

    @Column(name = "descripcion")
    private String descripcion;

    // Constructor vacío
    public Permiso() {}

    // Constructor con parámetros
    public Permiso(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getPermisoId() {
        return permisoId;
    }

    public void setPermisoId(Long permisoId) {
        this.permisoId = permisoId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}