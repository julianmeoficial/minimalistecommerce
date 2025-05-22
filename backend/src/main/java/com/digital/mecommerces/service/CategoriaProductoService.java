package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.CategoriaProducto;
import com.digital.mecommerces.repository.CategoriaProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CategoriaProductoService {

    private final CategoriaProductoRepository categoriaProductoRepository;

    public CategoriaProductoService(CategoriaProductoRepository categoriaProductoRepository) {
        this.categoriaProductoRepository = categoriaProductoRepository;
    }

    public List<CategoriaProducto> obtenerCategorias() {
        log.info("Obteniendo todas las categorías");
        return categoriaProductoRepository.findAll();
    }

    public List<CategoriaProducto> obtenerCategoriasPrincipales() {
        log.info("Obteniendo categorías principales");
        return categoriaProductoRepository.findCategoriasPrincipales();
    }

    public List<CategoriaProducto> obtenerSubcategoriasDe(Long categoriaPadreId) {
        log.info("Obteniendo subcategorías de la categoría padre ID: {}", categoriaPadreId);
        return categoriaProductoRepository.findByCategoriapadreId(categoriaPadreId);
    }

    public CategoriaProducto obtenerCategoriaPorId(Long id) {
        log.info("Obteniendo categoría por ID: {}", id);
        return categoriaProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el ID: " + id));
    }

    public CategoriaProducto obtenerCategoriaPorNombre(String nombre) {
        log.info("Obteniendo categoría por nombre: {}", nombre);
        return categoriaProductoRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el nombre: " + nombre));
    }

    public CategoriaProducto obtenerCategoriaPorSlug(String slug) {
        log.info("Obteniendo categoría por slug: {}", slug);
        return categoriaProductoRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el slug: " + slug));
    }

    public List<CategoriaProducto> obtenerCategoriasActivas() {
        log.info("Obteniendo categorías activas");
        return categoriaProductoRepository.findByActivoTrue();
    }

    public List<CategoriaProducto> obtenerCategoriasPrincipalesActivas() {
        log.info("Obteniendo categorías principales activas");
        return categoriaProductoRepository.findCategoriasPrincipalesActivas();
    }

    @Transactional
    public CategoriaProducto crearCategoria(CategoriaProducto categoria) {
        log.info("Creando nueva categoría: {}", categoria.getNombre());

        // Verificar que no existe una categoría con el mismo nombre
        if (categoriaProductoRepository.existsByNombre(categoria.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }

        // Generar slug si no se proporciona
        if (categoria.getSlug() == null || categoria.getSlug().isEmpty()) {
            String slug = generarSlug(categoria.getNombre());
            categoria.setSlug(slug);
        }

        CategoriaProducto nuevaCategoria = categoriaProductoRepository.save(categoria);
        log.info("Categoría creada exitosamente con ID: {}", nuevaCategoria.getCategoriaId());
        return nuevaCategoria;
    }

    @Transactional
    public CategoriaProducto actualizarCategoria(Long id, CategoriaProducto categoriaDetails) {
        log.info("Actualizando categoría con ID: {}", id);

        CategoriaProducto categoria = obtenerCategoriaPorId(id);

        // Verificar nombre único si se está cambiando
        if (!categoria.getNombre().equals(categoriaDetails.getNombre()) &&
                categoriaProductoRepository.existsByNombre(categoriaDetails.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriaDetails.getNombre());
        }

        categoria.setNombre(categoriaDetails.getNombre());
        categoria.setDescripcion(categoriaDetails.getDescripcion());
        categoria.setActivo(categoriaDetails.getActivo());
        categoria.setImagen(categoriaDetails.getImagen());

        // Actualizar categoría padre si se proporciona
        if (categoriaDetails.getCategoriaPadre() != null) {
            CategoriaProducto categoriaPadre = obtenerCategoriaPorId(categoriaDetails.getCategoriaPadre().getCategoriaId());
            categoria.setCategoriaPadre(categoriaPadre);
        } else {
            categoria.setCategoriaPadre(null);
        }

        // Actualizar slug si se cambió el nombre
        if (!categoria.getNombre().equals(categoriaDetails.getNombre())) {
            String nuevoSlug = generarSlug(categoriaDetails.getNombre());
            categoria.setSlug(nuevoSlug);
        }

        CategoriaProducto categoriaActualizada = categoriaProductoRepository.save(categoria);
        log.info("Categoría actualizada exitosamente: {}", categoriaActualizada.getNombre());
        return categoriaActualizada;
    }

    @Transactional
    public void eliminarCategoria(Long id) {
        log.info("Eliminando categoría con ID: {}", id);

        CategoriaProducto categoria = obtenerCategoriaPorId(id);

        // Verificar que no tenga subcategorías
        List<CategoriaProducto> subcategorias = categoriaProductoRepository.findByCategoriapadreId(id);
        if (!subcategorias.isEmpty()) {
            throw new IllegalStateException("No se puede eliminar la categoría porque tiene subcategorías asociadas");
        }

        categoriaProductoRepository.delete(categoria);
        log.info("Categoría eliminada exitosamente: {}", categoria.getNombre());
    }

    private String generarSlug(String nombre) {
        return nombre.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }

    public boolean existeCategoriaPorNombre(String nombre) {
        return categoriaProductoRepository.existsByNombre(nombre);
    }
}
