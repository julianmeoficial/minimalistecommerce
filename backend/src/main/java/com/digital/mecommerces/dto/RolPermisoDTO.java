package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para gestión de relaciones Rol-Permiso
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolPermisoDTO {

    @JsonProperty("rolId")
    @NotNull(message = "El ID del rol es obligatorio")
    private Long rolId;

    @JsonProperty("permisoId")
    @NotNull(message = "El ID del permiso es obligatorio")
    private Long permisoId;

    @JsonProperty("rolNombre")
    private String rolNombre;

    @JsonProperty("rolDescripcion")
    private String rolDescripcion;

    @JsonProperty("permisoCodigo")
    private String permisoCodigo;

    @JsonProperty("permisoDescripcion")
    private String permisoDescripcion;

    @JsonProperty("permisoNivel")
    private Integer permisoNivel;

    @JsonProperty("permisoCategoria")
    private String permisoCategoria;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("createdBy")
    private String createdBy;

    // Campos calculados
    @JsonProperty("esRelacionDelSistema")
    private Boolean esRelacionDelSistema;

    @JsonProperty("esPermisoAdministrativo")
    private Boolean esPermisoAdministrativo;

    @JsonProperty("relacionValida")
    private Boolean relacionValida;

    @JsonProperty("descripcionCompleta")
    private String descripcionCompleta;

    // Constructor básico para creación
    public RolPermisoDTO(Long rolId, Long permisoId) {
        this.rolId = rolId;
        this.permisoId = permisoId;
        this.createdBy = "SYSTEM";
        this.createdAt = LocalDateTime.now();
    }

    // Constructor con nombres
    public RolPermisoDTO(Long rolId, Long permisoId, String rolNombre, String permisoCodigo) {
        this.rolId = rolId;
        this.permisoId = permisoId;
        this.rolNombre = rolNombre;
        this.permisoCodigo = permisoCodigo;
        this.createdBy = "SYSTEM";
        this.createdAt = LocalDateTime.now();
    }

    // Constructor completo
    public RolPermisoDTO(Long rolId, Long permisoId, String rolNombre, String permisoCodigo,
                         String permisoDescripcion, Integer permisoNivel, String permisoCategoria) {
        this.rolId = rolId;
        this.permisoId = permisoId;
        this.rolNombre = rolNombre;
        this.permisoCodigo = permisoCodigo;
        this.permisoDescripcion = permisoDescripcion;
        this.permisoNivel = permisoNivel;
        this.permisoCategoria = permisoCategoria;
        this.createdBy = "SYSTEM";
        this.createdAt = LocalDateTime.now();
        calcularCamposDerivados();
    }

    // Métodos de validación
    public boolean isValid() {
        return rolId != null && rolId > 0 &&
                permisoId != null && permisoId > 0;
    }

    public boolean tieneInformacionCompleta() {
        return isValid() &&
                rolNombre != null && !rolNombre.trim().isEmpty() &&
                permisoCodigo != null && !permisoCodigo.trim().isEmpty();
    }

    // Métodos de negocio
    public void calcularCamposDerivados() {
        // Verificar si es relación del sistema
        this.esRelacionDelSistema = verificarSiEsRelacionDelSistema();

        // Verificar si es permiso administrativo
        this.esPermisoAdministrativo = permisoNivel != null && permisoNivel <= 2;

        // Verificar si la relación es válida
        this.relacionValida = verificarCompatibilidadRolPermiso();

        // Generar descripción completa
        this.descripcionCompleta = generarDescripcionCompleta();
    }

    private Boolean verificarSiEsRelacionDelSistema() {
        if (rolNombre == null || permisoCodigo == null) return false;

        // Relaciones críticas del sistema
        return switch (rolNombre.toUpperCase()) {
            case "ADMINISTRADOR" -> "ADMIN_TOTAL".equals(permisoCodigo) ||
                    "GESTIONAR_USUARIOS".equals(permisoCodigo) ||
                    "GESTIONAR_CATEGORIAS".equals(permisoCodigo);
            case "VENDEDOR" -> "VENDER_PRODUCTOS".equals(permisoCodigo) ||
                    "GESTIONAR_CATEGORIAS".equals(permisoCodigo);
            case "COMPRADOR" -> "COMPRAR_PRODUCTOS".equals(permisoCodigo);
            default -> false;
        };
    }

    private Boolean verificarCompatibilidadRolPermiso() {
        if (rolNombre == null || permisoCodigo == null) return false;

        return switch (rolNombre.toUpperCase()) {
            case "ADMINISTRADOR" -> true; // Admin puede tener cualquier permiso
            case "VENDEDOR" -> esPermisoCompatibleConVendedor();
            case "COMPRADOR" -> esPermisoCompatibleConComprador();
            default -> !esPermisoDelSistema(); // Roles personalizados no pueden tener permisos del sistema
        };
    }

    private boolean esPermisoCompatibleConVendedor() {
        if (permisoCodigo == null) return false;

        return "VENDER_PRODUCTOS".equals(permisoCodigo) ||
                "GESTIONAR_CATEGORIAS".equals(permisoCodigo) ||
                (permisoCategoria != null &&
                        ("VENTAS".equals(permisoCategoria) || "GESTION".equals(permisoCategoria)));
    }

    private boolean esPermisoCompatibleConComprador() {
        if (permisoCodigo == null) return false;

        return "COMPRAR_PRODUCTOS".equals(permisoCodigo) ||
                (permisoCategoria != null && "COMPRAS".equals(permisoCategoria));
    }

    private boolean esPermisoDelSistema() {
        if (permisoCodigo == null) return false;

        String[] permisosDelSistema = {
                "ADMIN_TOTAL", "VENDER_PRODUCTOS", "COMPRAR_PRODUCTOS",
                "GESTIONAR_USUARIOS", "GESTIONAR_CATEGORIAS"
        };

        for (String permiso : permisosDelSistema) {
            if (permiso.equals(permisoCodigo)) {
                return true;
            }
        }
        return false;
    }

    private String generarDescripcionCompleta() {
        if (rolNombre == null || permisoCodigo == null) {
            return "Relación rol-permiso";
        }

        String descripcionRol = switch (rolNombre.toUpperCase()) {
            case "ADMINISTRADOR" -> "Administrador";
            case "VENDEDOR" -> "Vendedor";
            case "COMPRADOR" -> "Comprador";
            default -> rolNombre;
        };

        String descripcionPermiso = permisoDescripcion != null ?
                permisoDescripcion : permisoCodigo;

        return descripcionRol + " tiene permiso: " + descripcionPermiso;
    }

    // Métodos de análisis
    public boolean esAsignacionCritica() {
        return Boolean.TRUE.equals(esRelacionDelSistema) &&
                Boolean.TRUE.equals(esPermisoAdministrativo);
    }

    public boolean esAsignacionBasica() {
        return permisoNivel != null && permisoNivel >= 4;
    }

    public boolean requiereRevision() {
        return !Boolean.TRUE.equals(relacionValida) ||
                (!Boolean.TRUE.equals(esRelacionDelSistema) && "ADMINISTRADOR".equals(rolNombre));
    }

    public String obtenerNivelDescriptivo() {
        if (permisoNivel == null) return "No definido";

        return switch (permisoNivel) {
            case 1 -> "Crítico";
            case 2 -> "Alto";
            case 3 -> "Medio";
            case 4 -> "Básico";
            default -> "Personalizado";
        };
    }

    public String obtenerCategoriaDescriptiva() {
        if (permisoCategoria == null) return "General";

        return switch (permisoCategoria.toUpperCase()) {
            case "ADMINISTRACION" -> "Administración";
            case "VENTAS" -> "Ventas";
            case "COMPRAS" -> "Compras";
            case "GESTION" -> "Gestión";
            default -> permisoCategoria;
        };
    }

    // Método para crear desde entidad
    public static RolPermisoDTO fromEntity(com.digital.mecommerces.model.RolPermiso rolPermiso) {
        if (rolPermiso == null) return null;

        RolPermisoDTO dto = new RolPermisoDTO();
        dto.setRolId(rolPermiso.getRolId());
        dto.setPermisoId(rolPermiso.getPermisoId());
        dto.setCreatedAt(rolPermiso.getCreatedat());
        dto.setCreatedBy(rolPermiso.getCreatedby());

        if (rolPermiso.getRol() != null) {
            dto.setRolNombre(rolPermiso.getRol().getNombre());
            dto.setRolDescripcion(rolPermiso.getRol().getDescripcion());
        }

        if (rolPermiso.getPermiso() != null) {
            dto.setPermisoCodigo(rolPermiso.getPermiso().getCodigo());
            dto.setPermisoDescripcion(rolPermiso.getPermiso().getDescripcion());
            dto.setPermisoNivel(rolPermiso.getPermiso().getNivel());
            dto.setPermisoCategoria(rolPermiso.getPermiso().getCategoria());
        }

        // Calcular campos derivados
        dto.calcularCamposDerivados();

        return dto;
    }

    // Método para crear versión simplificada
    public RolPermisoDTO toSimple() {
        RolPermisoDTO simpleDto = new RolPermisoDTO();
        simpleDto.setRolId(this.rolId);
        simpleDto.setPermisoId(this.permisoId);
        simpleDto.setRolNombre(this.rolNombre);
        simpleDto.setPermisoCodigo(this.permisoCodigo);
        simpleDto.setPermisoNivel(this.permisoNivel);
        simpleDto.setPermisoCategoria(this.permisoCategoria);
        simpleDto.setEsRelacionDelSistema(this.esRelacionDelSistema);
        simpleDto.setRelacionValida(this.relacionValida);
        return simpleDto;
    }

    // Método para crear versión para auditoría
    public RolPermisoDTO toAudit() {
        RolPermisoDTO auditDto = new RolPermisoDTO();
        auditDto.setRolId(this.rolId);
        auditDto.setPermisoId(this.permisoId);
        auditDto.setRolNombre(this.rolNombre);
        auditDto.setPermisoCodigo(this.permisoCodigo);
        auditDto.setPermisoDescripcion(this.permisoDescripcion);
        auditDto.setCreatedAt(this.createdAt);
        auditDto.setCreatedBy(this.createdBy);
        auditDto.setEsRelacionDelSistema(this.esRelacionDelSistema);
        auditDto.setDescripcionCompleta(this.descripcionCompleta);
        return auditDto;
    }

    // Métodos para ordenamiento
    public static java.util.Comparator<RolPermisoDTO> porRolYNivel() {
        return java.util.Comparator
                .comparing((RolPermisoDTO rp) -> rp.getRolNombre() != null ? rp.getRolNombre() : "")
                .thenComparing(rp -> rp.getPermisoNivel() != null ? rp.getPermisoNivel() : 999);
    }

    public static java.util.Comparator<RolPermisoDTO> porImportancia() {
        return java.util.Comparator
                .comparing((RolPermisoDTO rp) -> !Boolean.TRUE.equals(rp.getEsRelacionDelSistema()))
                .thenComparing(rp -> rp.getPermisoNivel() != null ? rp.getPermisoNivel() : 999)
                .thenComparing(rp -> rp.getPermisoCodigo() != null ? rp.getPermisoCodigo() : "");
    }

    @Override
    public String toString() {
        return "RolPermisoDTO{" +
                "rolId=" + rolId +
                ", permisoId=" + permisoId +
                ", rolNombre='" + rolNombre + '\'' +
                ", permisoCodigo='" + permisoCodigo + '\'' +
                ", permisoNivel=" + permisoNivel +
                ", esRelacionDelSistema=" + esRelacionDelSistema +
                ", relacionValida=" + relacionValida +
                '}';
    }
}
