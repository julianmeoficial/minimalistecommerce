package com.digital.mecommerces.model;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "vendedordetalles")
@Slf4j
public class VendedorDetalles {

    @Id
    @Column(name = "usuarioid")
    private Long usuarioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuarioid")
    private Usuario usuario;

    @Column(name = "rfc", length = 20)
    private String rfc;

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
    private String tipoDocumento = "CEDULA";

    @Column(name = "banco", length = 100)
    private String banco;

    @Column(name = "tipocuenta", length = 50)
    private String tipoCuenta = "AHORROS";

    @Column(name = "numerocuenta", length = 100)
    private String numeroCuenta;

    @Column(name = "fecharegistro")
    private LocalDateTime fechaRegistro;

    @Column(name = "comision")
    private Double comision = 0.05; // 5% por defecto

    @Column(name = "ventastotales")
    private Integer ventasTotales = 0;

    @Column(name = "calificacionpromedio")
    private Double calificacionPromedio = 5.0;

    @Column(name = "activo")
    private Boolean activo = true;

    // Constructor vacío requerido por JPA
    public VendedorDetalles() {
        this.verificado = false;
        this.fechaRegistro = LocalDateTime.now();
        this.tipoDocumento = "CEDULA";
        this.tipoCuenta = "AHORROS";
        this.comision = 0.05;
        this.ventasTotales = 0;
        this.calificacionPromedio = 5.0;
        this.activo = true;
    }

    // Constructor optimizado con usuario
    public VendedorDetalles(Usuario usuario) {
        this();
        this.usuario = usuario;

        if (usuario != null && usuario.getUsuarioId() != null) {
            this.usuarioId = usuario.getUsuarioId();

            // Verificar que sea realmente vendedor
            if (usuario.getRol() != null) {
                try {
                    TipoUsuario tipo = TipoUsuario.fromCodigo(usuario.getRol().getNombre());
                    if (tipo != TipoUsuario.VENDEDOR && tipo != TipoUsuario.ADMINISTRADOR) {
                        log.warn("⚠️ Usuario {} no es vendedor pero se está creando VendedorDetalles",
                                usuario.getEmail());
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("⚠️ Rol no reconocido para VendedorDetalles: {}", usuario.getRol().getNombre());
                }
            }
        }
    }

    // Constructor completo optimizado
    public VendedorDetalles(Usuario usuario, String numRegistroFiscal, String especialidad, String direccionComercial) {
        this(usuario);
        this.numRegistroFiscal = numRegistroFiscal;
        this.especialidad = especialidad;
        this.direccionComercial = direccionComercial;
    }

    // Asegurar que el ID se establezca correctamente antes de persistir
    @PrePersist
    public void prePersist() {
        if (usuario != null && usuario.getUsuarioId() != null) {
            this.usuarioId = usuario.getUsuarioId();
        }

        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }

        if (this.verificado == null) {
            this.verificado = false;
        }

        if (this.tipoDocumento == null || this.tipoDocumento.isEmpty()) {
            this.tipoDocumento = "CEDULA";
        }

        if (this.tipoCuenta == null || this.tipoCuenta.isEmpty()) {
            this.tipoCuenta = "AHORROS";
        }

        if (this.comision == null) {
            this.comision = 0.05;
        }

        if (this.ventasTotales == null) {
            this.ventasTotales = 0;
        }

        if (this.calificacionPromedio == null) {
            this.calificacionPromedio = 5.0;
        }

        if (this.activo == null) {
            this.activo = true;
        }

        log.debug("✅ VendedorDetalles preparado para persistir - Usuario ID: {}", this.usuarioId);
    }

    @PreUpdate
    public void preUpdate() {
        log.debug("🔄 VendedorDetalles actualizado - Usuario ID: {}", this.usuarioId);
    }

    // Métodos de utilidad optimizados
    public boolean esVendedorActivo() {
        return this.activo != null && this.activo &&
                this.usuario != null &&
                this.usuario.getActivo() != null &&
                this.usuario.getActivo();
    }

    public boolean estaVerificado() {
        return this.verificado != null && this.verificado;
    }

    public boolean puedeVender() {
        return esVendedorActivo() && estaVerificado();
    }

    public boolean tieneInformacionCompleta() {
        return this.numRegistroFiscal != null && !this.numRegistroFiscal.trim().isEmpty() &&
                this.especialidad != null && !this.especialidad.trim().isEmpty() &&
                this.direccionComercial != null && !this.direccionComercial.trim().isEmpty();
    }

