package com.digital.mecommerces.service;

import com.digital.mecommerces.enums.TipoCategoria;
import com.digital.mecommerces.exception.BusinessException;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.CategoriaProducto;
import com.digital.mecommerces.repository.CategoriaProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable("categorias")
    public List<CategoriaProducto> obtenerCategorias() {
        log.info("📋 Obteniendo todas las categorías optimizado");
        return categoriaProductoRepository.findByActivoTrue();
    }

    @Cacheable("categoriasPrincipales")
    public List<CategoriaProducto> obtenerCategoriasPrincipales() {
        log.info("🏠 Obteniendo categorías principales activas");
        return categoriaProductoRepository.findCategoriasPrincipalesActivas();
    }

    public List<CategoriaProducto> obtenerSubcategoriasDe(Long categoriaId) {
        log.info("📂 Obteniendo subcategorías de la categoría padre ID: {}", categoriaId);
        return categoriaProductoRepository.findSubcategoriasActivasPorPadre(categoriaId);
    }

    @Cacheable(value = "categoria", key = "#id")
    public CategoriaProducto obtenerCategoriaPorId(Long id) {
        log.info("🔍 Obteniendo categoría por ID: {}", id);
        return categoriaProductoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    public CategoriaProducto obtenerCategoriaPorNombre(String nombre) {
        log.info("📝 Obteniendo categoría por nombre: {}", nombre);
        return categoriaProductoRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con nombre: " + nombre));
    }

    public CategoriaProducto obtenerCategoriaPorSlug(String slug) {
        log.info("🔗 Obteniendo categoría por slug: {}", slug);
        return categoriaProductoRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con slug: " + slug));
    }

    public List<CategoriaProducto> obtenerCategoriasActivas() {
        log.info("✅ Obteniendo categorías activas");
        return categoriaProductoRepository.findByActivoTrue();
    }

    public List<CategoriaProducto> obtenerCategoriasDelSistema() {
        log.info("⚙️ Obteniendo categorías del sistema optimizado");
        return categoriaProductoRepository.findCategoriasDelSistema();
    }

    public List<CategoriaProducto> obtenerCategoriasPersonalizadas() {
        log.info("🎨 Obteniendo categorías personalizadas");
        return categoriaProductoRepository.findCategoriasPersonalizadas();
    }

    @Transactional
    @CacheEvict(value = {"categorias", "categoriasPrincipales"}, allEntries = true)
    public CategoriaProducto crearCategoria(CategoriaProducto categoria) {
        log.info("➕ Creando nueva categoría optimizada: {}", categoria.getNombre());

        try {
            // Verificar que no existe una categoría con el mismo nombre
            if (categoriaProductoRepository.existsByNombre(categoria.getNombre())) {
                throw new BusinessException("Ya existe una categoría con el nombre: " + categoria.getNombre());
            }

            // Verificar si es una categoría del sistema usando enum
            try {
                TipoCategoria tipo = TipoCategoria.fromCodigo(categoria.getNombre().toUpperCase());
                log.info("✅ Creando categoría del sistema: {} - {}", tipo.getCodigo(), tipo.getDescripcion());

                // Usar la descripción del enum si no se proporciona una personalizada
                if (categoria.getDescripcion() == null || categoria.getDescripcion().isEmpty()) {
                    categoria.setDescripcion(tipo.getDescripcion());
                }
            } catch (IllegalArgumentException e) {
                log.info("📝 Creando categoría personalizada: {}", categoria.getNombre());
            }

            // Generar slug si no se proporciona
            if (categoria.getSlug() == null || categoria.getSlug().isEmpty()) {
                String slug = generarSlug(categoria.getNombre());

                // Verificar que el slug sea único
                int contador = 1;
                String slugOriginal = slug;
                while (categoriaProductoRepository.existsBySlug(slug)) {
                    slug = slugOriginal + "-" + contador;
                    contador++;
                }
                categoria.setSlug(slug);
            } else {
                // Verificar que el slug proporcionado sea único
                if (categoriaProductoRepository.existsBySlug(categoria.getSlug())) {
                    throw new BusinessException("Ya existe una categoría con el slug: " + categoria.getSlug());
                }
            }

            // Validar categoría padre si se proporciona
            if (categoria.getCategoriaPadre() != null) {
                Long padreId = categoria.getCategoriaPadre().getCategoriaId();
                CategoriaProducto padre = obtenerCategoriaPorId(padreId);
                categoria.setCategoriaPadre(padre);
                categoria.setCategoriapadreId(padreId);
            }

            CategoriaProducto nuevaCategoria = categoriaProductoRepository.save(categoria);
            log.info("✅ Categoría creada exitosamente: {} con ID: {}",
                    nuevaCategoria.getNombre(), nuevaCategoria.getCategoriaId());

            return nuevaCategoria;

        } catch (Exception e) {
            log.error("❌ Error creando categoría: {}", e.getMessage());
            throw new BusinessException("Error al crear categoría: " + e.getMessage());
        }
    }

    @Transactional
    @CacheEvict(value = {"categorias", "categoriasPrincipales", "categoria"}, allEntries = true)
    public CategoriaProducto actualizarCategoria(Long id, CategoriaProducto categoriaDetails) {
        log.info("✏️ Actualizando categoría ID: {}", id);

        CategoriaProducto categoria = obtenerCategoriaPorId(id);

        // Verificar nombre único si se está cambiando
        if (categoriaDetails.getNombre() != null && !categoria.getNombre().equals(categoriaDetails.getNombre())) {
            if (categoriaProductoRepository.existsByNombreAndCategoriaIdNot(categoriaDetails.getNombre(), id)) {
                throw new BusinessException("Ya existe una categoría con el nombre: " + categoriaDetails.getNombre());
            }
            categoria.setNombre(categoriaDetails.getNombre());

            // Regenerar slug si cambió el nombre
            String nuevoSlug = generarSlug(categoriaDetails.getNombre());
            if (!nuevoSlug.equals(categoria.getSlug())) {
                // Verificar que el nuevo slug sea único
                if (categoriaProductoRepository.existsBySlugAndCategoriaIdNot(nuevoSlug, id)) {
                    int contador = 1;
                    String slugOriginal = nuevoSlug;
                    while (categoriaProductoRepository.existsBySlugAndCategoriaIdNot(nuevoSlug, id)) {
                        nuevoSlug = slugOriginal + "-" + contador;
                        contador++;
                    }
                }
                categoria.setSlug(nuevoSlug);
            }
        }

        // Actualizar descripción
        if (categoriaDetails.getDescripcion() != null) {
            categoria.setDescripcion(categoriaDetails.getDescripcion());
        }

        // Actualizar imagen
        if (categoriaDetails.getImagen() != null) {
            categoria.setImagen(categoriaDetails.getImagen());
        }

        // Actualizar estado activo
        if (categoriaDetails.getActivo() != null) {
            categoria.setActivo(categoriaDetails.getActivo());
        }

        CategoriaProducto categoriaActualizada = categoriaProductoRepository.save(categoria);
        log.info("✅ Categoría actualizada exitosamente: {}", categoriaActualizada.getNombre());

        return categoriaActualizada;
    }

    @Transactional
    @CacheEvict(value = {"categorias", "categoriasPrincipales", "categoria"}, allEntries = true)
    public void eliminarCategoria(Long id) {
        log.info("🗑️ Eliminando categoría ID: {}", id);

        CategoriaProducto categoria = obtenerCategoriaPorId(id);

        // Verificar que no tenga subcategorías
        List<CategoriaProducto> subcategorias = categoriaProductoRepository.findByCategoriapadreId(id);
        if (!subcategorias.isEmpty()) {
            throw new BusinessException("No se puede eliminar la categoría porque tiene " +
                    subcategorias.size() + " subcategorías asociadas");
        }

        // Verificar que no tenga productos asociados
        long productosAsociados = categoriaProductoRepository.findCategoriasConProductos().stream()
                .filter(c -> c.getCategoriaId().equals(id))
                .count();

        if (productosAsociados > 0) {
            throw new BusinessException("No se puede eliminar la categoría porque tiene productos asociados");
        }

        // Verificar si es una categoría crítica del sistema
        if (categoria.esCategoriaDelSistema()) {
            throw new BusinessException("No se puede eliminar una categoría del sistema: " + categoria.getNombre());
        }

        categoriaProductoRepository.delete(categoria);
        log.info("✅ Categoría eliminada exitosamente: {}", categoria.getNombre());
    }

    private String generarSlug(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return "";
        }

        return nombre.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    public boolean existeCategoriaPorNombre(String nombre) {
        return categoriaProductoRepository.existsByNombre(nombre);
    }

    public boolean esCategoriaDelSistema(String nombre) {
        return categoriaProductoRepository.esCategoriaDelSistema(nombre);
    }

    public List<CategoriaProducto> obtenerCategoriasConProductos() {
        log.info("📦 Obteniendo categorías que tienen productos");
        return categoriaProductoRepository.findCategoriasConProductos();
    }

    public List<CategoriaProducto> obtenerCategoriasSinProductos() {
        log.info("📪 Obteniendo categorías sin productos");
        return categoriaProductoRepository.findCategoriasSinProductos();
    }

    public List<CategoriaProducto> obtenerCategoriasPopulares() {
        log.info("🔥 Obteniendo categorías populares (con más productos)");
        return categoriaProductoRepository.findCategoriasPopulares();
    }

    public List<CategoriaProducto> obtenerJerarquiaCompleta() {
        log.info("🌳 Obteniendo jerarquía completa de categorías");
        return categoriaProductoRepository.findJerarquiaCompleta();
    }

    @Transactional
    public void crearCategoriasDelSistema() {
        log.info("🏗️ Verificando y creando categorías del sistema si no existen");

        for (TipoCategoria tipo : TipoCategoria.values()) {
            if (!categoriaProductoRepository.existsByNombre(tipo.getCodigo())) {
                CategoriaProducto categoria = new CategoriaProducto();
                categoria.setNombre(tipo.getCodigo());
                categoria.setDescripcion(tipo.getDescripcion());
                categoria.setSlug(generarSlug(tipo.getCodigo()));
                categoria.setActivo(true);

                categoriaProductoRepository.save(categoria);
                log.info("✅ Categoría del sistema creada: {}", tipo.getCodigo());
            } else {
                log.info("ℹ️ Categoría del sistema ya existe: {}", tipo.getCodigo());
            }
        }
    }

    public long contarCategorias() {
        return categoriaProductoRepository.countCategoriasActivas();
    }

    public long contarCategoriasPrincipales() {
        return categoriaProductoRepository.countCategoriasPrincipales();
    }

    public long contarSubcategorias() {
        return categoriaProductoRepository.countSubcategorias();
    }

    public List<Object[]> obtenerEstadisticasPorCategoria() {
        return categoriaProductoRepository.countProductosPorCategoria();
    }
}
