package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.model.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Boolean existsByEmail(String email);

    // Contar usuarios por rol
    Long countByRolRolId(Long rolId);

    // Encontrar usuarios por rol
    List<Usuario> findByRolRolId(Long rolId);
}
