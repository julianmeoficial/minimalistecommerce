package com.digital.mecommerces.controller;

import com.digital.mecommerces.dto.VendedorDetallesDTO;
import com.digital.mecommerces.model.VendedorDetalles;
import com.digital.mecommerces.service.VendedorDetallesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendedor-detalles")
public class VendedorDetallesController {
    private final VendedorDetallesService vendedorDetallesService;

    public VendedorDetallesController(VendedorDetallesService vendedorDetallesService) {
        this.vendedorDetallesService = vendedorDetallesService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<VendedorDetalles> obtenerDetallesVendedor(@PathVariable Long usuarioId) {
        VendedorDetalles detalles = vendedorDetallesService.obtenerDetallesPorUsuarioId(usuarioId);
        return ResponseEntity.ok(detalles);
    }

    @PostMapping("/{usuarioId}")
    public ResponseEntity<VendedorDetalles> crearDetallesVendedor(
            @PathVariable Long usuarioId,
            @RequestBody VendedorDetallesDTO detallesDTO
    ) {
        VendedorDetalles detalles = new VendedorDetalles();
        detalles.setNumRegistroFiscal(detallesDTO.getNumRegistroFiscal());
        detalles.setEspecialidad(detallesDTO.getEspecialidad());
        detalles.setDireccionComercial(detallesDTO.getDireccionComercial());

        VendedorDetalles creados = vendedorDetallesService.crearOActualizarDetalles(usuarioId, detalles);
        return new ResponseEntity<>(creados, HttpStatus.CREATED);
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<VendedorDetalles> actualizarDetallesVendedor(
            @PathVariable Long usuarioId,
            @RequestBody VendedorDetallesDTO detallesDTO
    ) {
        VendedorDetalles detalles = vendedorDetallesService.obtenerDetallesPorUsuarioId(usuarioId);
        detalles.setNumRegistroFiscal(detallesDTO.getNumRegistroFiscal());
        detalles.setEspecialidad(detallesDTO.getEspecialidad());
        detalles.setDireccionComercial(detallesDTO.getDireccionComercial());

        VendedorDetalles actualizados = vendedorDetallesService.crearOActualizarDetalles(usuarioId, detalles);
        return ResponseEntity.ok(actualizados);
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> eliminarDetallesVendedor(@PathVariable Long usuarioId) {
        vendedorDetallesService.eliminarDetalles(usuarioId);
        return ResponseEntity.noContent().build();
    }
}

