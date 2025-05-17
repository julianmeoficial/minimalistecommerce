package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {
    private final CarritoCompraRepository carritoCompraRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final OrdenRepository ordenRepository;
    private final OrdenDetalleRepository ordenDetalleRepository;

    public CarritoService(
            CarritoCompraRepository carritoCompraRepository,
            CarritoItemRepository carritoItemRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            OrdenRepository ordenRepository,
            OrdenDetalleRepository ordenDetalleRepository
    ) {
        this.carritoCompraRepository = carritoCompraRepository;
        this.carritoItemRepository = carritoItemRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.ordenRepository = ordenRepository;
        this.ordenDetalleRepository = ordenDetalleRepository;
    }

    // Obtener el carrito activo de un usuario
    public CarritoCompra obtenerCarritoActivo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        // Buscar carrito activo o crear uno nuevo
        return carritoCompraRepository.findByUsuarioAndActivo(usuario, true)
                .orElseGet(() -> {
                    CarritoCompra nuevoCarrito = new CarritoCompra(usuario);
                    return carritoCompraRepository.save(nuevoCarrito);
                });
    }

    // Agregar un producto al carrito
    @Transactional
    public CarritoItem agregarProductoAlCarrito(Long usuarioId, Long productoId, Integer cantidad) {
        CarritoCompra carrito = obtenerCarritoActivo(usuarioId);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productoId));

        // Verificar si el producto ya está en el carrito
        Optional<CarritoItem> itemExistente = carritoItemRepository.findByCarritoCompraAndProducto(carrito, producto);

        if (itemExistente.isPresent()) {
            // Actualizar cantidad
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            return carritoItemRepository.save(item);
        } else {
            // Crear nuevo item
            CarritoItem nuevoItem = new CarritoItem(producto, cantidad);
            carrito.addItem(nuevoItem);
            carritoCompraRepository.save(carrito);
            return nuevoItem;
        }
    }

    // Actualizar cantidad de un producto en el carrito
    @Transactional
    public CarritoItem actualizarCantidadProducto(Long usuarioId, Long itemId, Integer cantidad) {
        CarritoCompra carrito = obtenerCarritoActivo(usuarioId);

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado con id: " + itemId));

        // Verificar que el item pertenece al carrito del usuario
        if (!item.getCarritoCompra().getCarritoId().equals(carrito.getCarritoId())) {
            throw new IllegalArgumentException("El item no pertenece al carrito del usuario");
        }

        if (cantidad <= 0) {
            // Eliminar el item si la cantidad es 0 o negativa
            carrito.removeItem(item);
            carritoItemRepository.delete(item);
            carritoCompraRepository.save(carrito);
            return null;
        } else {
            // Actualizar cantidad
            item.setCantidad(cantidad);
            return carritoItemRepository.save(item);
        }
    }

    // Eliminar un producto del carrito
    @Transactional
    public void eliminarProductoDelCarrito(Long usuarioId, Long itemId) {
        CarritoCompra carrito = obtenerCarritoActivo(usuarioId);

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado con id: " + itemId));

        // Verificar que el item pertenece al carrito del usuario
        if (!item.getCarritoCompra().getCarritoId().equals(carrito.getCarritoId())) {
            throw new IllegalArgumentException("El item no pertenece al carrito del usuario");
        }

        carrito.removeItem(item);
        carritoItemRepository.delete(item);
        carritoCompraRepository.save(carrito);
    }

    // Vaciar el carrito
    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        CarritoCompra carrito = obtenerCarritoActivo(usuarioId);
        carrito.getItems().clear();
        carritoCompraRepository.save(carrito);
    }

    // Convertir carrito a orden
    @Transactional
    public Orden convertirCarritoAOrden(Long usuarioId) {
        CarritoCompra carrito = obtenerCarritoActivo(usuarioId);

        if (carrito.getItems().isEmpty()) {
            throw new IllegalStateException("No se puede crear una orden con un carrito vacío");
        }

        // Crear nueva orden
        Orden orden = new Orden(
                carrito.getUsuario(),
                "PENDIENTE",
                carrito.calcularTotal()
        );

        // Agregar detalles de la orden
        for (CarritoItem item : carrito.getItems()) {
            OrdenDetalle detalle = new OrdenDetalle(
                    item.getProducto(),
                    item.getCantidad(),
                    item.getPrecioUnitario()
            );
            orden.addDetalle(detalle);

            // Actualizar stock del producto
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }

        // Desactivar el carrito
        carrito.setActivo(false);
        carritoCompraRepository.save(carrito);

        // Guardar y retornar la orden
        return ordenRepository.save(orden);
    }
}
