package com.digital.mecommerces.controller;

import com.digital.mecommerces.model.Permiso;
import com.digital.mecommerces.service.PermisoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permisos")
public class PermisoController {
    private final PermisoService permisoService;

    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

    @GetMapping
    public ResponseEntity<List<Permiso>> listarPermisos() {
        List<Permiso> permisos = permisoService.obtenerPermisos();
        return ResponseEntity.ok(permisos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permiso> obtenerPermiso(@PathVariable Long id) {
        Permiso permiso = permisoService.obtenerPermisoPorId(id);
        return ResponseEntity.ok(permiso);
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Permiso> obtenerPermisoPorCodigo(@PathVariable String codigo) {
        Permiso permiso = permisoService.obtenerPermisoPorCodigo(codigo);
        return ResponseEntity.ok(permiso);
    }

    @PostMapping
    public ResponseEntity<Permiso> crearPermiso(@RequestBody Permiso permiso) {
        Permiso nuevoPermiso = permisoService.crearPermiso(permiso);
        return new ResponseEntity<>(nuevoPermiso, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permiso> actualizarPermiso(@PathVariable Long id, @RequestBody Permiso permiso) {
        Permiso permisoActualizado = permisoService.actualizarPermiso(id, permiso);
        return ResponseEntity.ok(permisoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPermiso(@PathVariable Long id) {
        permisoService.eliminarPermiso(id);
        return ResponseEntity.noContent().build();
    }
}

