package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    // Búsqueda básica por código (la más importante)
    Optional<Permiso> findByCodigo(String codigo);

    Optional<Permiso> findByCodigoIgnoreCase(String codigo);

    // Verificar existencia por código
    boolean existsByCodigo(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);

    // Búsqueda por descripción
    List<Permiso> findByDescripcionContainingIgnoreCase(String descripcion);

    // Permisos por nivel
    List<Permiso> findByNivel(Integer nivel);

    List<Permiso> findByNivelBetween(Integer nivelMin, Integer nivelMax);

    @Query("SELECT p FROM Permiso p WHERE p.nivel <= :maxNivel ORDER BY p.nivel ASC")
    List<Permiso> findPermisosPorNivelMaximo(@Param("maxNivel") Integer maxNivel);

    // Permisos de alto nivel (administrativos)
    @Query("SELECT p FROM Permiso p WHERE p.nivel <= 2 AND p.activo = true ORDER BY p.nivel ASC")
    List<Permiso> findPermisosAdministrativos();

    // Permisos básicos (nivel alto)
    @Query("SELECT p FROM Permiso p WHERE p.nivel >= 3 AND p.activo = true ORDER BY p.nivel ASC")
    List<Permiso> findPermisosBasicos();

    // Permisos activos
    List<Permiso> findByActivoTrue();

    List<Permiso> findByActivoFalse();

    @Query("SELECT p FROM Permiso p WHERE p.activo = true ORDER BY p.nivel ASC, p.codigo ASC")
    List<Permiso> findPermisosActivosOrdenados();

    // Búsquedas por categoría
    List<Permiso> findByCategoria(String categoria);

    List<Permiso> findByCategoriaIgnoreCase(String categoria);

    // Categorías específicas del sistema optimizado
    @Query("SELECT p FROM Permiso p WHERE p.categoria = 'ADMINISTRACION' AND p.activo = true ORDER BY p.nivel ASC")
    List<Permiso> findPermisosAdministracion();

    @Query("SELECT p FROM Permiso p WHERE p.categoria = 'VENTAS' AND p.activo = true ORDER BY p.nivel ASC")
    List<Permiso> findPermisosVentas();

    @Query("SELECT p FROM Permiso p WHERE p.categoria = 'COMPRAS' AND p.activo = true ORDER BY p.nivel ASC")
    List<Permiso> findPermisosCompras();

    @Query("SELECT p FROM Permiso p WHERE p.categoria = 'GESTION' AND p.activo = true ORDER BY p.nivel ASC")
    List<Permiso> findPermisosGestion();

    @Query("SELECT p FROM Permiso p WHERE p.categoria = 'GENERAL' AND p.activo = true ORDER BY p.nivel ASC")
    List<Permiso> findPermisosGenerales();

    // Permisos del sistema usando constantes optimizadas
    @Query("SELECT p FROM Permiso p WHERE p.codigo IN ('ADMIN_TOTAL', 'VENDER_PRODUCTOS', 'COMPRAR_PRODUCTOS', 'GESTIONAR_USUARIOS', 'GESTIONAR_CATEGORIAS') AND p.activo = true ORDER BY p.nivel ASC")
    List<Permiso> findPermisosDelSistema();

    // Permisos personalizados (no del sistema)
    @Query("SELECT p FROM Permiso p WHERE p.codigo NOT IN ('ADMIN_TOTAL', 'VENDER_PRODUCTOS', 'COMPRAR_PRODUCTOS', 'GESTIONAR_USUARIOS', 'GESTIONAR_CATEGORIAS') AND p.activo = true ORDER BY p.codigo ASC")
    List<Permiso> findPermisosPersonalizados();

    // Verificar si es permiso del sistema
    @Query("SELECT COUNT(p) > 0 FROM Permiso p WHERE p.codigo = :codigo AND p.codigo IN ('ADMIN_TOTAL', 'VENDER_PRODUCTOS', 'COMPRAR_PRODUCTOS', 'GESTIONAR_USUARIOS', 'GESTIONAR_CATEGORIAS')")
    boolean esPermisoDelSistema(@Param("codigo") String codigo);

    // Jerarquía de permisos
    @Query("SELECT p FROM Permiso p WHERE p.permisoPadre IS NULL AND p.activo = true ORDER BY p.nivel ASC")
    List<Permiso> findPermisosRaiz();

    List<Permiso> findByPermisopadreId(Long permisopadreId);

    List<Permiso> findByPermisoPadre(Permiso permisoPadre);

    @Query("SELECT p FROM Permiso p WHERE p.permisopadreId = :padreId AND p.activo = true ORDER BY p.nivel ASC")
    List<Permiso> findSubpermisosPorPadre(@Param("padreId") Long padreId);

    // Permisos asignados a roles
    @Query("SELECT DISTINCT p FROM Permiso p INNER JOIN RolPermiso rp ON p.permisoId = rp.permisoId WHERE p.activo = true ORDER BY p.nivel ASC")
    List<Permiso> findPermisosAsignados();

    // Permisos no asignados a ningún rol
    @Query("SELECT p FROM Permiso p WHERE p.activo = true AND NOT EXISTS (SELECT rp FROM RolPermiso rp WHERE rp.permisoId = p.permisoId) ORDER BY p.codigo ASC")
    List<Permiso> findPermisosNoAsignados();

    // Permisos asignados a un rol específico
    @Query("SELECT p FROM Permiso p INNER JOIN RolPermiso rp ON p.permisoId = rp.permisoId WHERE rp.rolId = :rolId AND p.activo = true ORDER BY p.nivel ASC")
    List<Permiso> findPermisosPorRol(@Param("rolId") Long rolId);

    // Búsqueda de texto en código o descripción
    @Query("SELECT p FROM Permiso p WHERE p.activo = true AND (LOWER(p.codigo) LIKE LOWER(CONCAT('%', :texto, '%')) OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))) ORDER BY p.nivel ASC")
    List<Permiso> findByTextoEnCodigoODescripcion(@Param("texto") String texto);

    // Estadísticas de permisos
    @Query("SELECT COUNT(p) FROM Permiso p WHERE p.activo = true")
    long countPermisosActivos();

    @Query("SELECT COUNT(p) FROM Permiso p WHERE p.activo = false")
    long countPermisosInactivos();

    @Query("SELECT p.categoria, COUNT(p) FROM Permiso p WHERE p.activo = true GROUP BY p.categoria ORDER BY COUNT(p) DESC")
    List<Object[]> countPermisosPorCategoria();

    @Query("SELECT p.nivel, COUNT(p) FROM Permiso p WHERE p.activo = true GROUP BY p.nivel ORDER BY p.nivel ASC")
    List<Object[]> countPermisosPorNivel();

    // Permisos más asignados
    @Query("SELECT p, COUNT(rp) as asignaciones FROM Permiso p LEFT JOIN RolPermiso rp ON p.permisoId = rp.permisoId WHERE p.activo = true GROUP BY p.permisoId ORDER BY asignaciones DESC")
    List<Object[]> findPermisosMasAsignados();

    // Permisos críticos del sistema
    @Query("SELECT p FROM Permiso p WHERE p.codigo = 'ADMIN_TOTAL' AND p.activo = true")
    Optional<Permiso> findPermisoAdminTotal();

    @Query("SELECT p FROM Permiso p WHERE p.codigo = 'VENDER_PRODUCTOS' AND p.activo = true")
    Optional<Permiso> findPermisoVenderProductos();

    @Query("SELECT p FROM Permiso p WHERE p.codigo = 'COMPRAR_PRODUCTOS' AND p.activo = true")
    Optional<Permiso> findPermisoComprarProductos();

    @Query("SELECT p FROM Permiso p WHERE p.codigo = 'GESTIONAR_USUARIOS' AND p.activo = true")
    Optional<Permiso> findPermisoGestionarUsuarios();

    @Query("SELECT p FROM Permiso p WHERE p.codigo = 'GESTIONAR_CATEGORIAS' AND p.activo = true")
    Optional<Permiso> findPermisoGestionarCategorias();

    // Validación de nombres únicos
    @Query("SELECT COUNT(p) > 0 FROM Permiso p WHERE p.codigo = :codigo AND p.permisoId != :id")
    boolean existsByCodigoAndPermisoIdNot(@Param("codigo") String codigo, @Param("id") Long id);

    // Permisos ordenados por importancia
    @Query("SELECT p FROM Permiso p WHERE p.activo = true ORDER BY CASE WHEN p.codigo = 'ADMIN_TOTAL' THEN 1 WHEN p.codigo = 'GESTIONAR_USUARIOS' THEN 2 WHEN p.codigo = 'GESTIONAR_CATEGORIAS' THEN 3 WHEN p.codigo = 'VENDER_PRODUCTOS' THEN 4 WHEN p.codigo = 'COMPRAR_PRODUCTOS' THEN 5 ELSE 999 END, p.codigo ASC")
    List<Permiso> findPermisosOrdenadosPorImportancia();

    // Permisos recomendados para cada tipo de rol
    @Query("SELECT p FROM Permiso p WHERE p.activo = true AND p.codigo IN ('ADMIN_TOTAL', 'GESTIONAR_USUARIOS', 'GESTIONAR_CATEGORIAS', 'VENDER_PRODUCTOS', 'COMPRAR_PRODUCTOS') ORDER BY p.nivel ASC")
    List<Permiso> findPermisosRecomendadosAdministrador();

    @Query("SELECT p FROM Permiso p WHERE p.activo = true AND p.codigo IN ('VENDER_PRODUCTOS', 'GESTIONAR_CATEGORIAS') ORDER BY p.nivel ASC")
    List<Permiso> findPermisosRecomendadosVendedor();

    @Query("SELECT p FROM Permiso p WHERE p.activo = true AND p.codigo = 'COMPRAR_PRODUCTOS'")
    List<Permiso> findPermisosRecomendadosComprador();

    // Permisos por compatibilidad de nivel
    @Query("SELECT p FROM Permiso p WHERE p.activo = true AND p.nivel >= :nivelUsuario ORDER BY p.nivel ASC")
    List<Permiso> findPermisosCompatiblesConNivel(@Param("nivelUsuario") Integer nivelUsuario);

    // Buscar permisos que implican otros permisos
    @Query("SELECT p FROM Permiso p WHERE p.activo = true AND p.nivel <= :nivelPermiso ORDER BY p.nivel ASC")
    List<Permiso> findPermisosImplicadosPor(@Param("nivelPermiso") Integer nivelPermiso);

    // Análisis de dependencias
    @Query("SELECT p FROM Permiso p WHERE p.permisoPadre IS NOT NULL AND p.activo = true ORDER BY p.permisoPadre.codigo, p.codigo")
    List<Permiso> findPermisosConDependencias();

    // Permisos huérfanos (sin padre cuando deberían tenerlo)
    @Query("SELECT p FROM Permiso p WHERE p.permisoPadre IS NULL AND p.nivel > 1 AND p.activo = true")
    List<Permiso> findPermisosHuerfanos();
}
