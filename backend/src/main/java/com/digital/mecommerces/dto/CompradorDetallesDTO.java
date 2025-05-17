package com.digital.mecommerces.dto;

import java.time.LocalDate;

public class CompradorDetallesDTO {
    private Long usuarioId;
    private LocalDate fechaNacimiento;
    private String direccionEnvio;
    private String telefono;

    // Constructor vacío
    public CompradorDetallesDTO() {}

    // Constructor con parámetros
    public CompradorDetallesDTO(LocalDate fechaNacimiento, String direccionEnvio, String telefono) {
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
