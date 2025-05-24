package com.digital.mecommerces.controller;

import com.digital.mecommerces.dto.ProductoSugeridoDTO;
import com.digital.mecommerces.service.ProductoSugeridoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos-sugeridos")
public class ProductoSugeridoController {

    private final ProductoSugeridoService productoSugeridoService;

    public ProductoSugeridoController(ProductoSugeridoService productoSugeridoService) {
        this.productoSugeridoService = productoSugeridoService;
    }

    @PostMapping
    public ResponseEntity<ProductoSugeridoDTO> crearSugerencia(@Valid @RequestBody ProductoSugeridoDTO sugerenciaDTO) {
        ProductoSugeridoDTO nuevaSugerencia = productoSugeridoService.crearSugerencia(sugerenciaDTO);
        return new ResponseEntity<>(nuevaSugerencia, HttpStatus.CREATED);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ProductoSugeridoDTO>> obtenerSugerenciasPorProducto(@PathVariable Long productoId) {
        List<ProductoSugeridoDTO> sugerencias = productoSugeridoService.obtenerSugerenciasPorProducto(productoId);
        return ResponseEntity.ok(sugerencias);
    }

    @GetMapping("/producto/{productoId}/tipo/{tipoRelacion}")
    public ResponseEntity<List<ProductoSugeridoDTO>> obtenerSugerenciasPorTipo(
            @PathVariable Long productoId,
            @PathVariable String tipoRelacion) {

        List<ProductoSugeridoDTO> sugerencias = productoSugeridoService.obtenerSugerenciasPorTipo(productoId, tipoRelacion);
        return ResponseEntity.ok(sugerencias);
    }

    @GetMapping("/producto/{productoId}/top")
    public ResponseEntity<List<ProductoSugeridoDTO>> obtenerTopSugerencias(
            @PathVariable Long productoId,
            @RequestParam(required = false, defaultValue = "5") Integer limite) {

        List<ProductoSugeridoDTO> sugerencias = productoSugeridoService.obtenerTopSugerencias(productoId, limite);
        return ResponseEntity.ok(sugerencias);
    }

    @PutMapping("/{sugerenciaId}")
    public ResponseEntity<ProductoSugeridoDTO> actualizarSugerencia(
            @PathVariable Long sugerenciaId,
            @Valid @RequestBody ProductoSugeridoDTO sugerenciaDTO) {

        ProductoSugeridoDTO sugerenciaActualizada = productoSugeridoService.actualizarSugerencia(sugerenciaId, sugerenciaDTO);
        return ResponseEntity.ok(sugerenciaActualizada);
    }

    @DeleteMapping("/{sugerenciaId}")
    public ResponseEntity<Map<String, String>> eliminarSugerencia(@PathVariable Long sugerenciaId) {
        productoSugeridoService.eliminarSugerencia(sugerenciaId);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Sugerencia eliminada exitosamente");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{sugerenciaId}/desactivar")
    public ResponseEntity<Map<String, String>> desactivarSugerencia(@PathVariable Long sugerenciaId) {
        productoSugeridoService.desactivarSugerencia(sugerenciaId);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Sugerencia desactivada exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mas-sugeridos")
    public ResponseEntity<List<ProductoSugeridoDTO>> obtenerProductosMasSugeridos(
            @RequestParam(required = false, defaultValue = "10") Integer limite) {

        List<ProductoSugeridoDTO> productos = productoSugeridoService.obtenerProductosMasSugeridos(limite);
        return ResponseEntity.ok(productos);
    }

    @PostMapping("/bidireccional")
    public ResponseEntity<Map<String, String>> crearSugerenciaBidireccional(
            @RequestParam Long producto1Id,
            @RequestParam Long producto2Id,
            @RequestParam(required = false, defaultValue = "COMPLEMENTO") String tipoRelacion,
            @RequestParam(required = false, defaultValue = "0") Integer prioridad) {

        productoSugeridoService.crearSugerenciaBidireccional(producto1Id, producto2Id, tipoRelacion, prioridad);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Sugerencia bidireccional creada exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoSugeridoDTO>> obtenerSugerenciasPorCategoria(@PathVariable Long categoriaId) {
        List<ProductoSugeridoDTO> sugerencias = productoSugeridoService.obtenerSugerenciasPorCategoria(categoriaId);
        return ResponseEntity.ok(sugerencias);
    }

    @GetMapping("/tipos")
    public ResponseEntity<Map<String, String>> obtenerTiposRelacion() {
        Map<String, String> tipos = new HashMap<>();
        tipos.put("COMPLEMENTO", "Productos que se complementan entre sí");
        tipos.put("SIMILAR", "Productos similares o alternativos");
        tipos.put("ACCESORIO", "Accesorios para el producto base");
        tipos.put("REEMPLAZO", "Productos que pueden reemplazar al base");
        tipos.put("UPGRADE", "Versión mejorada del producto base");

        return ResponseEntity.ok(tipos);
    }
}
