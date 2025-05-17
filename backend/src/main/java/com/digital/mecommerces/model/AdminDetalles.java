package com.digital.mecommerces.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin_detalles")
public class AdminDetalles {
    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "region")
    private String region;

    @Column(name = "nivel_acceso")
    private String nivelAcceso;

    // Constructor vacío
    public AdminDetalles() {}

    // Constructor con parámetros
    public AdminDetalles(Usuario usuario, String region, String nivelAcceso) {
        this.usuario = usuario;
        this.region = region;
        this.nivelAcceso = nivelAcceso;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getNivelAcceso() {
        return nivelAcceso;
    }

    public void setNivelAcceso(String nivelAcceso) {
        this.nivelAcceso = nivelAcceso;
    }
}
