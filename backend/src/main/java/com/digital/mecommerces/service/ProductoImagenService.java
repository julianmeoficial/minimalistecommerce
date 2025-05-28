package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.Producto;
import com.digital.mecommerces.model.ProductoImagen;
import com.digital.mecommerces.repository.ProductoImagenRepository;
import com.digital.mecommerces.repository.ProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de imágenes de productos
 * Optimizado para el sistema medbcommerce 3.0
 */
@Service
@Slf4j
public class ProductoImagenService {

    private final ProductoImagenRepository productoImagenRepository;
    private final ProductoRepository productoRepository;

    public ProductoImagenService(ProductoImagenRepository productoImagenRepository,
                                 ProductoRepository productoRepository) {
        this.productoImagenRepository = productoImagenRepository;
        this.productoRepository = productoRepository;
    }

    // === OPERACIONES BÁSICAS CRUD ===

    public List<ProductoImagen> obtenerImagenesPorProducto(Long productoId) {
        log.info("📸 Obteniendo imágenes del producto ID: {}", productoId);
        return productoImagenRepository.findByProductoProductoId(productoId);
    }

    public ProductoImagen obtenerImagenPorId(Long imagenId) {
        log.info("📸 Obteniendo imagen por ID: {}", imagenId);
        return productoImagenRepository.findById(imagenId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con ID: " + imagenId));
    }

    @Transactional
    public ProductoImagen agregarImagen(ProductoImagen imagen) {
        log.info("📸 Agregando nueva imagen para producto ID: {}", imagen.getProducto().getProductoId());

        // Validar que el producto existe
        if (imagen.getProducto() == null || imagen.getProducto().getProductoId() == null) {
            throw new IllegalArgumentException("El producto es obligatorio para la imagen");
        }

        // Validar que la imagen es válida
        List<String> erroresValidacion = imagen.validar();
        if (!erroresValidacion.isEmpty()) {
            throw new IllegalArgumentException("Imagen inválida: " + String.join(", ", erroresValidacion));
        }

        // Si es imagen principal, quitar esa propiedad de otras imágenes del mismo producto
        if (Boolean.TRUE.equals(imagen.getEsPrincipal())) {
            quitarImagenPrincipalExistente(imagen.getProducto().getProductoId());
        }

        // ✅ CORREGIDO: Usar métodos que ahora existen en ProductoImagen
        imagen.setCreatedAt(LocalDateTime.now());
        imagen.setUpdatedAt(LocalDateTime.now());

        if (imagen.getActiva() == null) {
            imagen.setActiva(true);
        }
        if (imagen.getOrden() == null) {
            imagen.setOrden(obtenerSiguienteOrden(imagen.getProducto().getProductoId()));
        }
        if (imagen.getTipo() == null || imagen.getTipo().trim().isEmpty()) {
            imagen.setTipo("principal");
        }

        ProductoImagen nuevaImagen = productoImagenRepository.save(imagen);
        log.info("✅ Imagen agregada con ID: {}", nuevaImagen.getImagenId());
        return nuevaImagen;
    }

    @Transactional
    public ProductoImagen actualizarImagen(Long imagenId, ProductoImagen imagenDetails) {
        log.info("📸 Actualizando imagen ID: {}", imagenId);

        ProductoImagen imagen = obtenerImagenPorId(imagenId);

        // Si se está estableciendo como principal, quitar esa propiedad de otras imágenes
        if (Boolean.TRUE.equals(imagenDetails.getEsPrincipal()) &&
                !Boolean.TRUE.equals(imagen.getEsPrincipal())) {
            quitarImagenPrincipalExistente(imagen.getProducto().getProductoId());
        }

        // Actualizar campos solo si vienen en imagenDetails
        if (imagenDetails.getUrl() != null && !imagenDetails.getUrl().trim().isEmpty()) {
            imagen.setUrl(imagenDetails.getUrl());
        }
        if (imagenDetails.getDescripcion() != null) {
            imagen.setDescripcion(imagenDetails.getDescripcion());
        }
        if (imagenDetails.getEsPrincipal() != null) {
            imagen.setEsPrincipal(imagenDetails.getEsPrincipal());
        }
        if (imagenDetails.getTipo() != null) {
            imagen.setTipo(imagenDetails.getTipo());
        }
        if (imagenDetails.getOrden() != null) {
            imagen.setOrden(imagenDetails.getOrden());
        }
        if (imagenDetails.getActiva() != null) {
            imagen.setActiva(imagenDetails.getActiva());
        }
        if (imagenDetails.getTamanio() != null) {
            imagen.setTamanio(imagenDetails.getTamanio());
        }

        // ✅ CORREGIDO: Usar método que ahora existe
        imagen.setUpdatedAt(LocalDateTime.now());

        ProductoImagen imagenActualizada = productoImagenRepository.save(imagen);
        log.info("✅ Imagen actualizada exitosamente");
        return imagenActualizada;
    }

    @Transactional
    public void eliminarImagen(Long imagenId) {
        log.info("📸 Eliminando imagen ID: {}", imagenId);

        ProductoImagen imagen = obtenerImagenPorId(imagenId);

        // Si era la imagen principal, establecer otra como principal
        if (Boolean.TRUE.equals(imagen.getEsPrincipal())) {
            establecerNuevaImagenPrincipal(imagen.getProducto().getProductoId(), imagenId);
        }

        productoImagenRepository.delete(imagen);
        log.info("✅ Imagen eliminada exitosamente");
    }

    // === OPERACIONES ESPECÍFICAS ===

    @Transactional
    public void establecerImagenPrincipal(Long imagenId) {
        log.info("📸 Estableciendo imagen ID: {} como principal", imagenId);

        ProductoImagen imagen = obtenerImagenPorId(imagenId);

        // Quitar propiedad principal de otras imágenes del mismo producto
        quitarImagenPrincipalExistente(imagen.getProducto().getProductoId());

        // Establecer como principal
        imagen.setEsPrincipal(true);
        // ✅ CORREGIDO: Usar método que ahora existe
        imagen.setUpdatedAt(LocalDateTime.now());
        productoImagenRepository.save(imagen);

        log.info("✅ Imagen establecida como principal");
    }

    @Transactional
    public void actualizarOrdenImagen(Long imagenId, Integer nuevoOrden) {
        log.info("📸 Actualizando orden de imagen ID: {} a: {}", imagenId, nuevoOrden);

        if (nuevoOrden == null || nuevoOrden < 1) {
            throw new IllegalArgumentException("El orden debe ser mayor a 0");
        }

        ProductoImagen imagen = obtenerImagenPorId(imagenId);
        imagen.setOrden(nuevoOrden);
        // ✅ CORREGIDO: Usar método que ahora existe
        imagen.setUpdatedAt(LocalDateTime.now());
        productoImagenRepository.save(imagen);

        log.info("✅ Orden de imagen actualizado");
    }

    public ProductoImagen obtenerImagenPrincipal(Long productoId) {
        log.info("📸 Obteniendo imagen principal del producto ID: {}", productoId);
        return productoImagenRepository.findByProductoProductoIdAndEsPrincipal(productoId, true);
    }

    public List<ProductoImagen> obtenerImagenesActivas(Long productoId) {
        log.info("📸 Obteniendo imágenes activas del producto ID: {}", productoId);

        return obtenerImagenesPorProducto(productoId).stream()
                .filter(img -> Boolean.TRUE.equals(img.getActiva()))
                .sorted(ProductoImagen.porOrden())
                .collect(Collectors.toList());
    }

    // === MÉTODOS DE UTILIDAD ===

    private void quitarImagenPrincipalExistente(Long productoId) {
        ProductoImagen imagenPrincipalActual = productoImagenRepository
                .findByProductoProductoIdAndEsPrincipal(productoId, true);

        if (imagenPrincipalActual != null) {
            imagenPrincipalActual.setEsPrincipal(false);
            // ✅ CORREGIDO: Usar método que ahora existe
            imagenPrincipalActual.setUpdatedAt(LocalDateTime.now());
            productoImagenRepository.save(imagenPrincipalActual);
            log.debug("📸 Imagen principal anterior removida: {}", imagenPrincipalActual.getImagenId());
        }
    }

    private void establecerNuevaImagenPrincipal(Long productoId, Long imagenEliminadaId) {
        List<ProductoImagen> imagenesRestantes = obtenerImagenesActivas(productoId).stream()
                .filter(img -> !img.getImagenId().equals(imagenEliminadaId))
                .collect(Collectors.toList());

        if (!imagenesRestantes.isEmpty()) {
            // Establecer la primera imagen activa como principal
            ProductoImagen nuevaPrincipal = imagenesRestantes.get(0);
            nuevaPrincipal.setEsPrincipal(true);
            // ✅ CORREGIDO: Usar método que ahora existe
            nuevaPrincipal.setUpdatedAt(LocalDateTime.now());
            productoImagenRepository.save(nuevaPrincipal);

            log.info("📸 Nueva imagen principal establecida: {}", nuevaPrincipal.getImagenId());
        } else {
            log.warn("⚠️ No hay imágenes activas restantes para establecer como principal en producto: {}", productoId);
        }
    }

    private Integer obtenerSiguienteOrden(Long productoId) {
        List<ProductoImagen> imagenes = obtenerImagenesPorProducto(productoId);

        if (imagenes.isEmpty()) {
            return 1;
        }

        return imagenes.stream()
                .mapToInt(img -> img.getOrden() != null ? img.getOrden() : 0)
                .max()
                .orElse(0) + 1;
    }

    // === OPERACIONES DE VALIDACIÓN ===

    public boolean validarImagenPerteneceAProducto(Long imagenId, Long productoId) {
        try {
            ProductoImagen imagen = obtenerImagenPorId(imagenId);
            return imagen.perteneceAProducto(productoId);
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public boolean productoTieneImagenes(Long productoId) {
        return !obtenerImagenesPorProducto(productoId).isEmpty();
    }

    public boolean productoTieneImagenPrincipal(Long productoId) {
        return obtenerImagenPrincipal(productoId) != null;
    }

    // === OPERACIONES EN LOTE ===

    @Transactional
    public void eliminarTodasLasImagenesDeProducto(Long productoId) {
        log.info("📸 Eliminando todas las imágenes del producto ID: {}", productoId);

        List<ProductoImagen> imagenes = obtenerImagenesPorProducto(productoId);
        if (!imagenes.isEmpty()) {
            productoImagenRepository.deleteAll(imagenes);
            log.info("✅ {} imágenes eliminadas del producto", imagenes.size());
        } else {
            log.info("ℹ️ No hay imágenes para eliminar en el producto ID: {}", productoId);
        }
    }

    @Transactional
    public void reordenarImagenesProducto(Long productoId, List<Long> ordenImagenes) {
        log.info("📸 Reordenando imágenes del producto ID: {}", productoId);

        if (ordenImagenes == null || ordenImagenes.isEmpty()) {
            throw new IllegalArgumentException("La lista de imágenes para reordenar no puede estar vacía");
        }

        // Validar que todas las imágenes pertenecen al producto
        for (Long imagenId : ordenImagenes) {
            if (!validarImagenPerteneceAProducto(imagenId, productoId)) {
                throw new IllegalArgumentException("La imagen " + imagenId + " no pertenece al producto " + productoId);
            }
        }

        // Reordenar imágenes
        for (int i = 0; i < ordenImagenes.size(); i++) {
            Long imagenId = ordenImagenes.get(i);
            ProductoImagen imagen = obtenerImagenPorId(imagenId);

            imagen.setOrden(i + 1);
            // ✅ CORREGIDO: Usar método que ahora existe
            imagen.setUpdatedAt(LocalDateTime.now());
            productoImagenRepository.save(imagen);
        }

        log.info("✅ {} imágenes reordenadas exitosamente", ordenImagenes.size());
    }

    // === ESTADÍSTICAS ===

    public long contarImagenes() {
        return productoImagenRepository.count();
    }

    public long contarImagenesActivas() {
        return productoImagenRepository.findAll().stream()
                .filter(img -> Boolean.TRUE.equals(img.getActiva()))
                .count();
    }

    public long contarImagenesPrincipales() {
        return productoImagenRepository.findAll().stream()
                .filter(img -> Boolean.TRUE.equals(img.getEsPrincipal()))
                .count();
    }

    // ✅ CORREGIDO: Usar tipos compatibles para Map
    public Map<String, Object> obtenerEstadisticasPorTipo() {
        Map<String, Long> estadisticasLong = productoImagenRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        img -> img.getTipo() != null ? img.getTipo() : "sin_tipo",
                        Collectors.counting()
                ));

        // Convertir a Map<String, Object>
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticasLong.forEach((k, v) -> estadisticas.put(k, v));
        return estadisticas;
    }

    // ✅ CORREGIDO: Usar tipos compatibles para Map
    public Map<String, Object> obtenerEstadisticasPorProducto() {
        Map<Long, Long> estadisticasLong = productoImagenRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        img -> img.getProducto().getProductoId(),
                        Collectors.counting()
                ));

