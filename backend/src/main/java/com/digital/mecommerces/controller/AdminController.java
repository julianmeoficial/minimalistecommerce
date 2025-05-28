package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.ProductoDTO;
import com.digital.mecommerces.dto.UsuarioDTO;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.OrdenRepository;
import com.digital.mecommerces.service.*;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para funcionalidades de administrador
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administración", description = "APIs para gestión administrativa del sistema")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class AdminController {

    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final OrdenRepository ordenRepository;
    private final RolUsuarioService rolUsuarioService;
    private final CategoriaProductoService categoriaProductoService;
    private final AdminDetallesService adminDetallesService;

    public AdminController(UsuarioService usuarioService,
                           ProductoService productoService,
                           OrdenRepository ordenRepository,
                           RolUsuarioService rolUsuarioService,
                           CategoriaProductoService categoriaProductoService,
                           AdminDetallesService adminDetallesService) {
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.ordenRepository = ordenRepository;
        this.rolUsuarioService = rolUsuarioService;
        this.categoriaProductoService = categoriaProductoService;
        this.adminDetallesService = adminDetallesService;
    }

    // === GESTIÓN DE USUARIOS ===

    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene lista completa de usuarios del sistema")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        log.info("👑 Admin solicitando lista de usuarios");

        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(UsuarioDTO::fromEntity)
                .toList();

        log.info("✅ {} usuarios encontrados", usuariosDTO.size());
        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable Long id) {
        log.info("👑 Admin obteniendo usuario ID: {}", id);

        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        UsuarioDTO usuarioDTO = UsuarioDTO.fromEntity(usuario);

        return ResponseEntity.ok(usuarioDTO);
    }

    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Actualizar usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@PathVariable Long id,
                                                        @Valid @RequestBody UsuarioDTO usuarioDTO) {
        log.info("👑 Admin actualizando usuario ID: {}", id);

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

        log.info("✅ Usuario actualizado exitosamente: {}", actualizado.getEmail());
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/usuarios/{id}")
    @Operation(summary = "Eliminar usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("👑 Admin eliminando usuario ID: {}", id);

        usuarioService.eliminarUsuario(id);

        log.info("✅ Usuario eliminado exitosamente");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/usuarios/{id}/activar")
    @Operation(summary = "Activar usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<Map<String, Object>> activarUsuario(@PathVariable Long id) {
        log.info("👑 Admin activando usuario ID: {}", id);

        usuarioService.activarUsuario(id);

        Map<String, Object> response = Map.of(
                "mensaje", "Usuario activado exitosamente",
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/usuarios/{id}/desactivar")
    @Operation(summary = "Desactivar usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<Map<String, Object>> desactivarUsuario(@PathVariable Long id) {
        log.info("👑 Admin desactivando usuario ID: {}", id);

        usuarioService.desactivarUsuario(id);

        Map<String, Object> response = Map.of(
                "mensaje", "Usuario desactivado exitosamente",
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    // === GESTIÓN DE PRODUCTOS ===

    @GetMapping("/productos")
    @Operation(summary = "Listar todos los productos")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Page<ProductoDTO>> listarProductos(Pageable pageable) {
        log.info("👑 Admin obteniendo productos con paginación");

        Page<Producto> productos = productoService.obtenerProductosActivos(pageable);
        Page<ProductoDTO> productosDTO = productos.map(ProductoDTO::fromEntity);

        log.info("✅ {} productos encontrados", productosDTO.getTotalElements());
        return ResponseEntity.ok(productosDTO);
    }

    @GetMapping("/productos/{id}")
    @Operation(summary = "Obtener producto por ID")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<ProductoDTO> obtenerProducto(@PathVariable Long id) {
        log.info("👑 Admin obteniendo producto ID: {}", id);

        Producto producto = productoService.obtenerProductoPorId(id);
        ProductoDTO productoDTO = ProductoDTO.fromEntity(producto);

        return ResponseEntity.ok(productoDTO);
    }

    @PostMapping("/productos")
    @Operation(summary = "Crear producto como administrador")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        log.info("👑 Admin creando producto: {}", productoDTO.getProductoNombre());

        Producto producto = new Producto();
        producto.setProductoNombre(productoDTO.getProductoNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStock(productoDTO.getStock());

        // Asignar categoría
        CategoriaProducto categoria = categoriaProductoService.obtenerCategoriaPorId(productoDTO.getCategoriaId());
        producto.setCategoria(categoria);

        // Asignar vendedor
        Usuario vendedor = usuarioService.obtenerUsuarioPorId(productoDTO.getVendedorId());
        producto.setVendedor(vendedor);

        Producto nuevoProducto = productoService.crearProducto(producto);
        ProductoDTO respuesta = ProductoDTO.fromEntity(nuevoProducto);

        log.info("✅ Producto creado por admin: {}", nuevoProducto.getProductoId());
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/productos/{id}")
    @Operation(summary = "Actualizar producto")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Long id,
                                                          @Valid @RequestBody ProductoDTO productoDTO) {
        log.info("👑 Admin actualizando producto ID: {}", id);

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

        log.info("✅ Producto actualizado por admin");
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/productos/{id}")
    @Operation(summary = "Eliminar producto")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        log.info("👑 Admin eliminando producto ID: {}", id);

        productoService.eliminarProducto(id);

        log.info("✅ Producto eliminado por admin");
        return ResponseEntity.noContent().build();
    }

    // === GESTIÓN DE ÓRDENES ===

    @GetMapping("/ordenes")
    @Operation(summary = "Listar todas las órdenes")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<Orden>> listarOrdenes() {
        log.info("👑 Admin obteniendo todas las órdenes");

        List<Orden> ordenes = ordenRepository.findAll();

        log.info("✅ {} órdenes encontradas", ordenes.size());
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/ordenes/{id}")
    @Operation(summary = "Obtener orden por ID")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Orden> obtenerOrden(@PathVariable Long id) {
        log.info("👑 Admin obteniendo orden ID: {}", id);

        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        return ResponseEntity.ok(orden);
    }

    @PutMapping("/ordenes/{id}/estado")
    @Operation(summary = "Actualizar estado de orden")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Orden> actualizarEstadoOrden(@PathVariable Long id,
                                                       @RequestParam String estado) {
        log.info("👑 Admin actualizando estado de orden {} a: {}", id, estado);

        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        orden.setEstado(estado);
        orden = ordenRepository.save(orden);

        log.info("✅ Estado de orden actualizado");
        return ResponseEntity.ok(orden);
    }

    // === DASHBOARD ADMINISTRATIVO ===

    @GetMapping("/dashboard")
    @Operation(summary = "Obtener datos del dashboard administrativo")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerDashboard() {
        log.info("👑 Admin obteniendo datos del dashboard");

        Map<String, Object> dashboard = new HashMap<>();

        try {
            // Estadísticas de usuarios por rol
            Map<String, Long> usuariosPorRol = new HashMap<>();
            usuariosPorRol.put("ADMINISTRADOR", usuarioService.contarAdministradores());
            usuariosPorRol.put("VENDEDOR", usuarioService.contarVendedores());
            usuariosPorRol.put("COMPRADOR", usuarioService.contarCompradores());

            // Estadísticas de productos
            Map<String, Object> productosStats = new HashMap<>();
            productosStats.put("total", productoService.contarProductosActivos());
            productosStats.put("destacados", productoService.contarProductosDestacados());
            productosStats.put("agotados", productoService.contarProductosAgotados());
            productosStats.put("precioPromedio", productoService.obtenerPrecioPromedio());

            // Estadísticas de órdenes
            List<Orden> ordenes = ordenRepository.findAll();
            Map<String, Long> ordenesPorEstado = new HashMap<>();
            ordenes.forEach(orden -> {
                String estado = orden.getEstado();
                ordenesPorEstado.merge(estado, 1L, Long::sum);
            });

            // Ingresos totales
            double ingresosTotales = ordenes.stream()
                    .mapToDouble(Orden::getTotal)
                    .sum();

            // Compilar dashboard
            dashboard.put("usuariosPorRol", usuariosPorRol);
            dashboard.put("productosStats", productosStats);
            dashboard.put("ordenesPorEstado", ordenesPorEstado);
            dashboard.put("ingresosTotales", ingresosTotales);
            dashboard.put("totalUsuarios", usuarioService.contarUsuarios());
            dashboard.put("totalOrdenes", ordenes.size());
            dashboard.put("timestamp", LocalDateTime.now());

            log.info("✅ Dashboard generado exitosamente");
            return ResponseEntity.ok(dashboard);

        } catch (Exception e) {
            log.error("❌ Error generando dashboard: {}", e.getMessage());
            dashboard.put("error", "Error generando estadísticas");
            dashboard.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dashboard);
        }
    }

    // === ESTADÍSTICAS ESPECÍFICAS ===

    @GetMapping("/estadisticas/usuarios")
    @Operation(summary = "Obtener estadísticas detalladas de usuarios")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasUsuarios() {
        log.info("👑 Admin obteniendo estadísticas de usuarios");

        Map<String, Object> stats = new HashMap<>();
        stats.put("activos", usuarioService.contarUsuariosActivos());
        stats.put("inactivos", usuarioService.contarUsuariosInactivos());
        stats.put("administradores", usuarioService.contarAdministradores());
        stats.put("vendedores", usuarioService.contarVendedores());
        stats.put("compradores", usuarioService.contarCompradores());
        stats.put("recientes", usuarioService.obtenerUsuariosRecientes());
        stats.put("ultimosLogins", usuarioService.obtenerUltimosLogins());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/estadisticas/productos")
    @Operation(summary = "Obtener estadísticas detalladas de productos")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasProductos() {
        log.info("👑 Admin obteniendo estadísticas de productos");

        Map<String, Object> stats = new HashMap<>();
        stats.put("activos", productoService.contarProductosActivos());
        stats.put("destacados", productoService.contarProductosDestacados());
        stats.put("agotados", productoService.contarProductosAgotados());
        stats.put("stockTotal", productoService.obtenerStockTotal());
        stats.put("precioPromedio", productoService.obtenerPrecioPromedio());
        stats.put("porCategoria", productoService.obtenerEstadisticasPorCategoria());
        stats.put("porVendedor", productoService.obtenerEstadisticasPorVendedor());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/configuracion/sistema")
    @Operation(summary = "Obtener configuración del sistema")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerConfiguracionSistema() {
        log.info("👑 Admin obteniendo configuración del sistema");

        Map<String, Object> config = new HashMap<>();
        config.put("rolesDelSistema", rolUsuarioService.obtenerRolesDelSistema());
        config.put("totalRoles", rolUsuarioService.contarRoles());
        config.put("integridad", rolUsuarioService.validarIntegridadDelSistema());
        config.put("version", "3.0");
        config.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(config);
    }
}
