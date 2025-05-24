package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.ListaDeseos;
import com.digital.mecommerces.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListaDeseosRepository extends JpaRepository<ListaDeseos, Long> {

    // Buscar listas de deseos por usuario
    List<ListaDeseos> findByUsuario(Usuario usuario);

    // Buscar listas de deseos por ID de usuario
    List<ListaDeseos> findByUsuarioUsuarioId(Long usuarioId);

    // Buscar la lista principal del usuario (primera lista)
    Optional<ListaDeseos> findFirstByUsuarioOrderByCreatedatAsc(Usuario usuario);

    // Buscar lista por usuario y nombre
    Optional<ListaDeseos> findByUsuarioAndNombre(Usuario usuario, String nombre);

    // Verificar si existe una lista con un nombre específico para un usuario
    boolean existsByUsuarioAndNombre(Usuario usuario, String nombre);

    // Contar cuántas listas tiene un usuario
    long countByUsuario(Usuario usuario);

    // Buscar listas por nombre que contenga cierto texto
    @Query("SELECT l FROM ListaDeseos l WHERE l.usuario = :usuario AND l.nombre LIKE %:nombre%")
    List<ListaDeseos> findByUsuarioAndNombreContaining(@Param("usuario") Usuario usuario, @Param("nombre") String nombre);

    // Obtener listas con el número de items
    @Query("SELECT l, COUNT(i) as itemCount FROM ListaDeseos l LEFT JOIN l.items i WHERE l.usuario = :usuario GROUP BY l")
    List<Object[]> findListasConConteoItems(@Param("usuario") Usuario usuario);
}
