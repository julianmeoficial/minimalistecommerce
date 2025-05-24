package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar por nombre del producto
    List<Producto> findByProductoNombre(String productoNombre);

    // Buscar productos activos
    List<Producto> findByActivoTrue();

    // Buscar productos destacados y activos
    List<Producto> findByDestacadoTrueAndActivoTrue();

    // Buscar por categoría
    List<Producto> findByCategoriaId(Long categoriaId);

    // Buscar por vendedor
    List<Producto> findByVendedorId(Long vendedorId);

    // Buscar por slug
    Optional<Producto> findBySlug(String slug);

    // Buscar productos con stock bajo
    List<Producto> findByStockLessThanAndActivoTrue(Integer stock);

    // Buscar por rango de precios
    List<Producto> findByPrecioBetweenAndActivoTrue(Double precioMin, Double precioMax);

    // Buscar por nombre conteniendo texto (ignorando mayúsculas)
    List<Producto> findByProductoNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    // Verificar si existe por slug
    boolean existsBySlug(String slug);

    // Contar productos activos
    long countByActivoTrue();

    // Contar por categoría
    long countByCategoriaId(Long categoriaId);

    // Contar por vendedor
    long countByVendedorId(Long vendedorId);

    // Buscar productos más vendidos
    @Query("SELECT p FROM Producto p JOIN OrdenDetalle od ON p.productoId = od.producto.productoId " +
            "WHERE p.activo = true GROUP BY p.productoId ORDER BY SUM(od.cantidad) DESC")
    List<Producto> findProductosMasVendidos();

    // Buscar productos similares por categoría
    @Query("SELECT p FROM Producto p WHERE p.categoria.categoriaId = :categoriaId " +
            "AND p.productoId != :productoId AND p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findProductosSimilares(@Param("categoriaId") Long categoriaId, @Param("productoId") Long productoId);

    // Buscar productos recientes
    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.createdat DESC")
    List<Producto> findProductosRecientes();
}
