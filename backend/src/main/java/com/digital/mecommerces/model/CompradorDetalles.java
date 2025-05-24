package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "compradordetalles")
public class CompradorDetalles {

    @Id
    @Column(name = "usuarioid")
    private Long usuarioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuarioid")
    private Usuario usuario;

    @Column(name = "fechanacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "preferencias", columnDefinition = "TEXT")
    private String preferencias;

    @Column(name = "direccionenvio", length = 255)
    private String direccionEnvio;

    @Column(name = "telefono", length = 255)
    private String telefono;

    @Column(name = "direccionalternativa", length = 255)
    private String direccionAlternativa;

    @Column(name = "telefonoalternativo", length = 50)
    private String telefonoAlternativo;

    @Column(name = "notificacionemail")
    private Boolean notificacionEmail = true;

    @Column(name = "notificacionsms")
    private Boolean notificacionSms = false;

    @Column(name = "calificacion", precision = 3, scale = 2)
    private BigDecimal calificacion = new BigDecimal("5.00");

    @Column(name = "totalcompras")
    private Integer totalCompras = 0;

    // Constructor vacío
    public CompradorDetalles() {}

    // Constructor con usuario
    public CompradorDetalles(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null && usuario.getUsuarioId() != null) {
            this.usuarioId = usuario.getUsuarioId();
        }
        this.notificacionEmail = true;
        this.notificacionSms = false;
        this.calificacion = new BigDecimal("5.00");
        this.totalCompras = 0;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(String preferencias) {
        this.preferencias = preferencias;
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

    public String getDireccionAlternativa() {
        return direccionAlternativa;
    }

    public void setDireccionAlternativa(String direccionAlternativa) {
        this.direccionAlternativa = direccionAlternativa;
    }

    public String getTelefonoAlternativo() {
        return telefonoAlternativo;
    }

    public void setTelefonoAlternativo(String telefonoAlternativo) {
        this.telefonoAlternativo = telefonoAlternativo;
    }

    public Boolean getNotificacionEmail() {
        return notificacionEmail;
    }

    public void setNotificacionEmail(Boolean notificacionEmail) {
        this.notificacionEmail = notificacionEmail;
    }

    public Boolean getNotificacionSms() {
        return notificacionSms;
    }

    public void setNotificacionSms(Boolean notificacionSms) {
        this.notificacionSms = notificacionSms;
    }

    public BigDecimal getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(BigDecimal calificacion) {
        this.calificacion = calificacion;
    }

    public Integer getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(Integer totalCompras) {
        this.totalCompras = totalCompras;
    }
}
