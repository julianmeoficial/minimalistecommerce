package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Tipo;
import com.digital.mecommerces.model.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {
    Optional<Tipo> findFirstByTipoNombreAndTipoCategoria(String tipoNombre, TipoCategoria tipoCategoria);
}