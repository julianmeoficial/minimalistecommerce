package com.digital.mecommerces.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vendedor_detalles")
public class VendedorDetalles {
    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "num_registro_fiscal")
    private String numRegistroFiscal;

    @Column(name = "especialidad")
    private String especialidad;

    @Column(name = "direccion_comercial")
    private String direccionComercial;

    // Constructor vacío
    public VendedorDetalles() {}

    // Constructor con parámetros
    public VendedorDetalles(Usuario usuario, String numRegistroFiscal,
                            String especialidad, String direccionComercial) {
        this.usuario = usuario;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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