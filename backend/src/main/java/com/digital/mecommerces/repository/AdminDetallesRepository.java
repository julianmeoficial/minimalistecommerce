package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.AdminDetalles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminDetallesRepository extends JpaRepository<AdminDetalles, Long> {
    Optional<AdminDetalles> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
}