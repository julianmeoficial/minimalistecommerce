package com.digital.mecommerces.controller;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.CarritoCompra;
import com.digital.mecommerces.model.CarritoItem;
import com.digital.mecommerces.model.Orden;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.UsuarioRepository;
import com.digital.mecommerces.service.CarritoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {
    private final CarritoService carritoService;
    private final UsuarioRepository usuarioRepository;

    public CarritoController(CarritoService carritoService, UsuarioRepository usuarioRepository) {
        this.carritoService = carritoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<CarritoCompra> obtenerCarritoActivo() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        CarritoCompra carrito = carritoService.obtenerCarritoActivo(usuarioId);
        return ResponseEntity.ok(carrito);
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoItem> agregarProductoAlCarrito(@RequestParam Long productoId, @RequestParam Integer cantidad) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        CarritoItem item = carritoService.agregarProductoAlCarrito(usuarioId, productoId, cantidad);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> actualizarCantidadProducto(@PathVariable Long itemId, @RequestParam Integer cantidad) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        CarritoItem item = carritoService.actualizarCantidadProducto(usuarioId, itemId, cantidad);
        if (item == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long itemId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        carritoService.eliminarProductoDelCarrito(usuarioId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<Orden> checkout() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        Orden orden = carritoService.convertirCarritoAOrden(usuarioId);
        return new ResponseEntity<>(orden, HttpStatus.CREATED);
    }

    private Long obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        return usuario.getUsuarioId();
    }
}
