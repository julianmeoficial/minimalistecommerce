package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.RolPermisoDTO;
import com.digital.mecommerces.model.Permiso;
import com.digital.mecommerces.model.RolPermiso;
import com.digital.mecommerces.service.RolPermisoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador para gestión de relaciones Rol-Permiso
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/roles-permisos")
@Tag(name = "Roles-Permisos", description = "APIs para gestión de relaciones entre roles y permisos")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class RolPermisoController {

    private final RolPermisoService rolPermisoService;

    public RolPermisoController(RolPermisoService rolPermisoService) {
        this.rolPermisoService = rolPermisoService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las relaciones rol-permiso")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<RolPermisoDTO>> listarTodosRolPermisos() {
        log.info("🔗 Obteniendo todas las relaciones rol-permiso");

        List<RolPermiso> rolPermisos = rolPermisoService.obtenerTodosRolPermisos();
        List<RolPermisoDTO> rolPermisosDTO = rolPermisos.stream()
                .map(RolPermisoDTO::fromEntity)
                .sorted(RolPermisoDTO.porImportancia())
                .toList();

        log.info("✅ {} relaciones rol-permiso encontradas", rolPermisosDTO.size());
        return ResponseEntity.ok(rolPermisosDTO);
    }

    @GetMapping("/rol/{rolId}")
    @Operation(summary = "Listar permisos de un rol específico")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<RolPermisoDTO>> listarPermisosPorRol(@PathVariable Long rolId) {
        log.info("🔗 Obteniendo permisos del rol ID: {}", rolId);

        List<RolPermiso> permisos = rolPermisoService.obtenerPermisosPorRol(rolId);
        List<RolPermisoDTO> permisosDTO = permisos.stream()
                .map(RolPermisoDTO::fromEntity)
                .sorted(RolPermisoDTO.porRolYNivel())
                .toList();

        return ResponseEntity.ok(permisosDTO);
    }

    @GetMapping("/rol/{rolId}/permisos")
    @Operation(summary = "Obtener solo los permisos (entidades) de un rol")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<Permiso>> listarPermisosDeRol(@PathVariable Long rolId) {
        log.info("🔗 Obteniendo entidades de permisos del rol ID: {}", rolId);

        List<Permiso> permisos = rolPermisoService.obtenerPermisosDeRol(rolId);

        return ResponseEntity.ok(permisos);
    }

    @PostMapping("/rol/{rolId}/permiso/{permisoId}")
    @Operation(summary = "Asignar permiso a rol")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<RolPermisoDTO> asignarPermisoARol(@PathVariable Long rolId,
                                                            @PathVariable Long permisoId) {
        log.info("🔗 Asignando permiso ID: {} al rol ID: {}", permisoId, rolId);

        RolPermiso rolPermiso = rolPermisoService.asignarPermisoARol(rolId, permisoId);
        RolPermisoDTO respuesta = RolPermisoDTO.fromEntity(rolPermiso);

        log.info("✅ Permiso asignado exitosamente");
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PostMapping("/rol/{rolId}/permisos/multiple")
    @Operation(summary = "Asignar múltiples permisos a un rol")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> asignarMultiplesPermisosARol(@PathVariable Long rolId,
                                                                            @RequestBody List<Long> permisosIds) {
        log.info("🔗 Asignando {} permisos al rol ID: {}", permisosIds.size(), rolId);

        try {
            rolPermisoService.asignarMultiplesPermisosARol(rolId, permisosIds);

            Map<String, Object> response = Map.of(
                    "mensaje", "Permisos asignados exitosamente",
                    "rolId", rolId,
                    "permisosAsignados", permisosIds.size(),
                    "timestamp", LocalDateTime.now()
            );

            log.info("✅ {} permisos asignados exitosamente al rol", permisosIds.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error asignando permisos múltiples: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error asignando permisos: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @DeleteMapping("/rol/{rolId}/permiso/{permisoId}")
    @Operation(summary = "Eliminar permiso de rol")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Void> eliminarPermisoDeRol(@PathVariable Long rolId,
                                                     @PathVariable Long permisoId) {
        log.info("🔗 Eliminando permiso ID: {} del rol ID: {}", permisoId, rolId);

        rolPermisoService.eliminarPermisoDeRol(rolId, permisoId);

        log.info("✅ Permiso eliminado del rol exitosamente");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rol/{rolId}/permisos")
    @Operation(summary = "Eliminar todos los permisos de un rol")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> eliminarTodosPermisosDeRol(@PathVariable Long rolId) {
        log.info("🔗 Eliminando todos los permisos del rol ID: {}", rolId);

        try {
            int permisosEliminados = rolPermisoService.eliminarTodosPermisosDeRol(rolId);

            Map<String, Object> response = Map.of(
                    "mensaje", "Todos los permisos eliminados del rol",
                    "rolId", rolId,
                    "permisosEliminados", permisosEliminados,
                    "timestamp", LocalDateTime.now()
            );

            log.info("✅ {} permisos eliminados del rol exitosamente", permisosEliminados);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error eliminando permisos del rol: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error eliminando permisos: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/permiso/{permisoId}/roles")
    @Operation(summary = "Obtener roles que tienen un permiso específico")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<RolPermisoDTO>> obtenerRolesConPermiso(@PathVariable Long permisoId) {
        log.info("🔗 Obteniendo roles que tienen el permiso ID: {}", permisoId);

        List<RolPermiso> rolesConPermiso = rolPermisoService.obtenerRolesConPermiso(permisoId);
        List<RolPermisoDTO> rolesDTO = rolesConPermiso.stream()
                .map(RolPermisoDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(rolesDTO);
    }

    @GetMapping("/validar/{rolId}/{permisoId}")
    @Operation(summary = "Validar si un rol tiene un permiso específico")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> validarRolTienePermiso(@PathVariable Long rolId,
                                                                      @PathVariable Long permisoId) {
        log.info("🔗 Validando si rol ID: {} tiene permiso ID: {}", rolId, permisoId);

        boolean tienePermiso = rolPermisoService.rolTienePermiso(rolId, permisoId);

        Map<String, Object> response = Map.of(
                "rolId", rolId,
                "permisoId", permisoId,
                "tienePermiso", tienePermiso,
                "mensaje", tienePermiso ? "El rol tiene el permiso" : "El rol no tiene el permiso",
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sincronizar-sistema")
    @Operation(summary = "Sincronizar permisos del sistema con roles predeterminados")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> sincronizarPermisosDelSistema() {
        log.info("🔗 Sincronizando permisos del sistema con roles");

        try {
            Map<String, Integer> resultado = rolPermisoService.sincronizarPermisosDelSistema();

            Map<String, Object> response = Map.of(
                    "mensaje", "Sincronización completada exitosamente",
                    "resultado", resultado,
                    "timestamp", LocalDateTime.now()
            );

            log.info("✅ Sincronización de permisos completada");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error sincronizando permisos del sistema: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error en sincronización: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de relaciones rol-permiso")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.info("🔗 Obteniendo estadísticas de relaciones rol-permiso");

        Map<String, Object> estadisticas = Map.of(
                "totalRelaciones", rolPermisoService.contarRelaciones(),
                "relacionesDelSistema", rolPermisoService.contarRelacionesDelSistema(),
                "relacionesPersonalizadas", rolPermisoService.contarRelacionesPersonalizadas(),
                "porRol", rolPermisoService.obtenerEstadisticasPorRol(),
                "porCategoria", rolPermisoService.obtenerEstadisticasPorCategoria(),
                "integridad", rolPermisoService.verificarIntegridadRelaciones(),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(estadisticas);
    }

    @PostMapping("/copiar-permisos")
    @Operation(summary = "Copiar permisos de un rol a otro")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> copiarPermisos(@RequestBody Map<String, Long> request) {
        Long rolOrigenId = request.get("rolOrigenId");
        Long rolDestinoId = request.get("rolDestinoId");

        log.info("🔗 Copiando permisos del rol ID: {} al rol ID: {}", rolOrigenId, rolDestinoId);

        try {
            int permisosCopidos = rolPermisoService.copiarPermisos(rolOrigenId, rolDestinoId);

            Map<String, Object> response = Map.of(
                    "mensaje", "Permisos copiados exitosamente",
                    "rolOrigenId", rolOrigenId,
                    "rolDestinoId", rolDestinoId,
                    "permisosCopidos", permisosCopidos,
                    "timestamp", LocalDateTime.now()
            );

            log.info("✅ {} permisos copiados exitosamente", permisosCopidos);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error copiando permisos: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error copiando permisos: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/auditoria")
    @Operation(summary = "Obtener auditoría de relaciones rol-permiso")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<RolPermisoDTO>> obtenerAuditoriaRelaciones() {
        log.info("🔗 Obteniendo auditoría de relaciones rol-permiso");

        List<RolPermiso> relaciones = rolPermisoService.obtenerTodosRolPermisos();
        List<RolPermisoDTO> auditoria = relaciones.stream()
                .map(RolPermisoDTO::fromEntity)
                .map(RolPermisoDTO::toAudit)
                .sorted(RolPermisoDTO.porImportancia())
                .toList();

        return ResponseEntity.ok(auditoria);
    }
}
