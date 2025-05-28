package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Orden;
import com.digital.mecommerces.model.OrdenDetalle;
import com.digital.mecommerces.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenDetalleRepository extends JpaRepository<OrdenDetalle, Long> {

    // Búsquedas básicas por orden
    List<OrdenDetalle> findByOrdenOrdenId(Long ordenId);

    List<OrdenDetalle> findByOrden(Orden orden);

    // Búsqueda específica de producto en orden
    Optional<OrdenDetalle> findByOrdenAndProducto(Orden orden, Producto producto);

    Optional<OrdenDetalle> findByOrdenOrdenIdAndProductoProductoId(Long ordenId, Long productoId);

    // Eliminar todos los detalles de una orden
    void deleteByOrdenOrdenId(Long ordenId);

    void deleteByOrden(Orden orden);

    // Detalles por producto específico
    List<OrdenDetalle> findByProducto(Producto producto);

    List<OrdenDetalle> findByProductoProductoId(Long productoId);

    // Detalles por fecha de creación
    List<OrdenDetalle> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT od FROM OrdenDetalle od WHERE od.fechaCreacion >= :fecha ORDER BY od.fechaCreacion DESC")
    List<OrdenDetalle> findDetallesCreadosDesde(@Param("fecha") LocalDateTime fecha);

    // Detalles modificados recientemente
    List<OrdenDetalle> findByFechaModificacionBetween(LocalDateTime inicio, LocalDateTime fin);

    // Detalles por rango de cantidad
    @Query("SELECT od FROM OrdenDetalle od WHERE od.cantidad BETWEEN :min AND :max")
    List<OrdenDetalle> findByCantidadBetween(@Param("min") Integer min, @Param("max") Integer max);

    // Detalles con cantidad alta
    @Query("SELECT od FROM OrdenDetalle od WHERE od.cantidad >= :cantidad")
    List<OrdenDetalle> findDetallesConCantidadMinima(@Param("cantidad") Integer cantidad);

    // Detalles por rango de precio
    @Query("SELECT od FROM OrdenDetalle od WHERE od.precioUnitario BETWEEN :min AND :max")
    List<OrdenDetalle> findByPrecioUnitarioBetween(@Param("min") Double min, @Param("max") Double max);

    // Calcular subtotales
    @Query("SELECT SUM(od.cantidad * od.precioUnitario) FROM OrdenDetalle od WHERE od.orden.ordenId = :ordenId")
    Double calcularTotalOrden(@Param("ordenId") Long ordenId);

    // Estadísticas de detalles
    @Query("SELECT COUNT(od) FROM OrdenDetalle od WHERE od.orden.estado = :estado")
    long countDetallesEnOrdenesConEstado(@Param("estado") String estado);

    @Query("SELECT SUM(od.cantidad) FROM OrdenDetalle od WHERE od.orden.estado = :estado")
    Long sumTotalCantidadEnOrdenesConEstado(@Param("estado") String estado);

    @Query("SELECT AVG(od.cantidad) FROM OrdenDetalle od")
    Double findPromedioCantidadPorDetalle();

    // Productos más vendidos
    @Query("SELECT od.producto.productoId, SUM(od.cantidad) as cantidad FROM OrdenDetalle od WHERE od.orden.estado = 'ENTREGADA' GROUP BY od.producto.productoId ORDER BY cantidad DESC")
    List<Object[]> findProductosMasVendidos();

    // Detalles por categoría de producto
    @Query("SELECT od FROM OrdenDetalle od WHERE od.producto.categoria.categoriaId = :categoriaId")
    List<OrdenDetalle> findDetallesPorCategoria(@Param("categoriaId") Long categoriaId);

    // Detalles por usuario
    @Query("SELECT od FROM OrdenDetalle od WHERE od.orden.usuario.usuarioId = :usuarioId")
    List<OrdenDetalle> findDetallesPorUsuario(@Param("usuarioId") Long usuarioId);

    // Verificar existencia de producto en orden específica
    @Query("SELECT COUNT(od) > 0 FROM OrdenDetalle od WHERE od.orden.ordenId = :ordenId AND od.producto.productoId = :productoId")
    boolean existsProductoEnOrden(@Param("ordenId") Long ordenId, @Param("productoId") Long productoId);

    // Detalles por vendedor (útil para estadísticas)
    @Query("SELECT od FROM OrdenDetalle od WHERE od.producto.vendedor.usuarioId = :vendedorId")
    List<OrdenDetalle> findDetallesPorVendedor(@Param("vendedorId") Long vendedorId);
}