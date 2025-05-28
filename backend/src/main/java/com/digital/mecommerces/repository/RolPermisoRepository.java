package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.RolPermiso;
import com.digital.mecommerces.model.RolPermisoId;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, RolPermisoId> {

    // Búsquedas básicas por rol
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rolId = :rolId")
    List<RolPermiso> findByRolId(@Param("rolId") Long rolId);

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.rolId = :rolId ORDER BY rp.permiso.nivel ASC")
    List<RolPermiso> findByRolIdOrderByPermisoNivel(@Param("rolId") Long rolId);

    // Búsquedas por permiso
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permisoId = :permisoId")
    List<RolPermiso> findByPermisoId(@Param("permisoId") Long permisoId);

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permiso.permisoId = :permisoId ORDER BY rp.rol.nombre ASC")
    List<RolPermiso> findByPermisoIdOrderByRolNombre(@Param("permisoId") Long permisoId);

    // Verificar existencia de relación específica
    @Query("SELECT COUNT(rp) > 0 FROM RolPermiso rp WHERE rp.rolId = :rolId AND rp.permisoId = :permisoId")
    boolean existsByRolIdAndPermisoId(@Param("rolId") Long rolId, @Param("permisoId") Long permisoId);

    // Buscar relación específica
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rolId = :rolId AND rp.permisoId = :permisoId")
    Optional<RolPermiso> findByRolIdAndPermisoId(@Param("rolId") Long rolId, @Param("permisoId") Long permisoId);

    // Eliminar por rol específico
    @Modifying
    @Transactional
    @Query("DELETE FROM RolPermiso rp WHERE rp.rolId = :rolId")
    void deleteByRolId(@Param("rolId") Long rolId);

    // Eliminar por permiso específico
    @Modifying
    @Transactional
    @Query("DELETE FROM RolPermiso rp WHERE rp.permisoId = :permisoId")
    void deleteByPermisoId(@Param("permisoId") Long permisoId);

    // Eliminar relación específica
    @Modifying
    @Transactional
    @Query("DELETE FROM RolPermiso rp WHERE rp.rolId = :rolId AND rp.permisoId = :permisoId")
    void deleteByRolIdAndPermisoId(@Param("rolId") Long rolId, @Param("permisoId") Long permisoId);

    // Contar permisos por rol
    @Query("SELECT COUNT(rp) FROM RolPermiso rp WHERE rp.rolId = :rolId")
    long countByRolId(@Param("rolId") Long rolId);

    // Contar roles por permiso
    @Query("SELECT COUNT(rp) FROM RolPermiso rp WHERE rp.permisoId = :permisoId")
    long countByPermisoId(@Param("permisoId") Long permisoId);

    // Búsquedas por roles específicos del sistema optimizado
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.nombre = 'ADMINISTRADOR' ORDER BY rp.permiso.nivel ASC")
    List<RolPermiso> findPermisosAdministrador();

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.nombre = 'VENDEDOR' ORDER BY rp.permiso.nivel ASC")
    List<RolPermiso> findPermisosVendedor();

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.nombre = 'COMPRADOR' ORDER BY rp.permiso.nivel ASC")
    List<RolPermiso> findPermisosComprador();

    // Permisos del sistema por rol
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.nombre = :rolNombre AND rp.permiso.codigo IN ('ADMIN_TOTAL', 'VENDER_PRODUCTOS', 'COMPRAR_PRODUCTOS', 'GESTIONAR_USUARIOS', 'GESTIONAR_CATEGORIAS')")
    List<RolPermiso> findPermisosDelSistemaPorRol(@Param("rolNombre") String rolNombre);

    // Permisos personalizados por rol
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.nombre = :rolNombre AND rp.permiso.codigo NOT IN ('ADMIN_TOTAL', 'VENDER_PRODUCTOS', 'COMPRAR_PRODUCTOS', 'GESTIONAR_USUARIOS', 'GESTIONAR_CATEGORIAS')")
    List<RolPermiso> findPermisosPersonalizadosPorRol(@Param("rolNombre") String rolNombre);

    // Búsquedas por nivel de permiso
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permiso.nivel <= :maxNivel ORDER BY rp.permiso.nivel ASC")
    List<RolPermiso> findByPermisoNivelMenorIgual(@Param("maxNivel") Integer maxNivel);

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.nombre = :rolNombre AND rp.permiso.nivel <= :maxNivel ORDER BY rp.permiso.nivel ASC")
    List<RolPermiso> findByRolAndPermisoNivelMenorIgual(@Param("rolNombre") String rolNombre, @Param("maxNivel") Integer maxNivel);

    // Búsquedas por categoría de permiso
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permiso.categoria = :categoria ORDER BY rp.rol.nombre ASC")
    List<RolPermiso> findByPermisoCategoria(@Param("categoria") String categoria);

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.nombre = :rolNombre AND rp.permiso.categoria = :categoria")
    List<RolPermiso> findByRolAndPermisoCategoria(@Param("rolNombre") String rolNombre, @Param("categoria") String categoria);

    // Búsquedas por fecha de creación
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.createdat >= :fecha ORDER BY rp.createdat DESC")
    List<RolPermiso> findAsignacionesDesde(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.createdat BETWEEN :inicio AND :fin ORDER BY rp.createdat DESC")
    List<RolPermiso> findAsignacionesEntreFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Búsquedas por usuario que creó la asignación
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.createdby = :createdBy ORDER BY rp.createdat DESC")
    List<RolPermiso> findByCreatedBy(@Param("createdBy") String createdBy);

    // Estadísticas de asignaciones
    @Query("SELECT COUNT(rp) FROM RolPermiso rp")
    long countTotalAsignaciones();

    @Query("SELECT rp.rol.nombre, COUNT(rp) FROM RolPermiso rp GROUP BY rp.rol.nombre ORDER BY COUNT(rp) DESC")
    List<Object[]> countAsignacionesPorRol();

    @Query("SELECT rp.permiso.codigo, COUNT(rp) FROM RolPermiso rp GROUP BY rp.permiso.codigo ORDER BY COUNT(rp) DESC")
    List<Object[]> countAsignacionesPorPermiso();

    @Query("SELECT rp.permiso.categoria, COUNT(rp) FROM RolPermiso rp GROUP BY rp.permiso.categoria ORDER BY COUNT(rp) DESC")
    List<Object[]> countAsignacionesPorCategoriaPermiso();

    // Verificar permisos específicos por rol
    @Query("SELECT COUNT(rp) > 0 FROM RolPermiso rp WHERE rp.rol.nombre = :rolNombre AND rp.permiso.codigo = :codigoPermiso")
    boolean rolTienePermiso(@Param("rolNombre") String rolNombre, @Param("codigoPermiso") String codigoPermiso);

    // Permisos críticos del sistema
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permiso.codigo = 'ADMIN_TOTAL'")
    List<RolPermiso> findAsignacionesAdminTotal();

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permiso.codigo = 'VENDER_PRODUCTOS'")
    List<RolPermiso> findAsignacionesVenderProductos();

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permiso.codigo = 'COMPRAR_PRODUCTOS'")
    List<RolPermiso> findAsignacionesComprarProductos();

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permiso.codigo = 'GESTIONAR_USUARIOS'")
    List<RolPermiso> findAsignacionesGestionarUsuarios();

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permiso.codigo = 'GESTIONAR_CATEGORIAS'")
    List<RolPermiso> findAsignacionesGestionarCategorias();

    // Roles que tienen un permiso específico
    @Query("SELECT rp.rol FROM RolPermiso rp WHERE rp.permiso.codigo = :codigoPermiso")
    List<RolUsuario> findRolesConPermiso(@Param("codigoPermiso") String codigoPermiso);

    // Permisos que tiene un rol específico
    @Query("SELECT rp.permiso FROM RolPermiso rp WHERE rp.rol.nombre = :rolNombre ORDER BY rp.permiso.nivel ASC")
    List<Permiso> findPermisosDeRol(@Param("rolNombre") String rolNombre);

    // Análisis de jerarquía de permisos
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permiso.nivel = 1 ORDER BY rp.rol.nombre ASC")
    List<RolPermiso> findAsignacionesNivelAlto();

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permiso.nivel BETWEEN 2 AND 3 ORDER BY rp.rol.nombre ASC")
    List<RolPermiso> findAsignacionesNivelMedio();

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permiso.nivel >= 4 ORDER BY rp.rol.nombre ASC")
    List<RolPermiso> findAsignacionesNivelBasico();

    // Validaciones de seguridad
    @Query("SELECT COUNT(rp) FROM RolPermiso rp WHERE rp.rol.nombre = 'ADMINISTRADOR' AND rp.permiso.codigo = 'ADMIN_TOTAL'")
    long countAdministradoresConPermisoTotal();

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.nombre != 'ADMINISTRADOR' AND rp.permiso.codigo = 'ADMIN_TOTAL'")
    List<RolPermiso> findAsignacionesIrregularesAdminTotal();

    // Búsquedas para auditoría
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.createdby = 'SYSTEM' ORDER BY rp.createdat ASC")
    List<RolPermiso> findAsignacionesDelSistema();

    @Query("SELECT rp FROM RolPermiso rp WHERE rp.createdby != 'SYSTEM' ORDER BY rp.createdat DESC")
    List<RolPermiso> findAsignacionesManuales();

    // Análisis de cobertura
    @Query("SELECT r FROM RolUsuario r WHERE NOT EXISTS (SELECT rp FROM RolPermiso rp WHERE rp.rol.rolId = r.rolId)")
    List<RolUsuario> findRolesSinPermisos();

    @Query("SELECT p FROM Permiso p WHERE NOT EXISTS (SELECT rp FROM RolPermiso rp WHERE rp.permiso.permisoId = p.permisoId)")
    List<Permiso> findPermisosNoAsignados();

    // Búsquedas optimizadas para carga lazy
    @Query("SELECT rp FROM RolPermiso rp JOIN FETCH rp.rol JOIN FETCH rp.permiso WHERE rp.rol.nombre = :rolNombre")
    List<RolPermiso> findByRolNombreWithDetails(@Param("rolNombre") String rolNombre);

    @Query("SELECT rp FROM RolPermiso rp JOIN FETCH rp.rol JOIN FETCH rp.permiso WHERE rp.permiso.codigo = :codigoPermiso")
    List<RolPermiso> findByPermisoCodigoWithDetails(@Param("codigoPermiso") String codigoPermiso);

    // Métodos de limpieza y mantenimiento
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol IS NULL OR rp.permiso IS NULL")
    List<RolPermiso> findAsignacionesHuerfanas();

    @Modifying
    @Transactional
    @Query("DELETE FROM RolPermiso rp WHERE rp.rol IS NULL OR rp.permiso IS NULL")
    void deleteAsignacionesHuerfanas();

    // Búsquedas para reportes
    @Query("SELECT DATE(rp.createdat) as fecha, COUNT(rp) as total FROM RolPermiso rp GROUP BY DATE(rp.createdat) ORDER BY fecha DESC")
    List<Object[]> findEstadisticasPorFecha();

    @Query("SELECT rp.createdby, COUNT(rp) as total FROM RolPermiso rp GROUP BY rp.createdby ORDER BY total DESC")
    List<Object[]> findEstadisticasPorCreador();
}
