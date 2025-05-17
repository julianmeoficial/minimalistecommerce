package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.CompradorDetalles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompradorDetallesRepository extends JpaRepository<CompradorDetalles, Long> {
}
