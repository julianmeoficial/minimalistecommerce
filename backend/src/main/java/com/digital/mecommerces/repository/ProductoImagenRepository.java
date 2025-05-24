package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Producto;
import com.digital.mecommerces.model.ProductoImagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Long> {

    // Buscar imágenes por ID de producto
    List<ProductoImagen> findByProductoProductoId(Long productoId);

    // Buscar imagen principal por ID de producto
    Optional<ProductoImagen> findByProductoProductoIdAndEsPrincipalTrue(Long productoId);

    // MÉTODO FALTANTE: Buscar imágenes por producto y si es principal
    List<ProductoImagen> findByProductoAndEsPrincipalTrue(Producto producto);

    // Buscar todas las imágenes de un producto
    List<ProductoImagen> findByProducto(Producto producto);

    // Buscar imagen principal de un producto
    Optional<ProductoImagen> findByProductoAndEsPrincipal(Producto producto, Boolean esPrincipal);

    // Verificar si un producto tiene imágenes
    boolean existsByProducto(Producto producto);

    // Contar imágenes por producto
    long countByProducto(Producto producto);

    // Buscar imágenes por tipo
    List<ProductoImagen> findByTipo(String tipo);

    // Buscar imágenes por producto y tipo
    List<ProductoImagen> findByProductoAndTipo(Producto producto, String tipo);

    // Eliminar todas las imágenes de un producto
    void deleteByProducto(Producto producto);

    // Buscar imágenes ordenadas por principal primero
    @Query("SELECT pi FROM ProductoImagen pi WHERE pi.producto = :producto ORDER BY pi.esPrincipal DESC, pi.createdat ASC")
    List<ProductoImagen> findByProductoOrderByEsPrincipalDesc(@Param("producto") Producto producto);

    // Buscar URLs de imágenes por producto
    @Query("SELECT pi.url FROM ProductoImagen pi WHERE pi.producto.productoId = :productoId")
    List<String> findUrlsByProductoId(@Param("productoId") Long productoId);

    // Buscar imagen principal URL por producto
    @Query("SELECT pi.url FROM ProductoImagen pi WHERE pi.producto = :producto AND pi.esPrincipal = true")
    Optional<String> findPrincipalUrlByProducto(@Param("producto") Producto producto);
}
