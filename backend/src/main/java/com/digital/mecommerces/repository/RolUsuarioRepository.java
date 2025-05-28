package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Long> {

    // Búsqueda básica por nombre (la más importante)
    Optional<RolUsuario> findByNombre(String nombre);

    Optional<RolUsuario> findByNombreIgnoreCase(String nombre);

    // Verificar existencia por nombre
    boolean existsByNombre(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    // Búsqueda por descripción
    List<RolUsuario> findByDescripcionContainingIgnoreCase(String descripcion);

    // Roles del sistema usando constantes optimizadas
    @Query("SELECT r FROM RolUsuario r WHERE r.nombre IN ('ADMINISTRADOR', 'VENDEDOR', 'COMPRADOR') ORDER BY r.nombre ASC")
    List<RolUsuario> findRolesDelSistema();

    // Roles personalizados (no del sistema)
    @Query("SELECT r FROM RolUsuario r WHERE r.nombre NOT IN ('ADMINISTRADOR', 'VENDEDOR', 'COMPRADOR') ORDER BY r.nombre ASC")
    List<RolUsuario> findRolesPersonalizados();

    // Verificar si es rol del sistema
    @Query("SELECT COUNT(r) > 0 FROM RolUsuario r WHERE r.nombre = :nombre AND r.nombre IN ('ADMINISTRADOR', 'VENDEDOR', 'COMPRADOR')")
    boolean esRolDelSistema(@Param("nombre") String nombre);

    // Roles específicos del sistema optimizado
    @Query("SELECT r FROM RolUsuario r WHERE r.nombre = 'ADMINISTRADOR'")
    Optional<RolUsuario> findRolAdministrador();

    @Query("SELECT r FROM RolUsuario r WHERE r.nombre = 'VENDEDOR'")
    Optional<RolUsuario> findRolVendedor();

    @Query("SELECT r FROM RolUsuario r WHERE r.nombre = 'COMPRADOR'")
    Optional<RolUsuario> findRolComprador();

    // Roles con usuarios asignados
    @Query("SELECT DISTINCT r FROM RolUsuario r INNER JOIN Usuario u ON u.rol.rolId = r.rolId")
    List<RolUsuario> findRolesConUsuarios();

    // Roles sin usuarios asignados
    @Query("SELECT r FROM RolUsuario r WHERE NOT EXISTS (SELECT u FROM Usuario u WHERE u.rol.rolId = r.rolId)")
    List<RolUsuario> findRolesSinUsuarios();

    // Contar usuarios por rol
    @Query("SELECT r, COUNT(u) FROM RolUsuario r LEFT JOIN Usuario u ON u.rol.rolId = r.rolId GROUP BY r.rolId ORDER BY COUNT(u) DESC")
    List<Object[]> countUsuariosPorRol();

    // Roles con más de X usuarios
    @Query("SELECT r FROM RolUsuario r WHERE (SELECT COUNT(u) FROM Usuario u WHERE u.rol.rolId = r.rolId) >= :minUsuarios ORDER BY r.nombre ASC")
    List<RolUsuario> findRolesConMinimoUsuarios(@Param("minUsuarios") Long minUsuarios);

    // Roles activos (que tienen usuarios activos)
    @Query("SELECT DISTINCT r FROM RolUsuario r INNER JOIN Usuario u ON u.rol.rolId = r.rolId WHERE u.activo = true")
    List<RolUsuario> findRolesActivos();

    // Roles con permisos asignados
    @Query("SELECT DISTINCT r FROM RolUsuario r INNER JOIN RolPermiso rp ON rp.rolId = r.rolId")
    List<RolUsuario> findRolesConPermisos();

    // Roles sin permisos asignados
    @Query("SELECT r FROM RolUsuario r WHERE NOT EXISTS (SELECT rp FROM RolPermiso rp WHERE rp.rolId = r.rolId)")
    List<RolUsuario> findRolesSinPermisos();

    // Contar permisos por rol
    @Query("SELECT r, COUNT(rp) FROM RolUsuario r LEFT JOIN RolPermiso rp ON rp.rolId = r.rolId GROUP BY r.rolId ORDER BY COUNT(rp) DESC")
    List<Object[]> countPermisosPorRol();

    // Roles que tienen un permiso específico
    @Query("SELECT r FROM RolUsuario r INNER JOIN RolPermiso rp ON rp.rolId = r.rolId INNER JOIN Permiso p ON p.permisoId = rp.permisoId WHERE p.codigo = :codigoPermiso")
    List<RolUsuario> findRolesConPermiso(@Param("codigoPermiso") String codigoPermiso);

    // Roles que NO tienen un permiso específico
    @Query("SELECT r FROM RolUsuario r WHERE NOT EXISTS (SELECT rp FROM RolPermiso rp INNER JOIN Permiso p ON p.permisoId = rp.permisoId WHERE rp.rolId = r.rolId AND p.codigo = :codigoPermiso)")
    List<RolUsuario> findRolesSinPermiso(@Param("codigoPermiso") String codigoPermiso);

    // Búsquedas para seguridad y auditoría
    @Query("SELECT r FROM RolUsuario r INNER JOIN RolPermiso rp ON rp.rolId = r.rolId INNER JOIN Permiso p ON p.permisoId = rp.permisoId WHERE p.codigo = 'ADMIN_TOTAL'")
    List<RolUsuario> findRolesConPermisoAdminTotal();

    @Query("SELECT r FROM RolUsuario r INNER JOIN RolPermiso rp ON rp.rolId = r.rolId INNER JOIN Permiso p ON p.permisoId = rp.permisoId WHERE p.nivel = 1")
    List<RolUsuario> findRolesConPermisosNivelAlto();

    // Estadísticas de roles
    @Query("SELECT COUNT(r) FROM RolUsuario r")
    long countTotalRoles();

    @Query("SELECT COUNT(r) FROM RolUsuario r WHERE r.nombre IN ('ADMINISTRADOR', 'VENDEDOR', 'COMPRADOR')")
    long countRolesDelSistema();

    @Query("SELECT COUNT(r) FROM RolUsuario r WHERE r.nombre NOT IN ('ADMINISTRADOR', 'VENDEDOR', 'COMPRADOR')")
    long countRolesPersonalizados();

    // Análisis de jerarquía de roles por número de permisos
    @Query("SELECT r FROM RolUsuario r ORDER BY (SELECT COUNT(rp) FROM RolPermiso rp WHERE rp.rolId = r.rolId) DESC")
    List<RolUsuario> findRolesOrdenadosPorNumeroPermisos();

    // Roles por nivel de permisos promedio
    @Query("SELECT r, AVG(p.nivel) as nivelPromedio FROM RolUsuario r LEFT JOIN RolPermiso rp ON rp.rolId = r.rolId LEFT JOIN Permiso p ON p.permisoId = rp.permisoId GROUP BY r.rolId ORDER BY nivelPromedio ASC")
    List<Object[]> findRolesPorNivelPermisoPromedio();

    // Búsquedas por patrones de nombre
    @Query("SELECT r FROM RolUsuario r WHERE r.nombre LIKE :patron ORDER BY r.nombre ASC")
    List<RolUsuario> findByNombrePattern(@Param("patron") String patron);

    // Búsquedas para validación
    @Query("SELECT COUNT(r) > 0 FROM RolUsuario r WHERE r.nombre = :nombre AND r.rolId != :id")
    boolean existsByNombreAndRolIdNot(@Param("nombre") String nombre, @Param("id") Long id);

    // Roles más utilizados (con más usuarios)
    @Query("SELECT r FROM RolUsuario r ORDER BY (SELECT COUNT(u) FROM Usuario u WHERE u.rol.rolId = r.rolId) DESC")
    List<RolUsuario> findRolesMasUtilizados();

    // Roles menos utilizados (con menos usuarios)
    @Query("SELECT r FROM RolUsuario r ORDER BY (SELECT COUNT(u) FROM Usuario u WHERE u.rol.rolId = r.rolId) ASC")
    List<RolUsuario> findRolesMenosUtilizados();

    // Análisis de distribución de usuarios por rol del sistema
    @Query("SELECT r.nombre, COUNT(u) as totalUsuarios FROM RolUsuario r LEFT JOIN Usuario u ON u.rol.rolId = r.rolId WHERE r.nombre IN ('ADMINISTRADOR', 'VENDEDOR', 'COMPRADOR') GROUP BY r.rolId ORDER BY totalUsuarios DESC")
    List<Object[]> findDistribucionUsuariosRolesSistema();

    // Roles que necesitan configuración de permisos
    @Query("SELECT r FROM RolUsuario r WHERE r.nombre IN ('ADMINISTRADOR', 'VENDEDOR', 'COMPRADOR') AND NOT EXISTS (SELECT rp FROM RolPermiso rp WHERE rp.rolId = r.rolId)")
    List<RolUsuario> findRolesSistemaSinPermisos();

    // Búsquedas para reportes administrativos
    @Query("SELECT r.nombre, COUNT(u) as usuarios, COUNT(rp) as permisos FROM RolUsuario r LEFT JOIN Usuario u ON u.rol.rolId = r.rolId LEFT JOIN RolPermiso rp ON rp.rolId = r.rolId GROUP BY r.rolId ORDER BY r.nombre ASC")
    List<Object[]> findResumenRoles();

    // Verificación de integridad del sistema
    @Query("SELECT r FROM RolUsuario r WHERE r.nombre = 'ADMINISTRADOR' AND EXISTS (SELECT rp FROM RolPermiso rp INNER JOIN Permiso p ON p.permisoId = rp.permisoId WHERE rp.rolId = r.rolId AND p.codigo = 'ADMIN_TOTAL')")
    Optional<RolUsuario> findAdministradorConfigurado();

    @Query("SELECT r FROM RolUsuario r WHERE r.nombre = 'VENDEDOR' AND EXISTS (SELECT rp FROM RolPermiso rp INNER JOIN Permiso p ON p.permisoId = rp.permisoId WHERE rp.rolId = r.rolId AND p.codigo = 'VENDER_PRODUCTOS')")
    Optional<RolUsuario> findVendedorConfigurado();

    @Query("SELECT r FROM RolUsuario r WHERE r.nombre = 'COMPRADOR' AND EXISTS (SELECT rp FROM RolPermiso rp INNER JOIN Permiso p ON p.permisoId = rp.permisoId WHERE rp.rolId = r.rolId AND p.codigo = 'COMPRAR_PRODUCTOS')")
    Optional<RolUsuario> findCompradorConfigurado();

    // Análisis de categorías de permisos por rol
    @Query("SELECT r.nombre, p.categoria, COUNT(rp) FROM RolUsuario r INNER JOIN RolPermiso rp ON rp.rolId = r.rolId INNER JOIN Permiso p ON p.permisoId = rp.permisoId GROUP BY r.rolId, p.categoria ORDER BY r.nombre, p.categoria")
    List<Object[]> findDistribucionPermisosPorCategoria();

    // Roles ordenados alfabéticamente
    @Query("SELECT r FROM RolUsuario r ORDER BY r.nombre ASC")
    List<RolUsuario> findAllOrderByNombre();

    // Búsquedas para configuración inicial del sistema
    @Query("SELECT COUNT(r) FROM RolUsuario r WHERE r.nombre IN ('ADMINISTRADOR', 'VENDEDOR', 'COMPRADOR')")
    long countRolesSistemaConfigurados();

    // Verificar completitud de configuración
    @Query("SELECT r FROM RolUsuario r WHERE r.nombre IN ('ADMINISTRADOR', 'VENDEDOR', 'COMPRADOR') ORDER BY CASE WHEN r.nombre = 'ADMINISTRADOR' THEN 1 WHEN r.nombre = 'VENDEDOR' THEN 2 WHEN r.nombre = 'COMPRADOR' THEN 3 END")
    List<RolUsuario> findRolesSistemaOrdenados();
}