        // Convertir a Map<String, Object>
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticasLong.forEach((k, v) -> estadisticas.put(k.toString(), v));
        return estadisticas;
    }

    public long contarProductosSinImagenes() {
        List<Producto> todosLosProductos = productoRepository.findAll();

        return todosLosProductos.stream()
                .filter(producto -> !productoTieneImagenes(producto.getProductoId()))
                .count();
    }

    // === OPERACIONES DE BÚSQUEDA AVANZADA ===

    public List<ProductoImagen> buscarImagenesPorTipo(String tipo) {
        return productoImagenRepository.findAll().stream()
                .filter(img -> tipo.equals(img.getTipo()))
                .collect(Collectors.toList());
    }

    public List<ProductoImagen> buscarImagenesGrandes(int tamanioMinimo) {
        return productoImagenRepository.findAll().stream()
                .filter(img -> img.getTamanio() != null && img.getTamanio() >= tamanioMinimo)
                .collect(Collectors.toList());
    }

    public List<ProductoImagen> buscarImagenesAnterioresA(LocalDateTime fecha) {
        return productoImagenRepository.findAll().stream()
                .filter(img -> img.getCreatedAt() != null && img.getCreatedAt().isBefore(fecha))
                .collect(Collectors.toList());
    }

    // === OPERACIONES DE MANTENIMIENTO ===

    @Transactional
    public int limpiarImagenesHuerfanas() {
        log.info("🧹 Limpiando imágenes huérfanas");

        List<ProductoImagen> imagenesHuerfanas = productoImagenRepository.findAll().stream()
                .filter(img -> img.getProducto() == null)
                .collect(Collectors.toList());

        if (!imagenesHuerfanas.isEmpty()) {
            productoImagenRepository.deleteAll(imagenesHuerfanas);
            log.info("✅ {} imágenes huérfanas eliminadas", imagenesHuerfanas.size());
        }

        return imagenesHuerfanas.size();
    }

    @Transactional
    public int asegurarImagenesPrincipales() {
        log.info("🎯 Asegurando que cada producto tenga una imagen principal");

        List<Producto> productos = productoRepository.findAll();
        int productosActualizados = 0;

        for (Producto producto : productos) {
            if (!productoTieneImagenPrincipal(producto.getProductoId())) {
                List<ProductoImagen> imagenesActivas = obtenerImagenesActivas(producto.getProductoId());
                if (!imagenesActivas.isEmpty()) {
                    ProductoImagen primerImagen = imagenesActivas.get(0);
                    primerImagen.setEsPrincipal(true);
                    primerImagen.setUpdatedAt(LocalDateTime.now());
                    productoImagenRepository.save(primerImagen);
                    productosActualizados++;
                    log.debug("✅ Imagen principal establecida para producto: {}", producto.getProductoId());
                }
            }
        }

        log.info("✅ {} productos con imagen principal asegurada", productosActualizados);
        return productosActualizados;
    }

    // === REPORTES Y ANÁLISIS ===

    public Map<String, Object> generarReporteCompleto() {
        log.info("📊 Generando reporte completo de imágenes");

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("timestamp", LocalDateTime.now());
        reporte.put("total", contarImagenes());
        reporte.put("activas", contarImagenesActivas());
        reporte.put("principales", contarImagenesPrincipales());
        reporte.put("porTipo", obtenerEstadisticasPorTipo());
        reporte.put("porProducto", obtenerEstadisticasPorProducto());
        reporte.put("productosSinImagenes", contarProductosSinImagenes());
        reporte.put("imagenesHuerfanas", limpiarImagenesHuerfanas());

        log.info("✅ Reporte de imágenes generado");
        return reporte;
    }

    public List<String> validarIntegridadImagenes() {
        List<String> problemas = new ArrayList<>();

        // Verificar productos sin imagen principal
        long sinPrincipal = contarProductosSinImagenes();
        if (sinPrincipal > 0) {
            problemas.add(sinPrincipal + " productos sin imagen principal");
        }

        // Verificar productos sin imágenes
        long sinImagenes = contarProductosSinImagenes();
        if (sinImagenes > 0) {
            problemas.add(sinImagenes + " productos sin imágenes");
        }

        return problemas;
    }
}
