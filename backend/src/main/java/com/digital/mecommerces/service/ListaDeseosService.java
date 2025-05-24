package com.digital.mecommerces.service;

import com.digital.mecommerces.dto.ListaDeseosDTO;
import com.digital.mecommerces.dto.ListaDeseosItemDTO;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ListaDeseosService {

    private final ListaDeseosRepository listaDeseosRepository;
    private final ListaDeseosItemRepository listaDeseosItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ProductoImagenRepository productoImagenRepository;

    public ListaDeseosService(ListaDeseosRepository listaDeseosRepository,
                              ListaDeseosItemRepository listaDeseosItemRepository,
                              UsuarioRepository usuarioRepository,
                              ProductoRepository productoRepository,
                              ProductoImagenRepository productoImagenRepository) {
        this.listaDeseosRepository = listaDeseosRepository;
        this.listaDeseosItemRepository = listaDeseosItemRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.productoImagenRepository = productoImagenRepository;
    }

    @Transactional
    public ListaDeseosDTO crearLista(ListaDeseosDTO listaDTO) {
        log.info("Creando nueva lista de deseos para usuario: {}", listaDTO.getUsuarioId());

        Usuario usuario = usuarioRepository.findById(listaDTO.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Verificar si ya existe una lista con ese nombre
        if (listaDeseosRepository.existsByUsuarioAndNombre(usuario, listaDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe una lista con ese nombre");
        }

        ListaDeseos lista = new ListaDeseos(usuario, listaDTO.getNombre());
        lista = listaDeseosRepository.save(lista);

        log.info("Lista de deseos creada exitosamente: {}", lista.getListaId());
        return convertirADTO(lista);
    }

    public List<ListaDeseosDTO> obtenerListasPorUsuario(Long usuarioId) {
        log.info("Obteniendo listas de deseos para usuario: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<ListaDeseos> listas = listaDeseosRepository.findByUsuario(usuario);
        return listas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ListaDeseosDTO obtenerListaPorId(Long listaId) {
        log.info("Obteniendo lista de deseos: {}", listaId);

        ListaDeseos lista = listaDeseosRepository.findById(listaId)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de deseos no encontrada"));

        return convertirADTOConItems(lista);
    }

    public ListaDeseosDTO obtenerListaPrincipal(Long usuarioId) {
        log.info("Obteniendo lista principal para usuario: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return listaDeseosRepository.findFirstByUsuarioOrderByCreatedatAsc(usuario)
                .map(this::convertirADTOConItems)
                .orElseGet(() -> {
                    // Crear lista principal si no existe
                    ListaDeseosDTO nuevaLista = new ListaDeseosDTO();
                    nuevaLista.setUsuarioId(usuarioId);
                    nuevaLista.setNombre("Mi Lista de Deseos");
                    return crearLista(nuevaLista);
                });
    }

    @Transactional
    public ListaDeseosItemDTO agregarProductoALista(Long listaId, Long productoId, Integer prioridad, String notas) {
        log.info("Agregando producto {} a lista {}", productoId, listaId);

        ListaDeseos lista = listaDeseosRepository.findById(listaId)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de deseos no encontrada"));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // Verificar si el producto ya está en la lista
        if (listaDeseosItemRepository.existsByListaAndProducto(lista, producto)) {
            throw new IllegalArgumentException("El producto ya está en esta lista de deseos");
        }

        ListaDeseosItem item = new ListaDeseosItem(lista, producto, prioridad, notas);
        item = listaDeseosItemRepository.save(item);

        log.info("Producto agregado exitosamente a la lista de deseos");
        return convertirItemADTO(item);
    }

    @Transactional
    public void eliminarProductoDeLista(Long listaId, Long productoId) {
        log.info("Eliminando producto {} de lista {}", productoId, listaId);

        ListaDeseos lista = listaDeseosRepository.findById(listaId)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de deseos no encontrada"));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        listaDeseosItemRepository.deleteByListaAndProducto(lista, producto);
        log.info("Producto eliminado exitosamente de la lista de deseos");
    }

    @Transactional
    public ListaDeseosItemDTO actualizarItemLista(Long itemId, Integer prioridad, String notas) {
        log.info("Actualizando item de lista: {}", itemId);

        ListaDeseosItem item = listaDeseosItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item de lista no encontrado"));

        if (prioridad != null) {
            item.setPrioridad(prioridad);
        }
        if (notas != null) {
            item.setNotas(notas);
        }

        item = listaDeseosItemRepository.save(item);
        log.info("Item de lista actualizado exitosamente");
        return convertirItemADTO(item);
    }

    @Transactional
    public void eliminarLista(Long listaId) {
        log.info("Eliminando lista de deseos: {}", listaId);

        if (!listaDeseosRepository.existsById(listaId)) {
            throw new ResourceNotFoundException("Lista de deseos no encontrada");
        }

        listaDeseosRepository.deleteById(listaId);
        log.info("Lista de deseos eliminada exitosamente");
    }

    public List<ListaDeseosItemDTO> obtenerItemsPorLista(Long listaId) {
        log.info("Obteniendo items de lista: {}", listaId);

        List<ListaDeseosItem> items = listaDeseosItemRepository.findByListaListaIdOrderByPrioridadDesc(listaId);
        return items.stream()
                .map(this::convertirItemADTO)
                .collect(Collectors.toList());
    }

    public boolean verificarProductoEnLista(Long listaId, Long productoId) {
        ListaDeseos lista = listaDeseosRepository.findById(listaId)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de deseos no encontrada"));

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        return listaDeseosItemRepository.existsByListaAndProducto(lista, producto);
    }

    private ListaDeseosDTO convertirADTO(ListaDeseos lista) {
        ListaDeseosDTO dto = new ListaDeseosDTO();
        dto.setListaId(lista.getListaId());
        dto.setUsuarioId(lista.getUsuario().getUsuarioId());
        dto.setNombre(lista.getNombre());
        dto.setCreatedat(lista.getCreatedat());
        dto.setTotalItems(lista.getTotalItems());
        return dto;
    }

    private ListaDeseosDTO convertirADTOConItems(ListaDeseos lista) {
        ListaDeseosDTO dto = convertirADTO(lista);
        List<ListaDeseosItemDTO> items = lista.getItems().stream()
                .map(this::convertirItemADTO)
                .collect(Collectors.toList());
        dto.setItems(items);
        return dto;
    }

    private ListaDeseosItemDTO convertirItemADTO(ListaDeseosItem item) {
        ListaDeseosItemDTO dto = new ListaDeseosItemDTO();
        dto.setItemId(item.getItemId());
        dto.setListaId(item.getLista().getListaId());
        dto.setProductoId(item.getProducto().getProductoId());
        dto.setFechaAgregado(item.getFechaAgregado());
        dto.setPrioridad(item.getPrioridad());
        dto.setNotas(item.getNotas());

        // Información del producto
        Producto producto = item.getProducto();
        dto.setProductoNombre(producto.getProductoNombre());
        dto.setProductoPrecio(producto.getPrecio());
        dto.setProductoActivo(producto.getActivo());
        dto.setProductoStock(producto.getStock());

        // Obtener imagen principal
        ProductoImagen imagenPrincipal = productoImagenRepository
                .findByProductoAndEsPrincipalTrue(producto)
                .stream()
                .findFirst()
                .orElse(null);

        if (imagenPrincipal != null) {
            dto.setProductoImagen(imagenPrincipal.getUrl());
        }

        return dto;
    }
}
