package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.ProductoDTO;
import com.digital.mecommerces.model.CategoriaProducto;
import com.digital.mecommerces.model.Producto;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.service.CategoriaProductoService;
import com.digital.mecommerces.service.ProductoService;
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
 * Controlador para gestión de productos
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "APIs para gestión de productos del sistema")
@Slf4j
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaProductoService categoriaProductoService;
    private final UsuarioService usuarioService;

    public ProductoController(ProductoService productoService,
                              CategoriaProductoService categoriaProductoService,
                              UsuarioService usuarioService) {
        this.productoService = productoService;
        this.categoriaProductoService = categoriaProductoService;
        this.usuarioService = usuarioService;
    }

    // === ENDPOINTS PÚBLICOS ===

    @GetMapping
    @Operation(summary = "Listar productos", description = "Endpoint público para obtener productos activos")
    public ResponseEntity<Page<ProductoDTO>> listarProductos(Pageable pageable) {
        log.info("🛍️ Obteniendo productos con paginación");

        Page<Producto> productos = productoService.obtenerProductosActivos(pageable);
        Page<ProductoDTO> productosDTO = productos.map(ProductoDTO::fromEntity)
                .map(ProductoDTO::toPublic);

        log.info("✅ {} productos encontrados", productosDTO.getTotalElements());
        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/destacados")
    @Operation(summary = "Listar productos destacados", description = "Obtiene productos marcados como destacados")
    public ResponseEntity<List<ProductoDTO>> listarProductosDestacados() {
        log.info("🛍️ Obteniendo productos destacados");

        List<Producto> productos = productoService.obtenerProductosDestacados();
        List<ProductoDTO> productosDTO = productos.stream()
                .map(ProductoDTO::fromEntity)
                .map(ProductoDTO::toPublic)
                .toList();

        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/recientes")
    @Operation(summary = "Listar productos recientes", description = "Obtiene productos agregados recientemente")
    public ResponseEntity<List<ProductoDTO>> listarProductosRecientes() {
        log.info("🛍️ Obteniendo productos recientes");

        List<Producto> productos = productoService.obtenerProductosRecientes();
        List<ProductoDTO> productosDTO = productos.stream()
                .map(ProductoDTO::fromEntity)
                .map(ProductoDTO::toPublic)
                .toList();

        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ProductoDTO> obtenerProducto(@PathVariable Long id) {
        log.info("🛍️ Obteniendo producto por ID: {}", id);

        Producto producto = productoService.obtenerProductoPorId(id);
        ProductoDTO productoDTO = ProductoDTO.fromEntity(producto);

        return ResponseEntity.ok(productoDTO.toPublic());
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Obtener producto por slug")
    public ResponseEntity<ProductoDTO> obtenerProductoPorSlug(@PathVariable String slug) {
        log.info("🛍️ Obteniendo producto por slug: {}", slug);

        Producto producto = productoService.obtenerProductoPorSlug(slug);
        ProductoDTO productoDTO = ProductoDTO.fromEntity(producto);

        return ResponseEntity.ok(productoDTO.toPublic());
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Obtener productos por categoría")
    public ResponseEntity<Page<ProductoDTO>> obtenerProductosPorCategoria(@PathVariable Long categoriaId,
                                                                          Pageable pageable) {
        log.info("🛍️ Obteniendo productos por categoría ID: {}", categoriaId);

        Page<Producto> productos = productoService.obtenerProductosPorCategoria(categoriaId, pageable);
        Page<ProductoDTO> productosDTO = productos.map(ProductoDTO::fromEntity)
                .map(ProductoDTO::toPublic);

        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/vendedor/{vendedorId}")
    @Operation(summary = "Obtener productos por vendedor")
    public ResponseEntity<Page<ProductoDTO>> obtenerProductosPorVendedor(@PathVariable Long vendedorId,
                                                                         Pageable pageable) {
        log.info("🛍️ Obteniendo productos por vendedor ID: {}", vendedorId);

        Page<Producto> productos = productoService.obtenerProductosPorVendedor(vendedorId, pageable);
        Page<ProductoDTO> productosDTO = productos.map(ProductoDTO::fromEntity)
                .map(ProductoDTO::toPublic);

        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos", description = "Busca productos por nombre o descripción")
    public ResponseEntity<Page<ProductoDTO>> buscarProductos(@RequestParam String q, Pageable pageable) {
        log.info("🛍️ Buscando productos con término: {}", q);

        Page<Producto> productos = productoService.buscarProductos(q, pageable);
        Page<ProductoDTO> productosDTO = productos.map(ProductoDTO::fromEntity)
                .map(ProductoDTO::toPublic);

        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/filtrar")
    @Operation(summary = "Filtrar productos", description = "Filtra productos por múltiples criterios")
    public ResponseEntity<Page<ProductoDTO>> filtrarProductos(
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Boolean disponible,
            Pageable pageable) {

        log.info("🛍️ Filtrando productos con criterios múltiples");

        Page<Producto> productos = productoService.filtrarProductos(
                precioMin, precioMax, categoriaId, disponible, pageable);
        Page<ProductoDTO> productosDTO = productos.map(ProductoDTO::fromEntity)
                .map(ProductoDTO::toPublic);

        return ResponseEntity.ok(productosDTO);
    }

    // === ENDPOINTS PARA VENDEDORES ===

    @PostMapping
    @Operation(summary = "Crear producto")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🛍️ Creando producto: {} para vendedor ID: {}", productoDTO.getProductoNombre(), vendedorId);

        Producto producto = new Producto();
        producto.setProductoNombre(productoDTO.getProductoNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStock(productoDTO.getStock());

        // Asignar categoría
        CategoriaProducto categoria = categoriaProductoService.obtenerCategoriaPorId(productoDTO.getCategoriaId());
        producto.setCategoria(categoria);

        // Asignar vendedor (usuario autenticado)
        Usuario vendedor = usuarioService.obtenerUsuarioPorId(vendedorId);
        producto.setVendedor(vendedor);

        Producto nuevoProducto = productoService.crearProducto(producto);
        ProductoDTO respuesta = ProductoDTO.fromEntity(nuevoProducto);

        log.info("✅ Producto creado con ID: {}", nuevoProducto.getProductoId());
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Long id,
                                                          @Valid @RequestBody ProductoDTO productoDTO) {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🛍️ Actualizando producto ID: {} por vendedor ID: {}", id, vendedorId);

        // Verificar que el producto pertenece al vendedor (a menos que sea admin)
        if (!esAdmin()) {
            Producto producto = productoService.obtenerProductoPorId(id);
            if (!producto.getVendedor().getUsuarioId().equals(vendedorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        Producto productoDetails = new Producto();
        productoDetails.setProductoNombre(productoDTO.getProductoNombre());
        productoDetails.setDescripcion(productoDTO.getDescripcion());
        productoDetails.setPrecio(productoDTO.getPrecio());
        productoDetails.setStock(productoDTO.getStock());
        productoDetails.setActivo(productoDTO.getActivo());
        productoDetails.setDestacado(productoDTO.getDestacado());

        if (productoDTO.getCategoriaId() != null) {
            CategoriaProducto categoria = categoriaProductoService.obtenerCategoriaPorId(productoDTO.getCategoriaId());
            productoDetails.setCategoria(categoria);
        }

        Producto actualizado = productoService.actualizarProducto(id, productoDetails);
        ProductoDTO respuesta = ProductoDTO.fromEntity(actualizado);

        log.info("✅ Producto actualizado exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🛍️ Eliminando producto ID: {} por vendedor ID: {}", id, vendedorId);

        // Verificar que el producto pertenece al vendedor (a menos que sea admin)
        if (!esAdmin()) {
            Producto producto = productoService.obtenerProductoPorId(id);
            if (!producto.getVendedor().getUsuarioId().equals(vendedorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        productoService.eliminarProducto(id);

        log.info("✅ Producto eliminado exitosamente");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mis-productos")
    @Operation(summary = "Obtener mis productos", description = "Obtiene productos del vendedor autenticado")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<ProductoDTO>> obtenerMisProductos(Pageable pageable) {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        log.info("🛍️ Obteniendo productos del vendedor ID: {}", vendedorId);

        Page<Producto> productos = productoService.obtenerProductosPorVendedor(vendedorId, pageable);
        Page<ProductoDTO> productosDTO = productos.map(ProductoDTO::fromEntity);

        return ResponseEntity.ok(productosDTO);
    }

    @PutMapping("/{id}/destacar")
    @Operation(summary = "Marcar/desmarcar producto como destacado")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> destacarProducto(@PathVariable Long id,
                                                                @RequestBody Map<String, Boolean> request) {
        Boolean destacado = request.get("destacado");
        log.info("🛍️ {} producto ID: {}", destacado ? "Destacando" : "Desmarcando", id);

        productoService.destacarProducto(id, destacado);

        Map<String, Object> response = Map.of(
                "mensaje", destacado ? "Producto destacado exitosamente" : "Producto desmarcado exitosamente",
                "destacado", destacado,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/stock")
    @Operation(summary = "Actualizar stock del producto")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> actualizarStock(@PathVariable Long id,
                                                               @RequestBody Map<String, Integer> request) {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        Integer nuevoStock = request.get("stock");

        log.info("🛍️ Actualizando stock del producto ID: {} a: {}", id, nuevoStock);

        // Verificar propiedad si no es admin
        if (!esAdmin()) {
            Producto producto = productoService.obtenerProductoPorId(id);
            if (!producto.getVendedor().getUsuarioId().equals(vendedorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        productoService.actualizarStock(id, nuevoStock);

        Map<String, Object> response = Map.of(
                "mensaje", "Stock actualizado exitosamente",
                "nuevoStock", nuevoStock,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    // === ENDPOINTS DE ESTADÍSTICAS ===

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de productos")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.info("🛍️ Obteniendo estadísticas de productos");

        Map<String, Object> estadisticas = Map.of(
                "total", productoService.contarProductos(),
                "activos", productoService.contarProductosActivos(),
                "destacados", productoService.contarProductosDestacados(),
                "agotados", productoService.contarProductosAgotados(),
                "stockTotal", productoService.obtenerStockTotal(),
                "precioPromedio", productoService.obtenerPrecioPromedio(),
                "porCategoria", productoService.obtenerEstadisticasPorCategoria(),
                "porVendedor", productoService.obtenerEstadisticasPorVendedor(),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/mas-vendidos")
    @Operation(summary = "Obtener productos más vendidos")
    public ResponseEntity<List<Object[]>> obtenerProductosMasVendidos() {
        log.info("🛍️ Obteniendo productos más vendidos");

        List<Object[]> productos = productoService.obtenerProductosMasVendidos();

        return ResponseEntity.ok(productos);
    }

    // Métodos auxiliares

    private Long obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
        return usuario.getUsuarioId();
    }

    private boolean esAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().contains("ADMIN_TOTAL"));
    }
}
