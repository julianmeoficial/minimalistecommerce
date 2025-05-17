package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.VendedorDetalles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendedorDetallesRepository extends JpaRepository<VendedorDetalles, Long> {
}