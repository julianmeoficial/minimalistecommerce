package com.digital.mecommerces.controller;

import com.digital.mecommerces.model.CarritoCompra;
import com.digital.mecommerces.model.CarritoItem;
import com.digital.mecommerces.model.Orden;
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

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    // Obtener el carrito activo del usuario autenticado
    @GetMapping
    public ResponseEntity<CarritoCompra> obtenerCarritoActivo() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        CarritoCompra carrito = carritoService.obtenerCarritoActivo(usuarioId);
        return ResponseEntity.ok(carrito);
    }

    // Agregar un producto al carrito
    @PostMapping("/items")
    public ResponseEntity<CarritoItem> agregarProductoAlCarrito(
            @RequestParam Long productoId,
            @RequestParam Integer cantidad) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        CarritoItem item = carritoService.agregarProductoAlCarrito(usuarioId, productoId, cantidad);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    // Actualizar cantidad de un producto en el carrito
    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> actualizarCantidadProducto(
            @PathVariable Long itemId,
            @RequestParam Integer cantidad) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        CarritoItem item = carritoService.actualizarCantidadProducto(usuarioId, itemId, cantidad);
        
        if (item == null) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(item);
    }

    // Eliminar un producto del carrito
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long itemId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        carritoService.eliminarProductoDelCarrito(usuarioId, itemId);
        return ResponseEntity.noContent().build();
    }

    // Vaciar el carrito
    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }

    // Convertir carrito a orden
    @PostMapping("/checkout")
    public ResponseEntity<Orden> checkout() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        Orden orden = carritoService.convertirCarritoAOrden(usuarioId);
        return new ResponseEntity<>(orden, HttpStatus.CREATED);
    }

    // Método auxiliar para obtener el ID del usuario autenticado
    private Long obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Aquí deberías implementar la lógica para obtener el ID del usuario a partir de la autenticación
        // Por simplicidad, asumimos que el nombre de usuario es el ID
        return Long.parseLong(authentication.getName());
    }
}