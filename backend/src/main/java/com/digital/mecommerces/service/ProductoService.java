package com.digital.mecommerces.service;

import com.digital.mecommerces.dto.ProductoDTO;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoImagenRepository productoImagenRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaProductoRepository categoriaProductoRepository;

    public ProductoService(ProductoRepository productoRepository,
                           ProductoImagenRepository productoImagenRepository,
                           UsuarioRepository usuarioRepository,
                           CategoriaProductoRepository categoriaProductoRepository) {
        this.productoRepository = productoRepository;
        this.productoImagenRepository = productoImagenRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaProductoRepository = categoriaProductoRepository;
    }

    @Cacheable("productos")
    public List<Producto> obtenerProductos() {
        log.info("Obteniendo todos los productos");
        return productoRepository.findAll();
    }

    @Cacheable(value = "productos", key = "#id")
    public Producto obtenerProductoPorId(Long id) {
        log.info("Obteniendo producto por ID: {}", id);
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    public List<Producto> obtenerProductosActivos() {
        log.info("Obteniendo productos activos");
        return productoRepository.findByActivoTrue();
    }

    public List<Producto> obtenerProductosPorCategoria(Long categoriaId) {
        log.info("Obteniendo productos por categoría: {}", categoriaId);
        return productoRepository.findByCategoriaId(categoriaId);
    }

    public List<Producto> obtenerProductosPorVendedor(Long vendedorId) {
        log.info("Obteniendo productos por vendedor: {}", vendedorId);
        return productoRepository.findByVendedorId(vendedorId);
    }

    public List<Producto> obtenerProductosDestacados() {
        log.info("Obteniendo productos destacados");
        return productoRepository.findByDestacadoTrueAndActivoTrue();
    }

    public List<Producto> buscarProductos(String termino) {
        log.info("Buscando productos con término: {}", termino);
        return productoRepository.findByProductoNombreContainingIgnoreCaseAndActivoTrue(termino);
    }

    public Optional<Producto> obtenerProductoPorSlug(String slug) {
        log.info("Obteniendo producto por slug: {}", slug);
        return productoRepository.findBySlug(slug);
    }

    @Transactional
    @CacheEvict(value = "productos", allEntries = true)
    public Producto crearProducto(ProductoDTO productoDTO) {
        log.info("Creando nuevo producto: {}", productoDTO.getProductoNombre());

        // Validar que la categoría existe
        CategoriaProducto categoria = categoriaProductoRepository.findById(productoDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + productoDTO.getCategoriaId()));

        // Validar que el vendedor existe
        Usuario vendedor = usuarioRepository.findById(productoDTO.getVendedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado con id: " + productoDTO.getVendedorId()));

        // Crear producto
        Producto producto = new Producto(
                productoDTO.getProductoNombre(),
                productoDTO.getDescripcion(),
                productoDTO.getPrecio(),
                productoDTO.getStock(),
                categoria,
                vendedor
        );

        // Generar slug si no se proporciona
        if (producto.getSlug() == null || producto.getSlug().isEmpty()) {
            String slug = generarSlug(productoDTO.getProductoNombre());
            producto.setSlug(slug);
        }

        // Guardar producto
        producto = productoRepository.save(producto);

        // Procesar imágenes si se proporcionan
        if (productoDTO.getImagenes() != null && !productoDTO.getImagenes().isEmpty()) {
            for (com.digital.mecommerces.dto.ProductoImagenDTO imagenDTO : productoDTO.getImagenes()) {
                ProductoImagen imagen = new ProductoImagen(
                        imagenDTO.getUrl(),
                        imagenDTO.getDescripcion(),
                        imagenDTO.getEsPrincipal(),
                        producto
                );
                producto.addImagen(imagen);
            }
            producto = productoRepository.save(producto);
        }

        log.info("Producto creado exitosamente con ID: {}", producto.getProductoId());
        return producto;
    }

    @Transactional
    @CacheEvict(value = "productos", allEntries = true)
    public Producto actualizarProducto(Long id, ProductoDTO productoDTO) {
        log.info("Actualizando producto con ID: {}", id);

        Producto producto = obtenerProductoPorId(id);

        // Actualizar categoría si se proporciona
        if (productoDTO.getCategoriaId() != null) {
            CategoriaProducto categoria = categoriaProductoRepository.findById(productoDTO.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + productoDTO.getCategoriaId()));
            producto.setCategoria(categoria);
        }

        // Actualizar campos básicos
        if (productoDTO.getProductoNombre() != null) {
            producto.setProductoNombre(productoDTO.getProductoNombre());
            // Regenerar slug si cambió el nombre
            String nuevoSlug = generarSlug(productoDTO.getProductoNombre());
            producto.setSlug(nuevoSlug);
        }

        if (productoDTO.getDescripcion() != null) {
            producto.setDescripcion(productoDTO.getDescripcion());
        }

        if (productoDTO.getPrecio() != null) {
            producto.setPrecio(productoDTO.getPrecio());
        }

        if (productoDTO.getStock() != null) {
            producto.setStock(productoDTO.getStock());
        }

        // Actualizar imágenes si se proporcionan
        if (productoDTO.getImagenes() != null) {
            // Eliminar imágenes existentes
            producto.getImagenes().clear();

            // Agregar nuevas imágenes
            for (com.digital.mecommerces.dto.ProductoImagenDTO imagenDTO : productoDTO.getImagenes()) {
                ProductoImagen imagen = new ProductoImagen(
                        imagenDTO.getUrl(),
                        imagenDTO.getDescripcion(),
                        imagenDTO.getEsPrincipal(),
                        producto
                );
                producto.addImagen(imagen);
            }
        }

        Producto productoActualizado = productoRepository.save(producto);
        log.info("Producto actualizado exitosamente: {}", id);
        return productoActualizado;
    }

    @Transactional
    @CacheEvict(value = "productos", allEntries = true)
    public void eliminarProducto(Long id) {
        log.info("Eliminando producto con ID: {}", id);

        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }

        productoRepository.deleteById(id);
        log.info("Producto eliminado exitosamente: {}", id);
    }

    @Transactional
    @CacheEvict(value = "productos", key = "#id")
    public Producto cambiarEstadoProducto(Long id, Boolean activo) {
        log.info("Cambiando estado del producto {} a: {}", id, activo);

        Producto producto = obtenerProductoPorId(id);
        producto.setActivo(activo);

        return productoRepository.save(producto);
    }

    @Transactional
    @CacheEvict(value = "productos", key = "#id")
    public Producto destacarProducto(Long id, Boolean destacado) {
        log.info("Cambiando destacado del producto {} a: {}", id, destacado);

        Producto producto = obtenerProductoPorId(id);
        producto.setDestacado(destacado);

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizarStock(Long id, Integer nuevoStock) {
        log.info("Actualizando stock del producto {} a: {}", id, nuevoStock);

        Producto producto = obtenerProductoPorId(id);
        producto.setStock(nuevoStock);

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto reducirStock(Long id, Integer cantidad) {
        log.info("Reduciendo stock del producto {} en: {}", id, cantidad);

        Producto producto = obtenerProductoPorId(id);

        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + producto.getStock());
        }

        producto.setStock(producto.getStock() - cantidad);
        return productoRepository.save(producto);
    }

    public boolean tieneStockSuficiente(Long id, Integer cantidad) {
        Producto producto = obtenerProductoPorId(id);
        return producto.getStock() >= cantidad;
    }

    public List<Producto> obtenerProductosConStockBajo(Integer limite) {
        log.info("Obteniendo productos con stock menor a: {}", limite);
        return productoRepository.findByStockLessThanAndActivoTrue(limite);
    }

    public List<Producto> obtenerProductosPorRangoPrecio(Double precioMin, Double precioMax) {
        log.info("Obteniendo productos con precio entre {} y {}", precioMin, precioMax);
        return productoRepository.findByPrecioBetweenAndActivoTrue(precioMin, precioMax);
    }

    private String generarSlug(String nombre) {
        String slug = nombre.toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        // Verificar unicidad
        String originalSlug = slug;
        int contador = 1;
        while (productoRepository.findBySlug(slug).isPresent()) {
            slug = originalSlug + "-" + contador;
            contador++;
        }

        return slug;
    }

    public long contarProductos() {
        return productoRepository.count();
    }

    public long contarProductosActivos() {
        return productoRepository.countByActivoTrue();
    }

    public long contarProductosPorCategoria(Long categoriaId) {
        return productoRepository.countByCategoriaId(categoriaId);
    }

    public long contarProductosPorVendedor(Long vendedorId) {
        return productoRepository.countByVendedorId(vendedorId);
    }
}
