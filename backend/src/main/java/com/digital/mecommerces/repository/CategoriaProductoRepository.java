package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Long> {

    // Búsqueda básica por nombre (optimizada para enums)
    Optional<CategoriaProducto> findByNombre(String nombre);

    Optional<CategoriaProducto> findByNombreIgnoreCase(String nombre);

    // Verificar existencia por nombre
    boolean existsByNombre(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    // Búsqueda por slug (para SEO optimizado)
    Optional<CategoriaProducto> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // Categorías principales (sin padre) - optimizado
    @Query("SELECT c FROM CategoriaProducto c WHERE c.categoriaPadre IS NULL")
    List<CategoriaProducto> findCategoriasPrincipales();

    // Categorías principales activas - más usado
    @Query("SELECT c FROM CategoriaProducto c WHERE c.categoriaPadre IS NULL AND c.activo = true")
    List<CategoriaProducto> findCategoriasPrincipalesActivas();

    // Subcategorías de una categoría padre
    List<CategoriaProducto> findByCategoriapadreId(Long categoriapadreId);

    List<CategoriaProducto> findByCategoriaPadre(CategoriaProducto categoriaPadre);

    // Subcategorías activas de una categoría padre
    @Query("SELECT c FROM CategoriaProducto c WHERE c.categoriapadreId = :categoriapadreId AND c.activo = true")
    List<CategoriaProducto> findSubcategoriasActivasPorPadre(@Param("categoriapadreId") Long categoriapadreId);

    // Todas las categorías activas
    List<CategoriaProducto> findByActivoTrue();

    List<CategoriaProducto> findByActivoFalse();

    // Búsqueda por descripción (para filtros avanzados)
    List<CategoriaProducto> findByDescripcionContainingIgnoreCase(String descripcion);

    // Categorías que contienen texto en nombre o descripción
    @Query("SELECT c FROM CategoriaProducto c WHERE c.activo = true AND (LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')))")
    List<CategoriaProducto> findByTextoEnNombreODescripcion(@Param("texto") String texto);

    // Categorías del sistema usando constantes optimizadas
    @Query("SELECT c FROM CategoriaProducto c WHERE c.nombre IN ('TECNOLOGIA', 'HOGAR', 'MODA', 'MASCOTAS', 'ARTEMANUALIDADES')")
    List<CategoriaProducto> findCategoriasDelSistema();

    // Categorías personalizadas (no del sistema)
    @Query("SELECT c FROM CategoriaProducto c WHERE c.nombre NOT IN ('TECNOLOGIA', 'HOGAR', 'MODA', 'MASCOTAS', 'ARTEMANUALIDADES')")
    List<CategoriaProducto> findCategoriasPersonalizadas();

    // Verificar si es categoría del sistema
    @Query("SELECT COUNT(c) > 0 FROM CategoriaProducto c WHERE c.nombre = :nombre AND c.nombre IN ('TECNOLOGIA', 'HOGAR', 'MODA', 'MASCOTAS', 'ARTEMANUALIDADES')")
    boolean esCategoriaDelSistema(@Param("nombre") String nombre);

    // Categorías con productos asociados
    @Query("SELECT DISTINCT c FROM CategoriaProducto c INNER JOIN Producto p ON p.categoria.categoriaId = c.categoriaId WHERE c.activo = true")
    List<CategoriaProducto> findCategoriasConProductos();

    // Categorías sin productos
    @Query("SELECT c FROM CategoriaProducto c WHERE c.activo = true AND NOT EXISTS (SELECT p FROM Producto p WHERE p.categoria.categoriaId = c.categoriaId)")
    List<CategoriaProducto> findCategoriasSinProductos();

    // Contar productos por categoría
    @Query("SELECT c, COUNT(p) FROM CategoriaProducto c LEFT JOIN Producto p ON p.categoria.categoriaId = c.categoriaId WHERE c.activo = true GROUP BY c.categoriaId")
    List<Object[]> countProductosPorCategoria();

    // Categorías con más de X productos
    @Query("SELECT c FROM CategoriaProducto c WHERE c.activo = true AND (SELECT COUNT(p) FROM Producto p WHERE p.categoria.categoriaId = c.categoriaId) >= :minProductos")
    List<CategoriaProducto> findCategoriasConMinimoProductos(@Param("minProductos") Long minProductos);

    // Jerarquía completa: categorías principales con sus subcategorías
    @Query("SELECT c FROM CategoriaProducto c WHERE c.activo = true ORDER BY c.categoriaPadre, c.nombre")
    List<CategoriaProducto> findJerarquiaCompleta();

    // Nivel específico de jerarquía
    @Query("SELECT c FROM CategoriaProducto c WHERE c.categoriaPadre IS NULL AND c.activo = true ORDER BY c.nombre")
    List<CategoriaProducto> findNivelRaiz();

    @Query("SELECT c FROM CategoriaProducto c WHERE c.categoriaPadre IS NOT NULL AND c.activo = true ORDER BY c.categoriaPadre.nombre, c.nombre")
    List<CategoriaProducto> findNivelSubcategorias();

    // Búsquedas para navegación optimizada
    @Query("SELECT c FROM CategoriaProducto c WHERE c.activo = true AND c.categoriaPadre IS NULL ORDER BY c.nombre")
    List<CategoriaProducto> findMenuPrincipal();

    // Categorías populares (con más productos activos)
    @Query("SELECT c FROM CategoriaProducto c WHERE c.activo = true AND (SELECT COUNT(p) FROM Producto p WHERE p.categoria.categoriaId = c.categoriaId AND p.activo = true) > 0 ORDER BY (SELECT COUNT(p) FROM Producto p WHERE p.categoria.categoriaId = c.categoriaId AND p.activo = true) DESC")
    List<CategoriaProducto> findCategoriasPopulares();

    // Estadísticas de categorías
    @Query("SELECT COUNT(c) FROM CategoriaProducto c WHERE c.activo = true")
    long countCategoriasActivas();

    @Query("SELECT COUNT(c) FROM CategoriaProducto c WHERE c.categoriaPadre IS NULL AND c.activo = true")
    long countCategoriasPrincipales();

    @Query("SELECT COUNT(c) FROM CategoriaProducto c WHERE c.categoriaPadre IS NOT NULL AND c.activo = true")
    long countSubcategorias();

    // Búsqueda por imagen (para categorías con imagen)
    List<CategoriaProducto> findByImagenIsNotNull();

    List<CategoriaProducto> findByImagenIsNull();

    // Categorías recién creadas o modificadas
    @Query("SELECT c FROM CategoriaProducto c WHERE c.activo = true ORDER BY c.categoriaId DESC")
    List<CategoriaProducto> findCategoriasRecientes();

    // Verificar nombres únicos excluyendo ID específico (para edición)
    @Query("SELECT COUNT(c) > 0 FROM CategoriaProducto c WHERE c.nombre = :nombre AND c.categoriaId != :id")
    boolean existsByNombreAndCategoriaIdNot(@Param("nombre") String nombre, @Param("id") Long id);

    // Verificar slugs únicos excluyendo ID específico
    @Query("SELECT COUNT(c) > 0 FROM CategoriaProducto c WHERE c.slug = :slug AND c.categoriaId != :id")
    boolean existsBySlugAndCategoriaIdNot(@Param("slug") String slug, @Param("id") Long id);

    // Obtener ruta completa de una categoría (breadcrumb)
    @Query("WITH RECURSIVE categoria_path AS (" +
            "SELECT c.categoriaId, c.nombre, c.slug, c.categoriapadreId, 0 as nivel " +
            "FROM CategoriaProducto c WHERE c.categoriaId = :categoriaId " +
            "UNION ALL " +
            "SELECT p.categoriaId, p.nombre, p.slug, p.categoriapadreId, cp.nivel + 1 " +
            "FROM CategoriaProducto p INNER JOIN categoria_path cp ON p.categoriaId = cp.categoriapadreId) " +
            "SELECT * FROM categoria_path ORDER BY nivel DESC")
    List<Object[]> findRutaCompleta(@Param("categoriaId") Long categoriaId);
}
