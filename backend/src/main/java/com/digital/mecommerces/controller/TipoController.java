package com.digital.mecommerces.controller;

import com.digital.mecommerces.model.Tipo;
import com.digital.mecommerces.service.TipoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipos")
public class TipoController {

    private final TipoService tipoService;

    public TipoController(TipoService tipoService) {
        this.tipoService = tipoService;
    }

    // Endpoint para obtener todos los tipos
    @GetMapping
    public ResponseEntity<List<Tipo>> listarTipos() {
        List<Tipo> tipos = tipoService.obtenerTipos();
        return ResponseEntity.ok(tipos);
    }

    // Endpoint para obtener un tipo por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Tipo> obtenerTipo(@PathVariable Long id) {
        Tipo tipo = tipoService.obtenerTipoPorId(id);
        return ResponseEntity.ok(tipo);
    }

    // Endpoint para crear un nuevo tipo
    @PostMapping
    public ResponseEntity<Tipo> crearTipo(@RequestBody Tipo tipo) {
        Tipo nuevoTipo = tipoService.crearTipo(tipo);
        return new ResponseEntity<>(nuevoTipo, HttpStatus.CREATED);
    }

    // Endpoint para actualizar un tipo existente
    @PutMapping("/{id}")
    public ResponseEntity<Tipo> actualizarTipo(@PathVariable Long id, @RequestBody Tipo tipo) {
        Tipo actualizado = tipoService.actualizarTipo(id, tipo);
        return ResponseEntity.ok(actualizado);
    }

    // Endpoint para eliminar un tipo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipo(@PathVariable Long id) {
        tipoService.eliminarTipo(id);
        return ResponseEntity.noContent().build();
    }
}