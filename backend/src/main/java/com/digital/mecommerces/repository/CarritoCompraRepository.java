package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.CarritoCompra;
import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoCompraRepository extends JpaRepository<CarritoCompra, Long> {
    List<CarritoCompra> findByUsuarioUsuarioId(Long usuarioId);
    Optional<CarritoCompra> findByUsuarioAndActivo(Usuario usuario, Boolean activo);
}