package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Producto;
import com.digital.mecommerces.model.CategoriaProducto;
import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar por nombre del producto
    Optional<Producto> findByProductoNombre(String productoNombre);

    // Usar el nombre correcto de la propiedad
    @Query("SELECT p FROM Producto p WHERE p.categoria.categoriaId = :categoriaId")
    List<Producto> findByCategoriaId(@Param("categoriaId") Long categoriaId);

    // Buscar por vendedor
    @Query("SELECT p FROM Producto p WHERE p.vendedor.usuarioId = :vendedorId")
    List<Producto> findByVendedorUsuarioId(@Param("vendedorId") Long vendedorId);

    // Buscar por categoría y vendedor
    List<Producto> findByCategoriaAndVendedor(CategoriaProducto categoria, Usuario vendedor);

    // Buscar productos activos
    List<Producto> findByActivoTrue();

    // Buscar productos por categoría y activos
    @Query("SELECT p FROM Producto p WHERE p.categoria.categoriaId = :categoriaId AND p.activo = true")
    List<Producto> findByCategoriaIdAndActivoTrue(@Param("categoriaId") Long categoriaId);

    // Buscar productos destacados
    List<Producto> findByDestacadoTrueAndActivoTrue();

    // Buscar por slug
    Optional<Producto> findBySlug(String slug);

    // Buscar productos por vendedor (método alternativo)
    @Query("SELECT p FROM Producto p WHERE p.vendedor.usuarioId = :vendedorId")
    List<Producto> findByVendedorId(@Param("vendedorId") Long vendedorId);
}
