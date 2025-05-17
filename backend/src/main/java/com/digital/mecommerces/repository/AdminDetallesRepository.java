package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.AdminDetalles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminDetallesRepository extends JpaRepository<AdminDetalles, Long> {
}