package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {

    Optional<Tipo> findByTipoNombre(String tipoNombre);

    List<Tipo> findByTipoCategoria(Tipo.TipoCategoria tipoCategoria);

    List<Tipo> findByStatusTrue();

    Optional<Tipo> findBySlug(String slug);

    boolean existsByTipoNombre(String tipoNombre);

    boolean existsBySlug(String slug);
}
