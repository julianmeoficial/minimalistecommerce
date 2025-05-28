package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.CategoriaProductoDTO;
import com.digital.mecommerces.enums.TipoCategoria;
import com.digital.mecommerces.model.CategoriaProducto;
import com.digital.mecommerces.service.CategoriaProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador para gestión de categorías de productos
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "APIs para gestión de categorías de productos")
@Slf4j
public class CategoriaProductoController {

    private final CategoriaProductoService categoriaProductoService;

    public CategoriaProductoController(CategoriaProductoService categoriaProductoService) {
        this.categoriaProductoService = categoriaProductoService;
    }

    // === ENDPOINTS PÚBLICOS ===

    @GetMapping
    @Operation(summary = "Listar todas las categorías activas", description = "Endpoint público para obtener categorías")
    public ResponseEntity<List<CategoriaProductoDTO>> listarCategorias() {
        log.info("📂 Obteniendo todas las categorías activas");

        List<CategoriaProducto> categorias = categoriaProductoService.obtenerCategoriasActivas();
        List<CategoriaProductoDTO> categoriasDTO = categorias.stream()
                .map(CategoriaProductoDTO::fromEntity)
                .map(CategoriaProductoDTO::toPublic)
                .toList();

        log.info("✅ {} categorías encontradas", categoriasDTO.size());
        return ResponseEntity.ok(categoriasDTO);
    }

