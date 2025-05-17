package com.digital.mecommerces.controller;

import com.digital.mecommerces.dto.ProductoDTO;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.OrdenRepository;
import com.digital.mecommerces.repository.RolUsuarioRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import com.digital.mecommerces.service.ProductoService;
import com.digital.mecommerces.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final OrdenRepository ordenRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final UsuarioRepository usuarioRepository;

    public AdminController(UsuarioService usuarioService,
                           ProductoService productoService,
                           OrdenRepository ordenRepository,
                           RolUsuarioRepository rolUsuarioRepository,
                           UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.ordenRepository = ordenRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.actualizarUsuario(id, usuario);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = productoService.obtenerProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        Producto producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        Producto nuevoProducto = productoService.crearProducto(productoDTO);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDTO productoDTO) {
        Producto actualizado = productoService.actualizarProducto(id, productoDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ordenes")
    public ResponseEntity<List<Orden>> listarOrdenes() {
        List<Orden> ordenes = ordenRepository.findAll();
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/ordenes/{id}")
    public ResponseEntity<Orden> obtenerOrden(@PathVariable Long id) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));
        return ResponseEntity.ok(orden);
    }

    @PutMapping("/ordenes/{id}/estado")
    public ResponseEntity<Orden> actualizarEstadoOrden(@PathVariable Long id, @RequestParam String estado) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));
        orden.setEstado(estado);
        ordenRepository.save(orden);
        return ResponseEntity.ok(orden);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> obtenerDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        List<RolUsuario> roles = rolUsuarioRepository.findAll();
        Map<String, Long> usuariosPorRol = new HashMap<>();
        for (RolUsuario rol : roles) {
            Long count = usuarioRepository.countByRolRolId(rol.getRolId());
            usuariosPorRol.put(rol.getNombre(), count);
        }

        List<Producto> productos = productoService.obtenerProductos();
        Map<String, Long> productosPorCategoria = new HashMap<>();
        for (Producto producto : productos) {
            try {
                // Acceso seguro a la categoría
                String categoria = producto.getCategoria().getNombre();
                productosPorCategoria.put(categoria, productosPorCategoria.getOrDefault(categoria, 0L) + 1);
            } catch (Exception e) {
                // Manejar productos con categorías inválidas
                productosPorCategoria.put("Sin categoría", productosPorCategoria.getOrDefault("Sin categoría", 0L) + 1);
            }
        }

        List<Orden> ordenes = ordenRepository.findAll();
        Map<String, Long> ordenesPorEstado = new HashMap<>();
        for (Orden orden : ordenes) {
            String estado = orden.getEstado();
            ordenesPorEstado.put(estado, ordenesPorEstado.getOrDefault(estado, 0L) + 1);
        }

        double ingresosTotales = ordenes.stream()
                .mapToDouble(Orden::getTotal)
                .sum();

        dashboard.put("usuariosPorRol", usuariosPorRol);
        dashboard.put("productosPorCategoria", productosPorCategoria);
        dashboard.put("ordenesPorEstado", ordenesPorEstado);
        dashboard.put("ingresosTotales", ingresosTotales);
        dashboard.put("totalUsuarios", usuarioRepository.count());
        dashboard.put("totalProductos", productos.size());
        dashboard.put("totalOrdenes", ordenes.size());

        return ResponseEntity.ok(dashboard);
    }
}
