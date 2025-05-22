package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.RolPermiso;
import com.digital.mecommerces.model.RolPermisoId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, RolPermisoId> {

    // Buscar por rol_id
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rolId = :rolId")
    List<RolPermiso> findByRolId(@Param("rolId") Long rolId);

    // Buscar por permiso_id
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.permisoId = :permisoId")
    List<RolPermiso> findByPermisoId(@Param("permisoId") Long permisoId);

    // Eliminar por rol_id
    @Modifying
    @Transactional
    @Query("DELETE FROM RolPermiso rp WHERE rp.rolId = :rolId")
    void deleteByRolId(@Param("rolId") Long rolId);

    // Eliminar por rol_id y permiso_id específicos
    @Modifying
    @Transactional
    @Query("DELETE FROM RolPermiso rp WHERE rp.rolId = :rolId AND rp.permisoId = :permisoId")
    void deleteByRolIdAndPermisoId(@Param("rolId") Long rolId, @Param("permisoId") Long permisoId);

    // Verificar si existe una relación específica
    @Query("SELECT COUNT(rp) > 0 FROM RolPermiso rp WHERE rp.rolId = :rolId AND rp.permisoId = :permisoId")
    boolean existsByRolIdAndPermisoId(@Param("rolId") Long rolId, @Param("permisoId") Long permisoId);

    // Contar permisos por rol
    @Query("SELECT COUNT(rp) FROM RolPermiso rp WHERE rp.rolId = :rolId")
    long countByRolId(@Param("rolId") Long rolId);
}
