package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordenid") // CORREGIDO: era "orden_id"
    private Long ordenId;

    @Column(name = "usuarioid", nullable = false) // CORREGIDO: era "usuario_id"
    private Long usuarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioid", insertable = false, updatable = false)
    private Usuario usuario;

    @Column(name = "estado", length = 50)
    private String estado = "PENDIENTE";

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "fechacreacion") // CORREGIDO: era "fecha_creacion"
    private LocalDateTime fechaCreacion;

    @Column(name = "metodopago", length = 50) // CORREGIDO: era "metodo_pago"
    private String metodoPago = "EFECTIVO";

    @Column(name = "referenciapago", length = 100) // CORREGIDO: era "referencia_pago"
    private String referenciaPago;

    @Column(name = "devolucion")
    private Boolean devolucion = false;

    @Column(name = "motivodevolucion", length = 255) // CORREGIDO: era "motivo_devolucion"
    private String motivoDevolucion;

    @Column(name = "fechadevolucion") // CORREGIDO: era "fecha_devolucion"
    private LocalDateTime fechaDevolucion;

    @Column(name = "facturar")
    private Boolean facturar = false;

    @Column(name = "datosfacturacion", columnDefinition = "TEXT") // CORREGIDO: era "datos_facturacion"
    private String datosFacturacion;

    @Column(name = "numerofactura", length = 50) // CORREGIDO: era "numero_factura"
    private String numeroFactura;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrdenDetalle> detalles = new ArrayList<>();

    // Constructor vacío
    public Orden() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Orden(Usuario usuario, String estado, Double total) {
        this.usuario = usuario;
        this.usuarioId = usuario.getUsuarioId();
        this.estado = estado;
        this.total = total;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(Long ordenId) {
        this.ordenId = ordenId;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
    }

    public Boolean getDevolucion() {
        return devolucion;
    }

    public void setDevolucion(Boolean devolucion) {
        this.devolucion = devolucion;
    }

    public String getMotivoDevolucion() {
        return motivoDevolucion;
    }

    public void setMotivoDevolucion(String motivoDevolucion) {
        this.motivoDevolucion = motivoDevolucion;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public Boolean getFacturar() {
        return facturar;
    }

    public void setFacturar(Boolean facturar) {
        this.facturar = facturar;
    }

    public String getDatosFacturacion() {
        return datosFacturacion;
    }

    public void setDatosFacturacion(String datosFacturacion) {
        this.datosFacturacion = datosFacturacion;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public List<OrdenDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<OrdenDetalle> detalles) {
        this.detalles = detalles;
    }

    public void addDetalle(OrdenDetalle detalle) {
        detalles.add(detalle);
        detalle.setOrden(this);
    }

    @Override
    public String toString() {
        return "Orden{" +
                "ordenId=" + ordenId +
                ", usuarioId=" + usuarioId +
                ", estado='" + estado + '\'' +
                ", total=" + total +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
