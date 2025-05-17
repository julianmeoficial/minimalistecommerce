package com.digital.mecommerces.controller;

import com.digital.mecommerces.dto.ProductoDTO;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.Orden;
import com.digital.mecommerces.model.OrdenDetalle;
import com.digital.mecommerces.model.Producto;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.OrdenRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import com.digital.mecommerces.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vendedor")
public class VendedorController {
    private final ProductoService productoService;
    private final UsuarioRepository usuarioRepository;
    private final OrdenRepository ordenRepository;

    public VendedorController(ProductoService productoService,
                              UsuarioRepository usuarioRepository,
                              OrdenRepository ordenRepository) {
        this.productoService = productoService;
        this.usuarioRepository = usuarioRepository;
        this.ordenRepository = ordenRepository;
    }

    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> obtenerProductosVendedor() {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        List<Producto> productos = productoService.obtenerProductosPorVendedor(vendedorId);
        return ResponseEntity.ok(productos);
    }

    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        Long vendedorId = obtenerUsuarioIdAutenticado();
        productoDTO.setVendedorId(vendedorId);
        Producto nuevoProducto = productoService.crearProducto(productoDTO);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDTO productoDTO) {
        Long vendedorId = obtenerUsuarioIdAutenticado();

        // Verificar que el producto pertenece al vendedor
        Producto producto = productoService.obtenerProductoPorId(id);
        if (!producto.getVendedor().getUsuarioId().equals(vendedorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        productoDTO.setVendedorId(vendedorId);
        Producto actualizado = productoService.actualizarProducto(id, productoDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        Long vendedorId = obtenerUsuarioIdAutenticado();

        // Verificar que el producto pertenece al vendedor
        Producto producto = productoService.obtenerProductoPorId(id);
        if (!producto.getVendedor().getUsuarioId().equals(vendedorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ventas")
    public ResponseEntity<List<Map<String, Object>>> obtenerVentas() {
        Long vendedorId = obtenerUsuarioIdAutenticado();

        // Obtener todas las órdenes
        List<Orden> todasLasOrdenes = ordenRepository.findAll();

        // Filtrar detalles de órdenes que contienen productos del vendedor
        List<Map<String, Object>> ventas = new ArrayList<>();

        for (Orden orden : todasLasOrdenes) {
            for (OrdenDetalle detalle : orden.getDetalles()) {
                if (detalle.getProducto().getVendedor().getUsuarioId().equals(vendedorId)) {
                    Map<String, Object> venta = new HashMap<>();
                    venta.put("ordenId", orden.getOrdenId());
                    venta.put("fechaCreacion", orden.getFechaCreacion());
                    venta.put("estado", orden.getEstado());
                    venta.put("comprador", orden.getUsuario().getUsuarioNombre());
                    venta.put("producto", detalle.getProducto().getProductoNombre());
                    venta.put("cantidad", detalle.getCantidad());
                    venta.put("precioUnitario", detalle.getPrecioUnitario());
                    venta.put("subtotal", detalle.getSubtotal());
                    ventas.add(venta);
                }
            }
        }

        return ResponseEntity.ok(ventas);
    }

    private Long obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        return usuario.getUsuarioId();
    }
}
