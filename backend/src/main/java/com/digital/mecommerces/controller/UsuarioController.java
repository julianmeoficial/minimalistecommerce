package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.UsuarioDTO;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.service.RolUsuarioService;
import com.digital.mecommerces.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador para gestión general de usuarios
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "APIs para gestión general de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolUsuarioService rolUsuarioService;

    public UsuarioController(UsuarioService usuarioService, RolUsuarioService rolUsuarioService) {
        this.usuarioService = usuarioService;
        this.rolUsuarioService = rolUsuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios con paginación", description = "Obtiene lista paginada de usuarios")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<Page<UsuarioDTO>> listarUsuarios(Pageable pageable) {
        log.info("👤 Obteniendo usuarios con paginación");

        Page<Usuario> usuarios = usuarioService.obtenerUsuariosPaginados(pageable);
        Page<UsuarioDTO> usuariosDTO = usuarios.map(UsuarioDTO::fromEntity);

        log.info("✅ {} usuarios encontrados", usuariosDTO.getTotalElements());
        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/todos")
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene lista completa sin paginación")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<UsuarioDTO>> listarTodosLosUsuarios() {
        log.info("👤 Obteniendo todos los usuarios");

        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(UsuarioDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable Long id) {
        log.info("👤 Obteniendo usuario por ID: {}", id);

        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        UsuarioDTO usuarioDTO = UsuarioDTO.fromEntity(usuario);

        return ResponseEntity.ok(usuarioDTO);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener usuario por email")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorEmail(@PathVariable String email) {
        log.info("👤 Obteniendo usuario por email: {}", email);

        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
        UsuarioDTO usuarioDTO = UsuarioDTO.fromEntity(usuario);

        return ResponseEntity.ok(usuarioDTO);
    }

    @GetMapping("/rol/{rolId}")
    @Operation(summary = "Obtener usuarios por rol")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuariosPorRol(@PathVariable Long rolId, Pageable pageable) {
        log.info("👤 Obteniendo usuarios por rol ID: {}", rolId);

        Page<Usuario> usuarios = usuarioService.obtenerUsuariosPorRol(rolId, pageable);
        Page<UsuarioDTO> usuariosDTO = usuarios.map(UsuarioDTO::fromEntity);

        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener usuarios activos")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuariosActivos(Pageable pageable) {
        log.info("👤 Obteniendo usuarios activos");

        Page<Usuario> usuarios = usuarioService.obtenerUsuariosActivos(pageable);
        Page<UsuarioDTO> usuariosDTO = usuarios.map(UsuarioDTO::fromEntity);

        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/inactivos")
    @Operation(summary = "Obtener usuarios inactivos")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Page<UsuarioDTO>> obtenerUsuariosInactivos(Pageable pageable) {
        log.info("👤 Obteniendo usuarios inactivos");

        Page<Usuario> usuarios = usuarioService.obtenerUsuariosInactivos(pageable);
        Page<UsuarioDTO> usuariosDTO = usuarios.map(UsuarioDTO::fromEntity);

        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar usuarios", description = "Busca usuarios por nombre o email")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<Page<UsuarioDTO>> buscarUsuarios(@RequestParam String q, Pageable pageable) {
        log.info("👤 Buscando usuarios con término: {}", q);

        Page<Usuario> usuarios = usuarioService.buscarUsuarios(q, pageable);
        Page<UsuarioDTO> usuariosDTO = usuarios.map(UsuarioDTO::fromEntity);

        return ResponseEntity.ok(usuariosDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@PathVariable Long id,
                                                        @Valid @RequestBody UsuarioDTO usuarioDTO) {
        log.info("👤 Actualizando usuario ID: {}", id);

        Usuario usuarioDetails = new Usuario();
        usuarioDetails.setUsuarioNombre(usuarioDTO.getUsuarioNombre());
        usuarioDetails.setEmail(usuarioDTO.getEmail());
        usuarioDetails.setActivo(usuarioDTO.getActivo());

        if (usuarioDTO.getRolId() != null) {
            RolUsuario rol = rolUsuarioService.obtenerRolPorId(usuarioDTO.getRolId());
            usuarioDetails.setRol(rol);
        }

        Usuario actualizado = usuarioService.actualizarUsuario(id, usuarioDetails);
        UsuarioDTO respuesta = UsuarioDTO.fromEntity(actualizado);

        log.info("✅ Usuario actualizado exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("👤 Eliminando usuario ID: {}", id);

        usuarioService.eliminarUsuario(id);

        log.info("✅ Usuario eliminado exitosamente");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activar")
    @Operation(summary = "Activar usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<Map<String, Object>> activarUsuario(@PathVariable Long id) {
        log.info("👤 Activando usuario ID: {}", id);

        usuarioService.activarUsuario(id);

        Map<String, Object> response = Map.of(
                "mensaje", "Usuario activado exitosamente",
                "usuarioId", id,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<Map<String, Object>> desactivarUsuario(@PathVariable Long id) {
        log.info("👤 Desactivando usuario ID: {}", id);

        usuarioService.desactivarUsuario(id);

        Map<String, Object> response = Map.of(
                "mensaje", "Usuario desactivado exitosamente",
                "usuarioId", id,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cambiar-rol")
    @Operation(summary = "Cambiar rol de usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<UsuarioDTO> cambiarRolUsuario(@PathVariable Long id,
                                                        @RequestBody Map<String, Long> request) {
        Long nuevoRolId = request.get("rolId");
        log.info("👤 Cambiando rol del usuario ID: {} al rol ID: {}", id, nuevoRolId);

        Usuario usuario = usuarioService.cambiarRolUsuario(id, nuevoRolId);
        UsuarioDTO respuesta = UsuarioDTO.fromEntity(usuario);

        log.info("✅ Rol de usuario cambiado exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/{id}/resetear-password")
    @Operation(summary = "Resetear contraseña de usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> resetearPassword(@PathVariable Long id) {
        log.info("👤 Reseteando contraseña del usuario ID: {}", id);

        String nuevaPassword = usuarioService.resetearPassword(id);

        Map<String, Object> response = Map.of(
                "mensaje", "Contraseña reseteada exitosamente",
                "usuarioId", id,
                "nuevaPassword", nuevaPassword,
                "timestamp", LocalDateTime.now()
        );

        log.info("✅ Contraseña reseteada exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/perfil")
    @Operation(summary = "Obtener perfil del usuario autenticado")
    public ResponseEntity<UsuarioDTO> obtenerMiPerfil() {
        String email = obtenerEmailUsuarioAutenticado();
        log.info("👤 Obteniendo perfil del usuario: {}", email);

        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
        UsuarioDTO usuarioDTO = UsuarioDTO.fromEntity(usuario);

        return ResponseEntity.ok(usuarioDTO);
    }

    @PutMapping("/perfil")
    @Operation(summary = "Actualizar perfil del usuario autenticado")
    public ResponseEntity<UsuarioDTO> actualizarMiPerfil(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        String email = obtenerEmailUsuarioAutenticado();
        log.info("👤 Actualizando perfil del usuario: {}", email);

        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);

        Usuario usuarioDetails = new Usuario();
        usuarioDetails.setUsuarioNombre(usuarioDTO.getUsuarioNombre());
        // No permitir cambio de email o rol en perfil propio

        Usuario actualizado = usuarioService.actualizarUsuario(usuario.getUsuarioId(), usuarioDetails);
        UsuarioDTO respuesta = UsuarioDTO.fromEntity(actualizado);

        log.info("✅ Perfil actualizado exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/cambiar-password")
    @Operation(summary = "Cambiar contraseña del usuario autenticado")
    public ResponseEntity<Map<String, Object>> cambiarPassword(@RequestBody Map<String, String> request) {
        String email = obtenerEmailUsuarioAutenticado();
        String passwordActual = request.get("passwordActual");
        String passwordNueva = request.get("passwordNueva");

        log.info("👤 Cambiando contraseña del usuario: {}", email);

        usuarioService.cambiarPassword(email, passwordActual, passwordNueva);

        Map<String, Object> response = Map.of(
                "mensaje", "Contraseña cambiada exitosamente",
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de usuarios")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.info("👤 Obteniendo estadísticas de usuarios");

        Map<String, Object> estadisticas = Map.of(
                "total", usuarioService.contarUsuarios(),
                "activos", usuarioService.contarUsuariosActivos(),
                "inactivos", usuarioService.contarUsuariosInactivos(),
                "administradores", usuarioService.contarAdministradores(),
                "vendedores", usuarioService.contarVendedores(),
                "compradores", usuarioService.contarCompradores(),
                "recientes", usuarioService.contarUsuariosRecientes(),
                "conDetalles", usuarioService.contarUsuariosConDetalles(),
                "porRol", usuarioService.obtenerDistribucionPorRol(),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/recientes")
    @Operation(summary = "Obtener usuarios registrados recientemente")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<List<UsuarioDTO>> obtenerUsuariosRecientes(@RequestParam(defaultValue = "10") int limite) {
        log.info("👤 Obteniendo {} usuarios recientes", limite);

        List<Usuario> usuarios = usuarioService.obtenerUsuariosRecientes(limite);
        List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(UsuarioDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/ultimos-logins")
    @Operation(summary = "Obtener usuarios con últimos logins")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<UsuarioDTO>> obtenerUltimosLogins(@RequestParam(defaultValue = "10") int limite) {
        log.info("👤 Obteniendo últimos {} logins", limite);

        List<Usuario> usuarios = usuarioService.obtenerUltimosLogins(limite);
        List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(UsuarioDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(usuariosDTO);
    }

    @PostMapping("/enviar-notificacion")
    @Operation(summary = "Enviar notificación a usuarios")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> enviarNotificacion(@RequestBody Map<String, Object> request) {
        String mensaje = (String) request.get("mensaje");
        List<Long> usuariosIds = (List<Long>) request.get("usuariosIds");

        log.info("👤 Enviando notificación a {} usuarios", usuariosIds.size());

        try {
            int notificacionesEnviadas = usuarioService.enviarNotificacion(usuariosIds, mensaje);

            Map<String, Object> response = Map.of(
                    "mensaje", "Notificaciones enviadas exitosamente",
                    "notificacionesEnviadas", notificacionesEnviadas,
                    "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error enviando notificaciones: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error enviando notificaciones: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    // Método auxiliar para obtener email del usuario autenticado
    private String obtenerEmailUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
