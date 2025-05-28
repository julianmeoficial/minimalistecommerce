package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Búsquedas básicas por email (la más importante para autenticación)
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    // Verificar existencia por email
    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    // Búsqueda por nombre de usuario
    List<Usuario> findByUsuarioNombre(String usuarioNombre);

    List<Usuario> findByUsuarioNombreContainingIgnoreCase(String usuarioNombre);

    // Búsquedas por rol usando optimización para enums
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = :rolNombre ORDER BY u.createdAt DESC")
    List<Usuario> findByRolNombre(@Param("rolNombre") String rolNombre);

    @Query("SELECT u FROM Usuario u WHERE u.rol.rolId = :rolId ORDER BY u.createdAt DESC")
    List<Usuario> findByRolRolId(@Param("rolId") Long rolId);

    // Usuarios por roles específicos del sistema optimizado
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'ADMINISTRADOR' ORDER BY u.createdAt ASC")
    List<Usuario> findAdministradores();

    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'VENDEDOR' ORDER BY u.createdAt DESC")
    List<Usuario> findVendedores();

    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'COMPRADOR' ORDER BY u.createdAt DESC")
    List<Usuario> findCompradores();

    // Usuarios activos e inactivos
    List<Usuario> findByActivoTrue();

    List<Usuario> findByActivoFalse();

    @Query("SELECT u FROM Usuario u WHERE u.activo = true ORDER BY u.createdAt DESC")
    List<Usuario> findUsuariosActivos();

    @Query("SELECT u FROM Usuario u WHERE u.activo = false ORDER BY u.updatedAt DESC")
    List<Usuario> findUsuariosInactivos();

    // Búsquedas por estado activo y rol combinadas
    @Query("SELECT u FROM Usuario u WHERE u.activo = :activo AND u.rol.nombre = :rolNombre ORDER BY u.createdAt DESC")
    List<Usuario> findByActivoAndRolNombre(@Param("activo") Boolean activo, @Param("rolNombre") String rolNombre);

    // Usuarios activos por rol específico del sistema
    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND u.rol.nombre = 'ADMINISTRADOR'")
    List<Usuario> findAdministradoresActivos();

    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND u.rol.nombre = 'VENDEDOR'")
    List<Usuario> findVendedoresActivos();

    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND u.rol.nombre = 'COMPRADOR'")
    List<Usuario> findCompradoresActivos();

    // Búsquedas por fecha de creación
    List<Usuario> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT u FROM Usuario u WHERE u.createdAt >= :fecha ORDER BY u.createdAt DESC")
    List<Usuario> findUsuariosCreadosDesde(@Param("fecha") LocalDateTime fecha);

    // Usuarios registrados hoy
    @Query("SELECT u FROM Usuario u WHERE DATE(u.createdAt) = CURRENT_DATE ORDER BY u.createdAt DESC")
    List<Usuario> findUsuariosDeHoy();

    // Usuarios registrados esta semana
    @Query("SELECT u FROM Usuario u WHERE WEEK(u.createdAt) = WEEK(CURRENT_DATE) AND YEAR(u.createdAt) = YEAR(CURRENT_DATE) ORDER BY u.createdAt DESC")
    List<Usuario> findUsuariosDeLaSemana();

    // Usuarios registrados este mes
    @Query("SELECT u FROM Usuario u WHERE MONTH(u.createdAt) = MONTH(CURRENT_DATE) AND YEAR(u.createdAt) = YEAR(CURRENT_DATE) ORDER BY u.createdAt DESC")
    List<Usuario> findUsuariosDelMes();

    // Búsquedas por último login
    @Query("SELECT u FROM Usuario u WHERE u.ultimoLogin IS NOT NULL ORDER BY u.ultimoLogin DESC")
    List<Usuario> findUsuariosConLogin();

    @Query("SELECT u FROM Usuario u WHERE u.ultimoLogin IS NULL AND u.activo = true ORDER BY u.createdAt DESC")
    List<Usuario> findUsuariosSinLogin();

    @Query("SELECT u FROM Usuario u WHERE u.ultimoLogin >= :fecha ORDER BY u.ultimoLogin DESC")
    List<Usuario> findUsuariosConLoginDesde(@Param("fecha") LocalDateTime fecha);

    // Usuarios con login reciente (últimas 24 horas)
    @Query("SELECT u FROM Usuario u WHERE u.ultimoLogin >= :ayer ORDER BY u.ultimoLogin DESC")
    List<Usuario> findUsuariosConLoginReciente(@Param("ayer") LocalDateTime ayer);

    // Usuarios inactivos (sin login en X tiempo)
    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND (u.ultimoLogin IS NULL OR u.ultimoLogin < :fecha) ORDER BY u.ultimoLogin ASC")
    List<Usuario> findUsuariosInactivosPorTiempo(@Param("fecha") LocalDateTime fecha);

    // Estadísticas de usuarios
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.activo = true")
    long countUsuariosActivos();

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.activo = false")
    long countUsuariosInactivos();

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.rolId = :rolId")
    long countByRolRolId(@Param("rolId") Long rolId);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.nombre = :rolNombre")
    long countByRolNombre(@Param("rolNombre") String rolNombre);

    // Conteos por rol específico del sistema
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.nombre = 'ADMINISTRADOR' AND u.activo = true")
    long countAdministradoresActivos();

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.nombre = 'VENDEDOR' AND u.activo = true")
    long countVendedoresActivos();

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.nombre = 'COMPRADOR' AND u.activo = true")
    long countCompradoresActivos();

    // Estadísticas por rol
    @Query("SELECT u.rol.nombre, COUNT(u) FROM Usuario u WHERE u.activo = true GROUP BY u.rol.nombre ORDER BY COUNT(u) DESC")
    List<Object[]> countUsuariosPorRol();

    @Query("SELECT u.rol.nombre, COUNT(u) as total FROM Usuario u GROUP BY u.rol.nombre ORDER BY total DESC")
    List<Object[]> countTotalUsuariosPorRol();

    // Búsquedas para análisis de actividad
    @Query("SELECT DATE(u.createdAt) as fecha, COUNT(u) as total FROM Usuario u GROUP BY DATE(u.createdAt) ORDER BY fecha DESC")
    List<Object[]> findRegistrosPorFecha();

    @Query("SELECT DATE(u.ultimoLogin) as fecha, COUNT(u) as total FROM Usuario u WHERE u.ultimoLogin IS NOT NULL GROUP BY DATE(u.ultimoLogin) ORDER BY fecha DESC")
    List<Object[]> findLoginsPorFecha();

    // Búsquedas por texto en nombre o email
    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND (LOWER(u.usuarioNombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :termino, '%'))) ORDER BY u.createdAt DESC")
    List<Usuario> findByTextoEnNombreOEmail(@Param("termino") String termino);

    // Usuarios con detalles específicos según su rol
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'ADMINISTRADOR' AND EXISTS (SELECT ad FROM AdminDetalles ad WHERE ad.usuario.usuarioId = u.usuarioId)")
    List<Usuario> findAdministradoresConDetalles();

    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'COMPRADOR' AND EXISTS (SELECT cd FROM CompradorDetalles cd WHERE cd.usuario.usuarioId = u.usuarioId)")
    List<Usuario> findCompradoresConDetalles();

    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'VENDEDOR' AND EXISTS (SELECT vd FROM VendedorDetalles vd WHERE vd.usuario.usuarioId = u.usuarioId)")
    List<Usuario> findVendedoresConDetalles();

    // Usuarios sin detalles específicos
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'ADMINISTRADOR' AND NOT EXISTS (SELECT ad FROM AdminDetalles ad WHERE ad.usuario.usuarioId = u.usuarioId)")
    List<Usuario> findAdministradoresSinDetalles();

    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'COMPRADOR' AND NOT EXISTS (SELECT cd FROM CompradorDetalles cd WHERE cd.usuario.usuarioId = u.usuarioId)")
    List<Usuario> findCompradoresSinDetalles();

    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'VENDEDOR' AND NOT EXISTS (SELECT vd FROM VendedorDetalles vd WHERE vd.usuario.usuarioId = u.usuarioId)")
    List<Usuario> findVendedoresSinDetalles();

    // Búsquedas para gestión de vendedores
    @Query("SELECT u FROM Usuario u INNER JOIN VendedorDetalles vd ON vd.usuario.usuarioId = u.usuarioId WHERE vd.verificado = true AND u.activo = true")
    List<Usuario> findVendedoresVerificados();

    @Query("SELECT u FROM Usuario u INNER JOIN VendedorDetalles vd ON vd.usuario.usuarioId = u.usuarioId WHERE vd.verificado = false AND u.activo = true")
    List<Usuario> findVendedoresNoVerificados();

    // Usuarios con productos (vendedores con inventario)
    @Query("SELECT DISTINCT u FROM Usuario u INNER JOIN Producto p ON p.vendedor.usuarioId = u.usuarioId WHERE u.activo = true AND p.activo = true")
    List<Usuario> findVendedoresConProductos();

    // Usuarios con órdenes (compradores con historial)
    @Query("SELECT DISTINCT u FROM Usuario u INNER JOIN Orden o ON o.usuario.usuarioId = u.usuarioId WHERE u.activo = true")
    List<Usuario> findUsuariosConOrdenes();

    // Usuarios con carritos activos
    @Query("SELECT DISTINCT u FROM Usuario u INNER JOIN CarritoCompra c ON c.usuario.usuarioId = u.usuarioId WHERE c.activo = true AND u.activo = true")
    List<Usuario> findUsuariosConCarritosActivos();

    // Validaciones para unicidad
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.email = :email AND u.usuarioId != :id")
    boolean existsByEmailAndUsuarioIdNot(@Param("email") String email, @Param("id") Long id);

    // Búsquedas para dashboard administrativo
    @Query("SELECT u FROM Usuario u ORDER BY u.createdAt DESC LIMIT 10")
    List<Usuario> findUsuariosRecientes();

    @Query("SELECT u FROM Usuario u WHERE u.ultimoLogin IS NOT NULL ORDER BY u.ultimoLogin DESC LIMIT 10")
    List<Usuario> findUltimosLogins();

    // Análisis de retención de usuarios
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.createdAt >= :fechaInicio AND EXISTS (SELECT o FROM Orden o WHERE o.usuario.usuarioId = u.usuarioId)")
    long countUsuariosConCompras(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.createdAt >= :fechaInicio AND u.ultimoLogin >= :fechaLogin")
    long countUsuariosActivosRegistradosDesde(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaLogin") LocalDateTime fechaLogin);

    // Búsquedas para limpieza y mantenimiento
    @Query("SELECT u FROM Usuario u WHERE u.activo = false AND u.updatedAt < :fecha")
    List<Usuario> findUsuariosInactivosAntiguos(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT u FROM Usuario u WHERE u.ultimoLogin IS NULL AND u.createdAt < :fecha AND u.activo = true")
    List<Usuario> findUsuariosSinLoginAntiguos(@Param("fecha") LocalDateTime fecha);

    // Estadísticas avanzadas
    @Query("SELECT AVG(DATEDIFF(CURRENT_DATE, DATE(u.createdAt))) FROM Usuario u WHERE u.activo = true")
    Double findPromedioAntiguedadUsuarios();

    @Query("SELECT u.rol.nombre, AVG(DATEDIFF(CURRENT_DATE, DATE(u.ultimoLogin))) FROM Usuario u WHERE u.ultimoLogin IS NOT NULL GROUP BY u.rol.nombre")
    List<Object[]> findPromedioInactividadPorRol();
}
