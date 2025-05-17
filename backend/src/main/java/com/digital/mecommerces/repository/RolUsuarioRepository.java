package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Long> {
    Optional<RolUsuario> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}