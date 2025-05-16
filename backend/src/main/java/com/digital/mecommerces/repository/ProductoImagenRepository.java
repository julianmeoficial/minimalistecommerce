package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.ProductoImagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Long> {
    List<ProductoImagen> findByProductoProductoId(Long productoId);
    ProductoImagen findByProductoProductoIdAndEsPrincipal(Long productoId, Boolean esPrincipal);
}