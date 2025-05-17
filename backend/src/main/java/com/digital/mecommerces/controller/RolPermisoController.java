package com.digital.mecommerces.controller;

import com.digital.mecommerces.model.Permiso;
import com.digital.mecommerces.model.RolPermiso;
import com.digital.mecommerces.service.RolPermisoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles-permisos")
public class RolPermisoController {
    private final RolPermisoService rolPermisoService;

    public RolPermisoController(RolPermisoService rolPermisoService) {
        this.rolPermisoService = rolPermisoService;
    }

    @GetMapping
    public ResponseEntity<List<RolPermiso>> listarTodosRolPermisos() {
        List<RolPermiso> rolPermisos = rolPermisoService.obtenerTodosRolPermisos();
        return ResponseEntity.ok(rolPermisos);
    }

    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<RolPermiso>> listarPermisosPorRol(@PathVariable Long rolId) {
        List<RolPermiso> permisos = rolPermisoService.obtenerPermisosPorRol(rolId);
        return ResponseEntity.ok(permisos);
    }

    @GetMapping("/rol/{rolId}/permisos")
    public ResponseEntity<List<Permiso>> listarPermisosDeRol(@PathVariable Long rolId) {
        List<Permiso> permisos = rolPermisoService.obtenerPermisosDeRol(rolId);
        return ResponseEntity.ok(permisos);
    }

    @PostMapping("/rol/{rolId}/permiso/{permisoId}")
    public ResponseEntity<RolPermiso> asignarPermisoARol(@PathVariable Long rolId, @PathVariable Long permisoId) {
        RolPermiso rolPermiso = rolPermisoService.asignarPermisoARol(rolId, permisoId);
        return new ResponseEntity<>(rolPermiso, HttpStatus.CREATED);
    }

    @DeleteMapping("/rol/{rolId}/permiso/{permisoId}")
    public ResponseEntity<Void> eliminarPermisoDeRol(@PathVariable Long rolId, @PathVariable Long permisoId) {
        rolPermisoService.eliminarPermisoDeRol(rolId, permisoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rol/{rolId}/permisos")
    public ResponseEntity<Void> eliminarTodosPermisosDeRol(@PathVariable Long rolId) {
        rolPermisoService.eliminarTodosPermisosDeRol(rolId);
        return ResponseEntity.noContent().build();
    }
}

