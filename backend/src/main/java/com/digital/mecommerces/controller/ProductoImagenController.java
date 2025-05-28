package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.ProductoImagenDTO;
import com.digital.mecommerces.model.Producto;
import com.digital.mecommerces.model.ProductoImagen;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.service.ProductoImagenService;
import com.digital.mecommerces.service.ProductoService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para gestión de imágenes de productos
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/producto-imagenes")
@Tag(name = "Imágenes de Productos", description = "APIs para gestión de imágenes de productos")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class ProductoImagenController {

    private final ProductoImagenService productoImagenService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public ProductoImagenController(ProductoImagenService productoImagenService,
                                    ProductoService productoService,
                                    UsuarioService usuarioService) {
        this.productoImagenService = productoImagenService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Obtener imágenes de un producto", description = "Obtiene todas las imágenes de un producto específico")
    public ResponseEntity<List<ProductoImagenDTO>> obtenerImagenesProducto(@PathVariable Long productoId) {
        log.info("📸 Obteniendo imágenes del producto ID: {}", productoId);

        List<ProductoImagen> imagenes = productoImagenService.obtenerImagenesActivas(productoId);
        List<ProductoImagenDTO> imagenesDTO = imagenes.stream()
                .map(ProductoImagenDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(imagenesDTO);
    }

    @PostMapping
    @Operation(summary = "Agregar imagen a producto")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<ProductoImagenDTO> agregarImagen(@Valid @RequestBody ProductoImagenDTO imagenDTO) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("📸 Agregando imagen al producto ID: {} por usuario ID: {}", imagenDTO.getProductoId(), usuarioId);

        // ✅ CORREGIDO: obtenerProductoPorId (era obtenerProductoPorld)
        if (!esAdmin()) {
            Producto producto = productoService.obtenerProductoPorId(imagenDTO.getProductoId());
            if (!producto.getVendedor().getUsuarioId().equals(usuarioId)) {
                log.warn("❌ Vendedor {} intentó agregar imagen a producto {} que no le pertenece", usuarioId, imagenDTO.getProductoId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        ProductoImagen imagen = new ProductoImagen();
        imagen.setUrl(imagenDTO.getUrl());
        imagen.setDescripcion(imagenDTO.getDescripcion());
        imagen.setEsPrincipal(imagenDTO.getEsPrincipal());
        imagen.setTipo(imagenDTO.getTipo());
        imagen.setOrden(imagenDTO.getOrden());
        imagen.setActiva(imagenDTO.getActiva());
        imagen.setTamanio(imagenDTO.getTamanio());

        // ✅ CORREGIDO: obtenerProductoPorId (era obtenerProductoPorld)
        Producto producto = productoService.obtenerProductoPorId(imagenDTO.getProductoId());
        imagen.setProducto(producto);

        ProductoImagen nuevaImagen = productoImagenService.agregarImagen(imagen);
        ProductoImagenDTO respuesta = ProductoImagenDTO.fromEntity(nuevaImagen);

        log.info("✅ Imagen agregada con ID: {}", nuevaImagen.getImagenId());
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{imagenId}")
    @Operation(summary = "Actualizar imagen")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<ProductoImagenDTO> actualizarImagen(@PathVariable Long imagenId,
                                                              @Valid @RequestBody ProductoImagenDTO imagenDTO) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("📸 Actualizando imagen ID: {} por usuario ID: {}", imagenId, usuarioId);

        // Verificar permisos
        if (!esAdmin()) {
            ProductoImagen imagenExistente = productoImagenService.obtenerImagenPorId(imagenId);
            Producto producto = imagenExistente.getProducto();
            if (!producto.getVendedor().getUsuarioId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        ProductoImagen imagenDetails = new ProductoImagen();
        imagenDetails.setUrl(imagenDTO.getUrl());
        imagenDetails.setDescripcion(imagenDTO.getDescripcion());
        imagenDetails.setEsPrincipal(imagenDTO.getEsPrincipal());
        imagenDetails.setTipo(imagenDTO.getTipo());
        imagenDetails.setOrden(imagenDTO.getOrden());
        imagenDetails.setActiva(imagenDTO.getActiva());
        imagenDetails.setTamanio(imagenDTO.getTamanio());

        ProductoImagen imagenActualizada = productoImagenService.actualizarImagen(imagenId, imagenDetails);
        ProductoImagenDTO respuesta = ProductoImagenDTO.fromEntity(imagenActualizada);

        log.info("✅ Imagen actualizada exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{imagenId}")
    @Operation(summary = "Eliminar imagen")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Void> eliminarImagen(@PathVariable Long imagenId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("📸 Eliminando imagen ID: {} por usuario ID: {}", imagenId, usuarioId);

        // Verificar permisos
        if (!esAdmin()) {
            ProductoImagen imagen = productoImagenService.obtenerImagenPorId(imagenId);
            Producto producto = imagen.getProducto();
            if (!producto.getVendedor().getUsuarioId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        productoImagenService.eliminarImagen(imagenId);

        log.info("✅ Imagen eliminada exitosamente");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/producto/{productoId}/reordenar")
    @Operation(summary = "Reordenar imágenes de producto")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> reordenarImagenes(@PathVariable Long productoId,
                                                                 @RequestBody List<Long> ordenImagenes) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("📸 Reordenando imágenes del producto ID: {} por usuario ID: {}", productoId, usuarioId);

        // ✅ CORREGIDO: obtenerProductoPorId (era obtenerProductoPorld)
        if (!esAdmin()) {
            Producto producto = productoService.obtenerProductoPorId(productoId);
            if (!producto.getVendedor().getUsuarioId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        try {
            productoImagenService.reordenarImagenesProducto(productoId, ordenImagenes);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Imágenes reordenadas exitosamente");
            response.put("productoId", productoId);
            response.put("imagenesReordenadas", ordenImagenes.size());
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error reordenando imágenes: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error reordenando imágenes: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/producto/{productoId}/multiple")
    @Operation(summary = "Agregar múltiples imágenes a producto")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> agregarMultiplesImagenes(@PathVariable Long productoId,
                                                                        @RequestBody List<ProductoImagenDTO> imagenesDTO) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("📸 Agregando {} imágenes al producto ID: {} por usuario ID: {}",
                imagenesDTO.size(), productoId, usuarioId);

        // ✅ CORREGIDO: obtenerProductoPorId (era obtenerProductoPorld)
        if (!esAdmin()) {
            Producto producto = productoService.obtenerProductoPorId(productoId);
            if (!producto.getVendedor().getUsuarioId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        List<ProductoImagenDTO> imagenesCreadas = new ArrayList<>();
        int exitosos = 0;
        int fallidos = 0;

        for (ProductoImagenDTO imagenDTO : imagenesDTO) {
            try {
                ProductoImagen imagen = new ProductoImagen();
                imagen.setUrl(imagenDTO.getUrl());
                imagen.setDescripcion(imagenDTO.getDescripcion());
                imagen.setEsPrincipal(imagenDTO.getEsPrincipal());
                imagen.setTipo(imagenDTO.getTipo());
                imagen.setOrden(imagenDTO.getOrden());
                imagen.setActiva(imagenDTO.getActiva());
                imagen.setTamanio(imagenDTO.getTamanio());

                // ✅ CORREGIDO: obtenerProductoPorId (era obtenerProductoPorld)
                Producto producto = productoService.obtenerProductoPorId(productoId);
                imagen.setProducto(producto);

                ProductoImagen nuevaImagen = productoImagenService.agregarImagen(imagen);
                imagenesCreadas.add(ProductoImagenDTO.fromEntity(nuevaImagen));
                exitosos++;

            } catch (Exception e) {
                log.warn("⚠️ Error agregando imagen: {}", e.getMessage());
                fallidos++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Proceso de agregado múltiple completado");
        response.put("productoId", productoId);
        response.put("totalEnviadas", imagenesDTO.size());
        response.put("exitosos", exitosos);
        response.put("fallidos", fallidos);
        response.put("imagenesCreadas", imagenesCreadas);
        response.put("timestamp", LocalDateTime.now());

        HttpStatus status = fallidos > 0 ? HttpStatus.PARTIAL_CONTENT : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("/{imagenId}/principal")
    @Operation(summary = "Establecer imagen como principal")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> establecerImagenPrincipal(@PathVariable Long imagenId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("📸 Estableciendo imagen ID: {} como principal por usuario ID: {}", imagenId, usuarioId);

        // Verificar permisos
        if (!esAdmin()) {
            ProductoImagen imagen = productoImagenService.obtenerImagenPorId(imagenId);
            Producto producto = imagen.getProducto();
            if (!producto.getVendedor().getUsuarioId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        productoImagenService.establecerImagenPrincipal(imagenId);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Imagen establecida como principal exitosamente");
        response.put("imagenId", imagenId);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de imágenes")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.info("📸 Obteniendo estadísticas de imágenes");

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("total", productoImagenService.contarImagenes());
        estadisticas.put("activas", productoImagenService.contarImagenesActivas());
        estadisticas.put("principales", productoImagenService.contarImagenesPrincipales());
        estadisticas.put("porTipo", productoImagenService.obtenerEstadisticasPorTipo());
        estadisticas.put("porProducto", productoImagenService.obtenerEstadisticasPorProducto());
        estadisticas.put("productosSinImagenes", productoImagenService.contarProductosSinImagenes());
        estadisticas.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(estadisticas);
    }

    // === MÉTODOS AUXILIARES ===

    private Long obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
        return usuario.getUsuarioId();
    }

    private boolean esAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(RoleConstants.PERM_ADMIN_TOTAL));
    }
}
