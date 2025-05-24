package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.RolPermiso;
import com.digital.mecommerces.model.RolPermisoId;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.model.Permiso;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, RolPermisoId> {

    // Buscar por rolId usando el campo directo
    List<RolPermiso> findByRolId(Long rolId);

    // Buscar por permisoId usando el campo directo
    List<RolPermiso> findByPermisoId(Long permisoId);

    // Buscar por rol usando la relación
    List<RolPermiso> findByRol(RolUsuario rol);

    // Buscar por permiso usando la relación
    List<RolPermiso> findByPermiso(Permiso permiso);

    // Verificar si existe una relación específica por IDs
    boolean existsByRolIdAndPermisoId(Long rolId, Long permisoId);

    // Verificar si existe por entidades
    boolean existsByRolAndPermiso(RolUsuario rol, Permiso permiso);

    // Eliminar por rolId
    @Modifying
    @Transactional
    void deleteByRolId(Long rolId);

    // Eliminar por permisoId
    @Modifying
    @Transactional
    void deleteByPermisoId(Long permisoId);

    // Eliminar por rolId y permisoId específicos
    @Modifying
    @Transactional
    void deleteByRolIdAndPermisoId(Long rolId, Long permisoId);

    // Eliminar por entidades
    @Modifying
    @Transactional
    void deleteByRolAndPermiso(RolUsuario rol, Permiso permiso);

    // Contar permisos por rol
    long countByRolId(Long rolId);

    // Contar roles por permiso
    long countByPermisoId(Long permisoId);

    // Verificar si existe por rolId
    boolean existsByRolId(Long rolId);

    // Verificar si existe por permisoId
    boolean existsByPermisoId(Long permisoId);

    // Obtener permisos de un rol específico
    @Query("SELECT rp.permiso FROM RolPermiso rp WHERE rp.rolId = :rolId")
    List<Permiso> findPermisosByRolId(@Param("rolId") Long rolId);

    // Obtener roles que tienen un permiso específico
    @Query("SELECT rp.rol FROM RolPermiso rp WHERE rp.permisoId = :permisoId")
    List<RolUsuario> findRolesByPermisoId(@Param("permisoId") Long permisoId);

    // Obtener permisos de un rol por nombre de rol
    @Query("SELECT rp.permiso FROM RolPermiso rp WHERE rp.rol.nombre = :nombreRol")
    List<Permiso> findPermisosByRolNombre(@Param("nombreRol") String nombreRol);

    // Verificar si un rol tiene un permiso específico por códigos
    @Query("SELECT COUNT(rp) > 0 FROM RolPermiso rp WHERE rp.rol.nombre = :nombreRol AND rp.permiso.codigo = :codigoPermiso")
    boolean tienePermisoRol(@Param("nombreRol") String nombreRol, @Param("codigoPermiso") String codigoPermiso);

    // Obtener todas las relaciones ordenadas
    @Query("SELECT rp FROM RolPermiso rp ORDER BY rp.rolId, rp.permisoId")
    List<RolPermiso> findAllOrderedByRolAndPermiso();

    // Búsqueda por nombre de rol y código de permiso
    @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.nombre = :nombreRol AND rp.permiso.codigo = :codigoPermiso")
    List<RolPermiso> findByRolNombreAndPermisoCodigo(@Param("nombreRol") String nombreRol, @Param("codigoPermiso") String codigoPermiso);
}
