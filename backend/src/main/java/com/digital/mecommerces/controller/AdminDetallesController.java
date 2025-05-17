package com.digital.mecommerces.controller;

import com.digital.mecommerces.dto.AdminDetallesDTO;
import com.digital.mecommerces.model.AdminDetalles;
import com.digital.mecommerces.service.AdminDetallesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin-detalles")
public class AdminDetallesController {
    private final AdminDetallesService adminDetallesService;

    public AdminDetallesController(AdminDetallesService adminDetallesService) {
        this.adminDetallesService = adminDetallesService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<AdminDetalles> obtenerDetallesAdmin(@PathVariable Long usuarioId) {
        AdminDetalles detalles = adminDetallesService.obtenerDetallesPorUsuarioId(usuarioId);
        return ResponseEntity.ok(detalles);
    }

    @PostMapping("/{usuarioId}")
    public ResponseEntity<AdminDetalles> crearDetallesAdmin(
            @PathVariable Long usuarioId,
            @RequestBody AdminDetallesDTO detallesDTO
    ) {
        AdminDetalles detalles = new AdminDetalles();
        detalles.setRegion(detallesDTO.getRegion());
        detalles.setNivelAcceso(detallesDTO.getNivelAcceso());

        AdminDetalles creados = adminDetallesService.crearOActualizarDetalles(usuarioId, detalles);
        return new ResponseEntity<>(creados, HttpStatus.CREATED);
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<AdminDetalles> actualizarDetallesAdmin(
            @PathVariable Long usuarioId,
            @RequestBody AdminDetallesDTO detallesDTO
    ) {
        AdminDetalles detalles = adminDetallesService.obtenerDetallesPorUsuarioId(usuarioId);
        detalles.setRegion(detallesDTO.getRegion());
        detalles.setNivelAcceso(detallesDTO.getNivelAcceso());

        AdminDetalles actualizados = adminDetallesService.crearOActualizarDetalles(usuarioId, detalles);
        return ResponseEntity.ok(actualizados);
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> eliminarDetallesAdmin(@PathVariable Long usuarioId) {
        adminDetallesService.eliminarDetalles(usuarioId);
        return ResponseEntity.noContent().build();
    }
}

