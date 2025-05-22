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

    // Busca una categoría por su nombre
    Optional<CategoriaProducto> findByNombre(String nombre);

    // Encuentra todas las categorías principales (sin padre)
    @Query("SELECT c FROM CategoriaProducto c WHERE c.categoriaPadre IS NULL")
    List<CategoriaProducto> findCategoriasPrincipales();

    // Encuentra todas las subcategorías de una categoría padre
    List<CategoriaProducto> findByCategoriapadreId(Long categoriapadreId);

    // Buscar categorías activas
    List<CategoriaProducto> findByActivoTrue();

    // Buscar categorías principales activas
    @Query("SELECT c FROM CategoriaProducto c WHERE c.categoriaPadre IS NULL AND c.activo = true")
    List<CategoriaProducto> findCategoriasPrincipalesActivas();

    // Verificar si existe por nombre
    boolean existsByNombre(String nombre);

    // Buscar por slug
    Optional<CategoriaProducto> findBySlug(String slug);
}
