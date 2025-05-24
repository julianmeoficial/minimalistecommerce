package com.digital.mecommerces.controller;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.Permiso;
import com.digital.mecommerces.service.PermisoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        // CORREGIDO: Manejar Optional correctamente
        Optional<Permiso> permisoOpt = permisoService.obtenerPermisoPorCodigo(codigo);
        if (permisoOpt.isPresent()) {
            return ResponseEntity.ok(permisoOpt.get());
        } else {
            throw new ResourceNotFoundException("Permiso no encontrado con código: " + codigo);
        }
    }

    @GetMapping("/padre")
    public ResponseEntity<List<Permiso>> obtenerPermisosPadre() {
        List<Permiso> permisosPadre = permisoService.obtenerPermisosPadre();
        return ResponseEntity.ok(permisosPadre);
    }

    @GetMapping("/hijos/{permisopadreId}")
    public ResponseEntity<List<Permiso>> obtenerPermisosHijos(@PathVariable Long permisopadreId) {
        List<Permiso> permisosHijos = permisoService.obtenerPermisosHijos(permisopadreId);
        return ResponseEntity.ok(permisosHijos);
    }

    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<List<Permiso>> obtenerPermisosPorNivel(@PathVariable Integer nivel) {
        List<Permiso> permisos = permisoService.obtenerPermisosPorNivel(nivel);
        return ResponseEntity.ok(permisos);
    }

    @GetMapping("/jerarquia")
    public ResponseEntity<List<Permiso>> obtenerJerarquiaCompleta() {
        List<Permiso> jerarquia = permisoService.obtenerJerarquiaCompleta();
        return ResponseEntity.ok(jerarquia);
    }

    @GetMapping("/hoja")
    public ResponseEntity<List<Permiso>> obtenerPermisosHoja() {
        List<Permiso> permisosHoja = permisoService.obtenerPermisosHoja();
        return ResponseEntity.ok(permisosHoja);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Permiso>> buscarPermisos(@RequestParam String termino) {
        List<Permiso> permisos = permisoService.buscarPermisos(termino);
        return ResponseEntity.ok(permisos);
    }

    @PostMapping
    public ResponseEntity<Permiso> crearPermiso(@RequestBody Permiso permiso) {
        Permiso nuevoPermiso = permisoService.crearPermiso(permiso);
        return new ResponseEntity<>(nuevoPermiso, HttpStatus.CREATED);
    }

    @PostMapping("/con-padre")
    public ResponseEntity<Permiso> crearPermisoConPadre(
            @RequestParam String codigo,
            @RequestParam String descripcion,
            @RequestParam Integer nivel,
            @RequestParam(required = false) Long permisopadreId) {

        Permiso nuevoPermiso = permisoService.crearPermisoConPadre(codigo, descripcion, nivel, permisopadreId);
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

    @GetMapping("/existe/{codigo}")
    public ResponseEntity<Boolean> existePermiso(@PathVariable String codigo) {
        boolean existe = permisoService.existePermiso(codigo);
        return ResponseEntity.ok(existe);
    }

    @GetMapping("/contar")
    public ResponseEntity<Long> contarPermisos() {
        long total = permisoService.contarPermisos();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/contar/nivel/{nivel}")
    public ResponseEntity<Long> contarPermisosPorNivel(@PathVariable Integer nivel) {
        long total = permisoService.contarPermisosPorNivel(nivel);
        return ResponseEntity.ok(total);
    }
}