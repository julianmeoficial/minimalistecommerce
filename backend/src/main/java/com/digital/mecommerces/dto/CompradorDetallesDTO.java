package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para detalles específicos de compradores
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompradorDetallesDTO {

    @JsonProperty("usuarioId")
    private Long usuarioId;

    @JsonProperty("fechaNacimiento")
    private LocalDate fechaNacimiento;

    @JsonProperty("preferencias")
    private String preferencias;

    @JsonProperty("direccionEnvio")
    private String direccionEnvio;

    @JsonProperty("telefono")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @JsonProperty("direccionAlternativa")
    private String direccionAlternativa;

    @JsonProperty("telefonoAlternativo")
    @Size(max = 20, message = "El teléfono alternativo no puede exceder 20 caracteres")
    private String telefonoAlternativo;

    @JsonProperty("notificacionEmail")
    private Boolean notificacionEmail = true;

    @JsonProperty("notificacionSms")
    private Boolean notificacionSms = false;

    @JsonProperty("calificacion")
    private BigDecimal calificacion;

    @JsonProperty("totalCompras")
    private Integer totalCompras;

    @JsonProperty("limiteCompra")
    private BigDecimal limiteCompra;

    @JsonProperty("activo")
    private Boolean activo;

    // Información del usuario asociado
    @JsonProperty("usuarioNombre")
    private String usuarioNombre;

    @JsonProperty("usuarioEmail")
    private String usuarioEmail;

    // Campos calculados
    @JsonProperty("nivelComprador")
    private String nivelComprador;

    @JsonProperty("fechaRegistro")
    private LocalDateTime fechaRegistro;

    @JsonProperty("ultimaCompra")
    private LocalDateTime ultimaCompra;

    // Constructor básico para creación
    public CompradorDetallesDTO(String direccionEnvio, String telefono) {
        this.direccionEnvio = direccionEnvio;
        this.telefono = telefono;
        this.notificacionEmail = true;
        this.notificacionSms = false;
        this.totalCompras = 0;
        this.calificacion = new BigDecimal("5.00");
        this.activo = true;
    }

    // Constructor con usuario
    public CompradorDetallesDTO(Long usuarioId, String direccionEnvio, String telefono) {
        this.usuarioId = usuarioId;
        this.direccionEnvio = direccionEnvio;
        this.telefono = telefono;
        this.notificacionEmail = true;
        this.notificacionSms = false;
        this.totalCompras = 0;
        this.calificacion = new BigDecimal("5.00");
        this.activo = true;
    }

    // Constructor completo con datos de compra
    public CompradorDetallesDTO(Long usuarioId, String direccionEnvio, String telefono,
                                Integer totalCompras, BigDecimal calificacion) {
        this.usuarioId = usuarioId;
        this.direccionEnvio = direccionEnvio;
        this.telefono = telefono;
        this.totalCompras = totalCompras;
        this.calificacion = calificacion;
        this.notificacionEmail = true;
        this.notificacionSms = false;
        this.activo = true;
    }

    // Métodos de validación
    public boolean isValid() {
        return direccionEnvio != null && !direccionEnvio.trim().isEmpty() &&
                telefono != null && !telefono.trim().isEmpty();
    }

    public boolean tieneInformacionCompleta() {
        return isValid() &&
                fechaNacimiento != null &&
                direccionAlternativa != null && !direccionAlternativa.trim().isEmpty();
    }

    public boolean tieneContactoCompleto() {
        return telefono != null && !telefono.trim().isEmpty() &&
                ((telefonoAlternativo != null && !telefonoAlternativo.trim().isEmpty()) ||
                        Boolean.TRUE.equals(notificacionEmail));
    }

    // Métodos de negocio
    public String calcularNivelComprador() {
        if (totalCompras == null || totalCompras == 0) {
            return "NUEVO";
        } else if (totalCompras <= 5) {
            return "BRONCE";
        } else if (totalCompras <= 15) {
            return "PLATA";
        } else if (totalCompras <= 30) {
            return "ORO";
        } else {
            return "PLATINO";
        }
    }

    public boolean esCompradorVIP() {
        return totalCompras != null && totalCompras >= 20 &&
                calificacion != null && calificacion.compareTo(new BigDecimal("4.5")) >= 0;
    }

    public boolean esCompradorFrecuente() {
        return totalCompras != null && totalCompras >= 5;
    }

    public boolean puedeComprar() {
        return Boolean.TRUE.equals(activo) && tieneInformacionBasica();
    }

    public boolean tieneInformacionBasica() {
        return direccionEnvio != null && !direccionEnvio.trim().isEmpty() &&
                telefono != null && !telefono.trim().isEmpty();
    }

    public boolean prefieseNotificacionesEmail() {
        return Boolean.TRUE.equals(notificacionEmail);
    }

    public boolean prefiereNotificacionesSms() {
        return Boolean.TRUE.equals(notificacionSms);
    }

    // Método para crear desde entidad
    public static CompradorDetallesDTO fromEntity(com.digital.mecommerces.model.CompradorDetalles compradorDetalles) {
        if (compradorDetalles == null) return null;

        CompradorDetallesDTO dto = new CompradorDetallesDTO();
        dto.setUsuarioId(compradorDetalles.getUsuarioId());
        dto.setFechaNacimiento(compradorDetalles.getFechaNacimiento());
        dto.setPreferencias(compradorDetalles.getPreferencias());
        dto.setDireccionEnvio(compradorDetalles.getDireccionEnvio());
        dto.setTelefono(compradorDetalles.getTelefono());
        dto.setDireccionAlternativa(compradorDetalles.getDireccionAlternativa());
        dto.setTelefonoAlternativo(compradorDetalles.getTelefonoAlternativo());
        dto.setNotificacionEmail(compradorDetalles.getNotificacionEmail());
        dto.setNotificacionSms(compradorDetalles.getNotificacionSms());
        dto.setCalificacion(compradorDetalles.getCalificacion());
        dto.setTotalCompras(compradorDetalles.getTotalCompras());
        dto.setLimiteCompra(compradorDetalles.getLimiteCompra());
        dto.setActivo(compradorDetalles.getActivo());

        if (compradorDetalles.getUsuario() != null) {
            dto.setUsuarioNombre(compradorDetalles.getUsuario().getUsuarioNombre());
            dto.setUsuarioEmail(compradorDetalles.getUsuario().getEmail());
            dto.setFechaRegistro(compradorDetalles.getUsuario().getCreatedAt());
        }

        // Calcular campos derivados
        dto.setNivelComprador(dto.calcularNivelComprador());

        return dto;
    }

    // Método para crear para vista pública (sin información sensible)
    public CompradorDetallesDTO toPublic() {
        CompradorDetallesDTO publicDto = new CompradorDetallesDTO();
        publicDto.setUsuarioId(this.usuarioId);
        publicDto.setUsuarioNombre(this.usuarioNombre);
        publicDto.setCalificacion(this.calificacion);
        publicDto.setTotalCompras(this.totalCompras);
        publicDto.setNivelComprador(this.nivelComprador);
        publicDto.setActivo(this.activo);
        // No incluir información sensible como direcciones, teléfonos, etc.
        return publicDto;
    }

    // Métodos de utilidad para estadísticas
    public boolean esElegibleParaCampanaEmail() {
        return Boolean.TRUE.equals(notificacionEmail) &&
                Boolean.TRUE.equals(activo) &&
                (totalCompras == null || totalCompras >= 1);
    }

    public boolean esElegibleParaCampanaSms() {
        return Boolean.TRUE.equals(notificacionSms) &&
                Boolean.TRUE.equals(activo) &&
                telefono != null && !telefono.trim().isEmpty();
    }

    public int calcularEdadAproximada() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }

    @Override
    public String toString() {
        return "CompradorDetallesDTO{" +
                "usuarioId=" + usuarioId +
                ", usuarioEmail='" + usuarioEmail + '\'' +
                ", totalCompras=" + totalCompras +
                ", calificacion=" + calificacion +
                ", nivelComprador='" + nivelComprador + '\'' +
                ", activo=" + activo +
                '}';
    }
}