    public boolean tieneInformacionBancaria() {
        return this.banco != null && !this.banco.trim().isEmpty() &&
                this.numeroCuenta != null && !this.numeroCuenta.trim().isEmpty();
    }

    // Métodos de verificación optimizados
    public void verificarVendedor() {
        this.verificado = true;
        this.fechaVerificacion = LocalDateTime.now();
        log.debug("✅ Vendedor verificado - Usuario ID: {}", this.usuarioId);
    }

    public void revocarVerificacion() {
        this.verificado = false;
        this.fechaVerificacion = null;
        log.debug("❌ Verificación revocada - Usuario ID: {}", this.usuarioId);
    }

    // Métodos de gestión de ventas optimizados
    public void registrarVenta() {
        this.ventasTotales = (this.ventasTotales == null ? 0 : this.ventasTotales) + 1;
        log.debug("🛒 Venta registrada para vendedor {}: Total ventas = {}",
                this.usuarioId, this.ventasTotales);
    }

    public void actualizarCalificacion(Double nuevaCalificacion) {
        if (nuevaCalificacion != null && nuevaCalificacion >= 0.0 && nuevaCalificacion <= 5.0) {
            // Promedio simple - en producción se podría usar un algoritmo más sofisticado
            if (this.calificacionPromedio == null) {
                this.calificacionPromedio = nuevaCalificacion;
            } else {
                this.calificacionPromedio = (this.calificacionPromedio + nuevaCalificacion) / 2.0;
            }
            log.debug("⭐ Calificación actualizada para vendedor {}: {}",
                    this.usuarioId, this.calificacionPromedio);
        }
    }

    public Double calcularComisionVenta(Double montoVenta) {
        if (montoVenta == null || montoVenta <= 0 || this.comision == null) {
            return 0.0;
        }
        return montoVenta * this.comision;
    }

    // Métodos de gestión de documentación optimizados
    public void configurarDocumentacion(String tipoDocumento, String documentoComercial) {
        this.tipoDocumento = tipoDocumento;
        this.documentoComercial = documentoComercial;
        log.debug("📄 Documentación configurada para vendedor {}: {}",
                this.usuarioId, tipoDocumento);
    }

    public void configurarInformacionBancaria(String banco, String tipoCuenta, String numeroCuenta) {
        this.banco = banco;
        this.tipoCuenta = tipoCuenta;
        this.numeroCuenta = numeroCuenta;
        log.debug("🏦 Información bancaria configurada para vendedor {}", this.usuarioId);
    }

    // Métodos de estado optimizados
    public String getNivelVendedor() {
        if (this.ventasTotales == null || this.ventasTotales == 0) {
            return "NUEVO";
        } else if (this.ventasTotales < 10) {
            return "BRONCE";
        } else if (this.ventasTotales < 50) {
            return "PLATA";
        } else if (this.ventasTotales < 100) {
            return "ORO";
        } else {
            return "PLATINO";
        }
    }

    public String getEstadoVerificacion() {
        if (!estaVerificado()) {
            return "PENDIENTE";
        } else if (this.fechaVerificacion != null) {
            return "VERIFICADO";
        } else {
            return "EN_REVISION";
        }
    }

    public boolean necesitaVerificacion() {
        return !estaVerificado() && tieneInformacionCompleta();
    }

    // Getters y Setters optimizados
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
        if (usuario != null && usuario.getUsuarioId() != null) {
            this.usuarioId = usuario.getUsuarioId();
        }
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
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
        if (verificado != null && verificado && this.fechaVerificacion == null) {
            this.fechaVerificacion = LocalDateTime.now();
        }
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Double getComision() {
        return comision;
    }

    public void setComision(Double comision) {
        this.comision = comision;
    }

    public Integer getVentasTotales() {
        return ventasTotales;
    }

    public void setVentasTotales(Integer ventasTotales) {
        this.ventasTotales = ventasTotales;
    }

    public Double getCalificacionPromedio() {
        return calificacionPromedio;
    }

    public void setCalificacionPromedio(Double calificacionPromedio) {
        this.calificacionPromedio = calificacionPromedio;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "VendedorDetalles{" +
                "usuarioId=" + usuarioId +
                ", numRegistroFiscal='" + numRegistroFiscal + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", direccionComercial='" + direccionComercial + '\'' +
                ", verificado=" + verificado +
                ", ventasTotales=" + ventasTotales +
                ", calificacionPromedio=" + calificacionPromedio +
                ", nivelVendedor='" + getNivelVendedor() + '\'' +
                ", estadoVerificacion='" + getEstadoVerificacion() + '\'' +
                ", activo=" + activo +
                '}';
    }
}
