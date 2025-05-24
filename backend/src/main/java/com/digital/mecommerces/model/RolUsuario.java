package com.digital.mecommerces.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rolusuario")
public class RolUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rolid", nullable = false)
    private Long rolId;

    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    // Constructor vacío requerido para JPA
    public RolUsuario() {}

    // Constructor con parámetros
    public RolUsuario(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
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

    @Override
    public String toString() {
        return "RolUsuario{" +
                "rolId=" + rolId +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
