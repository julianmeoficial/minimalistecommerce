package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Producto;
import com.digital.mecommerces.model.CategoriaProducto;
import com.digital.mecommerces.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Búsqueda básica por nombre
    Optional<Producto> findByProductoNombre(String productoNombre);

    List<Producto> findByProductoNombreContainingIgnoreCase(String nombre);

    // Verificar existencia por nombre
    boolean existsByProductoNombre(String productoNombre);

    // Búsqueda por slug (SEO optimizado)
    Optional<Producto> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // Productos activos
    List<Producto> findByActivoTrue();

    Page<Producto> findByActivoTrue(Pageable pageable);

    List<Producto> findByActivoFalse();

    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findProductosActivosRecientes();

    // Productos destacados
    List<Producto> findByDestacadoTrueAndActivoTrue();

    Page<Producto> findByDestacadoTrueAndActivoTrue(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.destacado = true AND p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findProductosDestacadosRecientes();

    // Búsquedas por categoría
    List<Producto> findByCategoriaAndActivoTrue(CategoriaProducto categoria);

    Page<Producto> findByCategoriaAndActivoTrue(CategoriaProducto categoria, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.categoria.categoriaId = :categoriaId AND p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findByCategoriaIdAndActivoTrue(@Param("categoriaId") Long categoriaId);

    @Query("SELECT p FROM Producto p WHERE p.categoria.categoriaId = :categoriaId AND p.activo = true")
    Page<Producto> findByCategoriaIdAndActivoTrue(@Param("categoriaId") Long categoriaId, Pageable pageable);

    // Búsquedas por categoría usando nombres optimizados para enums
    @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = :categoriaNombre AND p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findByCategoriaDelSistema(@Param("categoriaNombre") String categoriaNombre);

    @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = :categoriaNombre AND p.activo = true")
    Page<Producto> findByCategoriaDelSistema(@Param("categoriaNombre") String categoriaNombre, Pageable pageable);

    // Categorías específicas del sistema optimizado
    @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = 'TECNOLOGIA' AND p.activo = true ORDER BY p.precio ASC")
    List<Producto> findProductosTecnologia();

    @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = 'HOGAR' AND p.activo = true ORDER BY p.precio ASC")
    List<Producto> findProductosHogar();

    @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = 'MODA' AND p.activo = true ORDER BY p.precio ASC")
    List<Producto> findProductosModa();

    @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = 'MASCOTAS' AND p.activo = true ORDER BY p.precio ASC")
    List<Producto> findProductosMascotas();

    @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = 'ARTEMANUALIDADES' AND p.activo = true ORDER BY p.precio ASC")
    List<Producto> findProductosArteManualidades();

    // Búsquedas por vendedor
    List<Producto> findByVendedorAndActivoTrue(Usuario vendedor);

    Page<Producto> findByVendedorAndActivoTrue(Usuario vendedor, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.vendedor.usuarioId = :vendedorId AND p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findByVendedorUsuarioIdAndActivoTrue(@Param("vendedorId") Long vendedorId);

    @Query("SELECT p FROM Producto p WHERE p.vendedor.usuarioId = :vendedorId AND p.activo = true")
    Page<Producto> findByVendedorUsuarioIdAndActivoTrue(@Param("vendedorId") Long vendedorId, Pageable pageable);

    // Búsquedas por vendedor y categoría combinadas
    @Query("SELECT p FROM Producto p WHERE p.vendedor.usuarioId = :vendedorId AND p.categoria.categoriaId = :categoriaId AND p.activo = true")
    List<Producto> findByVendedorYCategoria(@Param("vendedorId") Long vendedorId, @Param("categoriaId") Long categoriaId);

    // Búsquedas por precio
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :minPrecio AND :maxPrecio AND p.activo = true ORDER BY p.precio ASC")
    List<Producto> findByPrecioBetween(@Param("minPrecio") Double minPrecio, @Param("maxPrecio") Double maxPrecio);

    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :minPrecio AND :maxPrecio AND p.activo = true")
    Page<Producto> findByPrecioBetween(@Param("minPrecio") Double minPrecio, @Param("maxPrecio") Double maxPrecio, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.precio <= :maxPrecio AND p.activo = true ORDER BY p.precio ASC")
    List<Producto> findByPrecioMenorIgual(@Param("maxPrecio") Double maxPrecio);

    @Query("SELECT p FROM Producto p WHERE p.precio >= :minPrecio AND p.activo = true ORDER BY p.precio ASC")
    List<Producto> findByPrecioMayorIgual(@Param("minPrecio") Double minPrecio);

    // Rangos de precio específicos
    @Query("SELECT p FROM Producto p WHERE p.precio < 50.0 AND p.activo = true ORDER BY p.precio ASC")
    List<Producto> findProductosEconomicos();

    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN 50.0 AND 200.0 AND p.activo = true ORDER BY p.precio ASC")
    List<Producto> findProductosRangoMedio();

    @Query("SELECT p FROM Producto p WHERE p.precio > 200.0 AND p.activo = true ORDER BY p.precio DESC")
    List<Producto> findProductosPremium();

    // Búsquedas por stock
    @Query("SELECT p FROM Producto p WHERE p.stock > 0 AND p.activo = true ORDER BY p.stock DESC")
    List<Producto> findProductosConStock();

    @Query("SELECT p FROM Producto p WHERE p.stock = 0 AND p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findProductosAgotados();

    @Query("SELECT p FROM Producto p WHERE p.stock <= :limite AND p.stock > 0 AND p.activo = true ORDER BY p.stock ASC")
    List<Producto> findProductosConPocoStock(@Param("limite") Integer limite);

    @Query("SELECT p FROM Producto p WHERE p.stock >= :minStock AND p.activo = true ORDER BY p.stock DESC")
    List<Producto> findProductosConStockMinimo(@Param("minStock") Integer minStock);

    // Búsquedas por fecha de creación
    List<Producto> findByCreatedatBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT p FROM Producto p WHERE p.createdat >= :fecha AND p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findProductosCreadosDesde(@Param("fecha") LocalDateTime fecha);

    // Productos recientes (últimos 30 días)
    @Query("SELECT p FROM Producto p WHERE p.createdat >= :fecha AND p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findProductosRecientes(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT p FROM Producto p WHERE p.createdat >= :fecha AND p.activo = true")
    Page<Producto> findProductosRecientes(@Param("fecha") LocalDateTime fecha, Pageable pageable);

    // Búsqueda de texto completo
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND (LOWER(p.productoNombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :termino, '%'))) ORDER BY p.createdat DESC")
    List<Producto> findByTextoEnNombreODescripcion(@Param("termino") String termino);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND (LOWER(p.productoNombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :termino, '%')))")
    Page<Producto> findByTextoEnNombreODescripcion(@Param("termino") String termino, Pageable pageable);

    // Búsqueda avanzada con múltiples filtros
    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
            "AND (:categoria IS NULL OR p.categoria.categoriaId = :categoria) " +
            "AND (:vendedor IS NULL OR p.vendedor.usuarioId = :vendedor) " +
            "AND (:minPrecio IS NULL OR p.precio >= :minPrecio) " +
            "AND (:maxPrecio IS NULL OR p.precio <= :maxPrecio) " +
            "AND (:soloConStock IS FALSE OR p.stock > 0)")
    Page<Producto> findConFiltrosAvanzados(@Param("categoria") Long categoria,
                                           @Param("vendedor") Long vendedor,
                                           @Param("minPrecio") Double minPrecio,
                                           @Param("maxPrecio") Double maxPrecio,
                                           @Param("soloConStock") Boolean soloConStock,
                                           Pageable pageable);

    // Estadísticas de productos
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true")
    long countProductosActivos();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = false")
    long countProductosInactivos();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.destacado = true AND p.activo = true")
    long countProductosDestacados();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock = 0 AND p.activo = true")
    long countProductosAgotados();

    @Query("SELECT AVG(p.precio) FROM Producto p WHERE p.activo = true")
    Double findPrecioPromedio();

    @Query("SELECT SUM(p.stock) FROM Producto p WHERE p.activo = true")
    Long sumStockTotal();

    // Productos más caros y más baratos
    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.precio DESC LIMIT 10")
    List<Producto> findProductosMasCaros();

    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.precio ASC LIMIT 10")
    List<Producto> findProductosMasBaratos();

    // Estadísticas por categoría
    @Query("SELECT p.categoria.nombre, COUNT(p) FROM Producto p WHERE p.activo = true GROUP BY p.categoria.categoriaId ORDER BY COUNT(p) DESC")
    List<Object[]> countProductosPorCategoria();

    @Query("SELECT p.categoria.nombre, AVG(p.precio) FROM Producto p WHERE p.activo = true GROUP BY p.categoria.categoriaId")
    List<Object[]> findPrecioPromedioPorCategoria();

    // Estadísticas por vendedor
    @Query("SELECT p.vendedor.usuarioNombre, COUNT(p) FROM Producto p WHERE p.activo = true GROUP BY p.vendedor.usuarioId ORDER BY COUNT(p) DESC")
    List<Object[]> countProductosPorVendedor();

    // Productos relacionados (misma categoría, diferente producto)
    @Query("SELECT p FROM Producto p WHERE p.categoria.categoriaId = :categoriaId AND p.productoId != :productoId AND p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findProductosRelacionados(@Param("categoriaId") Long categoriaId, @Param("productoId") Long productoId, Pageable pageable);

    // Productos del mismo vendedor (excluyendo el actual)
    @Query("SELECT p FROM Producto p WHERE p.vendedor.usuarioId = :vendedorId AND p.productoId != :productoId AND p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findOtrosProductosDelVendedor(@Param("vendedorId") Long vendedorId, @Param("productoId") Long productoId, Pageable pageable);

    // Verificar nombres únicos (excluyendo ID específico para edición)
    @Query("SELECT COUNT(p) > 0 FROM Producto p WHERE p.productoNombre = :nombre AND p.productoId != :id")
    boolean existsByProductoNombreAndProductoIdNot(@Param("nombre") String nombre, @Param("id") Long id);

    // Verificar slugs únicos (excluyendo ID específico)
    @Query("SELECT COUNT(p) > 0 FROM Producto p WHERE p.slug = :slug AND p.productoId != :id")
    boolean existsBySlugAndProductoIdNot(@Param("slug") String slug, @Param("id") Long id);

    // Productos que necesitan reabastecimiento
    @Query("SELECT p FROM Producto p WHERE p.stock <= 5 AND p.activo = true ORDER BY p.stock ASC, p.createdat DESC")
    List<Producto> findProductosQueNecesitanReabastecimiento();

    // Productos populares (se podría expandir con métricas de venta)
    @Query("SELECT p FROM Producto p WHERE p.destacado = true AND p.stock > 0 AND p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findProductosPopulares();

    // Productos para promociones
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN 20.0 AND 100.0 AND p.stock > 10 AND p.activo = true ORDER BY p.precio ASC")
    List<Producto> findProductosParaPromociones();

    // Análisis de inventario
    @Query("SELECT p.categoria.nombre, SUM(p.stock), AVG(p.precio) FROM Producto p WHERE p.activo = true GROUP BY p.categoria.categoriaId")
    List<Object[]> findAnalisisInventarioPorCategoria();

    // Productos recién actualizados
    @Query("SELECT p FROM Producto p WHERE p.updatedat >= :fecha AND p.activo = true ORDER BY p.updatedat DESC")
    List<Producto> findProductosActualizadosDesde(@Param("fecha") LocalDateTime fecha);

    // Top productos por cada categoría del sistema
    @Query("SELECT p FROM Producto p WHERE p.categoria.nombre = :categoriaNombre AND p.activo = true ORDER BY p.destacado DESC, p.createdat DESC")
    List<Producto> findTopProductosPorCategoriaDelSistema(@Param("categoriaNombre") String categoriaNombre, Pageable pageable);
}
