package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.CarritoCompra;
import com.digital.mecommerces.model.CarritoItem;
import com.digital.mecommerces.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    // Búsquedas básicas por carrito
    List<CarritoItem> findByCarritoCompraCarritoId(Long carritoId);

    List<CarritoItem> findByCarritoCompra(CarritoCompra carritoCompra);

    // Búsqueda específica de producto en carrito
    Optional<CarritoItem> findByCarritoCompraAndProducto(CarritoCompra carritoCompra, Producto producto);

    Optional<CarritoItem> findByCarritoCompraCarritoIdAndProductoProductoId(Long carritoId, Long productoId);

    // Eliminar todos los items de un carrito
    void deleteByCarritoCompraCarritoId(Long carritoId);

    void deleteByCarritoCompra(CarritoCompra carritoCompra);

    // Items por producto específico
    List<CarritoItem> findByProducto(Producto producto);

    List<CarritoItem> findByProductoProductoId(Long productoId);

    // Items guardados para después
    List<CarritoItem> findByGuardadoDespuesTrue();

    List<CarritoItem> findByCarritoCompraAndGuardadoDespuesTrue(CarritoCompra carritoCompra);

    @Query("SELECT ci FROM CarritoItem ci WHERE ci.carritoCompra.usuario.usuarioId = :usuarioId AND ci.guardadoDespues = true")
    List<CarritoItem> findItemsGuardadosPorUsuario(@Param("usuarioId") Long usuarioId);

    // Items disponibles/no disponibles
    List<CarritoItem> findByDisponibleTrue();

    List<CarritoItem> findByDisponibleFalse();

    @Query("SELECT ci FROM CarritoItem ci WHERE ci.carritoCompra.activo = true AND ci.disponible = false")
    List<CarritoItem> findItemsNoDisponiblesEnCarritosActivos();

    // Items por fecha de agregado
    List<CarritoItem> findByFechaAgregadoBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT ci FROM CarritoItem ci WHERE ci.fechaAgregado >= :fecha ORDER BY ci.fechaAgregado DESC")
    List<CarritoItem> findItemsAgregadosDesde(@Param("fecha") LocalDateTime fecha);

    // Items modificados recientemente
    List<CarritoItem> findByFechaModificacionBetween(LocalDateTime inicio, LocalDateTime fin);

    // Items por rango de cantidad
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.cantidad BETWEEN :min AND :max")
    List<CarritoItem> findByCantidadBetween(@Param("min") Integer min, @Param("max") Integer max);

    // Items con cantidad alta (posibles mayoristas)
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.cantidad >= :cantidad")
    List<CarritoItem> findItemsConCantidadMinima(@Param("cantidad") Integer cantidad);

    // Items por rango de precio
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.precioUnitario BETWEEN :min AND :max")
    List<CarritoItem> findByPrecioUnitarioBetween(@Param("min") Double min, @Param("max") Double max);

    // Calcular subtotales
    @Query("SELECT SUM(ci.cantidad * ci.precioUnitario) FROM CarritoItem ci WHERE ci.carritoCompra.carritoId = :carritoId")
    Double calcularTotalCarrito(@Param("carritoId") Long carritoId);

    @Query("SELECT SUM(ci.cantidad * ci.precioUnitario) FROM CarritoItem ci WHERE ci.carritoCompra.activo = true")
    Double calcularTotalTodosCarritosActivos();

    // Estadísticas de items
    @Query("SELECT COUNT(ci) FROM CarritoItem ci WHERE ci.carritoCompra.activo = true")
    long countItemsEnCarritosActivos();

    @Query("SELECT SUM(ci.cantidad) FROM CarritoItem ci WHERE ci.carritoCompra.activo = true")
    Long sumTotalCantidadEnCarritosActivos();

    @Query("SELECT AVG(ci.cantidad) FROM CarritoItem ci WHERE ci.carritoCompra.activo = true")
    Double findPromedioCantidadPorItem();

    // Productos más agregados a carritos
    @Query("SELECT ci.producto.productoId, COUNT(ci) as cantidad FROM CarritoItem ci WHERE ci.carritoCompra.activo = true GROUP BY ci.producto.productoId ORDER BY cantidad DESC")
    List<Object[]> findProductosMasAgregados();

    // Items por categoría de producto
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.producto.categoria.categoriaId = :categoriaId AND ci.carritoCompra.activo = true")
    List<CarritoItem> findItemsPorCategoria(@Param("categoriaId") Long categoriaId);

    // Items por tipo de usuario usando roles optimizados
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.carritoCompra.usuario.rol.nombre = 'COMPRADOR' AND ci.carritoCompra.activo = true")
    List<CarritoItem> findItemsDeCompradores();

    @Query("SELECT ci FROM CarritoItem ci WHERE ci.carritoCompra.usuario.rol.nombre = 'ADMINISTRADOR' AND ci.carritoCompra.activo = true")
    List<CarritoItem> findItemsDeAdministradores();

    // Items que necesitan actualización de precio
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.precioUnitario != ci.producto.precio AND ci.carritoCompra.activo = true")
    List<CarritoItem> findItemsConPrecioDesactualizado();

    // Items con productos inactivos
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.producto.activo = false AND ci.carritoCompra.activo = true")
    List<CarritoItem> findItemsConProductosInactivos();

    // Items con stock insuficiente
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.cantidad > ci.producto.stock AND ci.carritoCompra.activo = true")
    List<CarritoItem> findItemsConStockInsuficiente();

    // Items antiguos en carritos (para notificaciones)
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.carritoCompra.activo = true AND ci.fechaAgregado < :fecha")
    List<CarritoItem> findItemsAntiguosEnCarritosActivos(@Param("fecha") LocalDateTime fecha);

    // Verificar existencia de producto en carrito específico
    @Query("SELECT COUNT(ci) > 0 FROM CarritoItem ci WHERE ci.carritoCompra.carritoId = :carritoId AND ci.producto.productoId = :productoId")
    boolean existsProductoEnCarrito(@Param("carritoId") Long carritoId, @Param("productoId") Long productoId);

    // Items por vendedor (útil para estadísticas)
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.producto.vendedor.usuarioId = :vendedorId AND ci.carritoCompra.activo = true")
    List<CarritoItem> findItemsPorVendedor(@Param("vendedorId") Long vendedorId);
}
