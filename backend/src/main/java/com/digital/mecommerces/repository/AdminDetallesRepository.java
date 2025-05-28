package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.AdminDetalles;
import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminDetallesRepository extends JpaRepository<AdminDetalles, Long> {

    // Búsqueda básica por ID de usuario
    Optional<AdminDetalles> findByUsuarioId(Long usuarioId);

    // Verificar existencia por ID de usuario
    boolean existsByUsuarioId(Long usuarioId);

    // Eliminar por ID de usuario
    void deleteByUsuarioId(Long usuarioId);

    // Búsqueda por usuario completo
    Optional<AdminDetalles> findByUsuario(Usuario usuario);

    // Búsquedas optimizadas por región
    List<AdminDetalles> findByRegion(String region);

    List<AdminDetalles> findByRegionIgnoreCase(String region);

    // Búsquedas por nivel de acceso
    List<AdminDetalles> findByNivelAcceso(String nivelAcceso);

    List<AdminDetalles> findByNivelAccesoIgnoreCase(String nivelAcceso);

    // Búsquedas por estado activo
    @Query("SELECT ad FROM AdminDetalles ad WHERE ad.usuario.activo = :activo")
    List<AdminDetalles> findByUsuarioActivo(@Param("activo") Boolean activo);

    // Administradores activos en el sistema
    @Query("SELECT ad FROM AdminDetalles ad WHERE ad.usuario.activo = true AND ad.activo = true")
    List<AdminDetalles> findAdministradoresActivos();

    // Búsqueda por último login en rango de fechas
    List<AdminDetalles> findByUltimoLoginBetween(LocalDateTime inicio, LocalDateTime fin);

    // Administradores que han hecho login recientemente
    @Query("SELECT ad FROM AdminDetalles ad WHERE ad.ultimoLogin >= :fecha")
    List<AdminDetalles> findAdministradoresActivosDesde(@Param("fecha") LocalDateTime fecha);

    // Búsqueda por IP de acceso
    List<AdminDetalles> findByIpAcceso(String ipAcceso);

    // Administradores con sesiones activas
    @Query("SELECT ad FROM AdminDetalles ad WHERE ad.sesionesActivas > 0")
    List<AdminDetalles> findConSesionesActivas();

    // Contar administradores por nivel de acceso
    @Query("SELECT COUNT(ad) FROM AdminDetalles ad WHERE ad.nivelAcceso = :nivel AND ad.usuario.activo = true")
    long countByNivelAccesoAndUsuarioActivo(@Param("nivel") String nivel);

    // Administradores SUPER activos
    @Query("SELECT ad FROM AdminDetalles ad WHERE ad.nivelAcceso = 'SUPER' AND ad.usuario.activo = true")
    List<AdminDetalles> findAdministradoresSuper();

    // Administradores por rol específico del sistema
    @Query("SELECT ad FROM AdminDetalles ad WHERE ad.usuario.rol.nombre = 'ADMINISTRADOR' AND ad.usuario.activo = true")
    List<AdminDetalles> findAdministradoresByRolSistema();

    // Búsqueda por actividad reciente con paginación
    @Query("SELECT ad FROM AdminDetalles ad WHERE ad.ultimaActividad >= :fecha ORDER BY ad.ultimaActividad DESC")
    List<AdminDetalles> findByActividadReciente(@Param("fecha") LocalDateTime fecha);

    // Administradores que requieren verificación
    @Query("SELECT ad FROM AdminDetalles ad WHERE ad.usuario.activo = true AND ad.ultimoLogin IS NULL")
    List<AdminDetalles> findSinPrimerLogin();

    // Estadísticas de administradores
    @Query("SELECT COUNT(ad) FROM AdminDetalles ad WHERE ad.usuario.activo = true")
    long countAdministradoresActivos();

    @Query("SELECT COUNT(ad) FROM AdminDetalles ad WHERE ad.sesionesActivas > 0")
    long countConSesionesActivas();

    // Búsqueda por configuraciones específicas
    @Query("SELECT ad FROM AdminDetalles ad WHERE ad.configuraciones LIKE %:config%")
    List<AdminDetalles> findByConfiguracionesContaining(@Param("config") String config);

    // Último administrador que realizó una acción específica
    @Query("SELECT ad FROM AdminDetalles ad WHERE ad.ultimaAccion LIKE %:accion% ORDER BY ad.ultimaActividad DESC")
    List<AdminDetalles> findByUltimaAccionContaining(@Param("accion") String accion);
}
