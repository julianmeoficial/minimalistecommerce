package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.CategoriaProducto;
import com.digital.mecommerces.repository.CategoriaProductoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaProductoService {
    private final CategoriaProductoRepository categoriaProductoRepository;

    public CategoriaProductoService(CategoriaProductoRepository categoriaProductoRepository) {
        this.categoriaProductoRepository = categoriaProductoRepository;
    }

    public List<CategoriaProducto> obtenerCategorias() {
        return categoriaProductoRepository.findAll();
    }

    public List<CategoriaProducto> obtenerCategoriasPrincipales() {
        return categoriaProductoRepository.findCategoriasPrincipales();
    }

    public List<CategoriaProducto> obtenerSubcategoriasDe(Long categoriaPadreId) {
        return categoriaProductoRepository.findByCategoriaPadreCategoriaId(categoriaPadreId);
    }

    public CategoriaProducto obtenerCategoriaPorId(Long id) {
        return categoriaProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }

    public CategoriaProducto crearCategoria(CategoriaProducto categoria) {
        return categoriaProductoRepository.save(categoria);
    }

    public CategoriaProducto actualizarCategoria(Long id, CategoriaProducto categoriaDetails) {
        CategoriaProducto categoria = obtenerCategoriaPorId(id);
        categoria.setNombre(categoriaDetails.getNombre());
        categoria.setDescripcion(categoriaDetails.getDescripcion());

        if (categoriaDetails.getCategoriaPadre() != null) {
            CategoriaProducto categoriaPadre = obtenerCategoriaPorId(categoriaDetails.getCategoriaPadre().getCategoriaId());
            categoria.setCategoriaPadre(categoriaPadre);
        } else {
            categoria.setCategoriaPadre(null);
        }

        return categoriaProductoRepository.save(categoria);
    }

    public void eliminarCategoria(Long id) {
        CategoriaProducto categoria = obtenerCategoriaPorId(id);
        categoriaProductoRepository.delete(categoria);
    }
}

