package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.RolUsuarioDTO;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.service.RolUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador para gestión de roles de usuario
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles de Usuario", description = "APIs para gestión de roles del sistema")
@Slf4j
public class RolUsuarioController {

    private final RolUsuarioService rolUsuarioService;

    public RolUsuarioController(RolUsuarioService rolUsuarioService) {
        this.rolUsuarioService = rolUsuarioService;
    }

    // === ENDPOINTS PÚBLICOS (para registro) ===

    @GetMapping("/publicos")
    @Operation(summary = "Obtener roles disponibles para registro", description = "Endpoint público para formularios de registro")
    public ResponseEntity<List<RolUsuarioDTO>> obtenerRolesPublicos() {
        log.info("👥 Obteniendo roles públicos para registro");

        List<RolUsuario> roles = rolUsuarioService.obtenerRolesParaRegistro();
        List<RolUsuarioDTO> rolesDTO = roles.stream()
                .map(RolUsuarioDTO::fromEntity)
                .map(RolUsuarioDTO::toPublic)
                .toList();

        return ResponseEntity.ok(rolesDTO);
    }

    // === ENDPOINTS ADMINISTRATIVOS ===

    @GetMapping
    @Operation(summary = "Listar todos los roles", description = "Obtiene lista completa de roles del sistema")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<RolUsuarioDTO>> listarRoles() {
        log.info("👥 Obteniendo lista de todos los roles");

        List<RolUsuario> roles = rolUsuarioService.obtenerRoles();
        List<RolUsuarioDTO> rolesDTO = roles.stream()
                .map(RolUsuarioDTO::fromEntity)
                .sorted(RolUsuarioDTO.porImportancia())
                .toList();

        log.info("✅ {} roles encontrados", rolesDTO.size());
        return ResponseEntity.ok(rolesDTO);
    }

