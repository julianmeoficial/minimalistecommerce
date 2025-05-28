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
 * DTO para gestión de permisos del sistema
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermisoDTO {

    @JsonProperty("permisoId")
    private Long permisoId;

    @NotBlank(message = "El código del permiso es obligatorio")
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    @JsonProperty("codigo")
    private String codigo;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("nivel")
    private Integer nivel = 999;

    @JsonProperty("categoria")
    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String categoria = "GENERAL";

    @JsonProperty("permisopadreId")
    private Long permisopadreId;

    @JsonProperty("permisoPadreNombre")
    private String permisoPadreNombre;

    @JsonProperty("activo")
    private Boolean activo = true;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    // Campos calculados
    @JsonProperty("esDelSistema")
    private Boolean esDelSistema;

    @JsonProperty("esAdministrativo")
    private Boolean esAdministrativo;

    @JsonProperty("tieneSubpermisos")
    private Boolean tieneSubpermisos;

    @JsonProperty("rolesAsignados")
    private List<String> rolesAsignados;

    @JsonProperty("numeroRoles")
    private Integer numeroRoles;

    // Constructor básico para creación
    public PermisoDTO(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.nivel = 999;
        this.categoria = "GENERAL";
        this.activo = true;
    }

    // Constructor con nivel
    public PermisoDTO(String codigo, String descripcion, Integer nivel) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.nivel = nivel != null ? nivel : 999;
        this.categoria = "GENERAL";
        this.activo = true;
    }

    // Constructor completo
    public PermisoDTO(String codigo, String descripcion, Integer nivel, String categoria) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.nivel = nivel != null ? nivel : 999;
        this.categoria = categoria != null ? categoria : "GENERAL";
        this.activo = true;
    }

    // Métodos de validación
    public boolean isValid() {
        return codigo != null && !codigo.trim().isEmpty() &&
                nivel != null && nivel >= 1;
    }

    public boolean esPermisoValido() {
        return isValid() &&
                (categoria != null && !categoria.trim().isEmpty());
    }

    // Métodos de negocio
    public void calcularCamposDerivados() {
        // Verificar si es permiso del sistema
        this.esDelSistema = verificarSiEsDelSistema();

        // Verificar si es administrativo (nivel <= 2)
        this.esAdministrativo = nivel != null && nivel <= 2;

        // Inicializar número de roles si no está establecido
        if (numeroRoles == null) {
            numeroRoles = rolesAsignados != null ? rolesAsignados.size() : 0;
        }
    }

    private Boolean verificarSiEsDelSistema() {
        if (codigo == null) return false;

        // Permisos críticos del sistema
        String[] permisosDelSistema = {
                "ADMIN_TOTAL", "VENDER_PRODUCTOS", "COMPRAR_PRODUCTOS",
                "GESTIONAR_USUARIOS", "GESTIONAR_CATEGORIAS"
        };

        for (String permisoSistema : permisosDelSistema) {
            if (permisoSistema.equals(codigo.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean esPermisoRaiz() {
        return permisopadreId == null;
    }

    public boolean esSubpermiso() {
        return permisopadreId != null && permisopadreId > 0;
    }

    public boolean esPermisoAdministrativo() {
        return Boolean.TRUE.equals(esAdministrativo);
    }

    public boolean esPermisoBasico() {
        return nivel != null && nivel >= 4;
    }

    public boolean esPermisoCritico() {
        return Boolean.TRUE.equals(esDelSistema) && nivel != null && nivel <= 1;
    }

    public boolean estaAsignadoARoles() {
        return numeroRoles != null && numeroRoles > 0;
    }

    public boolean puedeSerEliminado() {
        return !Boolean.TRUE.equals(esDelSistema) &&
                !estaAsignadoARoles() &&
                !Boolean.TRUE.equals(tieneSubpermisos);
    }

    // Métodos para categorías específicas
    public boolean esCategoriaAdministracion() {
        return "ADMINISTRACION".equals(categoria);
    }

    public boolean esCategoriaVentas() {
        return "VENTAS".equals(categoria);
    }

    public boolean esCategoriaCompras() {
        return "COMPRAS".equals(categoria);
    }

    public boolean esCategoriaGestion() {
        return "GESTION".equals(categoria);
    }

    public boolean esCategoriaGeneral() {
        return "GENERAL".equals(categoria);
    }

    // Métodos para descripción y presentación
    public String obtenerDescripcionCompleta() {
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            return descripcion;
        }

        // Generar descripción basada en el código
        return switch (codigo != null ? codigo.toUpperCase() : "") {
            case "ADMIN_TOTAL" -> "Acceso total de administrador al sistema";
            case "VENDER_PRODUCTOS" -> "Crear, editar y gestionar productos para venta";
            case "COMPRAR_PRODUCTOS" -> "Realizar compras y gestionar órdenes";
            case "GESTIONAR_USUARIOS" -> "Gestionar usuarios del sistema";
            case "GESTIONAR_CATEGORIAS" -> "Gestionar categorías de productos";
            default -> "Permiso personalizado: " + codigo;
        };
    }

    public String obtenerCategoriaDescriptiva() {
        return switch (categoria != null ? categoria.toUpperCase() : "GENERAL") {
            case "ADMINISTRACION" -> "Administración del sistema";
            case "VENTAS" -> "Gestión de ventas";
            case "COMPRAS" -> "Gestión de compras";
            case "GESTION" -> "Gestión general";
            default -> "Permisos generales";
        };
    }

    public String obtenerNivelDescriptivo() {
        if (nivel == null) return "No definido";

        return switch (nivel) {
            case 1 -> "Crítico (Nivel 1)";
            case 2 -> "Alto (Nivel 2)";
            case 3 -> "Medio (Nivel 3)";
            case 4 -> "Básico (Nivel 4)";
            default -> nivel >= 5 ? "Personalizado (Nivel " + nivel + ")" : "Nivel " + nivel;
        };
    }

    // Métodos para jerarquía
    public String obtenerRutaJerarquica() {
        if (permisoPadreNombre != null && !permisoPadreNombre.trim().isEmpty()) {
            return permisoPadreNombre + " > " + codigo;
        }
        return codigo;
    }

    public boolean perteneceAJerarquia() {
        return esPermisoRaiz() || esSubpermiso();
    }

    // Método para crear desde entidad
    public static PermisoDTO fromEntity(com.digital.mecommerces.model.Permiso permiso) {
        if (permiso == null) return null;

        PermisoDTO dto = new PermisoDTO();
        dto.setPermisoId(permiso.getPermisoId());
        dto.setCodigo(permiso.getCodigo());
        dto.setDescripcion(permiso.getDescripcion());
        dto.setNivel(permiso.getNivel());
        dto.setCategoria(permiso.getCategoria());
        dto.setActivo(permiso.getActivo());
        // The Permiso entity doesn't have createdAt and updatedAt fields
        dto.setCreatedAt(null);
        dto.setUpdatedAt(null);

        if (permiso.getPermisoPadre() != null) {
            dto.setPermisopadreId(permiso.getPermisoPadre().getPermisoId());
            dto.setPermisoPadreNombre(permiso.getPermisoPadre().getCodigo());
        }

        // Calcular campos derivados
        dto.calcularCamposDerivados();

        return dto;
    }

    // Método para crear versión simplificada
    public PermisoDTO toSimple() {
        PermisoDTO simpleDto = new PermisoDTO();
        simpleDto.setPermisoId(this.permisoId);
        simpleDto.setCodigo(this.codigo);
        simpleDto.setDescripcion(this.descripcion);
        simpleDto.setNivel(this.nivel);
        simpleDto.setCategoria(this.categoria);
        simpleDto.setActivo(this.activo);
        simpleDto.setEsDelSistema(this.esDelSistema);
        simpleDto.setEsAdministrativo(this.esAdministrativo);
        simpleDto.setNumeroRoles(this.numeroRoles);
        return simpleDto;
    }

    // Método para crear versión pública (sin información administrativa)
    public PermisoDTO toPublic() {
        PermisoDTO publicDto = new PermisoDTO();
        publicDto.setCodigo(this.codigo);
        publicDto.setDescripcion(this.obtenerDescripcionCompleta());
        publicDto.setCategoria(this.categoria);
        publicDto.setEsDelSistema(this.esDelSistema);
        // No incluir información administrativa como IDs, fechas, etc.
        return publicDto;
    }

    // Métodos para análisis y reportes
    public boolean esCompatibleConRol(String rolNombre) {
        if (rolNombre == null) return false;

        return switch (rolNombre.toUpperCase()) {
            case "ADMINISTRADOR" -> true; // Admin puede tener cualquier permiso
            case "VENDEDOR" -> "VENDER_PRODUCTOS".equals(codigo) ||
                    "GESTIONAR_CATEGORIAS".equals(codigo) ||
                    esCategoriaVentas() || esCategoriaGestion();
            case "COMPRADOR" -> "COMPRAR_PRODUCTOS".equals(codigo) ||
                    esCategoriaCompras();
            default -> !Boolean.TRUE.equals(esDelSistema); // Roles personalizados no pueden tener permisos del sistema
        };
    }

    public int compararPorImportancia(PermisoDTO otro) {
        if (otro == null) return -1;

        // Primero por si es del sistema
        int comparacionSistema = Boolean.compare(
                !Boolean.TRUE.equals(this.esDelSistema),
                !Boolean.TRUE.equals(otro.esDelSistema)
        );
        if (comparacionSistema != 0) return comparacionSistema;

        // Luego por nivel (menor nivel = mayor importancia)
        int thisNivel = this.nivel != null ? this.nivel : 999;
        int otroNivel = otro.nivel != null ? otro.nivel : 999;
        int comparacionNivel = Integer.compare(thisNivel, otroNivel);
        if (comparacionNivel != 0) return comparacionNivel;

        // Finalmente por código alfabéticamente
        return this.codigo.compareTo(otro.codigo);
    }

    @Override
    public String toString() {
        return "PermisoDTO{" +
                "permisoId=" + permisoId +
                ", codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", nivel=" + nivel +
                ", categoria='" + categoria + '\'' +
                ", esDelSistema=" + esDelSistema +
                ", esAdministrativo=" + esAdministrativo +
                ", activo=" + activo +
                '}';
    }
}
