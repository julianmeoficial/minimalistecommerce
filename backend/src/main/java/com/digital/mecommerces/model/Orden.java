package com.digital.mecommerces.model;

import com.digital.mecommerces.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden")
@Slf4j
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordenid", nullable = false)
    private Long ordenId;

    @Column(name = "fechacreacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "estado", nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "total", nullable = false)
    private Double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioid", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrdenDetalle> detalles = new ArrayList<>();

    @Column(name = "metodopago", length = 50)
    private String metodoPago = "EFECTIVO";

    @Column(name = "referenciapago", length = 100)
    private String referenciaPago;

    @Column(name = "devolucion")
    private Boolean devolucion = false;

    @Column(name = "motivodevolucion", length = 255)
    private String motivoDevolucion;

    @Column(name = "fechadevolucion")
    private LocalDateTime fechaDevolucion;

    @Column(name = "facturar")
    private Boolean facturar = false;

    @Column(name = "datosfacturacion", columnDefinition = "TEXT")
    private String datosFacturacion;

    @Column(name = "numerofactura", length = 50)
    private String numeroFactura;

    @Column(name = "fechaentrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "direccionentrega", length = 500)
    private String direccionEntrega;

    @Column(name = "notas", length = 1000)
    private String notas;

    // Constructor vacío requerido por JPA
    public Orden() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.metodoPago = "EFECTIVO";
        this.devolucion = false;
        this.facturar = false;
    }

    // Constructor optimizado con usuario
    public Orden(Usuario usuario, String estado, Double total) {
        this();
        this.usuario = usuario;
        this.estado = estado;
        this.total = total;

        if (usuario != null && usuario.getRol() != null) {
            try {
                TipoUsuario tipo = TipoUsuario.fromCodigo(usuario.getRol().getNombre());
                log.debug("✅ Orden creada para usuario tipo: {}", tipo.getDescripcion());

                // Solo compradores y administradores pueden crear órdenes
                if (tipo != TipoUsuario.COMPRADOR && tipo != TipoUsuario.ADMINISTRADOR) {
                    log.warn("⚠️ Usuario tipo {} creando orden", tipo.getCodigo());
                }
            } catch (IllegalArgumentException e) {
                log.warn("⚠️ Rol no reconocido para orden: {}", usuario.getRol().getNombre());
            }
        }
    }

    // Constructor completo optimizado
    public Orden(Usuario usuario, String estado, Double total, String metodoPago) {
        this(usuario, estado, total);
        this.metodoPago = metodoPago;
    }

    // Métodos de gestión de la orden optimizados
    @PreUpdate
    public void preUpdate() {
        log.debug("🔄 Orden actualizada - ID: {}, Estado: {}", this.ordenId, this.estado);
    }

    public void addDetalle(OrdenDetalle detalle) {
        if (detalle != null) {
            detalles.add(detalle);
            detalle.setOrden(this);
            log.debug("➕ Detalle agregado a orden {}: {}", this.ordenId,
                    detalle.getProducto() != null ? detalle.getProducto().getProductoNombre() : "Producto null");
        }
    }

    public void removeDetalle(OrdenDetalle detalle) {
        if (detalle != null && detalles.remove(detalle)) {
            detalle.setOrden(null);
            log.debug("➖ Detalle removido de orden {}", this.ordenId);
        }
    }

    public Double recalcularTotal() {
        this.total = detalles.stream()
                .mapToDouble(OrdenDetalle::getSubtotal)
                .sum();

        log.debug("💰 Total recalculado para orden {}: ${}", this.ordenId, this.total);
        return this.total;
    }

    // Métodos de estado optimizados
    public void marcarComoPendiente() {
        this.estado = "PENDIENTE";
        log.info("⏳ Orden {} marcada como PENDIENTE", this.ordenId);
    }

    public void marcarComoPagada() {
        this.estado = "PAGADA";
        log.info("💳 Orden {} marcada como PAGADA", this.ordenId);
    }

    public void marcarComoEnviada() {
        this.estado = "ENVIADA";
        log.info("📦 Orden {} marcada como ENVIADA", this.ordenId);
    }

    public void marcarComoEntregada() {
        this.estado = "ENTREGADA";
        this.fechaEntrega = LocalDateTime.now();
        log.info("✅ Orden {} marcada como ENTREGADA", this.ordenId);
    }

    public void marcarComoCancelada() {
        this.estado = "CANCELADA";
        log.info("❌ Orden {} marcada como CANCELADA", this.ordenId);
    }

    public void procesarDevolucion(String motivo) {
        this.devolucion = true;
        this.motivoDevolucion = motivo;
        this.fechaDevolucion = LocalDateTime.now();
        this.estado = "DEVUELTA";
        log.info("↩️ Orden {} procesada como devolución: {}", this.ordenId, motivo);
    }

    public void configurarFacturacion(String datosFacturacion) {
        this.facturar = true;
        this.datosFacturacion = datosFacturacion;
        log.info("🧾 Facturación configurada para orden {}", this.ordenId);
    }

    public void asignarNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
        log.info("📄 Número de factura asignado a orden {}: {}", this.ordenId, numeroFactura);
    }

    // Métodos de validación optimizados
    public boolean puedeSerCancelada() {
        return "PENDIENTE".equals(this.estado) || "PAGADA".equals(this.estado);
    }

    public boolean puedeSerEnviada() {
        return "PAGADA".equals(this.estado);
    }

    public boolean puedeSerEntregada() {
        return "ENVIADA".equals(this.estado);
    }

    public boolean puedeSerDevuelta() {
        return "ENTREGADA".equals(this.estado) && !this.devolucion;
    }

    public boolean estaCompleta() {
        return !detalles.isEmpty() && this.total != null && this.total > 0;
    }

    public boolean necesitaFacturacion() {
        return this.facturar && (this.numeroFactura == null || this.numeroFactura.isEmpty());
    }

    public int getCantidadTotalItems() {
        return detalles.stream()
                .mapToInt(OrdenDetalle::getCantidad)
                .sum();
    }

    public String getEstadoDescriptivo() {
        switch (this.estado) {
            case "PENDIENTE":
                return "Pendiente de pago";
            case "PAGADA":
                return "Pagada - Preparando envío";
            case "ENVIADA":
                return "En camino";
            case "ENTREGADA":
                return "Entregada";
            case "CANCELADA":
                return "Cancelada";
            case "DEVUELTA":
                return "Devuelta";
            default:
                return this.estado;
        }
    }

    // Getters y Setters optimizados
    public Long getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(Long ordenId) {
        this.ordenId = ordenId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        String estadoAnterior = this.estado;
        this.estado = estado;

        if (!estado.equals(estadoAnterior)) {
            log.info("🔄 Estado de orden {} cambiado: {} -> {}", this.ordenId, estadoAnterior, estado);
        }
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<OrdenDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<OrdenDetalle> detalles) {
        this.detalles = detalles != null ? detalles : new ArrayList<>();
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

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    @Override
    public String toString() {
        return "Orden{" +
                "ordenId=" + ordenId +
                ", usuario=" + (usuario != null ? usuario.getEmail() : "null") +
                ", estado='" + estado + '\'' +
                ", total=" + total +
                ", metodoPago='" + metodoPago + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", detalles=" + (detalles != null ? detalles.size() : 0) + " items" +
                ", devolucion=" + devolucion +
                ", facturar=" + facturar +
                '}';
    }
}
