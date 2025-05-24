package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Producto;
import com.digital.mecommerces.model.ProductoSugerido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoSugeridoRepository extends JpaRepository<ProductoSugerido, Long> {

    // Buscar sugerencias por producto base
    List<ProductoSugerido> findByProductoBase(Producto productoBase);

    // Buscar sugerencias activas por producto base
    List<ProductoSugerido> findByProductoBaseAndActivoTrue(Producto productoBase);

    // Buscar sugerencias por producto base y tipo de relación
    List<ProductoSugerido> findByProductoBaseAndTipoRelacion(Producto productoBase, String tipoRelacion);

    // Buscar sugerencias por producto base, activas y ordenadas por prioridad
    List<ProductoSugerido> findByProductoBaseAndActivoTrueOrderByPrioridadDesc(Producto productoBase);

    // Buscar sugerencias por producto sugerido
    List<ProductoSugerido> findByProductoSugerido(Producto productoSugerido);

    // Verificar si existe una sugerencia específica
    boolean existsByProductoBaseAndProductoSugerido(Producto productoBase, Producto productoSugerido);

    // Buscar sugerencia específica
    Optional<ProductoSugerido> findByProductoBaseAndProductoSugerido(Producto productoBase, Producto productoSugerido);

    // Buscar por tipo de relación
    List<ProductoSugerido> findByTipoRelacionAndActivoTrue(String tipoRelacion);

    // Obtener top sugerencias por prioridad
    @Query("SELECT ps FROM ProductoSugerido ps WHERE ps.productoBase = :producto AND ps.activo = true ORDER BY ps.prioridad DESC")
    List<ProductoSugerido> findTopSugerenciasByProducto(@Param("producto") Producto producto);

    // Buscar sugerencias bidireccionales (A sugiere B y B sugiere A)
    @Query("SELECT ps FROM ProductoSugerido ps WHERE " +
            "(ps.productoBase = :producto1 AND ps.productoSugerido = :producto2) OR " +
            "(ps.productoBase = :producto2 AND ps.productoSugerido = :producto1)")
    List<ProductoSugerido> findSugerenciasBidireccionales(@Param("producto1") Producto producto1, @Param("producto2") Producto producto2);

    // Obtener productos más sugeridos
    @Query("SELECT ps.productoSugerido, COUNT(ps) as cantidad FROM ProductoSugerido ps WHERE ps.activo = true GROUP BY ps.productoSugerido ORDER BY cantidad DESC")
    List<Object[]> findProductosMasSugeridos();

    // Buscar sugerencias por categoría del producto base
    @Query("SELECT ps FROM ProductoSugerido ps WHERE ps.productoBase.categoria.categoriaId = :categoriaId AND ps.activo = true")
    List<ProductoSugerido> findByProductoBaseCategoria(@Param("categoriaId") Long categoriaId);

    // Eliminar sugerencias por producto base
    void deleteByProductoBase(Producto productoBase);

    // Eliminar sugerencias por producto sugerido
    void deleteByProductoSugerido(Producto productoSugerido);
}
