package com.digital.mecommerces.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class VendedorDetallesDTO {

    private Long usuarioId;

    @Size(max = 20, message = "El RUT no puede tener más de 20 caracteres")
    private String rut;

    @Size(max = 100, message = "La especialidad no puede tener más de 100 caracteres")
    private String especialidad;

    @Size(max = 255, message = "La dirección comercial no puede tener más de 255 caracteres")
    private String direccionComercial;

    @Size(max = 255, message = "El número de registro fiscal no puede tener más de 255 caracteres")
    private String numRegistroFiscal;

    private Boolean verificado = false;

    private LocalDateTime fechaVerificacion;

    @Size(max = 255, message = "El documento comercial no puede tener más de 255 caracteres")
    private String documentoComercial;

    @Size(max = 50, message = "El tipo de documento no puede tener más de 50 caracteres")
    private String tipoDocumento;

    @Size(max = 100, message = "El banco no puede tener más de 100 caracteres")
    private String banco;

    @Size(max = 50, message = "El tipo de cuenta no puede tener más de 50 caracteres")
    private String tipoCuenta;

    @Size(max = 100, message = "El número de cuenta no puede tener más de 100 caracteres")
    private String numeroCuenta;

    // Constructor vacío
    public VendedorDetallesDTO() {}

    // Constructor con parámetros básicos
    public VendedorDetallesDTO(String rut, String especialidad, String direccionComercial, String numRegistroFiscal) {
        this.rut = rut;
        this.especialidad = especialidad;
        this.direccionComercial = direccionComercial;
        this.numRegistroFiscal = numRegistroFiscal;
        this.verificado = false;
    }

    // Getters y Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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
