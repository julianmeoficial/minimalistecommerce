package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "passwordresettokens")
public class PasswordResetTokens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuarioid")
    private Usuario usuario;

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "fechaexpiracion")
    private LocalDateTime fechaExpiracion;

    @Column(name = "usado")
    private Boolean usado = false;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    // Constructor vacío
    public PasswordResetTokens() {
        this.createdat = LocalDateTime.now();
        this.usado = false;
    }

    // Constructor con parámetros
    public PasswordResetTokens(Usuario usuario, String token, LocalDateTime fechaExpiracion) {
        this.usuario = usuario;
        this.token = token;
        this.fechaExpiracion = fechaExpiracion;
        this.createdat = LocalDateTime.now();
        this.usado = false;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Boolean getUsado() {
        return usado;
    }

    public void setUsado(Boolean usado) {
        this.usado = usado;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    // Método para verificar si el token ha expirado
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }
}
