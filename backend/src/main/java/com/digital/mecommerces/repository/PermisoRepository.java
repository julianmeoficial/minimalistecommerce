package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    // Buscar por código
    Optional<Permiso> findByCodigo(String codigo);

    // Verificar si existe por código
    boolean existsByCodigo(String codigo);

    // Buscar por nivel
    List<Permiso> findByNivel(Integer nivel);

    // Buscar permisos padre (sin padre)
    List<Permiso> findByPermisoPadreIsNull();

    // Buscar permisos hijos de un padre específico
    List<Permiso> findByPermisoPadre(Permiso permisoPadre);

    // Buscar permisos hijos por ID del padre
    List<Permiso> findByPermisopadreId(Long permisopadreId);

    // Buscar permisos por código que contenga cierto texto
    List<Permiso> findByCodigoContainingIgnoreCase(String codigo);

    // Buscar permisos por descripción que contenga cierto texto
    List<Permiso> findByDescripcionContainingIgnoreCase(String descripcion);

    // Buscar permisos por nivel mayor o igual
    List<Permiso> findByNivelGreaterThanEqual(Integer nivel);

    // Buscar permisos por nivel menor o igual
    List<Permiso> findByNivelLessThanEqual(Integer nivel);

    // Contar permisos por nivel
    long countByNivel(Integer nivel);

    // Contar permisos hijos de un padre
    long countByPermisoPadre(Permiso permisoPadre);

    // Buscar permisos ordenados por nivel
    List<Permiso> findAllByOrderByNivelAsc();

    // Buscar permisos ordenados por código
    List<Permiso> findAllByOrderByCodigoAsc();

    // Consulta personalizada para obtener jerarquía completa
    @Query("SELECT p FROM Permiso p LEFT JOIN FETCH p.permisosHijos ORDER BY p.nivel, p.codigo")
    List<Permiso> findAllWithHijos();

    // Consulta para obtener permisos de un nivel específico con sus hijos
    @Query("SELECT p FROM Permiso p LEFT JOIN FETCH p.permisosHijos WHERE p.nivel = :nivel ORDER BY p.codigo")
    List<Permiso> findByNivelWithHijos(@Param("nivel") Integer nivel);

    // ELIMINADA: La consulta problemática WITH RECURSIVE
    // Reemplazada por método en el servicio

    // Buscar permisos que no tienen hijos (permisos hoja)
    @Query("SELECT p FROM Permiso p WHERE p.permisoId NOT IN (SELECT DISTINCT pp.permisopadreId FROM Permiso pp WHERE pp.permisopadreId IS NOT NULL)")
    List<Permiso> findPermisosHoja();

    // Buscar permisos por múltiples códigos
    List<Permiso> findByCodigoIn(List<String> codigos);

    // Buscar permisos por múltiples IDs
    List<Permiso> findByPermisoIdIn(List<Long> permisoIds);

    // Buscar permisos raíz (nivel 0 y sin padre)
    @Query("SELECT p FROM Permiso p WHERE p.nivel = 0 AND p.permisoPadre IS NULL ORDER BY p.codigo")
    List<Permiso> findPermisosRaiz();

    // Buscar todos los descendientes de un permiso (usando JPQL válido)
    @Query("SELECT p FROM Permiso p WHERE p.permisopadreId = :permisoId ORDER BY p.nivel, p.codigo")
    List<Permiso> findDescendientesDirectos(@Param("permisoId") Long permisoId);
}
