package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.CarritoCompra;
import com.digital.mecommerces.model.CarritoItem;
import com.digital.mecommerces.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByCarritoCompraCarritoId(Long carritoId);
    Optional<CarritoItem> findByCarritoCompraAndProducto(CarritoCompra carritoCompra, Producto producto);
    void deleteByCarritoCompraCarritoId(Long carritoId);
}