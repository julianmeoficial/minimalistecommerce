package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usersessions")
public class UserSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "usuarioid") // CORREGIDO: era "usuario_id"
    private Long usuarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioid", insertable = false, updatable = false)
    private Usuario usuario;

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "ipaddress", length = 50) // CORREGIDO: era "ip_address"
    private String ipAddress;

    @Column(name = "useragent", length = 500) // CORREGIDO: era "user_agent"
    private String userAgent;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "expiresat") // CORREGIDO: era "expires_at"
    private LocalDateTime expiresAt;

    @Column(name = "active")
    private Boolean active = true;

    // Constructor vacío
    public UserSessions() {
        this.createdat = LocalDateTime.now();
        this.active = true;
    }

    // Constructor con parámetros
    public UserSessions(Usuario usuario, String token, String ipAddress, String userAgent, LocalDateTime expiresAt) {
        this.usuario = usuario;
        this.usuarioId = usuario.getUsuarioId();
        this.token = token;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.expiresAt = expiresAt;
        this.createdat = LocalDateTime.now();
        this.active = true;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