    @GetMapping("/del-sistema")
    @Operation(summary = "Listar roles del sistema", description = "Obtiene roles críticos del sistema")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<RolUsuarioDTO>> listarRolesDelSistema() {
        log.info("👥 Obteniendo roles del sistema");

        List<RolUsuario> roles = rolUsuarioService.obtenerRolesDelSistema();
        List<RolUsuarioDTO> rolesDTO = roles.stream()
                .map(RolUsuarioDTO::fromEntity)
                .map(RolUsuarioDTO::toSimple)
                .toList();

        return ResponseEntity.ok(rolesDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por ID")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RolUsuarioDTO> obtenerRol(@PathVariable Long id) {
        log.info("👥 Obteniendo rol por ID: {}", id);

        RolUsuario rol = rolUsuarioService.obtenerRolPorId(id);
        RolUsuarioDTO rolDTO = RolUsuarioDTO.fromEntity(rol);

        return ResponseEntity.ok(rolDTO);
    }

    @GetMapping("/nombre/{nombre}")
    @Operation(summary = "Obtener rol por nombre")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RolUsuarioDTO> obtenerRolPorNombre(@PathVariable String nombre) {
        log.info("👥 Obteniendo rol por nombre: {}", nombre);

        RolUsuario rol = rolUsuarioService.obtenerRolPorNombre(nombre);
        RolUsuarioDTO rolDTO = RolUsuarioDTO.fromEntity(rol);

        return ResponseEntity.ok(rolDTO);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo rol")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RolUsuarioDTO> crearRol(@Valid @RequestBody RolUsuarioDTO rolDTO) {
        log.info("👥 Creando nuevo rol: {}", rolDTO.getNombre());

        RolUsuario rol = new RolUsuario();
        rol.setNombre(rolDTO.getNombre());
        rol.setDescripcion(rolDTO.getDescripcion());

        RolUsuario nuevoRol = rolUsuarioService.crearRol(rol);
        RolUsuarioDTO respuesta = RolUsuarioDTO.fromEntity(nuevoRol);

        log.info("✅ Rol creado con ID: {}", nuevoRol.getRolId());
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar rol")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RolUsuarioDTO> actualizarRol(@PathVariable Long id,
                                                       @Valid @RequestBody RolUsuarioDTO rolDTO) {
        log.info("👥 Actualizando rol ID: {}", id);

        RolUsuario rolDetails = new RolUsuario();
        rolDetails.setNombre(rolDTO.getNombre());
        rolDetails.setDescripcion(rolDTO.getDescripcion());

        RolUsuario rolActualizado = rolUsuarioService.actualizarRol(id, rolDetails);
        RolUsuarioDTO respuesta = RolUsuarioDTO.fromEntity(rolActualizado);

        log.info("✅ Rol actualizado exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar rol")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> eliminarRol(@PathVariable Long id) {
        log.info("👥 Eliminando rol ID: {}", id);

        rolUsuarioService.eliminarRol(id);

        log.info("✅ Rol eliminado exitosamente");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/usuarios")
    @Operation(summary = "Obtener usuarios con un rol específico")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> obtenerUsuariosDelRol(@PathVariable Long id) {
        log.info("👥 Obteniendo usuarios del rol ID: {}", id);

        Map<String, Object> usuarios = rolUsuarioService.obtenerUsuariosDelRol(id);
        usuarios.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de roles")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.info("👥 Obteniendo estadísticas de roles");

        Map<String, Object> estadisticas = Map.of(
                "totalRoles", rolUsuarioService.contarRoles(),
                "rolesDelSistema", rolUsuarioService.contarRolesDelSistema(),
                "rolesPersonalizados", rolUsuarioService.contarRolesPersonalizados(),
                "usuariosPorRol", rolUsuarioService.obtenerDistribucionUsuarios(),
                "rolMasUsado", rolUsuarioService.obtenerRolMasUsado(),
                "rolesVacios", rolUsuarioService.contarRolesSinUsuarios(),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/completos")
    @Operation(summary = "Obtener roles con estadísticas completas")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<RolUsuarioDTO>> obtenerRolesCompletos() {
        log.info("👥 Obteniendo roles con estadísticas completas");

        List<RolUsuario> roles = rolUsuarioService.obtenerRoles();
        List<RolUsuarioDTO> rolesCompletos = roles.stream()
                .map(RolUsuarioDTO::fromEntity)
                .map(this::enriquecerConEstadisticas)
                .map(RolUsuarioDTO::toCompleteStats)
                .sorted(RolUsuarioDTO.porImportancia())
                .toList();

        return ResponseEntity.ok(rolesCompletos);
    }

    @PostMapping("/inicializar-sistema")
    @Operation(summary = "Inicializar roles del sistema")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> inicializarRolesDelSistema() {
        log.info("👥 Inicializando roles del sistema");

        try {
            Map<String, Object> resultado = rolUsuarioService.inicializarRolesDelSistema();
            resultado.put("timestamp", LocalDateTime.now());

            log.info("✅ Roles del sistema inicializados");
            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            log.error("❌ Error inicializando roles: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error inicializando roles: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/validar-integridad")
    @Operation(summary = "Validar integridad de roles del sistema")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> validarIntegridad() {
        log.info("👥 Validando integridad de roles del sistema");

        Map<String, Object> resultado = rolUsuarioService.validarIntegridadDelSistema();
        resultado.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/jerarquia")
    @Operation(summary = "Obtener jerarquía de roles")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<RolUsuarioDTO>> obtenerJerarquiaRoles() {
        log.info("👥 Obteniendo jerarquía de roles");

        List<RolUsuario> roles = rolUsuarioService.obtenerRoles();
        List<RolUsuarioDTO> jerarquia = roles.stream()
                .map(RolUsuarioDTO::fromEntity)
                .sorted(RolUsuarioDTO.porImportancia())
                .toList();

        return ResponseEntity.ok(jerarquia);
    }

    @GetMapping("/configuracion")
    @Operation(summary = "Obtener configuración de roles del sistema")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> obtenerConfiguracion() {
        log.info("👥 Obteniendo configuración de roles");

        Map<String, Object> configuracion = Map.of(
                "rolesDelSistema", rolUsuarioService.obtenerRolesDelSistema().stream()
                        .map(RolUsuarioDTO::fromEntity)
                        .map(RolUsuarioDTO::toSimple)
                        .toList(),
                "rolesMasUsados", rolUsuarioService.obtenerRolesMasUsados(),
                "configuracionCompleta", rolUsuarioService.esConfiguracionCompleta(),
                "requiereConfiguracion", rolUsuarioService.requiereConfiguracion(),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(configuracion);
    }

    // Método auxiliar para enriquecer con estadísticas
    private RolUsuarioDTO enriquecerConEstadisticas(RolUsuarioDTO rolDTO) {
        try {
            Map<String, Object> usuarios = rolUsuarioService.obtenerUsuariosDelRol(rolDTO.getRolId());

            rolDTO.setNumeroUsuarios((Integer) usuarios.getOrDefault("total", 0));
            rolDTO.setUsuariosActivos((Integer) usuarios.getOrDefault("activos", 0));
            rolDTO.setUsuariosInactivos((Integer) usuarios.getOrDefault("inactivos", 0));

            // Calcular campos derivados
            rolDTO.calcularCamposDerivados();

        } catch (Exception e) {
            log.warn("⚠️ Error enriqueciendo estadísticas para rol {}: {}", rolDTO.getRolId(), e.getMessage());
        }

        return rolDTO;
    }
}
