package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para gestión de usuarios del sistema
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    @JsonProperty("usuarioId")
    private Long usuarioId;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @JsonProperty("usuarioNombre")
    private String usuarioNombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email es inválido")
    @JsonProperty("email")
    private String email;

    // Contraseña opcional para actualizaciones
    @JsonProperty("password")
    private String password;

    @NotNull(message = "El rol de usuario es obligatorio")
    @JsonProperty("rolId")
    private Long rolId;

    @JsonProperty("rolNombre")
    private String rolNombre;

    @JsonProperty("activo")
    private Boolean activo;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @JsonProperty("ultimoLogin")
    private LocalDateTime ultimoLogin;

    // Campos adicionales para información del rol
    @JsonProperty("permisos")
    private java.util.List<String> permisos;

    @JsonProperty("tieneDetalles")
    private Boolean tieneDetalles;

    // Constructor básico para creación
    public UsuarioDTO(String usuarioNombre, String email, Long rolId) {
        this.usuarioNombre = usuarioNombre;
        this.email = email;
        this.rolId = rolId;
        this.activo = true;
    }

    // Constructor completo para listado
    public UsuarioDTO(Long usuarioId, String usuarioNombre, String email,
                      Long rolId, String rolNombre, Boolean activo) {
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.email = email;
        this.rolId = rolId;
        this.rolNombre = rolNombre;
        this.activo = activo;
    }

    // Métodos de utilidad
    public boolean isAdministrador() {
        return "ADMINISTRADOR".equals(rolNombre) ||
                (rolId != null && rolId.equals(1L));
    }

    public boolean isVendedor() {
        return "VENDEDOR".equals(rolNombre) ||
                (rolId != null && rolId.equals(3L));
    }

    public boolean isComprador() {
        return "COMPRADOR".equals(rolNombre) ||
                (rolId != null && rolId.equals(2L));
    }

    public boolean isValid() {
        return usuarioNombre != null && !usuarioNombre.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() && email.contains("@") &&
                rolId != null && rolId > 0;
    }

    public boolean tienePermiso(String permiso) {
        return permisos != null && permisos.contains(permiso);
    }

    // Método para crear DTO desde entidad
    public static UsuarioDTO fromEntity(com.digital.mecommerces.model.Usuario usuario) {
        if (usuario == null) return null;

        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsuarioId(usuario.getUsuarioId());
        dto.setUsuarioNombre(usuario.getUsuarioNombre());
        dto.setEmail(usuario.getEmail());
        dto.setActivo(usuario.getActivo());
        dto.setCreatedAt(usuario.getCreatedAt());
        dto.setUpdatedAt(usuario.getUpdatedAt());
        dto.setUltimoLogin(usuario.getUltimoLogin());

        if (usuario.getRol() != null) {
            dto.setRolId(usuario.getRol().getRolId());
            dto.setRolNombre(usuario.getRol().getNombre());
        }

        return dto;
    }

    // Método para ocultar información sensible
    public UsuarioDTO toPublic() {
        UsuarioDTO publicDto = new UsuarioDTO();
        publicDto.setUsuarioId(this.usuarioId);
        publicDto.setUsuarioNombre(this.usuarioNombre);
        publicDto.setEmail(this.email);
        publicDto.setRolNombre(this.rolNombre);
        publicDto.setActivo(this.activo);
        // No incluir fechas internas, permisos, etc.
        return publicDto;
    }

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "usuarioId=" + usuarioId +
                ", usuarioNombre='" + usuarioNombre + '\'' +
                ", email='" + email + '\'' +
                ", rolNombre='" + rolNombre + '\'' +
                ", activo=" + activo +
                '}';
    }
}