    @GetMapping("/principales")
    @Operation(summary = "Listar categorías principales", description = "Obtiene categorías sin padre (nivel raíz)")
    public ResponseEntity<List<CategoriaProductoDTO>> listarCategoriasPrincipales() {
        log.info("📂 Obteniendo categorías principales");

        List<CategoriaProducto> categorias = categoriaProductoService.obtenerCategoriasPrincipales();
        List<CategoriaProductoDTO> categoriasDTO = categorias.stream()
                .map(CategoriaProductoDTO::fromEntity)
                .map(CategoriaProductoDTO::toPublic)
                .toList();

        return ResponseEntity.ok(categoriasDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID")
    public ResponseEntity<CategoriaProductoDTO> obtenerCategoria(@PathVariable Long id) {
        log.info("📂 Obteniendo categoría ID: {}", id);

        CategoriaProducto categoria = categoriaProductoService.obtenerCategoriaPorId(id);
        CategoriaProductoDTO categoriaDTO = CategoriaProductoDTO.fromEntity(categoria);

        return ResponseEntity.ok(categoriaDTO.toPublic());
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Obtener categoría por slug")
    public ResponseEntity<CategoriaProductoDTO> obtenerCategoriaPorSlug(@PathVariable String slug) {
        log.info("📂 Obteniendo categoría por slug: {}", slug);

        CategoriaProducto categoria = categoriaProductoService.obtenerCategoriaPorSlug(slug);
        CategoriaProductoDTO categoriaDTO = CategoriaProductoDTO.fromEntity(categoria);

        return ResponseEntity.ok(categoriaDTO.toPublic());
    }

    @GetMapping("/{id}/subcategorias")
    @Operation(summary = "Obtener subcategorías de una categoría")
    public ResponseEntity<List<CategoriaProductoDTO>> obtenerSubcategorias(@PathVariable Long id) {
        log.info("📂 Obteniendo subcategorías de categoría ID: {}", id);

        List<CategoriaProducto> subcategorias = categoriaProductoService.obtenerSubcategorias(id);
        List<CategoriaProductoDTO> subcategoriasDTO = subcategorias.stream()
                .map(CategoriaProductoDTO::fromEntity)
                .map(CategoriaProductoDTO::toPublic)
                .toList();

        return ResponseEntity.ok(subcategoriasDTO);
    }

    @GetMapping("/jerarquia")
    @Operation(summary = "Obtener estructura jerárquica completa")
    public ResponseEntity<List<CategoriaProductoDTO>> obtenerJerarquiaCompleta() {
        log.info("📂 Obteniendo jerarquía completa de categorías");

        List<CategoriaProducto> categorias = categoriaProductoService.obtenerCategoriasPrincipales();
        List<CategoriaProductoDTO> jerarquia = categorias.stream()
                .map(CategoriaProductoDTO::fromEntity)
                .map(CategoriaProductoDTO::toHierarchical)
                .toList();

        return ResponseEntity.ok(jerarquia);
    }

    // === ENDPOINTS PARA CATEGORÍAS DEL SISTEMA ===

    @GetMapping("/sistema")
    @Operation(summary = "Obtener categorías del sistema optimizado")
    public ResponseEntity<List<CategoriaProductoDTO>> obtenerCategoriasDelSistema() {
        log.info("📂 Obteniendo categorías del sistema optimizado");

        List<CategoriaProducto> categorias = categoriaProductoService.obtenerCategoriasDelSistema();
        List<CategoriaProductoDTO> categoriasDTO = categorias.stream()
                .map(CategoriaProductoDTO::fromEntity)
                .map(CategoriaProductoDTO::toPublic)
                .toList();

        return ResponseEntity.ok(categoriasDTO);
    }

    @GetMapping("/sistema/{tipoCategoria}")
    @Operation(summary = "Obtener categoría específica del sistema")
    public ResponseEntity<CategoriaProductoDTO> obtenerCategoriaDelSistema(@PathVariable String tipoCategoria) {
        try {
            log.info("📂 Obteniendo categoría del sistema: {}", tipoCategoria);

            TipoCategoria tipo = TipoCategoria.valueOf(tipoCategoria.toUpperCase());
            CategoriaProducto categoria = categoriaProductoService.obtenerCategoriaDelSistema(tipo);
            CategoriaProductoDTO categoriaDTO = CategoriaProductoDTO.fromEntity(categoria);

            return ResponseEntity.ok(categoriaDTO.toPublic());
        } catch (IllegalArgumentException e) {
            log.warn("❌ Tipo de categoría inválido: {}", tipoCategoria);
            return ResponseEntity.badRequest().build();
        }
    }

    // === ENDPOINTS ADMINISTRATIVOS ===

    @PostMapping
    @Operation(summary = "Crear nueva categoría")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_CATEGORIAS + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CategoriaProductoDTO> crearCategoria(@Valid @RequestBody CategoriaProductoDTO categoriaDTO) {
        log.info("📂 Creando nueva categoría: {}", categoriaDTO.getNombre());

        CategoriaProducto categoria = new CategoriaProducto();
        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        categoria.setSlug(categoriaDTO.getSlug());
        categoria.setImagen(categoriaDTO.getImagen());

        if (categoriaDTO.getCategoriaPadreId() != null) {
            CategoriaProducto padre = categoriaProductoService.obtenerCategoriaPorId(categoriaDTO.getCategoriaPadreId());
            categoria.setCategoriaPadre(padre);
        }

        CategoriaProducto nuevaCategoria = categoriaProductoService.crearCategoria(categoria);
        CategoriaProductoDTO respuesta = CategoriaProductoDTO.fromEntity(nuevaCategoria);

        log.info("✅ Categoría creada con ID: {}", nuevaCategoria.getCategoriaId());
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_CATEGORIAS + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CategoriaProductoDTO> actualizarCategoria(@PathVariable Long id,
                                                                    @Valid @RequestBody CategoriaProductoDTO categoriaDTO) {
        log.info("📂 Actualizando categoría ID: {}", id);

        CategoriaProducto categoriaDetails = new CategoriaProducto();
        categoriaDetails.setNombre(categoriaDTO.getNombre());
        categoriaDetails.setDescripcion(categoriaDTO.getDescripcion());
        categoriaDetails.setSlug(categoriaDTO.getSlug());
        categoriaDetails.setImagen(categoriaDTO.getImagen());
        categoriaDetails.setActivo(categoriaDTO.getActivo());

        CategoriaProducto categoriaActualizada = categoriaProductoService.actualizarCategoria(id, categoriaDetails);
        CategoriaProductoDTO respuesta = CategoriaProductoDTO.fromEntity(categoriaActualizada);

        log.info("✅ Categoría actualizada exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        log.info("📂 Eliminando categoría ID: {}", id);

        categoriaProductoService.eliminarCategoria(id);

        log.info("✅ Categoría eliminada exitosamente");
        return ResponseEntity.noContent().build();
    }

    // === ENDPOINTS DE ESTADÍSTICAS ===

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de categorías")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_CATEGORIAS + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Object> obtenerEstadisticas() {
        log.info("📂 Obteniendo estadísticas de categorías");

        var estadisticas = Map.of(
                "total", categoriaProductoService.contarCategorias(),
                "activas", categoriaProductoService.contarCategoriasActivas(),
                "principales", categoriaProductoService.contarCategoriasPrincipales(),
                "conProductos", categoriaProductoService.contarCategoriasConProductos(),
                "delSistema", categoriaProductoService.contarCategoriasDelSistema(),
                "porTipo", categoriaProductoService.obtenerEstadisticasPorTipo()
        );

        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/mas-utilizadas")
    @Operation(summary = "Obtener categorías más utilizadas")
    public ResponseEntity<List<Object[]>> obtenerCategoriaMasUtilizadas() {
        log.info("📂 Obteniendo categorías más utilizadas");

        List<Object[]> categoriasPopulares = categoriaProductoService.obtenerCategoriasMasUtilizadas();

        return ResponseEntity.ok(categoriasPopulares);
    }
}
