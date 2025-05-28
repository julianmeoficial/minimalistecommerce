package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.PermisoDTO;
import com.digital.mecommerces.model.Permiso;
import com.digital.mecommerces.service.PermisoService;
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
 * Controlador para gestión de permisos del sistema
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/permisos")
@Tag(name = "Permisos", description = "APIs para gestión de permisos del sistema")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class PermisoController {

    private final PermisoService permisoService;

    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los permisos", description = "Obtiene lista completa de permisos del sistema")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<PermisoDTO>> listarPermisos() {
        log.info("🔑 Obteniendo lista de todos los permisos");

        List<Permiso> permisos = permisoService.obtenerPermisos();
        List<PermisoDTO> permisosDTO = permisos.stream()
                .map(PermisoDTO::fromEntity)
                .toList();

        log.info("✅ {} permisos encontrados", permisosDTO.size());
        return ResponseEntity.ok(permisosDTO);
    }

    @GetMapping("/del-sistema")
    @Operation(summary = "Listar permisos del sistema", description = "Obtiene permisos críticos del sistema")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<PermisoDTO>> listarPermisosDelSistema() {
        log.info("🔑 Obteniendo permisos del sistema");

        List<Permiso> permisos = permisoService.obtenerPermisosDelSistema();
        List<PermisoDTO> permisosDTO = permisos.stream()
                .map(PermisoDTO::fromEntity)
                .map(PermisoDTO::toSimple)
                .toList();

        return ResponseEntity.ok(permisosDTO);
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar permisos por categoría")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<PermisoDTO>> listarPermisosPorCategoria(@PathVariable String categoria) {
        log.info("🔑 Obteniendo permisos por categoría: {}", categoria);

        List<Permiso> permisos = permisoService.obtenerPermisosPorCategoria(categoria);
        List<PermisoDTO> permisosDTO = permisos.stream()
                .map(PermisoDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(permisosDTO);
    }

    @GetMapping("/nivel/{nivel}")
    @Operation(summary = "Listar permisos por nivel")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<PermisoDTO>> listarPermisosPorNivel(@PathVariable Integer nivel) {
        log.info("🔑 Obteniendo permisos por nivel: {}", nivel);

        List<Permiso> permisos = permisoService.obtenerPermisosPorNivel(nivel);
        List<PermisoDTO> permisosDTO = permisos.stream()
                .map(PermisoDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(permisosDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener permiso por ID")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<PermisoDTO> obtenerPermiso(@PathVariable Long id) {
        log.info("🔑 Obteniendo permiso por ID: {}", id);

        Permiso permiso = permisoService.obtenerPermisoPorId(id);
        PermisoDTO permisoDTO = PermisoDTO.fromEntity(permiso);

        return ResponseEntity.ok(permisoDTO);
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtener permiso por código")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<PermisoDTO> obtenerPermisoPorCodigo(@PathVariable String codigo) {
        log.info("🔑 Obteniendo permiso por código: {}", codigo);

        Permiso permiso = permisoService.obtenerPermisoPorCodigo(codigo);
        PermisoDTO permisoDTO = PermisoDTO.fromEntity(permiso);

        return ResponseEntity.ok(permisoDTO);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo permiso")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<PermisoDTO> crearPermiso(@Valid @RequestBody PermisoDTO permisoDTO) {
        log.info("🔑 Creando nuevo permiso: {}", permisoDTO.getCodigo());

        Permiso permiso = new Permiso();
        permiso.setCodigo(permisoDTO.getCodigo());
        permiso.setDescripcion(permisoDTO.getDescripcion());
        permiso.setNivel(permisoDTO.getNivel());
        permiso.setCategoria(permisoDTO.getCategoria());

        if (permisoDTO.getPermisopadreId() != null) {
            Permiso permisoPadre = permisoService.obtenerPermisoPorId(permisoDTO.getPermisopadreId());
            permiso.setPermisoPadre(permisoPadre);
        }

        Permiso nuevoPermiso = permisoService.crearPermiso(permiso);
        PermisoDTO respuesta = PermisoDTO.fromEntity(nuevoPermiso);

        log.info("✅ Permiso creado con ID: {}", nuevoPermiso.getPermisoId());
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar permiso")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<PermisoDTO> actualizarPermiso(@PathVariable Long id,
                                                        @Valid @RequestBody PermisoDTO permisoDTO) {
        log.info("🔑 Actualizando permiso ID: {}", id);

        Permiso permisoDetails = new Permiso();
        permisoDetails.setCodigo(permisoDTO.getCodigo());
        permisoDetails.setDescripcion(permisoDTO.getDescripcion());
        permisoDetails.setNivel(permisoDTO.getNivel());
        permisoDetails.setCategoria(permisoDTO.getCategoria());
        permisoDetails.setActivo(permisoDTO.getActivo());

        Permiso permisoActualizado = permisoService.actualizarPermiso(id, permisoDetails);
        PermisoDTO respuesta = PermisoDTO.fromEntity(permisoActualizado);

        log.info("✅ Permiso actualizado exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar permiso")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Void> eliminarPermiso(@PathVariable Long id) {
        log.info("🔑 Eliminando permiso ID: {}", id);

        permisoService.eliminarPermiso(id);

        log.info("✅ Permiso eliminado exitosamente");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inicializar-sistema")
    @Operation(summary = "Inicializar permisos del sistema")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> inicializarPermisosDelSistema() {
        log.info("🔑 Inicializando permisos del sistema");

        try {
            permisoService.inicializarPermisosDelSistema();

            Map<String, Object> response = Map.of(
                    "mensaje", "Permisos del sistema inicializados exitosamente",
                    "timestamp", LocalDateTime.now()
            );

            log.info("✅ Permisos del sistema inicializados");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error inicializando permisos: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error inicializando permisos: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de permisos")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.info("🔑 Obteniendo estadísticas de permisos");

        Map<String, Object> estadisticas = Map.of(
                "total", permisoService.contarPermisos(),
                "delSistema", permisoService.contarPermisosDelSistema(),
                "personalizados", permisoService.contarPermisosPersonalizados(),
                "porCategoria", permisoService.obtenerEstadisticasPorCategoria(),
                "porNivel", permisoService.obtenerEstadisticasPorNivel(),
                "huerfanos", permisoService.contarPermisosHuerfanos(),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/jerarquia")
    @Operation(summary = "Obtener jerarquía de permisos")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<PermisoDTO>> obtenerJerarquiaPermisos() {
        log.info("🔑 Obteniendo jerarquía de permisos");

        List<Permiso> permisos = permisoService.obtenerJerarquiaPermisos();
        List<PermisoDTO> jerarquia = permisos.stream()
                .map(PermisoDTO::fromEntity)
                .sorted((p1, p2) -> {
                    // Ordenar por nivel y luego por código
                    int nivelComparison = Integer.compare(
                            p1.getNivel() != null ? p1.getNivel() : 999,
                            p2.getNivel() != null ? p2.getNivel() : 999
                    );
                    return nivelComparison != 0 ? nivelComparison : p1.getCodigo().compareTo(p2.getCodigo());
                })
                .toList();

        return ResponseEntity.ok(jerarquia);
    }

    @PostMapping("/verificar-integridad")
    @Operation(summary = "Verificar integridad de permisos")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> verificarIntegridad() {
        log.info("🔑 Verificando integridad de permisos");

        Map<String, Object> resultado = permisoService.verificarIntegridadPermisos();
        resultado.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(resultado);
    }
}
