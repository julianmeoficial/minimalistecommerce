package com.digital.mecommerces.controller;

import com.digital.mecommerces.dto.CompradorDetallesDTO;
import com.digital.mecommerces.model.CompradorDetalles;
import com.digital.mecommerces.service.CompradorDetallesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comprador-detalles")
public class CompradorDetallesController {
    private final CompradorDetallesService compradorDetallesService;

    public CompradorDetallesController(CompradorDetallesService compradorDetallesService) {
        this.compradorDetallesService = compradorDetallesService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<CompradorDetalles> obtenerDetallesComprador(@PathVariable Long usuarioId) {
        CompradorDetalles detalles = compradorDetallesService.obtenerDetallesPorUsuarioId(usuarioId);
        return ResponseEntity.ok(detalles);
    }

    @PostMapping("/{usuarioId}")
    public ResponseEntity<CompradorDetalles> crearDetallesComprador(
            @PathVariable Long usuarioId,
            @RequestBody CompradorDetallesDTO detallesDTO
    ) {
        CompradorDetalles detalles = new CompradorDetalles();
        detalles.setFechaNacimiento(detallesDTO.getFechaNacimiento());
        detalles.setDireccionEnvio(detallesDTO.getDireccionEnvio());
        detalles.setTelefono(detallesDTO.getTelefono());

        CompradorDetalles creados = compradorDetallesService.crearOActualizarDetalles(usuarioId, detalles);
        return new ResponseEntity<>(creados, HttpStatus.CREATED);
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<CompradorDetalles> actualizarDetallesComprador(
            @PathVariable Long usuarioId,
            @RequestBody CompradorDetallesDTO detallesDTO
    ) {
        CompradorDetalles detalles = compradorDetallesService.obtenerDetallesPorUsuarioId(usuarioId);
        detalles.setFechaNacimiento(detallesDTO.getFechaNacimiento());
        detalles.setDireccionEnvio(detallesDTO.getDireccionEnvio());
        detalles.setTelefono(detallesDTO.getTelefono());

        CompradorDetalles actualizados = compradorDetallesService.crearOActualizarDetalles(usuarioId, detalles);
        return ResponseEntity.ok(actualizados);
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> eliminarDetallesComprador(@PathVariable Long usuarioId) {
        compradorDetallesService.eliminarDetalles(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
