package com.digital.mecommerces.service;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.enums.TipoUsuario;
import com.digital.mecommerces.exception.BusinessException;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CarritoService {

    private final CarritoCompraRepository carritoCompraRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final OrdenRepository ordenRepository;

    public CarritoService(CarritoCompraRepository carritoCompraRepository,
                          CarritoItemRepository carritoItemRepository,
                          UsuarioRepository usuarioRepository,
                          ProductoRepository productoRepository,
                          OrdenRepository ordenRepository) {
        this.carritoCompraRepository = carritoCompraRepository;
        this.carritoItemRepository = carritoItemRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.ordenRepository = ordenRepository;
    }

    public CarritoCompra obtenerCarritoActivo(Long usuarioId) {
        log.info("🛒 Obteniendo carrito activo para usuario ID: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        // Verificar que el usuario puede tener carrito
        validarUsuarioPuedeComprar(usuario);

        // Buscar carrito activo o crear uno nuevo
        return carritoCompraRepository.findByUsuarioAndActivo(usuario, true)
                .orElseGet(() -> {
                    log.info("📝 Creando nuevo carrito para usuario: {}", usuario.getEmail());
                    CarritoCompra nuevoCarrito = new CarritoCompra(usuario);
                    return carritoCompraRepository.save(nuevoCarrito);
                });
    }

    @Transactional
    public CarritoItem agregarProductoAlCarrito(Long usuarioId, Long productoId, Integer cantidad) {
        log.info("➕ Agregando producto {} al carrito del usuario {} (cantidad: {})",
                productoId, usuarioId, cantidad);

        if (cantidad <= 0) {
            throw new BusinessException("La cantidad debe ser mayor a 0");
        }

        CarritoCompra carrito = obtenerCarritoActivo(usuarioId);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        // Validaciones del producto
        validarProductoParaCarrito(producto, cantidad);

        // Verificar si ya existe el producto en el carrito
        CarritoItem itemExistente = carrito.findItemByProducto(producto);

        if (itemExistente != null) {
            // Actualizar cantidad del item existente
            Integer nuevaCantidad = itemExistente.getCantidad() + cantidad;
            validarProductoParaCarrito(producto, nuevaCantidad);

            itemExistente.setCantidad(nuevaCantidad);
            CarritoItem itemActualizado = carritoItemRepository.save(itemExistente);

            // Recalcular totales del carrito
            carrito.recalcularTotales();
            carritoCompraRepository.save(carrito);

            log.info("✅ Cantidad actualizada del producto en carrito: {}", nuevaCantidad);
            return itemActualizado;
        } else {
            // Crear nuevo item
            CarritoItem nuevoItem = new CarritoItem(producto, cantidad, carrito);
            carrito.addItem(nuevoItem);

            CarritoItem itemGuardado = carritoItemRepository.save(nuevoItem);
            carritoCompraRepository.save(carrito);

            log.info("✅ Producto agregado exitosamente al carrito");
            return itemGuardado;
        }
    }

    @Transactional
    public CarritoItem actualizarCantidadProducto(Long usuarioId, Long itemId, Integer cantidad) {
        log.info("🔄 Actualizando cantidad del item {} para usuario {} (nueva cantidad: {})",
                itemId, usuarioId, cantidad);

        if (cantidad <= 0) {
            // Si la cantidad es 0 o negativa, eliminar el item
            eliminarProductoDelCarrito(usuarioId, itemId);
            return null;
        }

        CarritoCompra carrito = obtenerCarritoActivo(usuarioId);

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado con ID: " + itemId));

        // Verificar que el item pertenece al carrito del usuario
        if (!item.getCarritoCompra().getCarritoId().equals(carrito.getCarritoId())) {
            throw new BusinessException("El item no pertenece al carrito del usuario");
        }

        // Validar disponibilidad con la nueva cantidad
        validarProductoParaCarrito(item.getProducto(), cantidad);

        item.setCantidad(cantidad);
        CarritoItem itemActualizado = carritoItemRepository.save(item);

        // Recalcular totales del carrito
        carrito.recalcularTotales();
        carritoCompraRepository.save(carrito);

        log.info("✅ Cantidad actualizada exitosamente");
        return itemActualizado;
    }

    @Transactional
    public void eliminarProductoDelCarrito(Long usuarioId, Long itemId) {
        log.info("🗑️ Eliminando item {} del carrito del usuario {}", itemId, usuarioId);

        CarritoCompra carrito = obtenerCarritoActivo(usuarioId);

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado con ID: " + itemId));

        // Verificar que el item pertenece al carrito del usuario
        if (!item.getCarritoCompra().getCarritoId().equals(carrito.getCarritoId())) {
            throw new BusinessException("El item no pertenece al carrito del usuario");
        }

        carrito.removeItem(item);
        carritoItemRepository.delete(item);
        carritoCompraRepository.save(carrito);

        log.info("✅ Producto eliminado exitosamente del carrito");
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        log.info("🗑️ Vaciando carrito completo del usuario {}", usuarioId);

        CarritoCompra carrito = obtenerCarritoActivo(usuarioId);
        carrito.vaciarCarrito();
        carritoCompraRepository.save(carrito);

        log.info("✅ Carrito vaciado exitosamente");
    }

    @Transactional
    public Orden convertirCarritoAOrden(Long usuarioId) {
        log.info("📦 Convirtiendo carrito a orden para usuario {}", usuarioId);

        CarritoCompra carrito = obtenerCarritoActivo(usuarioId);

        if (carrito.estaVacio()) {
            throw new BusinessException("No se puede crear una orden con un carrito vacío");
        }

        if (!carrito.puedeComprar()) {
            throw new BusinessException("El carrito contiene productos no disponibles o sin stock suficiente");
        }

        // Crear nueva orden
        Orden orden = new Orden(carrito.getUsuario(), "PENDIENTE", carrito.getTotalEstimado());
        orden.setDireccionEntrega("Por definir"); // Se puede actualizar después

        // Reducir stock de productos y crear la orden
        for (CarritoItem item : carrito.getItems()) {
            Producto producto = item.getProducto();

            // Verificar stock una vez más antes de procesar
            if (producto.getStock() < item.getCantidad()) {
                throw new BusinessException("Stock insuficiente para el producto: " + producto.getProductoNombre());
            }

            // Reducir stock
            producto.reducirStock(item.getCantidad());
            productoRepository.save(producto);
        }

        // Guardar la orden
        Orden ordenGuardada = ordenRepository.save(orden);

        // Marcar carrito como convertido
        carrito.convertirAOrden();
        carritoCompraRepository.save(carrito);

        log.info("✅ Carrito convertido exitosamente a orden ID: {}", ordenGuardada.getOrdenId());
        return ordenGuardada;
    }

    public List<CarritoCompra> obtenerCarritosPorUsuario(Long usuarioId) {
        log.info("📋 Obteniendo historial de carritos para usuario {}", usuarioId);
        return carritoCompraRepository.findByUsuarioUsuarioId(usuarioId);
    }

    public List<CarritoCompra> obtenerCarritosAbandonados(LocalDateTime fecha) {
        log.info("📊 Obteniendo carritos abandonados desde: {}", fecha);
        return carritoCompraRepository.findCarritosAbandonados(fecha);
    }

    @Transactional
    public void marcarItemComoGuardadoDespues(Long usuarioId, Long itemId) {
        log.info("💾 Marcando item {} como guardado para después", itemId);

        CarritoCompra carrito = obtenerCarritoActivo(usuarioId);

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado con ID: " + itemId));

        if (!item.getCarritoCompra().getCarritoId().equals(carrito.getCarritoId())) {
            throw new BusinessException("El item no pertenece al carrito del usuario");
        }

        item.marcarComoGuardadoDespues();
        carritoItemRepository.save(item);

        log.info("✅ Item marcado como guardado para después");
    }

    public List<CarritoItem> obtenerItemsGuardadosParaDespues(Long usuarioId) {
        log.info("💾 Obteniendo items guardados para después del usuario {}", usuarioId);
        return carritoItemRepository.findItemsGuardadosPorUsuario(usuarioId);
    }

    @Transactional
    public void limpiarCarritosAntiguos(LocalDateTime fechaLimite) {
        log.info("🧹 Limpiando carritos antiguos anteriores a: {}", fechaLimite);

        List<CarritoCompra> carritosParaLimpiar = carritoCompraRepository.findCarritosParaLimpiar(fechaLimite);

        for (CarritoCompra carrito : carritosParaLimpiar) {
            carritoItemRepository.deleteByCarritoCompra(carrito);
            carritoCompraRepository.delete(carrito);
        }

        log.info("✅ Limpieza completada. {} carritos eliminados", carritosParaLimpiar.size());
    }

    public Double calcularTotalCarritosActivos() {
        return carritoCompraRepository.findTotalValorCarritosActivos();
    }

    public long contarCarritosActivos() {
        return carritoCompraRepository.countCarritosActivos();
    }

    public long contarCarritosConProductos() {
        return carritoCompraRepository.countCarritosConProductos();
    }

    // Métodos privados de validación
    private void validarUsuarioPuedeComprar(Usuario usuario) {
        if (!usuario.getActivo()) {
            throw new BusinessException("Usuario inactivo no puede usar carrito de compras");
        }

        String rolNombre = usuario.getRol().getNombre();

        // Solo compradores y administradores pueden tener carrito
        if (!RoleConstants.ROLE_COMPRADOR.equals(rolNombre) &&
                !RoleConstants.ROLE_ADMINISTRADOR.equals(rolNombre)) {

            try {
                TipoUsuario tipo = TipoUsuario.fromCodigo(rolNombre);
                if (tipo != TipoUsuario.COMPRADOR && tipo != TipoUsuario.ADMINISTRADOR) {
                    throw new BusinessException("Usuario tipo " + tipo.getDescripcion() + " no puede usar carrito de compras");
                }
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Usuario con rol no válido no puede usar carrito de compras");
            }
        }
    }

    private void validarProductoParaCarrito(Producto producto, Integer cantidad) {
        if (!producto.getActivo()) {
            throw new BusinessException("El producto no está disponible: " + producto.getProductoNombre());
        }

        if (producto.getStock() < cantidad) {
            throw new BusinessException("Stock insuficiente. Disponible: " + producto.getStock() +
                    ", solicitado: " + cantidad);
        }

        if (cantidad > 100) { // Límite de seguridad
            throw new BusinessException("No se pueden agregar más de 100 unidades de un producto");
        }
    }
}
