package com.digital.mecommerces.model;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "admindetalles")
@Slf4j
public class AdminDetalles {

    @Id
    @Column(name = "usuarioid")
    private Long usuarioId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuarioid")
    private Usuario usuario;

    @Column(name = "region", length = 100)
    private String region;

    @Column(name = "nivelacceso", length = 255)
    private String nivelAcceso;

    @Column(name = "ultimaaccion", length = 255)
    private String ultimaAccion;

    @Column(name = "ultimologin")
    private LocalDateTime ultimoLogin;

    @Column(name = "ipacceso", length = 50)
    private String ipAcceso;

    @Column(name = "sesionesactivas")
    private Integer sesionesActivas = 0;

    @Column(name = "ultimaactividad")
    private LocalDateTime ultimaActividad;

    @Column(name = "configuraciones", columnDefinition = "TEXT")
    private String configuraciones;

    @Column(name = "activo")
    private Boolean activo = true;

    // Constructor vacío requerido por JPA
    public AdminDetalles() {
        this.sesionesActivas = 0;
        this.activo = true;
        this.ultimaActividad = LocalDateTime.now();
    }

    // Constructor optimizado con usuario
    public AdminDetalles(Usuario usuario) {
        this();
        this.usuario = usuario;

        if (usuario != null && usuario.getUsuarioId() != null) {
            this.usuarioId = usuario.getUsuarioId();

            // Verificar que sea realmente administrador
            if (usuario.getRol() != null) {
                try {
                    TipoUsuario tipo = TipoUsuario.fromCodigo(usuario.getRol().getNombre());
                    if (tipo != TipoUsuario.ADMINISTRADOR) {
                        log.warn("⚠️ Usuario {} no es administrador pero se está creando AdminDetalles",
                                usuario.getEmail());
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("⚠️ Rol no reconocido para AdminDetalles: {}", usuario.getRol().getNombre());
                }
            }
        }

        // Configuraciones por defecto para administrador
        this.region = "Global";
        this.nivelAcceso = "ESTANDAR";
        this.ultimaAccion = "Registro inicial del sistema optimizado";
    }

    // Constructor completo optimizado
    public AdminDetalles(Usuario usuario, String region, String nivelAcceso) {
        this(usuario);
        this.region = region;
        this.nivelAcceso = nivelAcceso;
        this.ultimaAccion = "Configuración inicial personalizada";
    }

    // Asegurar que el ID se establezca correctamente antes de persistir
    @PrePersist
    public void prePersist() {
        if (usuario != null && usuario.getUsuarioId() != null) {
            this.usuarioId = usuario.getUsuarioId();
        }

        if (this.ultimaActividad == null) {
            this.ultimaActividad = LocalDateTime.now();
        }

        if (this.sesionesActivas == null) {
            this.sesionesActivas = 0;
        }

        if (this.activo == null) {
            this.activo = true;
        }

        log.debug("✅ AdminDetalles preparado para persistir - Usuario ID: {}", this.usuarioId);
    }

    @PreUpdate
    public void preUpdate() {
        this.ultimaActividad = LocalDateTime.now();
        log.debug("🔄 AdminDetalles actualizado - Usuario ID: {}", this.usuarioId);
    }

    // Métodos de utilidad optimizados
    public boolean esAdministradorActivo() {
        return this.activo != null && this.activo &&
                this.usuario != null &&
                this.usuario.getActivo() != null &&
                this.usuario.getActivo();
    }

    public boolean tieneNivelAccesoTotal() {
        return "SUPER".equals(this.nivelAcceso) || "TOTAL".equals(this.nivelAcceso);
    }

    public void registrarActividad(String actividad) {
        this.ultimaAccion = actividad;
        this.ultimaActividad = LocalDateTime.now();
        log.debug("📝 Actividad registrada para admin {}: {}", this.usuarioId, actividad);
    }

    public void iniciarSesion(String ip) {
        this.ultimoLogin = LocalDateTime.now();
        this.ipAcceso = ip;
        this.sesionesActivas = (this.sesionesActivas == null ? 0 : this.sesionesActivas) + 1;
        this.ultimaActividad = LocalDateTime.now();
        registrarActividad("Inicio de sesión desde IP: " + ip);
    }

    public void cerrarSesion() {
        if (this.sesionesActivas != null && this.sesionesActivas > 0) {
            this.sesionesActivas--;
        }
        registrarActividad("Cierre de sesión");
    }

    public boolean puedeEjecutarAccionSistema() {
        return esAdministradorActivo() &&
                (tieneNivelAccesoTotal() || "ESTANDAR".equals(this.nivelAcceso));
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

    public String getUltimaAccion() {
        return ultimaAccion;
    }

    public void setUltimaAccion(String ultimaAccion) {
        this.ultimaAccion = ultimaAccion;
        this.ultimaActividad = LocalDateTime.now();
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public String getIpAcceso() {
        return ipAcceso;
    }

    public void setIpAcceso(String ipAcceso) {
        this.ipAcceso = ipAcceso;
    }

    public Integer getSesionesActivas() {
        return sesionesActivas;
    }

    public void setSesionesActivas(Integer sesionesActivas) {
        this.sesionesActivas = sesionesActivas;
    }

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(LocalDateTime ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public String getConfiguraciones() {
        return configuraciones;
    }

    public void setConfiguraciones(String configuraciones) {
        this.configuraciones = configuraciones;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "AdminDetalles{" +
                "usuarioId=" + usuarioId +
                ", region='" + region + '\'' +
                ", nivelAcceso='" + nivelAcceso + '\'' +
                ", activo=" + activo +
                ", sesionesActivas=" + sesionesActivas +
                '}';
    }
}
