package com.digital.mecommerces.repository;

import com.digital.mecommerces.model.ListaDeseos;
import com.digital.mecommerces.model.ListaDeseosItem;
import com.digital.mecommerces.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListaDeseosItemRepository extends JpaRepository<ListaDeseosItem, Long> {

    // Buscar items por lista
    List<ListaDeseosItem> findByLista(ListaDeseos lista);

    // Buscar items por ID de lista
    List<ListaDeseosItem> findByListaListaId(Long listaId);

    // MÉTODO FALTANTE: Buscar items por ID de lista ordenados por prioridad
    List<ListaDeseosItem> findByListaListaIdOrderByPrioridadDesc(Long listaId);

    // Buscar items por producto
    List<ListaDeseosItem> findByProducto(Producto producto);

    // Verificar si un producto ya está en una lista específica
    boolean existsByListaAndProducto(ListaDeseos lista, Producto producto);

    // Buscar item específico por lista y producto
    Optional<ListaDeseosItem> findByListaAndProducto(ListaDeseos lista, Producto producto);

    // Buscar items por usuario (a través de la lista)
    @Query("SELECT i FROM ListaDeseosItem i WHERE i.lista.usuario.usuarioId = :usuarioId")
    List<ListaDeseosItem> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Buscar items ordenados por prioridad
    List<ListaDeseosItem> findByListaOrderByPrioridadDesc(ListaDeseos lista);

    // Buscar items agregados recientemente
    List<ListaDeseosItem> findByListaAndFechaAgregadoAfter(ListaDeseos lista, LocalDateTime fecha);

    // Contar items en una lista
    long countByLista(ListaDeseos lista);

    // Eliminar por lista y producto
    void deleteByListaAndProducto(ListaDeseos lista, Producto producto);

    // Buscar los productos más deseados
    @Query("SELECT i.producto, COUNT(i) as cantidad FROM ListaDeseosItem i GROUP BY i.producto ORDER BY cantidad DESC")
    List<Object[]> findProductosMasDeseados();

    // Buscar productos similares en listas de deseos
    @Query("SELECT DISTINCT i.producto FROM ListaDeseosItem i WHERE i.lista.usuario.usuarioId IN " +
            "(SELECT l.usuario.usuarioId FROM ListaDeseosItem li JOIN li.lista l WHERE li.producto = :producto)")
    List<Producto> findProductosSimilaresPorListasDeseos(@Param("producto") Producto producto);
}
