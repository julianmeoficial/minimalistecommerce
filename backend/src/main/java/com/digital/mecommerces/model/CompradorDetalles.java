package com.digital.mecommerces.model;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "compradordetalles")
@Slf4j
public class CompradorDetalles {

    @Id
    @Column(name = "usuarioid")
    private Long usuarioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuarioid")
    private Usuario usuario;

    @Column(name = "fechanacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "preferencias", columnDefinition = "TEXT")
    private String preferencias;

    @Column(name = "direccionenvio", length = 255)
    private String direccionEnvio;

    @Column(name = "telefono", length = 255)
    private String telefono;

    @Column(name = "direccionalternativa", length = 255)
    private String direccionAlternativa;

    @Column(name = "telefonoalternativo", length = 50)
    private String telefonoAlternativo;

    @Column(name = "notificacionemail")
    private Boolean notificacionEmail = true;

    @Column(name = "notificacionsms")
    private Boolean notificacionSms = false;

    @Column(name = "calificacion", precision = 3, scale = 2)
    private BigDecimal calificacion = new BigDecimal("5.00");

    @Column(name = "totalcompras")
    private Integer totalCompras = 0;

    @Column(name = "limitecompra")
    private BigDecimal limiteCompra;

    @Column(name = "fecharegistro")
    private LocalDateTime fechaRegistro;

    @Column(name = "ultimacompra")
    private LocalDateTime ultimaCompra;

    @Column(name = "activo")
    private Boolean activo = true;

    // Constructor vacío requerido por JPA
    public CompradorDetalles() {
        this.notificacionEmail = true;
        this.notificacionSms = false;
        this.calificacion = new BigDecimal("5.00");
        this.totalCompras = 0;
        this.fechaRegistro = LocalDateTime.now();
        this.activo = true;
    }

    // Constructor optimizado con usuario
    public CompradorDetalles(Usuario usuario) {
        this();
        this.usuario = usuario;

        if (usuario != null && usuario.getUsuarioId() != null) {
            this.usuarioId = usuario.getUsuarioId();

            // Verificar que sea realmente comprador
            if (usuario.getRol() != null) {
                try {
                    TipoUsuario tipo = TipoUsuario.fromCodigo(usuario.getRol().getNombre());
                    if (tipo != TipoUsuario.COMPRADOR && tipo != TipoUsuario.ADMINISTRADOR) {
                        log.warn("⚠️ Usuario {} no es comprador pero se está creando CompradorDetalles",
                                usuario.getEmail());
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("⚠️ Rol no reconocido para CompradorDetalles: {}", usuario.getRol().getNombre());
                }
            }
        }
    }

    // Constructor completo optimizado
    public CompradorDetalles(Usuario usuario, LocalDate fechaNacimiento, String direccionEnvio, String telefono) {
        this(usuario);
        this.fechaNacimiento = fechaNacimiento;
        this.direccionEnvio = direccionEnvio;
        this.telefono = telefono;
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

        if (this.notificacionEmail == null) {
            this.notificacionEmail = true;
        }

        if (this.notificacionSms == null) {
            this.notificacionSms = false;
        }

        if (this.calificacion == null) {
            this.calificacion = new BigDecimal("5.00");
        }

        if (this.totalCompras == null) {
            this.totalCompras = 0;
        }

        if (this.activo == null) {
            this.activo = true;
        }

        log.debug("✅ CompradorDetalles preparado para persistir - Usuario ID: {}", this.usuarioId);
    }

    @PreUpdate
    public void preUpdate() {
        log.debug("🔄 CompradorDetalles actualizado - Usuario ID: {}", this.usuarioId);
    }

    // Métodos de utilidad optimizados
    public boolean esCompradorActivo() {
        return this.activo != null && this.activo &&
                this.usuario != null &&
                this.usuario.getActivo() != null &&
                this.usuario.getActivo();
    }

    public boolean puedeComprar() {
        return esCompradorActivo() &&
                (this.limiteCompra == null || this.limiteCompra.compareTo(BigDecimal.ZERO) > 0);
    }

    public void registrarCompra(BigDecimal monto) {
        if (monto != null && monto.compareTo(BigDecimal.ZERO) > 0) {
            this.totalCompras = (this.totalCompras == null ? 0 : this.totalCompras) + 1;
            this.ultimaCompra = LocalDateTime.now();

            if (this.limiteCompra != null) {
                this.limiteCompra = this.limiteCompra.subtract(monto);
            }

            log.debug("🛒 Compra registrada para usuario {}: ${}", this.usuarioId, monto);
        }
    }

    public void actualizarCalificacion(BigDecimal nuevaCalificacion) {
        if (nuevaCalificacion != null &&
                nuevaCalificacion.compareTo(BigDecimal.ZERO) >= 0 &&
                nuevaCalificacion.compareTo(new BigDecimal("5.00")) <= 0) {

            this.calificacion = nuevaCalificacion;
            log.debug("⭐ Calificación actualizada para usuario {}: {}", this.usuarioId, nuevaCalificacion);
        }
    }

    public String getNivelComprador() {
        if (this.totalCompras == null || this.totalCompras == 0) {
            return "NUEVO";
        } else if (this.totalCompras < 5) {
            return "BRONCE";
        } else if (this.totalCompras < 15) {
            return "PLATA";
        } else if (this.totalCompras < 30) {
            return "ORO";
        } else {
            return "PLATINO";
        }
    }

    public boolean tieneInformacionCompleta() {
        return this.direccionEnvio != null && !this.direccionEnvio.trim().isEmpty() &&
                this.telefono != null && !this.telefono.trim().isEmpty();
    }

    public void configurarNotificaciones(boolean email, boolean sms) {
        this.notificacionEmail = email;
        this.notificacionSms = sms;
        log.debug("🔔 Notificaciones configuradas para usuario {}: Email={}, SMS={}",
                this.usuarioId, email, sms);
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

    public BigDecimal getLimiteCompra() {
        return limiteCompra;
    }

    public void setLimiteCompra(BigDecimal limiteCompra) {
        this.limiteCompra = limiteCompra;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getUltimaCompra() {
        return ultimaCompra;
    }

    public void setUltimaCompra(LocalDateTime ultimaCompra) {
        this.ultimaCompra = ultimaCompra;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "CompradorDetalles{" +
                "usuarioId=" + usuarioId +
                ", fechaNacimiento=" + fechaNacimiento +
                ", direccionEnvio='" + direccionEnvio + '\'' +
                ", telefono='" + telefono + '\'' +
                ", notificacionEmail=" + notificacionEmail +
                ", calificacion=" + calificacion +
                ", totalCompras=" + totalCompras +
                ", nivel='" + getNivelComprador() + '\'' +
                ", activo=" + activo +
                '}';
    }
}
