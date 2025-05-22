package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.VendedorDetalles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendedorDetallesRepository extends JpaRepository<VendedorDetalles, Long> {
    Optional<VendedorDetalles> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
}