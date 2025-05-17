package com.digital.mecommerces.service;

import com.digital.mecommerces.dto.ProductoDTO;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final ProductoImagenRepository productoImagenRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaProductoRepository categoriaProductoRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            ProductoImagenRepository productoImagenRepository,
            UsuarioRepository usuarioRepository,
            CategoriaProductoRepository categoriaProductoRepository
    ) {
        this.productoRepository = productoRepository;
        this.productoImagenRepository = productoImagenRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaProductoRepository = categoriaProductoRepository;
    }

    public List<Producto> obtenerProductos() {
        return productoRepository.findAll();
    }

    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    @Transactional
    public Producto crearProducto(ProductoDTO productoDTO) {
        CategoriaProducto categoria = categoriaProductoRepository.findById(productoDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + productoDTO.getCategoriaId()));

        Usuario vendedor = usuarioRepository.findById(productoDTO.getVendedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado: " + productoDTO.getVendedorId()));

        Producto producto = new Producto(
                productoDTO.getProductoNombre(),
                productoDTO.getDescripcion(),
                productoDTO.getPrecio(),
                productoDTO.getStock(),
                categoria,
                vendedor
        );

        producto = productoRepository.save(producto);

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
            productoRepository.save(producto);
        }

        return producto;
    }

    @Transactional
    public Producto actualizarProducto(Long id, ProductoDTO productoDTO) {
        Producto producto = obtenerProductoPorId(id);

        if (productoDTO.getCategoriaId() != null) {
            CategoriaProducto categoria = categoriaProductoRepository.findById(productoDTO.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + productoDTO.getCategoriaId()));
            producto.setCategoria(categoria);
        }

        producto.setProductoNombre(productoDTO.getProductoNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStock(productoDTO.getStock());

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

        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = obtenerProductoPorId(id);
        productoRepository.delete(producto);
    }

    public List<Producto> obtenerProductosPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaCategoriaId(categoriaId);
    }

    public List<Producto> obtenerProductosPorVendedor(Long vendedorId) {
        return productoRepository.findByVendedorUsuarioId(vendedorId);
    }
}