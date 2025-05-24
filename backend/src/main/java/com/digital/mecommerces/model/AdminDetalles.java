package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admindetalles")
public class AdminDetalles {

    @Id
    @Column(name = "usuarioid")
    private Long usuarioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuarioid")
    private Usuario usuario;

    @Column(name = "region", length = 100)
    private String region;

    @Column(name = "nivelacceso", length = 255)
    private String nivelAcceso;

    @Column(name = "ultimaaccion", length = 255)
    private String ultimaAccion;

    @Column(name = "ultimologin")
    private LocalDateTime ultimoLogin;

    @Column(name = "ipacceso", length = 50)
    private String ipAcceso;

    // Constructor vacío
    public AdminDetalles() {}

    // Constructor con usuario
    public AdminDetalles(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null && usuario.getUsuarioId() != null) {
            this.usuarioId = usuario.getUsuarioId();
        }
    }

    @PrePersist
    public void prePersist() {
        if (usuario != null && usuario.getUsuarioId() != null) {
            this.usuarioId = usuario.getUsuarioId();
        }
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
        if (usuario != null) {
            this.usuarioId = usuario.getUsuarioId();
        }
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

    public String getUltimaAccion() {
        return ultimaAccion;
    }

    public void setUltimaAccion(String ultimaAccion) {
        this.ultimaAccion = ultimaAccion;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public String getIpAcceso() {
        return ipAcceso;
    }

    public void setIpAcceso(String ipAcceso) {
        this.ipAcceso = ipAcceso;
    }
}
