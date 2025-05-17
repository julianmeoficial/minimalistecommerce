package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "comprador_detalles")
public class CompradorDetalles {
    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    @Column(name = "telefono")
    private String telefono;

    // Constructor vacío
    public CompradorDetalles() {}

    // Constructor con parámetros
    public CompradorDetalles(Usuario usuario, LocalDate fechaNacimiento,
                             String direccionEnvio, String telefono) {
        this.usuario = usuario;
        this.fechaNacimiento = fechaNacimiento;
        this.direccionEnvio = direccionEnvio;
        this.telefono = telefono;
    }

    // Getters y Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
