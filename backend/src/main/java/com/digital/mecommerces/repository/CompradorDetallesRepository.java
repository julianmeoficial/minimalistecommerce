package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.CompradorDetalles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompradorDetallesRepository extends JpaRepository<CompradorDetalles, Long> {
    Optional<CompradorDetalles> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
}