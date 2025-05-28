package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Orden;
import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    // Búsquedas básicas por usuario
    List<Orden> findByUsuarioUsuarioId(Long usuarioId);

    List<Orden> findByUsuario(Usuario usuario);

    // Órdenes por usuario ordenadas por fecha
    @Query("SELECT o FROM Orden o WHERE o.usuario.usuarioId = :usuarioId ORDER BY o.fechaCreacion DESC")
    List<Orden> findByUsuarioOrderByFechaCreacionDesc(@Param("usuarioId") Long usuarioId);

    // Búsquedas por estado específico
    List<Orden> findByEstado(String estado);

    List<Orden> findByEstadoIgnoreCase(String estado);

    // Estados específicos optimizados para el sistema
    @Query("SELECT o FROM Orden o WHERE o.estado = 'PENDIENTE' ORDER BY o.fechaCreacion ASC")
    List<Orden> findOrdenesPendientes();

    @Query("SELECT o FROM Orden o WHERE o.estado = 'PAGADA' ORDER BY o.fechaCreacion ASC")
    List<Orden> findOrdenesPagadas();

    @Query("SELECT o FROM Orden o WHERE o.estado = 'ENVIADA' ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesEnviadas();

    @Query("SELECT o FROM Orden o WHERE o.estado = 'ENTREGADA' ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesEntregadas();

    @Query("SELECT o FROM Orden o WHERE o.estado = 'CANCELADA' ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesCanceladas();

    @Query("SELECT o FROM Orden o WHERE o.estado = 'DEVUELTA' ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesDevueltas();

    // Búsquedas por fecha de creación
    List<Orden> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT o FROM Orden o WHERE o.fechaCreacion >= :fecha ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesDesde(@Param("fecha") LocalDateTime fecha);

    // Órdenes creadas hoy
    @Query("SELECT o FROM Orden o WHERE DATE(o.fechaCreacion) = CURRENT_DATE ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesDeHoy();

    // Órdenes de esta semana
    @Query("SELECT o FROM Orden o WHERE WEEK(o.fechaCreacion) = WEEK(CURRENT_DATE) AND YEAR(o.fechaCreacion) = YEAR(CURRENT_DATE) ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesDeLaSemana();

    // Órdenes de este mes
    @Query("SELECT o FROM Orden o WHERE MONTH(o.fechaCreacion) = MONTH(CURRENT_DATE) AND YEAR(o.fechaCreacion) = YEAR(CURRENT_DATE) ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesDelMes();

    // Búsquedas por rango de total
    @Query("SELECT o FROM Orden o WHERE o.total BETWEEN :min AND :max ORDER BY o.total DESC")
    List<Orden> findByTotalBetween(@Param("min") Double min, @Param("max") Double max);

    // Órdenes con valor mínimo
    @Query("SELECT o FROM Orden o WHERE o.total >= :minimo ORDER BY o.total DESC")
    List<Orden> findOrdenesConValorMinimo(@Param("minimo") Double minimo);

    // Órdenes de alto valor (mayores a un monto específico)
    @Query("SELECT o FROM Orden o WHERE o.total >= 1000.0 ORDER BY o.total DESC")
    List<Orden> findOrdenesAltoValor();

    // Búsquedas por método de pago
    List<Orden> findByMetodoPago(String metodoPago);

    List<Orden> findByMetodoPagoIgnoreCase(String metodoPago);

    // Órdenes por métodos de pago específicos
    @Query("SELECT o FROM Orden o WHERE o.metodoPago IN ('TARJETA_CREDITO', 'TARJETA_DEBITO') ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesPorTarjeta();

    @Query("SELECT o FROM Orden o WHERE o.metodoPago = 'EFECTIVO' ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesPorEfectivo();

    @Query("SELECT o FROM Orden o WHERE o.metodoPago = 'TRANSFERENCIA' ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesPorTransferencia();

    // Búsquedas por devoluciones
    List<Orden> findByDevolucionTrue();

    List<Orden> findByDevolucionFalse();

    @Query("SELECT o FROM Orden o WHERE o.devolucion = true AND o.fechaDevolucion >= :fecha")
    List<Orden> findDevolucionesDesde(@Param("fecha") LocalDateTime fecha);

    // Búsquedas por facturación
    List<Orden> findByFacturarTrue();

    List<Orden> findByFacturarFalse();

    @Query("SELECT o FROM Orden o WHERE o.facturar = true AND o.numeroFactura IS NULL")
    List<Orden> findOrdenesPendientesFacturacion();

    @Query("SELECT o FROM Orden o WHERE o.numeroFactura IS NOT NULL ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesConFactura();

    // Búsquedas por tipo de usuario usando roles optimizados
    @Query("SELECT o FROM Orden o WHERE o.usuario.rol.nombre = 'COMPRADOR' ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesDeCompradores();

    @Query("SELECT o FROM Orden o WHERE o.usuario.rol.nombre = 'ADMINISTRADOR' ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesDeAdministradores();

    // Órdenes por entrega
    @Query("SELECT o FROM Orden o WHERE o.fechaEntrega IS NOT NULL ORDER BY o.fechaEntrega DESC")
    List<Orden> findOrdenesEntregadasConFecha();

    @Query("SELECT o FROM Orden o WHERE o.estado = 'ENVIADA' AND o.fechaEntrega IS NULL")
    List<Orden> findOrdenesEnviadasSinEntrega();

    // Estadísticas de órdenes
    @Query("SELECT COUNT(o) FROM Orden o")
    long countTotalOrdenes();

    @Query("SELECT COUNT(o) FROM Orden o WHERE o.estado = :estado")
    long countByEstado(@Param("estado") String estado);

    @Query("SELECT SUM(o.total) FROM Orden o WHERE o.estado = 'ENTREGADA'")
    Double sumTotalVentasEntregadas();

    @Query("SELECT SUM(o.total) FROM Orden o WHERE o.fechaCreacion >= :fecha")
    Double sumVentasDesde(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT AVG(o.total) FROM Orden o WHERE o.estado != 'CANCELADA'")
    Double findPromedioValorOrdenes();

    // Estadísticas por usuario
    @Query("SELECT COUNT(o) FROM Orden o WHERE o.usuario.usuarioId = :usuarioId")
    long countOrdenesPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT SUM(o.total) FROM Orden o WHERE o.usuario.usuarioId = :usuarioId AND o.estado = 'ENTREGADA'")
    Double sumTotalComprasPorUsuario(@Param("usuarioId") Long usuarioId);

    // Órdenes que requieren atención
    @Query("SELECT o FROM Orden o WHERE o.estado = 'PENDIENTE' AND o.fechaCreacion < :fecha")
    List<Orden> findOrdenesPendientesAntiguas(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT o FROM Orden o WHERE o.estado = 'ENVIADA' AND o.fechaCreacion < :fecha")
    List<Orden> findOrdenesEnviadasAntiguas(@Param("fecha") LocalDateTime fecha);

    // Análisis de comportamiento de compra
    @Query("SELECT o.usuario, COUNT(o) as totalOrdenes FROM Orden o GROUP BY o.usuario ORDER BY totalOrdenes DESC")
    List<Object[]> findUsuariosConMasOrdenes();

    @Query("SELECT o.metodoPago, COUNT(o) as total FROM Orden o GROUP BY o.metodoPago ORDER BY total DESC")
    List<Object[]> findEstadisticasPorMetodoPago();

    @Query("SELECT DATE(o.fechaCreacion) as fecha, COUNT(o) as total FROM Orden o GROUP BY DATE(o.fechaCreacion) ORDER BY fecha DESC")
    List<Object[]> findOrdenesAgrupadasPorFecha();

    // Búsquedas para reportes
    @Query("SELECT o FROM Orden o WHERE o.fechaCreacion BETWEEN :inicio AND :fin AND o.estado = :estado ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesParaReporte(@Param("inicio") LocalDateTime inicio,
                                       @Param("fin") LocalDateTime fin,
                                       @Param("estado") String estado);

    // Búsqueda por referencia de pago
    Optional<Orden> findByReferenciaPago(String referenciaPago);

    @Query("SELECT o FROM Orden o WHERE o.referenciaPago IS NOT NULL AND o.referenciaPago != ''")
    List<Orden> findOrdenesConReferenciaPago();

    // Órdenes por dirección de entrega
    @Query("SELECT o FROM Orden o WHERE o.direccionEntrega LIKE %:direccion%")
    List<Orden> findByDireccionEntregaContaining(@Param("direccion") String direccion);

    // Órdenes con notas especiales
    @Query("SELECT o FROM Orden o WHERE o.notas IS NOT NULL AND o.notas != ''")
    List<Orden> findOrdenesConNotas();

    // Verificar existencia de orden por usuario
    @Query("SELECT COUNT(o) > 0 FROM Orden o WHERE o.usuario.usuarioId = :usuarioId")
    boolean existsOrdenByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Última orden de un usuario
    @Query("SELECT o FROM Orden o WHERE o.usuario.usuarioId = :usuarioId ORDER BY o.fechaCreacion DESC LIMIT 1")
    Optional<Orden> findUltimaOrdenDeUsuario(@Param("usuarioId") Long usuarioId);

    // Órdenes recientes (últimas 24 horas)
    @Query("SELECT o FROM Orden o WHERE o.fechaCreacion >= :fecha ORDER BY o.fechaCreacion DESC")
    List<Orden> findOrdenesRecientes(@Param("fecha") LocalDateTime fecha);

    // Órdenes para dashboard
    @Query("SELECT COUNT(o) FROM Orden o WHERE DATE(o.fechaCreacion) = CURRENT_DATE")
    long countOrdenesDeHoy();

    @Query("SELECT COUNT(o) FROM Orden o WHERE o.estado = 'PENDIENTE'")
    long countOrdenesPendientes();

    @Query("SELECT SUM(o.total) FROM Orden o WHERE DATE(o.fechaCreacion) = CURRENT_DATE AND o.estado != 'CANCELADA'")
    Double sumVentasDeHoy();
}
