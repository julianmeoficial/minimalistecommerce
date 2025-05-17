package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.RolPermiso;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {
    List<RolPermiso> findByRolRolId(Long rolId);
    List<RolPermiso> findByPermisoPermisoId(Long permisoId);
    void deleteByRolRolId(Long rolId);
}
