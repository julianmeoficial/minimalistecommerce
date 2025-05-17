package com.digital.mecommerces.controller;

import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.service.RolUsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolUsuarioController {
    private final RolUsuarioService rolUsuarioService;

    public RolUsuarioController(RolUsuarioService rolUsuarioService) {
        this.rolUsuarioService = rolUsuarioService;
    }

    @GetMapping
    public ResponseEntity<List<RolUsuario>> listarRoles() {
        List<RolUsuario> roles = rolUsuarioService.obtenerRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolUsuario> obtenerRol(@PathVariable Long id) {
        RolUsuario rol = rolUsuarioService.obtenerRolPorId(id);
        return ResponseEntity.ok(rol);
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<RolUsuario> obtenerRolPorNombre(@PathVariable String nombre) {
        RolUsuario rol = rolUsuarioService.obtenerRolPorNombre(nombre);
        return ResponseEntity.ok(rol);
    }

    @PostMapping
    public ResponseEntity<RolUsuario> crearRol(@RequestBody RolUsuario rol) {
        RolUsuario nuevoRol = rolUsuarioService.crearRol(rol);
        return new ResponseEntity<>(nuevoRol, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolUsuario> actualizarRol(@PathVariable Long id, @RequestBody RolUsuario rol) {
        RolUsuario rolActualizado = rolUsuarioService.actualizarRol(id, rol);
        return ResponseEntity.ok(rolActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRol(@PathVariable Long id) {
        rolUsuarioService.eliminarRol(id);
        return ResponseEntity.noContent().build();
    }
}
