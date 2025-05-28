package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para detalles específicos de administradores
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDetallesDTO {

    @JsonProperty("usuarioId")
    private Long usuarioId;

    @NotBlank(message = "La región es obligatoria")
    @JsonProperty("region")
    private String region;

    @NotBlank(message = "El nivel de acceso es obligatorio")
    @JsonProperty("nivelAcceso")
    private String nivelAcceso;

    @JsonProperty("configuraciones")
    private String configuraciones;

    @JsonProperty("ultimaAccion")
    private String ultimaAccion;

    @JsonProperty("ultimoLogin")
    private LocalDateTime ultimoLogin;

    @JsonProperty("ultimaActividad")
    private LocalDateTime ultimaActividad;

    @JsonProperty("ipAcceso")
    private String ipAcceso;

    @JsonProperty("sesionesActivas")
    private Integer sesionesActivas;

    @JsonProperty("activo")
    private Boolean activo;

    // Información del usuario asociado
    @JsonProperty("usuarioNombre")
    private String usuarioNombre;

    @JsonProperty("usuarioEmail")
    private String usuarioEmail;

    // Constructor básico para creación
    public AdminDetallesDTO(String region, String nivelAcceso) {
        this.region = region;
        this.nivelAcceso = nivelAcceso;
        this.activo = true;
        this.sesionesActivas = 0;
    }

    // Constructor con usuario
    public AdminDetallesDTO(Long usuarioId, String region, String nivelAcceso) {
        this.usuarioId = usuarioId;
        this.region = region;
        this.nivelAcceso = nivelAcceso;
        this.activo = true;
        this.sesionesActivas = 0;
    }

    // Métodos de validación
    public boolean isValid() {
        return region != null && !region.trim().isEmpty() &&
                nivelAcceso != null && !nivelAcceso.trim().isEmpty();
    }

    public boolean esSuperAdmin() {
        return "SUPER".equals(nivelAcceso) || "TOTAL".equals(nivelAcceso);
    }

    public boolean esAdminRegional() {
        return "REGIONAL".equals(nivelAcceso);
    }

    public boolean tieneAccesoTotal() {
        return esSuperAdmin() || "ADMIN_TOTAL".equals(nivelAcceso);
    }

    // Método para crear desde entidad
    public static AdminDetallesDTO fromEntity(com.digital.mecommerces.model.AdminDetalles adminDetalles) {
        if (adminDetalles == null) return null;

        AdminDetallesDTO dto = new AdminDetallesDTO();
        dto.setUsuarioId(adminDetalles.getUsuarioId());
        dto.setRegion(adminDetalles.getRegion());
        dto.setNivelAcceso(adminDetalles.getNivelAcceso());
        dto.setConfiguraciones(adminDetalles.getConfiguraciones());
        dto.setUltimaAccion(adminDetalles.getUltimaAccion());
        dto.setUltimoLogin(adminDetalles.getUltimoLogin());
        dto.setUltimaActividad(adminDetalles.getUltimaActividad());
        dto.setIpAcceso(adminDetalles.getIpAcceso());
        dto.setSesionesActivas(adminDetalles.getSesionesActivas());
        dto.setActivo(adminDetalles.getActivo());

        if (adminDetalles.getUsuario() != null) {
            dto.setUsuarioNombre(adminDetalles.getUsuario().getUsuarioNombre());
            dto.setUsuarioEmail(adminDetalles.getUsuario().getEmail());
        }

        return dto;
    }

    // Método para crear configuraciones como Map
    public Map<String, Object> getConfiguracionesAsMap() {
        if (configuraciones == null || configuraciones.trim().isEmpty()) {
            return Map.of();
        }

        try {
            // Implementar parsing de configuraciones JSON si es necesario
            return Map.of("raw", configuraciones);
        } catch (Exception e) {
            return Map.of("error", "Configuraciones inválidas");
        }
    }

    // Método para establecer configuraciones desde Map
    public void setConfiguracionesFromMap(Map<String, Object> configMap) {
        if (configMap == null || configMap.isEmpty()) {
            this.configuraciones = null;
            return;
        }

        try {
            // Implementar serialización a JSON si es necesario
            this.configuraciones = configMap.toString();
        } catch (Exception e) {
            this.configuraciones = "{}";
        }
    }

    @Override
    public String toString() {
        return "AdminDetallesDTO{" +
                "usuarioId=" + usuarioId +
                ", region='" + region + '\'' +
                ", nivelAcceso='" + nivelAcceso + '\'' +
                ", usuarioEmail='" + usuarioEmail + '\'' +
                ", activo=" + activo +
                '}';
    }
}
