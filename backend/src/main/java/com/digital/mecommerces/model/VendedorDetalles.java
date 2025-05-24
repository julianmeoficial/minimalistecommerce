package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendedordetalles")
public class VendedorDetalles {

    @Id
    @Column(name = "usuarioid")
    private Long usuarioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuarioid")
    private Usuario usuario;

    @Column(name = "rut", length = 20)
    private String rut;

    @Column(name = "especialidad", length = 100)
    private String especialidad;

    @Column(name = "direccioncomercial", length = 255)
    private String direccionComercial;

    @Column(name = "numregistrofiscal", length = 255)
    private String numRegistroFiscal;

    @Column(name = "verificado")
    private Boolean verificado = false;

    @Column(name = "fechaverificacion")
    private LocalDateTime fechaVerificacion;

    @Column(name = "documentocomercial", length = 255)
    private String documentoComercial;

    @Column(name = "tipodocumento", length = 50)
    private String tipoDocumento;

    @Column(name = "banco", length = 100)
    private String banco;

    @Column(name = "tipocuenta", length = 50)
    private String tipoCuenta;

    @Column(name = "numerocuenta", length = 100)
    private String numeroCuenta;

    // Constructor vacío
    public VendedorDetalles() {}

    // Constructor con usuario
    public VendedorDetalles(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null && usuario.getUsuarioId() != null) {
            this.usuarioId = usuario.getUsuarioId();
        }
        this.verificado = false;
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

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getDireccionComercial() {
        return direccionComercial;
    }

    public void setDireccionComercial(String direccionComercial) {
        this.direccionComercial = direccionComercial;
    }

    public String getNumRegistroFiscal() {
        return numRegistroFiscal;
    }

    public void setNumRegistroFiscal(String numRegistroFiscal) {
        this.numRegistroFiscal = numRegistroFiscal;
    }

    public Boolean getVerificado() {
        return verificado;
    }

    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
    }

    public LocalDateTime getFechaVerificacion() {
        return fechaVerificacion;
    }

    public void setFechaVerificacion(LocalDateTime fechaVerificacion) {
        this.fechaVerificacion = fechaVerificacion;
    }

    public String getDocumentoComercial() {
        return documentoComercial;
    }

    public void setDocumentoComercial(String documentoComercial) {
        this.documentoComercial = documentoComercial;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }
}
