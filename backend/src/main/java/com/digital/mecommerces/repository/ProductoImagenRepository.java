package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.ProductoImagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestión de imágenes de productos
 * MÉTODOS ADICIONALES para ProductoImagenRepository
 */
@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Long> {

    // === MÉTODOS BÁSICOS (YA EXISTEN) ===

    List<ProductoImagen> findByProductoProductoId(Long productoId);

    ProductoImagen findByProductoProductoIdAndEsPrincipal(Long productoId, Boolean esPrincipal);

    // === MÉTODOS ADICIONALES NECESARIOS ===

    /**
     * Buscar imágenes activas
     */
    List<ProductoImagen> findByActivaTrue();

    /**
     * Buscar imágenes por tipo
     */
    List<ProductoImagen> findByTipo(String tipo);

    /**
     * Buscar imágenes activas de un producto ordenadas
     */
    @Query("SELECT pi FROM ProductoImagen pi WHERE pi.producto.productoId = :productoId " +
            "AND pi.activa = true ORDER BY pi.esPrincipal DESC, pi.orden ASC")
    List<ProductoImagen> findActivasByProductoIdOrdered(@Param("productoId") Long productoId);

    /**
     * Buscar imágenes principales de productos activos
     */
    @Query("SELECT pi FROM ProductoImagen pi WHERE pi.esPrincipal = true " +
            "AND pi.producto.activo = true AND pi.activa = true")
    List<ProductoImagen> findImagenesPrincipalesActivas();

    /**
     * Contar imágenes por producto
     */
    long countByProductoProductoId(Long productoId);

    /**
     * Contar imágenes activas por producto
     */
    long countByProductoProductoIdAndActivaTrue(Long productoId);

    /**
     * Eliminar todas las imágenes de un producto
     */
    void deleteByProductoProductoId(Long productoId);

    /**
     * Buscar imágenes por múltiples tipos
     */
    List<ProductoImagen> findByTipoIn(List<String> tipos);

    /**
     * Buscar productos sin imagen principal
     */
    @Query("SELECT DISTINCT p.productoId FROM Producto p " +
            "WHERE NOT EXISTS (SELECT pi FROM ProductoImagen pi " +
            "WHERE pi.producto.productoId = p.productoId AND pi.esPrincipal = true)")
    List<Long> findProductosSinImagenPrincipal();

    /**
     * Buscar productos sin imágenes
     */
    @Query("SELECT DISTINCT p.productoId FROM Producto p " +
            "WHERE NOT EXISTS (SELECT pi FROM ProductoImagen pi " +
            "WHERE pi.producto.productoId = p.productoId)")
    List<Long> findProductosSinImagenes();

    /**
     * Obtener estadísticas de imágenes por tipo
     */
    @Query("SELECT pi.tipo, COUNT(pi) FROM ProductoImagen pi " +
            "WHERE pi.activa = true GROUP BY pi.tipo")
    List<Object[]> countImagenesPorTipo();

    /**
     * Obtener imágenes por rango de orden
     */
    List<ProductoImagen> findByProductoProductoIdAndOrdenBetween(Long productoId, Integer ordenMin, Integer ordenMax);

    /**
     * Buscar imágenes duplicadas por URL
     */
    @Query("SELECT pi.url, COUNT(pi) FROM ProductoImagen pi GROUP BY pi.url HAVING COUNT(pi) > 1")
    List<Object[]> findUrlsDuplicadas();

    /**
     * Obtener la imagen con mayor orden de un producto
     */
    @Query("SELECT MAX(pi.orden) FROM ProductoImagen pi WHERE pi.producto.productoId = :productoId")
    Integer findMaxOrdenByProductoId(@Param("productoId") Long productoId);

    /**
     * Verificar si existe imagen con URL específica
     */
    boolean existsByUrl(String url);

    /**
     * Buscar imágenes por tamaño
     */
    List<ProductoImagen> findByTamanioGreaterThan(Integer tamanio);

    /**
     * Obtener estadísticas de uso de imágenes por producto
     */
    @Query("SELECT p.productoNombre, COUNT(pi) as totalImagenes " +
            "FROM Producto p LEFT JOIN ProductoImagen pi ON p.productoId = pi.producto.productoId " +
            "GROUP BY p.productoId, p.productoNombre " +
            "ORDER BY totalImagenes DESC")
    List<Object[]> findEstadisticasImagenesPorProducto();

    /**
     * Buscar imágenes de productos por categoría
     */
    @Query("SELECT pi FROM ProductoImagen pi " +
            "WHERE pi.producto.categoria.categoriaId = :categoriaId " +
            "AND pi.activa = true")
    List<ProductoImagen> findByProductoCategoriaId(@Param("categoriaId") Long categoriaId);

    /**
     * Buscar imágenes de productos por vendedor
     */
    @Query("SELECT pi FROM ProductoImagen pi " +
            "WHERE pi.producto.vendedor.usuarioId = :vendedorId " +
            "AND pi.activa = true")
    List<ProductoImagen> findByProductoVendedorId(@Param("vendedorId") Long vendedorId);

    /**
     * Obtener imágenes principales por categoría
     */
    @Query("SELECT pi FROM ProductoImagen pi " +
            "WHERE pi.esPrincipal = true " +
            "AND pi.producto.categoria.categoriaId = :categoriaId " +
            "AND pi.producto.activo = true " +
            "AND pi.activa = true")
    List<ProductoImagen> findImagenesPrincipalesByCategoria(@Param("categoriaId") Long categoriaId);
}
