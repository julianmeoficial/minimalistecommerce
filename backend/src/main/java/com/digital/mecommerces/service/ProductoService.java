package com.digital.mecommerces.service;

import com.digital.mecommerces.model.Producto;
import com.digital.mecommerces.model.ProductoImagen;
import com.digital.mecommerces.model.Tipo;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.ProductoRepository;
import com.digital.mecommerces.repository.ProductoImagenRepository;
import com.digital.mecommerces.repository.TipoRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoImagenRepository productoImagenRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoRepository tipoRepository;

    public ProductoService(ProductoRepository productoRepository, 
                          ProductoImagenRepository productoImagenRepository,
                          UsuarioRepository usuarioRepository,
                          TipoRepository tipoRepository) {
        this.productoRepository = productoRepository;
        this.productoImagenRepository = productoImagenRepository;
        this.usuarioRepository = usuarioRepository;
        this.tipoRepository = tipoRepository;
    }

    // Obtener todos los productos
    public List<Producto> obtenerProductos() {
        return productoRepository.findAll();
    }

    // Obtener un producto por ID
    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    // Crear un nuevo producto
    @Transactional
    public Producto crearProducto(com.digital.mecommerces.dto.ProductoDTO productoDTO) {
        // Buscar el tipo y el vendedor
        com.digital.mecommerces.model.Tipo tipo = tipoRepository.findById(productoDTO.getTipoId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado con id: " + productoDTO.getTipoId()));

        Usuario vendedor = usuarioRepository.findById(productoDTO.getVendedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado con id: " + productoDTO.getVendedorId()));

        // Crear el producto
        Producto producto = new Producto(
                productoDTO.getProductoNombre(),
                productoDTO.getDescripcion(),
                productoDTO.getPrecio(),
                productoDTO.getStock(),
                tipo,
                vendedor
        );

        // Guardar el producto para obtener su ID
        producto = productoRepository.save(producto);

        // Agregar imágenes si existen
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
            // Guardar el producto con sus imágenes
            producto = productoRepository.save(producto);
        }

        return producto;
    }

    // Actualizar un producto existente
    @Transactional
    public Producto actualizarProducto(Long id, com.digital.mecommerces.dto.ProductoDTO productoDTO) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        // Buscar el tipo y el vendedor si se proporcionan
        if (productoDTO.getTipoId() != null) {
            com.digital.mecommerces.model.Tipo tipo = tipoRepository.findById(productoDTO.getTipoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado con id: " + productoDTO.getTipoId()));
            producto.setTipo(tipo);
        }

        if (productoDTO.getVendedorId() != null) {
            Usuario vendedor = usuarioRepository.findById(productoDTO.getVendedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado con id: " + productoDTO.getVendedorId()));
            producto.setVendedor(vendedor);
        }

        // Actualizar los datos básicos del producto
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

    // Eliminar un producto
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        productoRepository.delete(producto);
    }
}
