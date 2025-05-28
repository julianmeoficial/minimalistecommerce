package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.CarritoCompra;
import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoCompraRepository extends JpaRepository<CarritoCompra, Long> {

    // Búsquedas básicas por usuario
    List<CarritoCompra> findByUsuarioUsuarioId(Long usuarioId);

    List<CarritoCompra> findByUsuario(Usuario usuario);

    // Carrito activo por usuario (el más importante)
    Optional<CarritoCompra> findByUsuarioAndActivo(Usuario usuario, Boolean activo);

    Optional<CarritoCompra> findByUsuarioUsuarioIdAndActivo(Long usuarioId, Boolean activo);

    // Múltiples carritos activos por usuario (para verificación)
    List<CarritoCompra> findByUsuarioUsuarioIdAndActivoTrue(Long usuarioId);

    // Carritos por estado específico
    List<CarritoCompra> findByEstado(String estado);

    List<CarritoCompra> findByEstadoIgnoreCase(String estado);

    // Carritos activos en el sistema
    List<CarritoCompra> findByActivoTrue();

    List<CarritoCompra> findByActivoFalse();

    // Búsquedas por fecha de creación
    List<CarritoCompra> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT c FROM CarritoCompra c WHERE c.fechaCreacion >= :fecha")
    List<CarritoCompra> findCarritosCreadosDesde(@Param("fecha") LocalDateTime fecha);

    // Carritos modificados recientemente
    List<CarritoCompra> findByFechaModificacionBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT c FROM CarritoCompra c WHERE c.fechaModificacion >= :fecha ORDER BY c.fechaModificacion DESC")
    List<CarritoCompra> findCarritosModificadosDesde(@Param("fecha") LocalDateTime fecha);

    // Carritos con items específicos
    @Query("SELECT c FROM CarritoCompra c WHERE c.totalItems > 0 AND c.activo = true")
    List<CarritoCompra> findCarritosConItems();

    @Query("SELECT c FROM CarritoCompra c WHERE c.totalItems = 0 AND c.activo = true")
    List<CarritoCompra> findCarritosVacios();

    // Carritos por rango de total estimado
    @Query("SELECT c FROM CarritoCompra c WHERE c.totalEstimado BETWEEN :min AND :max AND c.activo = true")
    List<CarritoCompra> findByTotalEstimadoBetween(@Param("min") Double min, @Param("max") Double max);

    // Carritos con valor mínimo
    @Query("SELECT c FROM CarritoCompra c WHERE c.totalEstimado >= :minimo AND c.activo = true")
    List<CarritoCompra> findCarritosConValorMinimo(@Param("minimo") Double minimo);

    // Carritos por tipo de usuario (usando roles del sistema optimizado)
    @Query("SELECT c FROM CarritoCompra c WHERE c.usuario.rol.nombre = 'COMPRADOR' AND c.activo = true")
    List<CarritoCompra> findCarritosCompradores();

    @Query("SELECT c FROM CarritoCompra c WHERE c.usuario.rol.nombre = 'ADMINISTRADOR' AND c.activo = true")
    List<CarritoCompra> findCarritosAdministradores();

    // Usuarios con carritos activos
    @Query("SELECT DISTINCT c.usuario FROM CarritoCompra c WHERE c.activo = true")
    List<Usuario> findUsuariosConCarritosActivos();

    // Carritos abandonados (sin modificar en X tiempo)
    @Query("SELECT c FROM CarritoCompra c WHERE c.activo = true AND c.fechaModificacion < :fecha AND c.totalItems > 0")
    List<CarritoCompra> findCarritosAbandonados(@Param("fecha") LocalDateTime fecha);

    // Estadísticas de carritos
    @Query("SELECT COUNT(c) FROM CarritoCompra c WHERE c.activo = true")
    long countCarritosActivos();

    @Query("SELECT COUNT(c) FROM CarritoCompra c WHERE c.activo = true AND c.totalItems > 0")
    long countCarritosConProductos();

    @Query("SELECT AVG(c.totalEstimado) FROM CarritoCompra c WHERE c.activo = true AND c.totalItems > 0")
    Double findPromedioValorCarritos();

    @Query("SELECT SUM(c.totalEstimado) FROM CarritoCompra c WHERE c.activo = true")
    Double findTotalValorCarritosActivos();

    // Carritos por usuario específico ordenados
    @Query("SELECT c FROM CarritoCompra c WHERE c.usuario.usuarioId = :usuarioId ORDER BY c.fechaCreacion DESC")
    List<CarritoCompra> findByUsuarioOrderByFechaCreacionDesc(@Param("usuarioId") Long usuarioId);

    // Verificar si usuario tiene carrito activo
    @Query("SELECT COUNT(c) > 0 FROM CarritoCompra c WHERE c.usuario.usuarioId = :usuarioId AND c.activo = true")
    boolean existsCarritoActivoByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Eliminar carritos vacíos antiguos
    @Query("SELECT c FROM CarritoCompra c WHERE c.totalItems = 0 AND c.fechaModificacion < :fecha")
    List<CarritoCompra> findCarritosVaciosAntiguos(@Param("fecha") LocalDateTime fecha);

    // Carritos convertidos a órdenes
    @Query("SELECT c FROM CarritoCompra c WHERE c.estado = 'CONVERTIDO'")
    List<CarritoCompra> findCarritosConvertidos();

    // Búsqueda de carritos para limpieza automática
    @Query("SELECT c FROM CarritoCompra c WHERE c.activo = false AND c.fechaModificacion < :fecha")
    List<CarritoCompra> findCarritosParaLimpiar(@Param("fecha") LocalDateTime fecha);
}
