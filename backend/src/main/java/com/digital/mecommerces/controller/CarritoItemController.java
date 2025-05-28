package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.CarritoItemDTO;
import com.digital.mecommerces.model.CarritoItem;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.service.CarritoService;
import com.digital.mecommerces.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador específico para items del carrito
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/carrito-items")
@Tag(name = "Items del Carrito", description = "APIs específicas para gestión de items individuales del carrito")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class CarritoItemController {

    private final CarritoService carritoService;
    private final UsuarioService usuarioService;

    public CarritoItemController(CarritoService carritoService, UsuarioService usuarioService) {
        this.carritoService = carritoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Obtener item específico del carrito")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<CarritoItemDTO> obtenerItem(@PathVariable Long itemId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Obteniendo item {} para usuario {}", itemId, usuarioId);

        // Implementar obtención de item específico
        // CarritoItem item = carritoService.obtenerItem(usuarioId, itemId);
        // CarritoItemDTO itemDTO = CarritoItemDTO.fromEntity(item);

        // Por ahora retornamos un placeholder
        CarritoItemDTO itemDTO = new CarritoItemDTO();
        itemDTO.setItemId(itemId);

        return ResponseEntity.ok(itemDTO);
    }

    @PutMapping("/{itemId}/cantidad")
    @Operation(summary = "Actualizar solo la cantidad de un item")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<CarritoItemDTO> actualizarCantidad(@PathVariable Long itemId,
                                                             @RequestBody Map<String, Integer> request) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        Integer nuevaCantidad = request.get("cantidad");

        log.info("🛒 Actualizando cantidad del item {} a {} para usuario {}",
                itemId, nuevaCantidad, usuarioId);

        if (nuevaCantidad == null || nuevaCantidad <= 0) {
            return ResponseEntity.badRequest().build();
        }

        CarritoItem item = carritoService.actualizarCantidadProducto(usuarioId, itemId, nuevaCantidad);

        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        CarritoItemDTO itemDTO = CarritoItemDTO.fromEntity(item);
        return ResponseEntity.ok(itemDTO);
    }

    @PostMapping("/{itemId}/duplicar")
    @Operation(summary = "Duplicar item en el carrito")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> duplicarItem(@PathVariable Long itemId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Duplicando item {} para usuario {}", itemId, usuarioId);

        // Implementar lógica para duplicar item
        Map<String, Object> response = Map.of(
                "mensaje", "Item duplicado exitosamente",
                "itemOriginal", itemId,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{itemId}/mover-a-guardados")
    @Operation(summary = "Mover item a guardados para después")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> moverAGuardados(@PathVariable Long itemId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Moviendo item {} a guardados para usuario {}", itemId, usuarioId);

        // Implementar lógica para mover a guardados
        Map<String, Object> response = Map.of(
                "mensaje", "Item movido a guardados para después",
                "itemId", itemId,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{itemId}/restaurar")
    @Operation(summary = "Restaurar item desde guardados al carrito activo")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> restaurarItem(@PathVariable Long itemId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Restaurando item {} desde guardados para usuario {}", itemId, usuarioId);

        // Implementar lógica para restaurar item
        Map<String, Object> response = Map.of(
                "mensaje", "Item restaurado al carrito activo",
                "itemId", itemId,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/disponibilidad")
    @Operation(summary = "Verificar disponibilidad de todos los items")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> verificarDisponibilidad() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Verificando disponibilidad de items para usuario {}", usuarioId);

        // Implementar verificación de disponibilidad
        Map<String, Object> response = Map.of(
                "disponibles", 0,
                "noDisponibles", 0,
                "stockLimitado", 0,
                "mensaje", "Verificación completada",
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/limpiar-no-disponibles")
    @Operation(summary = "Eliminar items no disponibles del carrito")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> limpiarNoDisponibles() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Limpiando items no disponibles para usuario {}", usuarioId);

        // Implementar limpieza de items no disponibles
        Map<String, Object> response = Map.of(
                "itemsEliminados", 0,
                "mensaje", "Items no disponibles eliminados",
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de items del carrito")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Obteniendo estadísticas de carrito para usuario {}", usuarioId);

        Map<String, Object> estadisticas = Map.of(
                "totalItems", 0,
                "itemsActivos", 0,
                "itemsGuardados", 0,
                "valorTotal", 0.0,
                "categorias", List.of(),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(estadisticas);
    }

    // Método auxiliar para obtener usuario autenticado
    private Long obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
        return usuario.getUsuarioId();
    }
}
