package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.OrdenDetalle;
import com.digital.mecommerces.model.Orden;
import com.digital.mecommerces.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrdenDetalleRepository extends JpaRepository<OrdenDetalle, Long> {
    List<OrdenDetalle> findByOrdenOrdenId(Long ordenId);
    List<OrdenDetalle> findByProducto(Producto producto);
    List<OrdenDetalle> findByProductoProductoId(Long productoId);
    List<OrdenDetalle> findByOrden(Orden orden);
    void deleteByOrdenOrdenId(Long ordenId);
}
