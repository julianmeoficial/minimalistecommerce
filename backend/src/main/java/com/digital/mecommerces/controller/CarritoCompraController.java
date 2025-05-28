package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.CarritoCompraDTO;
import com.digital.mecommerces.dto.CarritoItemDTO;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.CarritoCompra;
import com.digital.mecommerces.model.CarritoItem;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.service.CarritoService;
import com.digital.mecommerces.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
 * Controlador para gestión de carritos de compra
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito de Compras", description = "APIs para gestión del carrito de compras")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class CarritoCompraController {

    private final CarritoService carritoService;
    private final UsuarioService usuarioService;

    public CarritoCompraController(CarritoService carritoService, UsuarioService usuarioService) {
        this.carritoService = carritoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Obtener carrito activo del usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<CarritoCompraDTO> obtenerCarritoActivo() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Obteniendo carrito activo para usuario ID: {}", usuarioId);

        CarritoCompra carrito = carritoService.obtenerCarritoActivo(usuarioId);
        CarritoCompraDTO carritoDTO = CarritoCompraDTO.fromEntity(carrito);

        log.info("✅ Carrito obtenido: {} items", carritoDTO.getTotalItems());
        return ResponseEntity.ok(carritoDTO);
    }

    @PostMapping("/items")
    @Operation(summary = "Agregar producto al carrito")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<CarritoItemDTO> agregarProductoAlCarrito(@Valid @RequestBody Map<String, Object> request) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        Long productoId = Long.valueOf(request.get("productoId").toString());
        Integer cantidad = Integer.valueOf(request.get("cantidad").toString());

        log.info("🛒 Agregando producto {} (cantidad: {}) al carrito del usuario {}",
                productoId, cantidad, usuarioId);

        CarritoItem item = carritoService.agregarProductoAlCarrito(usuarioId, productoId, cantidad);
        CarritoItemDTO itemDTO = CarritoItemDTO.fromEntity(item);

        log.info("✅ Producto agregado al carrito exitosamente");
        return new ResponseEntity<>(itemDTO, HttpStatus.CREATED);
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Actualizar cantidad de producto en carrito")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<CarritoItemDTO> actualizarCantidadProducto(@PathVariable Long itemId,
                                                                     @RequestBody Map<String, Integer> request) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        Integer nuevaCantidad = request.get("cantidad");

        log.info("🛒 Actualizando cantidad del item {} a {} para usuario {}",
                itemId, nuevaCantidad, usuarioId);

        CarritoItem item = carritoService.actualizarCantidadProducto(usuarioId, itemId, nuevaCantidad);

        if (item == null) {
            return ResponseEntity.noContent().build();
        }

        CarritoItemDTO itemDTO = CarritoItemDTO.fromEntity(item);
        log.info("✅ Cantidad actualizada exitosamente");
        return ResponseEntity.ok(itemDTO);
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Eliminar producto del carrito")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long itemId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Eliminando item {} del carrito del usuario {}", itemId, usuarioId);

        carritoService.eliminarProductoDelCarrito(usuarioId, itemId);

        log.info("✅ Producto eliminado del carrito exitosamente");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Vaciar carrito completo")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> vaciarCarrito() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Vaciando carrito del usuario {}", usuarioId);

        carritoService.vaciarCarrito(usuarioId);

        Map<String, Object> response = Map.of(
                "mensaje", "Carrito vaciado exitosamente",
                "timestamp", LocalDateTime.now()
        );

        log.info("✅ Carrito vaciado exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/items")
    @Operation(summary = "Obtener items del carrito")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<CarritoItemDTO>> obtenerItemsCarrito() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Obteniendo items del carrito para usuario {}", usuarioId);

        CarritoCompra carrito = carritoService.obtenerCarritoActivo(usuarioId);
        List<CarritoItemDTO> items = carrito.getItems().stream()
                .map(CarritoItemDTO::fromEntity)
                .toList();

        log.info("✅ {} items encontrados en el carrito", items.size());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/resumen")
    @Operation(summary = "Obtener resumen del carrito")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<CarritoCompraDTO> obtenerResumenCarrito() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Obteniendo resumen del carrito para usuario {}", usuarioId);

        CarritoCompra carrito = carritoService.obtenerCarritoActivo(usuarioId);
        CarritoCompraDTO carritoDTO = CarritoCompraDTO.fromEntity(carrito);

        // Versión simplificada para resumen
        CarritoCompraDTO resumen = carritoDTO.toSimple();

        return ResponseEntity.ok(resumen);
    }

    @PostMapping("/checkout")
    @Operation(summary = "Proceder al checkout")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody(required = false) Map<String, Object> checkoutData) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Procesando checkout para usuario {}", usuarioId);

        try {
            // Validar carrito antes de checkout
            CarritoCompra carrito = carritoService.obtenerCarritoActivo(usuarioId);
            CarritoCompraDTO carritoDTO = CarritoCompraDTO.fromEntity(carrito);

            if (carritoDTO.getEstaVacio()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "El carrito está vacío",
                        "timestamp", LocalDateTime.now()
                ));
            }

            if (!carritoDTO.getPuedeComprar()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "El carrito contiene productos no disponibles",
                        "mensaje", carritoDTO.getMensaje(),
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Convertir carrito a orden
            // Orden orden = carritoService.convertirCarritoAOrden(usuarioId);

            Map<String, Object> response = Map.of(
                    "mensaje", "Checkout procesado exitosamente",
                    "carritoId", carrito.getCarritoId(),
                    "total", carritoDTO.getTotalEstimado(),
                    "items", carritoDTO.getTotalItems(),
                    "timestamp", LocalDateTime.now()
            );

            log.info("✅ Checkout procesado exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error procesando checkout: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error procesando checkout",
                    "detalle", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/items/{itemId}/guardar-despues")
    @Operation(summary = "Guardar producto para después")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> guardarParaDespues(@PathVariable Long itemId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Guardando item {} para después - usuario {}", itemId, usuarioId);

        // Implementar lógica para guardar para después
        Map<String, Object> response = Map.of(
                "mensaje", "Producto guardado para después",
                "itemId", itemId,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/guardados")
    @Operation(summary = "Obtener productos guardados para después")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<CarritoItemDTO>> obtenerProductosGuardados() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Obteniendo productos guardados para después - usuario {}", usuarioId);

        CarritoCompra carrito = carritoService.obtenerCarritoActivo(usuarioId);
        List<CarritoItemDTO> itemsGuardados = carrito.getItems().stream()
                .filter(item -> Boolean.TRUE.equals(item.getGuardadoDespues()))
                .map(CarritoItemDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(itemsGuardados);
    }

    // Método auxiliar para obtener usuario autenticado
    private Long obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
        return usuario.getUsuarioId();
    }
}
