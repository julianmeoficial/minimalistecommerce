package com.digital.mecommerces.dto;

public class VendedorDetallesDTO {
    private Long usuarioId;
    private String numRegistroFiscal;
    private String especialidad;
    private String direccionComercial;

    // Constructor vacío
    public VendedorDetallesDTO() {}

    // Constructor con parámetros
    public VendedorDetallesDTO(String numRegistroFiscal, String especialidad, String direccionComercial) {
        this.numRegistroFiscal = numRegistroFiscal;
        this.especialidad = especialidad;
        this.direccionComercial = direccionComercial;
    }

    // Getters y Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNumRegistroFiscal() {
        return numRegistroFiscal;
    }

    public void setNumRegistroFiscal(String numRegistroFiscal) {
        this.numRegistroFiscal = numRegistroFiscal;
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
}

