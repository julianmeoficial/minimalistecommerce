package com.digital.mecommerces.model;

import com.digital.mecommerces.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "passwordresettokens")
@Slf4j
public class PasswordResetTokens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioid", nullable = false)
    private Usuario usuario;

    @Column(name = "token", unique = true, nullable = false, length = 255)
    private String token;

    @Column(name = "fechaexpiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(name = "usado")
    private Boolean usado = false;

    @Column(name = "createdat")
    private LocalDateTime createdAt;

    @Column(name = "usadoen")
    private LocalDateTime usadoEn;

    @Column(name = "ipsolicitante", length = 50)
    private String ipSolicitante;

    @Column(name = "useragent", length = 500)
    private String userAgent;

    @Column(name = "intentos")
    private Integer intentos = 0;

    @Column(name = "activo")
    private Boolean activo = true;

    // Constructor vacío requerido por JPA
    public PasswordResetTokens() {
        this.createdAt = LocalDateTime.now();
        this.usado = false;
        this.intentos = 0;
        this.activo = true;
        // Token válido por 1 hora por defecto
        this.fechaExpiracion = LocalDateTime.now().plusHours(1);
    }

    // Constructor optimizado con usuario y token
    public PasswordResetTokens(Usuario usuario, String token) {
        this();
        this.usuario = usuario;
        this.token = token;

        if (usuario != null && usuario.getRol() != null) {
            try {
                TipoUsuario tipo = TipoUsuario.fromCodigo(usuario.getRol().getNombre());
                log.debug("✅ Token de reset creado para usuario tipo: {}", tipo.getDescripcion());
            } catch (IllegalArgumentException e) {
                log.warn("⚠️ Rol no reconocido para token de reset: {}", usuario.getRol().getNombre());
            }
        }
    }

    // Constructor completo optimizado
    public PasswordResetTokens(Usuario usuario, String token, LocalDateTime fechaExpiracion, String ipSolicitante) {
        this(usuario, token);
        this.fechaExpiracion = fechaExpiracion;
        this.ipSolicitante = ipSolicitante;
    }

    // Constructor con duración personalizada en horas
    public PasswordResetTokens(Usuario usuario, String token, int horasValidez) {
        this(usuario, token);
        this.fechaExpiracion = LocalDateTime.now().plusHours(horasValidez);
    }

    // Constructor completo con metadatos
    public PasswordResetTokens(Usuario usuario, String token, int horasValidez, String ipSolicitante, String userAgent) {
        this(usuario, token, horasValidez);
        this.ipSolicitante = ipSolicitante;
        this.userAgent = userAgent;
    }

    // Métodos de gestión optimizados
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.fechaExpiracion == null) {
            this.fechaExpiracion = LocalDateTime.now().plusHours(1);
        }

        if (this.usado == null) {
            this.usado = false;
        }

        if (this.intentos == null) {
            this.intentos = 0;
        }

        if (this.activo == null) {
            this.activo = true;
        }

        log.debug("✅ PasswordResetToken preparado para persistir - Usuario: {}",
                this.usuario != null ? this.usuario.getEmail() : "null");
    }

    @PreUpdate
    public void preUpdate() {
        log.debug("🔄 PasswordResetToken actualizado - ID: {}, Usado: {}", this.id, this.usado);
    }

    // Métodos de validación optimizados
    public boolean esValido() {
        return this.activo &&
                !this.usado &&
                this.fechaExpiracion.isAfter(LocalDateTime.now()) &&
                this.intentos < 3; // Máximo 3 intentos
    }

    public boolean estaExpirado() {
        return this.fechaExpiracion.isBefore(LocalDateTime.now());
    }

    public boolean estaUsado() {
        return this.usado != null && this.usado;
    }

    public boolean estaActivo() {
        return this.activo != null && this.activo;
    }

    public boolean alcanzóLimiteIntentos() {
        return this.intentos != null && this.intentos >= 3;
    }

    public boolean puedeSerUtilizado() {
        return esValido() && !estaExpirado() && !estaUsado() && !alcanzóLimiteIntentos();
    }

    // Métodos de acción optimizados
    public void marcarComoUsado() {
        this.usado = true;
        this.usadoEn = LocalDateTime.now();
        this.activo = false;
        log.info("✅ Token de reset marcado como usado para usuario: {}",
                this.usuario != null ? this.usuario.getEmail() : "unknown");
    }

    public void incrementarIntentos() {
        this.intentos = (this.intentos == null ? 0 : this.intentos) + 1;

        if (this.intentos >= 3) {
            this.activo = false;
            log.warn("⚠️ Token de reset desactivado por demasiados intentos: {}",
                    this.usuario != null ? this.usuario.getEmail() : "unknown");
        }

        log.debug("🔢 Intentos incrementados para token: {} (Total: {})", this.id, this.intentos);
    }

    public void marcarComoInactivo() {
        this.activo = false;
        log.info("❌ Token de reset marcado como inactivo para usuario: {}",
                this.usuario != null ? this.usuario.getEmail() : "unknown");
    }

    public void extenderExpiracion(int horas) {
        this.fechaExpiracion = LocalDateTime.now().plusHours(horas);
        log.info("⏰ Expiración extendida {} horas para token de usuario: {}",
                horas, this.usuario != null ? this.usuario.getEmail() : "unknown");
    }

    public void configurarMetadatos(String ipSolicitante, String userAgent) {
        this.ipSolicitante = ipSolicitante;
        this.userAgent = userAgent;
        log.debug("📝 Metadatos configurados para token - IP: {}", ipSolicitante);
    }

    public void reiniciarIntentos() {
        this.intentos = 0;
        if (!this.usado && !estaExpirado()) {
            this.activo = true;
        }
        log.debug("🔄 Intentos reiniciados para token: {}", this.id);
    }

    // Métodos de utilidad optimizados
    public long getMinutosParaExpiracion() {
        if (estaExpirado()) {
            return 0;
        }

        return java.time.Duration.between(LocalDateTime.now(), this.fechaExpiracion).toMinutes();
    }

    public long getSegundosParaExpiracion() {
        if (estaExpirado()) {
            return 0;
        }

        return java.time.Duration.between(LocalDateTime.now(), this.fechaExpiracion).toSeconds();
    }

    public String getEstadoToken() {
        if (!this.activo) {
            return "INACTIVO";
        } else if (this.usado) {
            return "USADO";
        } else if (estaExpirado()) {
            return "EXPIRADO";
        } else if (alcanzóLimiteIntentos()) {
            return "BLOQUEADO";
        } else {
            return "VÁLIDO";
        }
    }

    public String getTiempoRestanteFormateado() {
        long minutos = getMinutosParaExpiracion();
        if (minutos <= 0) {
            return "Expirado";
        } else if (minutos < 60) {
            return minutos + " minutos";
        } else {
            long horas = minutos / 60;
            long minutosRestantes = minutos % 60;
            return horas + "h " + minutosRestantes + "m";
        }
    }

    public boolean esTokenReciente() {
        // Considera un token como reciente si fue creado en los últimos 5 minutos
        return this.createdAt != null &&
                this.createdAt.isAfter(LocalDateTime.now().minusMinutes(5));
    }

    public boolean necesitaRenovacion() {
        // Un token necesita renovación si expira en menos de 10 minutos
        return getMinutosParaExpiracion() <= 10 && getMinutosParaExpiracion() > 0;
    }

    // Getters y Setters optimizados
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Boolean getUsado() {
        return usado;
    }

    public void setUsado(Boolean usado) {
        this.usado = usado;
        if (usado != null && usado && this.usadoEn == null) {
            this.usadoEn = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUsadoEn() {
        return usadoEn;
    }

    public void setUsadoEn(LocalDateTime usadoEn) {
        this.usadoEn = usadoEn;
    }

    public String getIpSolicitante() {
        return ipSolicitante;
    }

    public void setIpSolicitante(String ipSolicitante) {
        this.ipSolicitante = ipSolicitante;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Integer getIntentos() {
        return intentos;
    }

    public void setIntentos(Integer intentos) {
        this.intentos = intentos;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "PasswordResetTokens{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getEmail() : "null") +
                ", usado=" + usado +
                ", fechaExpiracion=" + fechaExpiracion +
                ", intentos=" + intentos +
                ", activo=" + activo +
                ", estado='" + getEstadoToken() + '\'' +
                ", tiempoRestante='" + getTiempoRestanteFormateado() + '\'' +
                ", esValido=" + esValido() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordResetTokens)) return false;
        PasswordResetTokens that = (PasswordResetTokens) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
