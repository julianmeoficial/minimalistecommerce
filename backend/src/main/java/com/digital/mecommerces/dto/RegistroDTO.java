package com.digital.mecommerces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistroDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email(message = "Email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Long rolId;

    // Campos específicos para CompradorDetalles
    private LocalDate fechaNacimiento;
    private String preferencias;
    private String direccionEnvio;
    private String telefono;
    private String direccionAlternativa;
    private String telefonoAlternativo;
    private Boolean notificacionEmail = true;
    private Boolean notificacionSms = false;

    // Campos específicos para VendedorDetalles
    private String rut;
    private String especialidad;
    private String direccionComercial;
    private String numRegistroFiscal;
    private String documentoComercial;
    private String tipoDocumento;
    private String banco;
    private String tipoCuenta;
    private String numeroCuenta;

    // Campos específicos para AdminDetalles
    private String region;
    private String nivelAcceso;
    private String ipAcceso;

    // Constructor vacío
    public RegistroDTO() {}

    // Getters y Setters

    // Campos básicos getters/setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    // CompradorDetalles getters/setters
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

    // VendedorDetalles getters/setters
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

    // AdminDetalles getters/setters
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

    public String getIpAcceso() {
        return ipAcceso;
    }

    public void setIpAcceso(String ipAcceso) {
        this.ipAcceso = ipAcceso;
    }

    @Override
    public String toString() {
        return "RegistroDTO{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rolId=" + rolId +
                ", fechaNacimiento=" + fechaNacimiento +
                ", direccionEnvio='" + direccionEnvio + '\'' +
                ", telefono='" + telefono + '\'' +
                ", rut='" + rut + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", region='" + region + '\'' +
                ", nivelAcceso='" + nivelAcceso + '\'' +
                '}';
    }
}