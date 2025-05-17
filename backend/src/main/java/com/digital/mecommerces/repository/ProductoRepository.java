package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Producto;
import com.digital.mecommerces.model.CategoriaProducto;
import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByProductoNombre(String productoNombre);
    List<Producto> findByCategoriaCategoriaId(Long categoriaId);
    List<Producto> findByVendedorUsuarioId(Long vendedorId);
    List<Producto> findByCategoriaAndVendedor(CategoriaProducto categoria, Usuario vendedor);
}