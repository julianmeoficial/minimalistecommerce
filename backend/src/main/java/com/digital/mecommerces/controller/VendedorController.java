package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.ProductoDTO;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.*;
import com.digital.mecommerces.service.CategoriaProductoService;
import com.digital.mecommerces.service.ProductoService;
import com.digital.mecommerces.service.UsuarioService;
import com.digital.mecommerces.service.VendedorDetallesService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para funcionalidades específicas de vendedores
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/vendedor")
@Tag(name = "Vendedor", description = "APIs específicas para funcionalidades de vendedores")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class VendedorController {

    private final ProductoService productoService;
    private final CategoriaProductoService categoriaProductoService;
    private final UsuarioService usuarioService;
    private final VendedorDetallesService vendedorDetallesService;

    public VendedorController(ProductoService productoService,
                              CategoriaProductoService categoriaProductoService,
                              UsuarioService usuarioService,
                              VendedorDetallesService vendedorDetallesService) {
        this.productoService = productoService;
        this.categoriaProductoService = categoriaProductoService;
        this.usuarioService = usuarioService;
        this.vendedorDetallesService = vendedorDetallesService;
    }

    // === GESTIÓN DE PRODUCTOS DEL VENDEDOR ===

    @GetMapping("/productos")
    @Operation(summary = "Obtener productos del vendedor autenticado")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Page<ProductoDTO>> obtenerMisProductos(Pageable pageable) {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Obteniendo productos del vendedor ID: {}", vendedorId);

        Page<Producto> productos = productoService.obtenerProductosPorVendedor(vendedorId, pageable);
        Page<ProductoDTO> productosDTO = productos.map(ProductoDTO::fromEntity);

        log.info("✅ {} productos encontrados para el vendedor", productosDTO.getTotalElements());
        return ResponseEntity.ok(productosDTO);
    }

    @PostMapping("/productos")
    @Operation(summary = "Crear nuevo producto")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Creando producto: {} para vendedor ID: {}", productoDTO.getProductoNombre(), vendedorId);

        Producto producto = new Producto();
        producto.setProductoNombre(productoDTO.getProductoNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStock(productoDTO.getStock());

        // Asignar categoría
        CategoriaProducto categoria = categoriaProductoService.obtenerCategoriaPorId(productoDTO.getCategoriaId());
        producto.setCategoria(categoria);

        // Asignar vendedor autenticado
        Usuario vendedor = usuarioService.obtenerUsuarioPorId(vendedorId);
        producto.setVendedor(vendedor);

        Producto nuevoProducto = productoService.crearProducto(producto);
        ProductoDTO respuesta = ProductoDTO.fromEntity(nuevoProducto);

        log.info("✅ Producto creado con ID: {}", nuevoProducto.getProductoId());
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/productos/{id}")
    @Operation(summary = "Actualizar producto propio")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<ProductoDTO> actualizarMiProducto(@PathVariable Long id,
                                                            @Valid @RequestBody ProductoDTO productoDTO) {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Actualizando producto ID: {} por vendedor ID: {}", id, vendedorId);

        // Verificar que el producto pertenece al vendedor
        Producto producto = productoService.obtenerProductoPorId(id);
        if (!producto.getVendedor().getUsuarioId().equals(vendedorId)) {
            log.warn("❌ Vendedor {} intentó actualizar producto {} que no le pertenece", vendedorId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Producto productoDetails = new Producto();
        productoDetails.setProductoNombre(productoDTO.getProductoNombre());
        productoDetails.setDescripcion(productoDTO.getDescripcion());
        productoDetails.setPrecio(productoDTO.getPrecio());
        productoDetails.setStock(productoDTO.getStock());
        productoDetails.setActivo(productoDTO.getActivo());

        if (productoDTO.getCategoriaId() != null) {
            CategoriaProducto categoria = categoriaProductoService.obtenerCategoriaPorId(productoDTO.getCategoriaId());
            productoDetails.setCategoria(categoria);
        }

        Producto actualizado = productoService.actualizarProducto(id, productoDetails);
        ProductoDTO respuesta = ProductoDTO.fromEntity(actualizado);

        log.info("✅ Producto actualizado exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/productos/{id}")
    @Operation(summary = "Eliminar producto propio")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Void> eliminarMiProducto(@PathVariable Long id) {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Eliminando producto ID: {} por vendedor ID: {}", id, vendedorId);

        // Verificar que el producto pertenece al vendedor
        Producto producto = productoService.obtenerProductoPorId(id);
        if (!producto.getVendedor().getUsuarioId().equals(vendedorId)) {
            log.warn("❌ Vendedor {} intentó eliminar producto {} que no le pertenece", vendedorId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        productoService.eliminarProducto(id);

        log.info("✅ Producto eliminado exitosamente");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/productos/{id}/stock")
    @Operation(summary = "Actualizar stock de producto propio")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> actualizarStockProducto(@PathVariable Long id,
                                                                       @RequestBody Map<String, Integer> request) {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        Integer nuevoStock = request.get("stock");

        log.info("🏪 Actualizando stock del producto ID: {} a: {} por vendedor ID: {}", id, nuevoStock, vendedorId);

        // Verificar que el producto pertenece al vendedor
        Producto producto = productoService.obtenerProductoPorId(id);
        if (!producto.getVendedor().getUsuarioId().equals(vendedorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        productoService.actualizarStock(id, nuevoStock);

        Map<String, Object> response = Map.of(
                "mensaje", "Stock actualizado exitosamente",
                "productoId", id,
                "nuevoStock", nuevoStock,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/productos/estadisticas")
    @Operation(summary = "Obtener estadísticas de productos del vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasProductos() {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Obteniendo estadísticas de productos para vendedor ID: {}", vendedorId);

        Map<String, Object> estadisticas = Map.of(
                "totalProductos", productoService.contarProductosPorVendedor(vendedorId),
                "productosActivos", productoService.contarProductosActivosPorVendedor(vendedorId),
                "productosAgotados", productoService.contarProductosAgotadosPorVendedor(vendedorId),
                "stockTotal", productoService.obtenerStockTotalPorVendedor(vendedorId),
                "precioPromedio", productoService.obtenerPrecioPromedioPorVendedor(vendedorId),
                "valorInventario", productoService.obtenerValorInventarioPorVendedor(vendedorId),
                "porCategoria", productoService.obtenerEstadisticasPorCategoriaVendedor(vendedorId),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(estadisticas);
    }

    // === GESTIÓN DE VENTAS ===

    @GetMapping("/ventas")
    @Operation(summary = "Obtener ventas del vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<Map<String, Object>>> obtenerMisVentas() {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Obteniendo ventas del vendedor ID: {}", vendedorId);

        // Obtener ventas del vendedor
        List<Map<String, Object>> ventas = productoService.obtenerVentasPorVendedor(vendedorId);

        log.info("✅ {} ventas encontradas", ventas.size());
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/ventas/estadisticas")
    @Operation(summary = "Obtener estadísticas de ventas del vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasVentas() {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Obteniendo estadísticas de ventas para vendedor ID: {}", vendedorId);

        Map<String, Object> estadisticas = Map.of(
                "totalVentas", productoService.contarVentasPorVendedor(vendedorId),
                "ingresosTotales", productoService.obtenerIngresosTotalesPorVendedor(vendedorId),
                "promedioVenta", productoService.obtenerPromedioVentaPorVendedor(vendedorId),
                "ventasHoy", productoService.contarVentasHoyPorVendedor(vendedorId),
                "ventasMes", productoService.contarVentasMesPorVendedor(vendedorId),
                "productoMasVendido", productoService.obtenerProductoMasVendidoPorVendedor(vendedorId),
                "clientesFrecuentes", productoService.obtenerClientesFrecuentesPorVendedor(vendedorId),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(estadisticas);
    }

    // === GESTIÓN DE PERFIL DE VENDEDOR ===

    @GetMapping("/perfil")
    @Operation(summary = "Obtener perfil de vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerMiPerfil() {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Obteniendo perfil del vendedor ID: {}", vendedorId);

        try {
            // Obtener información del usuario
            Usuario usuario = usuarioService.obtenerUsuarioPorId(vendedorId);

            // Obtener detalles del vendedor
            VendedorDetalles detalles = vendedorDetallesService.obtenerDetallesPorUsuarioId(vendedorId);

            Map<String, Object> perfil = new HashMap<>();
            perfil.put("usuario", Map.of(
                    "id", usuario.getUsuarioId(),
                    "nombre", usuario.getUsuarioNombre(),
                    "email", usuario.getEmail(),
                    "activo", usuario.getActivo(),
                    "ultimoLogin", usuario.getUltimoLogin()
            ));

            if (detalles != null) {
                perfil.put("detalles", Map.of(
                        "rfc", detalles.getRfc() != null ? detalles.getRfc() : "",
                        "especialidad", detalles.getEspecialidad() != null ? detalles.getEspecialidad() : "",
                        "direccionComercial", detalles.getDireccionComercial() != null ? detalles.getDireccionComercial() : "",
                        "verificado", detalles.getVerificado() != null ? detalles.getVerificado() : false,
                        "fechaVerificacion", detalles.getFechaVerificacion(),
                        "ventasTotales", detalles.getVentasTotales() != null ? detalles.getVentasTotales() : 0,
                        "calificacion", detalles.getCalificacion() != null ? detalles.getCalificacion() : 0.0
                ));
            }

            perfil.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(perfil);

        } catch (Exception e) {
            log.error("❌ Error obteniendo perfil del vendedor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error obteniendo perfil"));
        }
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Obtener dashboard del vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerDashboard() {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Obteniendo dashboard del vendedor ID: {}", vendedorId);

        try {
            Map<String, Object> dashboard = new HashMap<>();

            // Estadísticas de productos
            dashboard.put("productos", Map.of(
                    "total", productoService.contarProductosPorVendedor(vendedorId),
                    "activos", productoService.contarProductosActivosPorVendedor(vendedorId),
                    "agotados", productoService.contarProductosAgotadosPorVendedor(vendedorId),
                    "valorInventario", productoService.obtenerValorInventarioPorVendedor(vendedorId)
            ));

            // Estadísticas de ventas
            dashboard.put("ventas", Map.of(
                    "total", productoService.contarVentasPorVendedor(vendedorId),
                    "ingresos", productoService.obtenerIngresosTotalesPorVendedor(vendedorId),
                    "hoy", productoService.contarVentasHoyPorVendedor(vendedorId),
                    "mes", productoService.contarVentasMesPorVendedor(vendedorId)
            ));

            // Productos más vendidos
            dashboard.put("topProductos", productoService.obtenerTopProductosVendedor(vendedorId, 5));

            // Resumen rápido
            dashboard.put("resumen", Map.of(
                    "necesitanRestock", productoService.contarProductosConPocoStockPorVendedor(vendedorId),
                    "ventasPendientes", productoService.contarVentasPendientesPorVendedor(vendedorId),
                    "calificacionPromedio", vendedorDetallesService.obtenerCalificacionPromedio(vendedorId)
            ));

            dashboard.put("timestamp", LocalDateTime.now());

            log.info("✅ Dashboard generado exitosamente");
            return ResponseEntity.ok(dashboard);

        } catch (Exception e) {
            log.error("❌ Error generando dashboard: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error generando dashboard"));
        }
    }

    @GetMapping("/categorias-disponibles")
    @Operation(summary = "Obtener categorías disponibles para productos")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<Map<String, Object>>> obtenerCategoriasDisponibles() {
        log.info("🏪 Obteniendo categorías disponibles para vendedor");

        List<CategoriaProducto> categorias = categoriaProductoService.obtenerCategoriasActivas();
        List<Map<String, Object>> categoriasDTO = categorias.stream()
                .map(categoria -> Map.of(
                        "id", categoria.getCategoriaId(),
                        "nombre", categoria.getNombre(),
                        "descripcion", categoria.getDescripcion() != null ? categoria.getDescripcion() : "",
                        "slug", categoria.getSlug() != null ? categoria.getSlug() : ""
                ))
                .toList();

        return ResponseEntity.ok(categoriasDTO);
    }

    @GetMapping("/notificaciones")
    @Operation(summary = "Obtener notificaciones del vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<Map<String, Object>>> obtenerNotificaciones() {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Obteniendo notificaciones del vendedor ID: {}", vendedorId);

        List<Map<String, Object>> notificaciones = new ArrayList<>();

        // Verificar productos con poco stock
        long productosPocoStock = productoService.contarProductosConPocoStockPorVendedor(vendedorId);
        if (productosPocoStock > 0) {
            notificaciones.add(Map.of(
                    "tipo", "stock",
                    "titulo", "Productos con poco stock",
                    "mensaje", productosPocoStock + " productos necesitan reabastecimiento",
                    "prioridad", "alta",
                    "timestamp", LocalDateTime.now()
            ));
        }

        // Verificar ventas pendientes
        long ventasPendientes = productoService.contarVentasPendientesPorVendedor(vendedorId);
        if (ventasPendientes > 0) {
            notificaciones.add(Map.of(
                    "tipo", "ventas",
                    "titulo", "Ventas pendientes",
                    "mensaje", ventasPendientes + " ventas requieren atención",
                    "prioridad", "media",
                    "timestamp", LocalDateTime.now()
            ));
        }

        return ResponseEntity.ok(notificaciones);
    }

    // Método auxiliar para obtener usuario autenticado
    private Long obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
        return usuario.getUsuarioId();
    }
}
