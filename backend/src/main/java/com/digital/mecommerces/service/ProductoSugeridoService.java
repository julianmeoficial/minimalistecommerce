package com.digital.mecommerces.service;

import com.digital.mecommerces.dto.ProductoSugeridoDTO;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductoSugeridoService {

    private final ProductoSugeridoRepository productoSugeridoRepository;
    private final ProductoRepository productoRepository;
    private final ProductoImagenRepository productoImagenRepository;

    public ProductoSugeridoService(ProductoSugeridoRepository productoSugeridoRepository,
                                   ProductoRepository productoRepository,
                                   ProductoImagenRepository productoImagenRepository) {
        this.productoSugeridoRepository = productoSugeridoRepository;
        this.productoRepository = productoRepository;
        this.productoImagenRepository = productoImagenRepository;
    }

    @Transactional
    public ProductoSugeridoDTO crearSugerencia(ProductoSugeridoDTO sugerenciaDTO) {
        log.info("Creando nueva sugerencia de producto: {} -> {}",
                sugerenciaDTO.getProductoBaseId(), sugerenciaDTO.getProductoSugeridoId());

        Producto productoBase = productoRepository.findById(sugerenciaDTO.getProductoBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto base no encontrado"));

        Producto productoSugerido = productoRepository.findById(sugerenciaDTO.getProductoSugeridoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto sugerido no encontrado"));

        // Verificar que no sea el mismo producto
        if (productoBase.equals(productoSugerido)) {
            throw new IllegalArgumentException("Un producto no puede ser sugerencia de sí mismo");
        }

        // Verificar si ya existe la sugerencia
        if (productoSugeridoRepository.existsByProductoBaseAndProductoSugerido(productoBase, productoSugerido)) {
            throw new IllegalArgumentException("Esta sugerencia ya existe");
        }

        ProductoSugerido sugerencia = new ProductoSugerido(
                productoBase,
                productoSugerido,
                sugerenciaDTO.getTipoRelacion(),
                sugerenciaDTO.getPrioridad()
        );
        sugerencia.setDescripcionRelacion(sugerenciaDTO.getDescripcionRelacion());

        sugerencia = productoSugeridoRepository.save(sugerencia);

        log.info("Sugerencia de producto creada exitosamente: {}", sugerencia.getSugerenciaId());
        return convertirADTO(sugerencia);
    }

    public List<ProductoSugeridoDTO> obtenerSugerenciasPorProducto(Long productoId) {
        log.info("Obteniendo sugerencias para producto: {}", productoId);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        List<ProductoSugerido> sugerencias = productoSugeridoRepository
                .findByProductoBaseAndActivoTrueOrderByPrioridadDesc(producto);

        return sugerencias.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ProductoSugeridoDTO> obtenerSugerenciasPorTipo(Long productoId, String tipoRelacion) {
        log.info("Obteniendo sugerencias de tipo {} para producto: {}", tipoRelacion, productoId);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        List<ProductoSugerido> sugerencias = productoSugeridoRepository
                .findByProductoBaseAndTipoRelacion(producto, tipoRelacion);

        return sugerencias.stream()
                .filter(s -> s.getActivo())
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ProductoSugeridoDTO> obtenerTopSugerencias(Long productoId, Integer limite) {
        log.info("Obteniendo top {} sugerencias para producto: {}", limite, productoId);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        List<ProductoSugerido> sugerencias = productoSugeridoRepository
                .findTopSugerenciasByProducto(producto);

        return sugerencias.stream()
                .limit(limite != null ? limite : 5)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoSugeridoDTO actualizarSugerencia(Long sugerenciaId, ProductoSugeridoDTO sugerenciaDTO) {
        log.info("Actualizando sugerencia: {}", sugerenciaId);

        ProductoSugerido sugerencia = productoSugeridoRepository.findById(sugerenciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sugerencia no encontrada"));

        if (sugerenciaDTO.getTipoRelacion() != null) {
            sugerencia.setTipoRelacion(sugerenciaDTO.getTipoRelacion());
        }
        if (sugerenciaDTO.getPrioridad() != null) {
            sugerencia.setPrioridad(sugerenciaDTO.getPrioridad());
        }
        if (sugerenciaDTO.getDescripcionRelacion() != null) {
            sugerencia.setDescripcionRelacion(sugerenciaDTO.getDescripcionRelacion());
        }
        if (sugerenciaDTO.getActivo() != null) {
            sugerencia.setActivo(sugerenciaDTO.getActivo());
        }

        sugerencia = productoSugeridoRepository.save(sugerencia);
        log.info("Sugerencia actualizada exitosamente");
        return convertirADTO(sugerencia);
    }

    @Transactional
    public void eliminarSugerencia(Long sugerenciaId) {
        log.info("Eliminando sugerencia: {}", sugerenciaId);

        if (!productoSugeridoRepository.existsById(sugerenciaId)) {
            throw new ResourceNotFoundException("Sugerencia no encontrada");
        }

        productoSugeridoRepository.deleteById(sugerenciaId);
        log.info("Sugerencia eliminada exitosamente");
    }

    @Transactional
    public void desactivarSugerencia(Long sugerenciaId) {
        log.info("Desactivando sugerencia: {}", sugerenciaId);

        ProductoSugerido sugerencia = productoSugeridoRepository.findById(sugerenciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sugerencia no encontrada"));

        sugerencia.setActivo(false);
        productoSugeridoRepository.save(sugerencia);
        log.info("Sugerencia desactivada exitosamente");
    }

    public List<ProductoSugeridoDTO> obtenerProductosMasSugeridos(Integer limite) {
        log.info("Obteniendo productos más sugeridos");

        List<Object[]> resultados = productoSugeridoRepository.findProductosMasSugeridos();

        return resultados.stream()
                .limit(limite != null ? limite : 10)
                .map(resultado -> {
                    Producto producto = (Producto) resultado[0];
                    Long cantidad = (Long) resultado[1];

                    ProductoSugeridoDTO dto = new ProductoSugeridoDTO();
                    dto.setProductoSugeridoId(producto.getProductoId());
                    dto.setProductoSugeridoNombre(producto.getProductoNombre());
                    dto.setProductoSugeridoPrecio(producto.getPrecio());

                    // Obtener imagen principal
                    ProductoImagen imagenPrincipal = productoImagenRepository
                            .findByProductoAndEsPrincipalTrue(producto)
                            .stream()
                            .findFirst()
                            .orElse(null);

                    if (imagenPrincipal != null) {
                        dto.setProductoSugeridoImagen(imagenPrincipal.getUrl());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void crearSugerenciaBidireccional(Long producto1Id, Long producto2Id, String tipoRelacion, Integer prioridad) {
        log.info("Creando sugerencia bidireccional entre productos: {} y {}", producto1Id, producto2Id);

        // Crear sugerencia A -> B
        ProductoSugeridoDTO sugerencia1 = new ProductoSugeridoDTO(producto1Id, producto2Id);
        sugerencia1.setTipoRelacion(tipoRelacion);
        sugerencia1.setPrioridad(prioridad);

        // Crear sugerencia B -> A
        ProductoSugeridoDTO sugerencia2 = new ProductoSugeridoDTO(producto2Id, producto1Id);
        sugerencia2.setTipoRelacion(tipoRelacion);
        sugerencia2.setPrioridad(prioridad);

        try {
            crearSugerencia(sugerencia1);
            crearSugerencia(sugerencia2);
            log.info("Sugerencia bidireccional creada exitosamente");
        } catch (IllegalArgumentException e) {
            log.warn("Algunas sugerencias ya existían: {}", e.getMessage());
        }
    }

    private ProductoSugeridoDTO convertirADTO(ProductoSugerido sugerencia) {
        ProductoSugeridoDTO dto = new ProductoSugeridoDTO();
        dto.setSugerenciaId(sugerencia.getSugerenciaId());
        dto.setProductoBaseId(sugerencia.getProductoBase().getProductoId());
        dto.setProductoSugeridoId(sugerencia.getProductoSugerido().getProductoId());
        dto.setTipoRelacion(sugerencia.getTipoRelacion());
        dto.setPrioridad(sugerencia.getPrioridad());
        dto.setCreatedat(sugerencia.getCreatedat());
        dto.setActivo(sugerencia.getActivo());
        dto.setDescripcionRelacion(sugerencia.getDescripcionRelacion());

        // Información del producto base
        Producto productoBase = sugerencia.getProductoBase();
        dto.setProductoBaseNombre(productoBase.getProductoNombre());
        dto.setProductoBasePrecio(productoBase.getPrecio());

        // Información del producto sugerido
        Producto productoSugerido = sugerencia.getProductoSugerido();
        dto.setProductoSugeridoNombre(productoSugerido.getProductoNombre());
        dto.setProductoSugeridoPrecio(productoSugerido.getPrecio());
        dto.setProductoSugeridoActivo(productoSugerido.getActivo());
        dto.setProductoSugeridoStock(productoSugerido.getStock());

        // Obtener imágenes principales
        ProductoImagen imagenBase = productoImagenRepository
                .findByProductoAndEsPrincipalTrue(productoBase)
                .stream()
                .findFirst()
                .orElse(null);

        if (imagenBase != null) {
            dto.setProductoBaseImagen(imagenBase.getUrl());
        }

        ProductoImagen imagenSugerido = productoImagenRepository
                .findByProductoAndEsPrincipalTrue(productoSugerido)
                .stream()
                .findFirst()
                .orElse(null);

        if (imagenSugerido != null) {
            dto.setProductoSugeridoImagen(imagenSugerido.getUrl());
        }

        return dto;
    }

    public List<ProductoSugeridoDTO> obtenerSugerenciasPorCategoria(Long categoriaId) {
        log.info("Obteniendo sugerencias por categoría: {}", categoriaId);

        List<ProductoSugerido> sugerencias = productoSugeridoRepository
                .findByProductoBaseCategoria(categoriaId);

        return sugerencias.stream()
                .filter(s -> s.getActivo())
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

}
