package com.digital.mecommerces.controller;

import com.digital.mecommerces.model.CategoriaProducto;
import com.digital.mecommerces.service.CategoriaProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaProductoController {
    private final CategoriaProductoService categoriaProductoService;

    public CategoriaProductoController(CategoriaProductoService categoriaProductoService) {
        this.categoriaProductoService = categoriaProductoService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaProducto>> listarCategorias() {
        List<CategoriaProducto> categorias = categoriaProductoService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/principales")
    public ResponseEntity<List<CategoriaProducto>> listarCategoriasPrincipales() {
        List<CategoriaProducto> categorias = categoriaProductoService.obtenerCategoriasPrincipales();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProducto> obtenerCategoria(@PathVariable Long id) {
        CategoriaProducto categoria = categoriaProductoService.obtenerCategoriaPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/{id}/subcategorias")
    public ResponseEntity<List<CategoriaProducto>> obtenerSubcategorias(@PathVariable Long id) {
        List<CategoriaProducto> subcategorias = categoriaProductoService.obtenerSubcategoriasDe(id);
        return ResponseEntity.ok(subcategorias);
    }

    @PostMapping
    public ResponseEntity<CategoriaProducto> crearCategoria(@RequestBody CategoriaProducto categoria) {
        CategoriaProducto nuevaCategoria = categoriaProductoService.crearCategoria(categoria);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProducto> actualizarCategoria(@PathVariable Long id, @RequestBody CategoriaProducto categoria) {
        CategoriaProducto categoriaActualizada = categoriaProductoService.actualizarCategoria(id, categoria);
        return ResponseEntity.ok(categoriaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaProductoService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
