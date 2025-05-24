package com.digital.mecommerces.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CompradorDetallesDTO {

    private Long usuarioId;

    private LocalDate fechaNacimiento;

    @Size(max = 65535, message = "Las preferencias no pueden exceder los 65535 caracteres")
    private String preferencias;

    @Size(max = 255, message = "La dirección de envío no puede tener más de 255 caracteres")
    private String direccionEnvio;

    @Size(max = 255, message = "El teléfono no puede tener más de 255 caracteres")
    private String telefono;

    @Size(max = 255, message = "La dirección alternativa no puede tener más de 255 caracteres")
    private String direccionAlternativa;

    @Size(max = 50, message = "El teléfono alternativo no puede tener más de 50 caracteres")
    private String telefonoAlternativo;

    private Boolean notificacionEmail = true;

    private Boolean notificacionSms = false;

    @DecimalMin(value = "0.00", message = "La calificación no puede ser negativa")
    @DecimalMax(value = "10.00", message = "La calificación no puede ser mayor a 10")
    private BigDecimal calificacion = new BigDecimal("5.00");

    private Integer totalCompras = 0;

    // Constructor vacío
    public CompradorDetallesDTO() {}

    // Constructor con parámetros básicos
    public CompradorDetallesDTO(LocalDate fechaNacimiento, String direccionEnvio, String telefono) {
        this.fechaNacimiento = fechaNacimiento;
        this.direccionEnvio = direccionEnvio;
        this.telefono = telefono;
        this.notificacionEmail = true;
        this.notificacionSms = false;
        this.calificacion = new BigDecimal("5.00");
        this.totalCompras = 0;
    }

    // Getters y Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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
