package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Métodos adicionales de consulta si son necesarios
    List<Producto> findByProductoNombre(String productoNombre);
}

