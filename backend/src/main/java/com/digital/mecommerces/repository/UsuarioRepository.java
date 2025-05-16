package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Boolean existsByEmail(String email);

    // Contar usuarios por tipo
    Long countByTipoTipoId(Long tipoId);

    // Encontrar usuarios por tipo
    List<Usuario> findByTipoTipoId(Long tipoId);
}
