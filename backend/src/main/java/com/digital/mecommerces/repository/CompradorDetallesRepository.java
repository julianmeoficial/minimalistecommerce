package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.CompradorDetalles;
import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompradorDetallesRepository extends JpaRepository<CompradorDetalles, Long> {

    // Búsquedas básicas por ID de usuario
    Optional<CompradorDetalles> findByUsuarioId(Long usuarioId);

    // Verificar existencia por ID de usuario
    boolean existsByUsuarioId(Long usuarioId);

    // Eliminar por ID de usuario
    void deleteByUsuarioId(Long usuarioId);

    // Búsqueda por usuario completo
    Optional<CompradorDetalles> findByUsuario(Usuario usuario);

    // Búsquedas por estado activo
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.usuario.activo = :activo AND cd.activo = true")
    List<CompradorDetalles> findByUsuarioActivo(@Param("activo") Boolean activo);

    // Compradores activos en el sistema
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.usuario.activo = true AND cd.activo = true")
    List<CompradorDetalles> findCompradoresActivos();

    // Búsquedas por fecha de nacimiento
    List<CompradorDetalles> findByFechaNacimiento(LocalDate fechaNacimiento);

    List<CompradorDetalles> findByFechaNacimientoBetween(LocalDate inicio, LocalDate fin);

    // Compradores por rango de edad
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.fechaNacimiento BETWEEN :fechaFin AND :fechaInicio AND cd.usuario.activo = true")
    List<CompradorDetalles> findByEdadBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    // Búsquedas por ubicación
    List<CompradorDetalles> findByDireccionEnvioContainingIgnoreCase(String direccion);

    List<CompradorDetalles> findByTelefonoContaining(String telefono);

    // Búsquedas por preferencias de notificación
    List<CompradorDetalles> findByNotificacionEmailTrue();

    List<CompradorDetalles> findByNotificacionEmailFalse();

    List<CompradorDetalles> findByNotificacionSmsTrue();

    List<CompradorDetalles> findByNotificacionSmsFalse();

    // Combinación de preferencias de notificación
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.notificacionEmail = :email AND cd.notificacionSms = :sms AND cd.usuario.activo = true")
    List<CompradorDetalles> findByPreferenciasNotificacion(@Param("email") Boolean email, @Param("sms") Boolean sms);

    // Búsquedas por calificación
    List<CompradorDetalles> findByCalificacion(BigDecimal calificacion);

    List<CompradorDetalles> findByCalificacionBetween(BigDecimal minCalificacion, BigDecimal maxCalificacion);

    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.calificacion >= :minCalificacion AND cd.usuario.activo = true ORDER BY cd.calificacion DESC")
    List<CompradorDetalles> findByCalificacionMinima(@Param("minCalificacion") BigDecimal minCalificacion);

    // Compradores VIP (alta calificación)
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.calificacion >= 4.5 AND cd.usuario.activo = true")
    List<CompradorDetalles> findCompradoresVIP();

    // Búsquedas por total de compras
    List<CompradorDetalles> findByTotalCompras(Integer totalCompras);

    List<CompradorDetalles> findByTotalComprasBetween(Integer min, Integer max);

    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.totalCompras >= :minCompras AND cd.usuario.activo = true ORDER BY cd.totalCompras DESC")
    List<CompradorDetalles> findByComprasMinimas(@Param("minCompras") Integer minCompras);

    // Compradores frecuentes
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.totalCompras >= 5 AND cd.usuario.activo = true")
    List<CompradorDetalles> findCompradoriesFrecuentes();

    // Compradores nuevos (sin compras)
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.totalCompras = 0 AND cd.usuario.activo = true")
    List<CompradorDetalles> findCompradoresNuevos();

    // Búsquedas por límite de compra
    List<CompradorDetalles> findByLimiteCompraBetween(BigDecimal min, BigDecimal max);

    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.limiteCompra >= :limite AND cd.usuario.activo = true")
    List<CompradorDetalles> findConLimiteCompraMayor(@Param("limite") BigDecimal limite);

    // Compradores sin límite de compra
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.limiteCompra IS NULL AND cd.usuario.activo = true")
    List<CompradorDetalles> findSinLimiteCompra();

    // Búsquedas por fecha de registro
    List<CompradorDetalles> findByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.fechaRegistro >= :fecha AND cd.usuario.activo = true ORDER BY cd.fechaRegistro DESC")
    List<CompradorDetalles> findRegistradosDesde(@Param("fecha") LocalDateTime fecha);

    // Búsquedas por última compra
    List<CompradorDetalles> findByUltimaCompraBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.ultimaCompra >= :fecha AND cd.usuario.activo = true ORDER BY cd.ultimaCompra DESC")
    List<CompradorDetalles> findCompradoresActivosDesde(@Param("fecha") LocalDateTime fecha);

    // Compradores inactivos (sin compras recientes)
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.ultimaCompra < :fecha AND cd.usuario.activo = true")
    List<CompradorDetalles> findCompradoresInactivos(@Param("fecha") LocalDateTime fecha);

    // Compradores que nunca han comprado
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.ultimaCompra IS NULL AND cd.usuario.activo = true")
    List<CompradorDetalles> findSinCompras();

    // Búsquedas por información completa
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.direccionEnvio IS NOT NULL AND cd.telefono IS NOT NULL AND cd.usuario.activo = true")
    List<CompradorDetalles> findConInformacionCompleta();

    @Query("SELECT cd FROM CompradorDetalles cd WHERE (cd.direccionEnvio IS NULL OR cd.telefono IS NULL) AND cd.usuario.activo = true")
    List<CompradorDetalles> findConInformacionIncompleta();

    // Compradores por rol específico del sistema
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.usuario.rol.nombre = 'COMPRADOR' AND cd.usuario.activo = true")
    List<CompradorDetalles> findCompradoriesByRolSistema();

    // Estadísticas de compradores
    @Query("SELECT COUNT(cd) FROM CompradorDetalles cd WHERE cd.usuario.activo = true")
    long countCompradoresActivos();

    @Query("SELECT COUNT(cd) FROM CompradorDetalles cd WHERE cd.totalCompras > 0 AND cd.usuario.activo = true")
    long countCompradoresConCompras();

    @Query("SELECT AVG(cd.calificacion) FROM CompradorDetalles cd WHERE cd.usuario.activo = true")
    BigDecimal findPromedioCalificacion();

    @Query("SELECT AVG(cd.totalCompras) FROM CompradorDetalles cd WHERE cd.usuario.activo = true")
    Double findPromedioTotalCompras();

    @Query("SELECT SUM(cd.totalCompras) FROM CompradorDetalles cd WHERE cd.usuario.activo = true")
    Long sumTotalComprasGenerales();

    // Segmentación de compradores por nivel
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.totalCompras BETWEEN 0 AND 4 AND cd.usuario.activo = true")
    List<CompradorDetalles> findCompradoriesNivel1();

    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.totalCompras BETWEEN 5 AND 14 AND cd.usuario.activo = true")
    List<CompradorDetalles> findCompradoriesNivel2();

    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.totalCompras BETWEEN 15 AND 29 AND cd.usuario.activo = true")
    List<CompradorDetalles> findCompradoriesNivel3();

    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.totalCompras >= 30 AND cd.usuario.activo = true")
    List<CompradorDetalles> findCompradoriesNivel4();

    // Búsquedas para campañas de marketing
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.notificacionEmail = true AND cd.totalCompras >= :minCompras AND cd.usuario.activo = true")
    List<CompradorDetalles> findParaCampanaEmail(@Param("minCompras") Integer minCompras);

    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.notificacionSms = true AND cd.telefono IS NOT NULL AND cd.usuario.activo = true")
    List<CompradorDetalles> findParaCampanaSms();

    // Compradores potenciales (registrados pero sin compras)
    @Query("SELECT cd FROM CompradorDetalles cd WHERE cd.totalCompras = 0 AND cd.fechaRegistro >= :fecha AND cd.usuario.activo = true")
    List<CompradorDetalles> findPotencialesRecientes(@Param("fecha") LocalDateTime fecha);
}
