package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para gestión de roles de usuario
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolUsuarioDTO {

    @JsonProperty("rolId")
    private Long rolId;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @JsonProperty("nombre")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    // Campos calculados
    @JsonProperty("esDelSistema")
    private Boolean esDelSistema;

    @JsonProperty("numeroUsuarios")
    private Integer numeroUsuarios = 0;

    @JsonProperty("numeroPermisos")
    private Integer numeroPermisos = 0;

    @JsonProperty("tieneUsuarios")
    private Boolean tieneUsuarios;

    @JsonProperty("tienePermisos")
    private Boolean tienePermisos;

    @JsonProperty("puedeSerEliminado")
    private Boolean puedeSerEliminado;

    @JsonProperty("nivelImportancia")
    private Integer nivelImportancia;

    // Información de permisos asociados
    @JsonProperty("permisos")
    private List<String> permisos;

    @JsonProperty("permisosDelSistema")
    private List<String> permisosDelSistema;

    @JsonProperty("permisosPersonalizados")
    private List<String> permisosPersonalizados;

    // Información de usuarios asociados
    @JsonProperty("usuariosActivos")
    private Integer usuariosActivos = 0;

    @JsonProperty("usuariosInactivos")
    private Integer usuariosInactivos = 0;

    // Constructor básico para creación
    public RolUsuarioDTO(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.numeroUsuarios = 0;
        this.numeroPermisos = 0;
    }

    // Constructor completo para listado
    public RolUsuarioDTO(Long rolId, String nombre, String descripcion,
                         Integer numeroUsuarios, Integer numeroPermisos) {
        this.rolId = rolId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.numeroUsuarios = numeroUsuarios != null ? numeroUsuarios : 0;
        this.numeroPermisos = numeroPermisos != null ? numeroPermisos : 0;
        calcularCamposDerivados();
    }

    // Métodos de validación
    public boolean isValid() {
        return nombre != null && !nombre.trim().isEmpty();
    }

    public boolean tieneInformacionCompleta() {
        return isValid() &&
                descripcion != null && !descripcion.trim().isEmpty();
    }

    // Métodos de negocio
    public void calcularCamposDerivados() {
        // Verificar si es rol del sistema
        this.esDelSistema = verificarSiEsDelSistema();

        // Calcular si tiene usuarios y permisos
        this.tieneUsuarios = numeroUsuarios != null && numeroUsuarios > 0;
        this.tienePermisos = numeroPermisos != null && numeroPermisos > 0;

        // Calcular si puede ser eliminado
        this.puedeSerEliminado = !Boolean.TRUE.equals(esDelSistema) &&
                !Boolean.TRUE.equals(tieneUsuarios);

        // Calcular nivel de importancia
        this.nivelImportancia = calcularNivelImportancia();
    }

    private Boolean verificarSiEsDelSistema() {
        if (nombre == null) return false;

        // Roles críticos del sistema
        String[] rolesDelSistema = {"ADMINISTRADOR", "VENDEDOR", "COMPRADOR"};

        String nombreUpper = nombre.toUpperCase();
        for (String rolSistema : rolesDelSistema) {
            if (rolSistema.equals(nombreUpper)) {
                return true;
            }
        }
        return false;
    }

    private Integer calcularNivelImportancia() {
        if (Boolean.TRUE.equals(esDelSistema)) {
            return switch (nombre.toUpperCase()) {
                case "ADMINISTRADOR" -> 1;
                case "VENDEDOR" -> 2;
                case "COMPRADOR" -> 3;
                default -> 4;
            };
        }
        return 5; // Roles personalizados tienen menor importancia
    }

    // Métodos de análisis de rol
    public boolean esRolAdministrador() {
        return "ADMINISTRADOR".equals(nombre);
    }

    public boolean esRolVendedor() {
        return "VENDEDOR".equals(nombre);
    }

    public boolean esRolComprador() {
        return "COMPRADOR".equals(nombre);
    }

    public boolean esRolPersonalizado() {
        return !Boolean.TRUE.equals(esDelSistema);
    }

    public boolean esRolCritico() {
        return Boolean.TRUE.equals(esDelSistema) && nivelImportancia != null && nivelImportancia <= 2;
    }

    public boolean necesitaConfiguracion() {
        return Boolean.TRUE.equals(esDelSistema) &&
                (numeroPermisos == null || numeroPermisos == 0);
    }

    public boolean estaEnUso() {
        return Boolean.TRUE.equals(tieneUsuarios);
    }

    public boolean tienePermisosAdministrativos() {
        return permisosDelSistema != null &&
                permisosDelSistema.contains("ADMIN_TOTAL");
    }

    public boolean tienePermisosVenta() {
        return permisosDelSistema != null &&
                permisosDelSistema.contains("VENDER_PRODUCTOS");
    }

    public boolean tienePermisosCompra() {
        return permisosDelSistema != null &&
                permisosDelSistema.contains("COMPRAR_PRODUCTOS");
    }

    // Métodos para descripción y presentación
    public String obtenerDescripcionCompleta() {
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            return descripcion;
        }

        // Generar descripción basada en el nombre para roles del sistema
        return switch (nombre != null ? nombre.toUpperCase() : "") {
            case "ADMINISTRADOR" -> "Administrador del sistema con acceso total";
            case "VENDEDOR" -> "Vendedor con permisos para gestionar productos";
            case "COMPRADOR" -> "Comprador con permisos para realizar compras";
            default -> "Rol personalizado: " + nombre;
        };
    }

    public String obtenerNivelDescriptivo() {
        if (nivelImportancia == null) return "No definido";

        return switch (nivelImportancia) {
            case 1 -> "Crítico (Administrador)";
            case 2 -> "Alto (Vendedor)";
            case 3 -> "Medio (Comprador)";
            case 4 -> "Básico del sistema";
            default -> "Personalizado";
        };
    }

    public String obtenerEstadoRol() {
        if (Boolean.TRUE.equals(esDelSistema)) {
            if (Boolean.TRUE.equals(tienePermisos)) {
                return "CONFIGURADO";
            } else {
                return "PENDIENTE_CONFIGURACION";
            }
        } else {
            if (Boolean.TRUE.equals(tieneUsuarios)) {
                return "EN_USO";
            } else {
                return "DISPONIBLE";
            }
        }
    }

    // Métodos para estadísticas
    public double obtenerPorcentajeUsuariosActivos() {
        if (numeroUsuarios == null || numeroUsuarios == 0) return 0.0;

        int activos = usuariosActivos != null ? usuariosActivos : 0;
        return (activos * 100.0) / numeroUsuarios;
    }

    public boolean tieneMayoriaUsuariosActivos() {
        return obtenerPorcentajeUsuariosActivos() > 50.0;
    }

    public String obtenerResumenUso() {
        if (numeroUsuarios == null || numeroUsuarios == 0) {
            return "Sin usuarios asignados";
        }

        return String.format("%d usuarios (%d activos, %d inactivos)",
                numeroUsuarios,
                usuariosActivos != null ? usuariosActivos : 0,
                usuariosInactivos != null ? usuariosInactivos : 0);
    }

    // Método para crear desde entidad
    public static RolUsuarioDTO fromEntity(com.digital.mecommerces.model.RolUsuario rol) {
        if (rol == null) return null;

        RolUsuarioDTO dto = new RolUsuarioDTO();
        dto.setRolId(rol.getRolId());
        dto.setNombre(rol.getNombre());
        dto.setDescripcion(rol.getDescripcion());
        // Removed non-existent field access
        // dto.setCreatedAt(rol.getCreatedAt());
        // dto.setUpdatedAt(rol.getUpdatedAt());

        // Calcular campos derivados
        dto.calcularCamposDerivados();

        return dto;
    }

    // Método para crear versión simplificada
    public RolUsuarioDTO toSimple() {
        RolUsuarioDTO simpleDto = new RolUsuarioDTO();
        simpleDto.setRolId(this.rolId);
        simpleDto.setNombre(this.nombre);
        simpleDto.setDescripcion(this.descripcion);
        simpleDto.setEsDelSistema(this.esDelSistema);
        simpleDto.setNumeroUsuarios(this.numeroUsuarios);
        simpleDto.setNumeroPermisos(this.numeroPermisos);
        simpleDto.setNivelImportancia(this.nivelImportancia);
        return simpleDto;
    }

    // Método para crear versión pública (sin información administrativa)
    public RolUsuarioDTO toPublic() {
        RolUsuarioDTO publicDto = new RolUsuarioDTO();
        publicDto.setNombre(this.nombre);
        publicDto.setDescripcion(this.obtenerDescripcionCompleta());
        publicDto.setEsDelSistema(this.esDelSistema);
        // No incluir información administrativa como IDs, fechas, números de usuarios, etc.
        return publicDto;
    }

    // Método para crear versión con estadísticas completas
    public RolUsuarioDTO toCompleteStats() {
        RolUsuarioDTO statsDto = new RolUsuarioDTO();
        statsDto.setRolId(this.rolId);
        statsDto.setNombre(this.nombre);
        statsDto.setDescripcion(this.descripcion);
        statsDto.setEsDelSistema(this.esDelSistema);
        statsDto.setNumeroUsuarios(this.numeroUsuarios);
        statsDto.setNumeroPermisos(this.numeroPermisos);
        statsDto.setUsuariosActivos(this.usuariosActivos);
        statsDto.setUsuariosInactivos(this.usuariosInactivos);
        statsDto.setTieneUsuarios(this.tieneUsuarios);
        statsDto.setTienePermisos(this.tienePermisos);
        statsDto.setPuedeSerEliminado(this.puedeSerEliminado);
        statsDto.setNivelImportancia(this.nivelImportancia);
        statsDto.setPermisos(this.permisos);
        statsDto.setPermisosDelSistema(this.permisosDelSistema);
        statsDto.setPermisosPersonalizados(this.permisosPersonalizados);
        return statsDto;
    }

    // Métodos para ordenamiento
    public static java.util.Comparator<RolUsuarioDTO> porImportancia() {
        return java.util.Comparator
                .comparing((RolUsuarioDTO rol) -> rol.getNivelImportancia() != null ? rol.getNivelImportancia() : 999)
                .thenComparing(rol -> rol.getNombre() != null ? rol.getNombre() : "");
    }

    public static java.util.Comparator<RolUsuarioDTO> porNombre() {
        return java.util.Comparator.comparing(
                rol -> rol.getNombre() != null ? rol.getNombre() : "",
                java.util.Comparator.naturalOrder()
        );
    }

    public static java.util.Comparator<RolUsuarioDTO> porNumeroUsuarios() {
        return java.util.Comparator.comparing(
                rol -> rol.getNumeroUsuarios() != null ? rol.getNumeroUsuarios() : 0,
                java.util.Comparator.reverseOrder()
        );
    }

    @Override
    public String toString() {
        return "RolUsuarioDTO{" +
                "rolId=" + rolId +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", esDelSistema=" + esDelSistema +
                ", numeroUsuarios=" + numeroUsuarios +
                ", numeroPermisos=" + numeroPermisos +
                ", nivelImportancia=" + nivelImportancia +
                ", puedeSerEliminado=" + puedeSerEliminado +
                '}';
    }
}
