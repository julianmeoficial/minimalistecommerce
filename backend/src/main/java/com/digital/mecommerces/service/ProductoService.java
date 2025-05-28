package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.Producto;
import com.digital.mecommerces.repository.ProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestión de productos - MÉTODOS ADICIONALES
 * Estos métodos se agregan al ProductoService existente
 */
@Service
@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // === MÉTODOS PARA VENDEDORES ESPECÍFICOS ===

    public long contarProductosPorVendedor(Long vendedorId) {
        log.info("📊 Contando productos del vendedor ID: {}", vendedorId);
        return productoRepository.findByVendedorUsuarioIdAndActivoTrue(vendedorId).size();
    }

    public long contarProductosActivosPorVendedor(Long vendedorId) {
        log.info("📊 Contando productos activos del vendedor ID: {}", vendedorId);
        return productoRepository.findByVendedorUsuarioIdAndActivoTrue(vendedorId).size();
    }

    public long contarProductosAgotadosPorVendedor(Long vendedorId) {
        log.info("📊 Contando productos agotados del vendedor ID: {}", vendedorId);
        return productoRepository.findByVendedorUsuarioIdAndActivoTrue(vendedorId).stream()
                .filter(p -> p.getStock() <= 0)
                .count();
    }

    public long contarProductosConPocoStockPorVendedor(Long vendedorId) {
        log.info("📊 Contando productos con poco stock del vendedor ID: {}", vendedorId);
        return productoRepository.findByVendedorUsuarioIdAndActivoTrue(vendedorId).stream()
                .filter(p -> p.getStock() > 0 && p.getStock() <= 5)
                .count();
    }

    public long obtenerStockTotalPorVendedor(Long vendedorId) {
        log.info("📊 Obteniendo stock total del vendedor ID: {}", vendedorId);
        return productoRepository.findByVendedorUsuarioIdAndActivoTrue(vendedorId).stream()
                .mapToLong(p -> p.getStock() != null ? p.getStock() : 0)
                .sum();
    }

    public double obtenerPrecioPromedioPorVendedor(Long vendedorId) {
        log.info("📊 Obteniendo precio promedio del vendedor ID: {}", vendedorId);
        List<Producto> productos = productoRepository.findByVendedorUsuarioIdAndActivoTrue(vendedorId);
        if (productos.isEmpty()) {
            return 0.0;
        }
        return productos.stream()
                .mapToDouble(p -> p.getPrecio() != null ? p.getPrecio() : 0.0)
                .average()
                .orElse(0.0);
    }

    public double obtenerValorInventarioPorVendedor(Long vendedorId) {
        log.info("📊 Calculando valor de inventario del vendedor ID: {}", vendedorId);
        List<Producto> productos = productoRepository.findByVendedorUsuarioIdAndActivoTrue(vendedorId);

        return productos.stream()
                .mapToDouble(p -> (p.getPrecio() != null ? p.getPrecio() : 0.0) *
                        (p.getStock() != null ? p.getStock() : 0))
                .sum();
    }

    public Map<String, Object> obtenerEstadisticasPorCategoriaVendedor(Long vendedorId) {
        log.info("📊 Obteniendo estadísticas por categoría del vendedor ID: {}", vendedorId);

        // Implementación alternativa usando los métodos disponibles
        List<Producto> productos = productoRepository.findByVendedorUsuarioIdAndActivoTrue(vendedorId);
        Map<String, Object> estadisticas = new HashMap<>();

        // Agrupar productos por categoría y contar
        Map<String, Long> conteo = productos.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        p -> p.getCategoria().getNombre(),
                        java.util.stream.Collectors.counting()
                ));

        // Convertir a Map<String, Object>
        conteo.forEach(estadisticas::put);

        return estadisticas;
    }

    // === MÉTODOS PARA ESTADÍSTICAS GENERALES ===

    public long contarProductos() {
        log.info("📊 Contando todos los productos");
        return productoRepository.count();
    }

    public long contarProductosActivos() {
        log.info("📊 Contando productos activos");
        return productoRepository.findByActivoTrue().size();
    }

    public long contarProductosDestacados() {
        log.info("📊 Contando productos destacados");
        return productoRepository.findByDestacadoTrueAndActivoTrue().size();
    }

    public long contarProductosAgotados() {
        log.info("📊 Contando productos agotados");
        return productoRepository.findByActivoTrue().stream()
                .filter(p -> p.getStock() <= 0)
                .count();
    }

    public long obtenerStockTotal() {
        log.info("📊 Obteniendo stock total del sistema");
        return productoRepository.findByActivoTrue().stream()
                .mapToLong(p -> p.getStock() != null ? p.getStock() : 0)
                .sum();
    }

    public double obtenerPrecioPromedio() {
        log.info("📊 Obteniendo precio promedio del sistema");
        List<Producto> productos = productoRepository.findByActivoTrue();
        if (productos.isEmpty()) {
            return 0.0;
        }
        return productos.stream()
                .mapToDouble(p -> p.getPrecio() != null ? p.getPrecio() : 0.0)
                .average()
                .orElse(0.0);
    }

    public Map<String, Object> obtenerEstadisticasPorCategoria() {
        log.info("📊 Obteniendo estadísticas por categoría");

        // Implementación alternativa usando los métodos disponibles
        List<Producto> productos = productoRepository.findByActivoTrue();
        Map<String, Object> estadisticas = new HashMap<>();

        // Agrupar productos por categoría y contar
        Map<String, Long> conteo = productos.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        p -> p.getCategoria().getNombre(),
                        java.util.stream.Collectors.counting()
                ));

        // Convertir a Map<String, Object>
        conteo.forEach(estadisticas::put);

        return estadisticas;
    }

    public Map<String, Object> obtenerEstadisticasPorVendedor() {
        log.info("📊 Obteniendo estadísticas por vendedor");

        // Implementación alternativa usando los métodos disponibles
        List<Producto> productos = productoRepository.findByActivoTrue();
        Map<String, Object> estadisticas = new HashMap<>();

        // Agrupar productos por vendedor y contar
        Map<String, Long> conteo = productos.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        p -> p.getVendedor().getUsuarioNombre(),
                        java.util.stream.Collectors.counting()
                ));

        // Convertir a Map<String, Object>
        conteo.forEach(estadisticas::put);

        return estadisticas;
    }

    // === MÉTODOS PARA BÚSQUEDAS Y FILTROS ===

    public Page<Producto> buscarProductos(String query, Pageable pageable) {
        log.info("🔍 Buscando productos con término: {}", query);

        if (query == null || query.trim().isEmpty()) {
            return obtenerProductosActivos(pageable);
        }

        String searchTerm = "%" + query.trim().toLowerCase() + "%";
        return productoRepository.findByTextoEnNombreODescripcion(query, pageable);
    }

    public Page<Producto> filtrarProductos(Double precioMin, Double precioMax,
                                           Long categoriaId, Boolean disponible, Pageable pageable) {
        log.info("🔍 Filtrando productos con criterios múltiples");

        if (precioMin == null) precioMin = 0.0;
        if (precioMax == null) precioMax = Double.MAX_VALUE;
        if (disponible == null) disponible = true;

        // Usar el método de filtros avanzados que ya existe en el repositorio
        return productoRepository.findConFiltrosAvanzados(
                categoriaId, 
                null, // vendedor no especificado
                precioMin, 
                precioMax, 
                disponible, 
                pageable);
    }

    // === MÉTODOS PARA GESTIÓN DE STOCK Y ESTADO ===

    @Transactional
    public void actualizarStock(Long productoId, Integer nuevoStock) {
        log.info("📦 Actualizando stock del producto ID: {} a: {}", productoId, nuevoStock);

        Producto producto = obtenerProductoPorId(productoId);
        producto.setStock(nuevoStock);
        producto.setUpdatedat(LocalDateTime.now());

        productoRepository.save(producto);
        log.info("✅ Stock actualizado exitosamente");
    }

    @Transactional
    public void destacarProducto(Long productoId, Boolean destacado) {
        log.info("⭐ {} producto ID: {}", destacado ? "Destacando" : "Desmarcando", productoId);

        Producto producto = obtenerProductoPorId(productoId);
        producto.setDestacado(destacado);
        producto.setUpdatedat(LocalDateTime.now());

        productoRepository.save(producto);
        log.info("✅ Estado destacado actualizado");
    }

    // === MÉTODOS PARA VENTAS (SIMULADAS PARA DESARROLLO) ===

    public List<Map<String, Object>> obtenerVentasPorVendedor(Long vendedorId) {
        log.info("💰 Obteniendo ventas del vendedor ID: {}", vendedorId);

        // Por ahora retornamos datos simulados hasta que se implemente el módulo de órdenes
        return List.of(
                Map.of(
                        "fecha", LocalDateTime.now().minusDays(1),
                        "producto", "Producto Demo",
                        "cantidad", 2,
                        "total", 150.00,
                        "estado", "COMPLETADA"
                ),
                Map.of(
                        "fecha", LocalDateTime.now().minusDays(3),
                        "producto", "Otro Producto",
                        "cantidad", 1,
                        "total", 75.00,
                        "estado", "PENDIENTE"
                )
        );
    }

    public long contarVentasPorVendedor(Long vendedorId) {
        log.info("💰 Contando ventas del vendedor ID: {}", vendedorId);
        // Implementar cuando se tenga el módulo de órdenes
        return 0L;
    }

    public double obtenerIngresosTotalesPorVendedor(Long vendedorId) {
        log.info("💰 Obteniendo ingresos totales del vendedor ID: {}", vendedorId);
        // Implementar cuando se tenga el módulo de órdenes
        return 0.0;
    }

    public double obtenerPromedioVentaPorVendedor(Long vendedorId) {
        log.info("💰 Obteniendo promedio de venta del vendedor ID: {}", vendedorId);
        // Implementar cuando se tenga el módulo de órdenes
        return 0.0;
    }

    public long contarVentasHoyPorVendedor(Long vendedorId) {
        log.info("💰 Contando ventas de hoy del vendedor ID: {}", vendedorId);
        // Implementar cuando se tenga el módulo de órdenes
        return 0L;
    }

    public long contarVentasMesPorVendedor(Long vendedorId) {
        log.info("💰 Contando ventas del mes del vendedor ID: {}", vendedorId);
        // Implementar cuando se tenga el módulo de órdenes
        return 0L;
    }

    public Map<String, Object> obtenerProductoMasVendidoPorVendedor(Long vendedorId) {
        log.info("💰 Obteniendo producto más vendido del vendedor ID: {}", vendedorId);
        // Implementar cuando se tenga el módulo de órdenes
        return Map.of("mensaje", "Módulo de ventas en desarrollo");
    }

    public List<Map<String, Object>> obtenerClientesFrecuentesPorVendedor(Long vendedorId) {
        log.info("💰 Obteniendo clientes frecuentes del vendedor ID: {}", vendedorId);
        // Implementar cuando se tenga el módulo de órdenes
        return List.of();
    }

    public List<Map<String, Object>> obtenerTopProductosVendedor(Long vendedorId, int limite) {
        log.info("📊 Obteniendo top {} productos del vendedor ID: {}", limite, vendedorId);

        // Usar el método existente y luego limitar los resultados
        List<Producto> todosProductos = productoRepository.findByVendedorUsuarioIdAndActivoTrue(vendedorId);

        // Ordenar por fecha de creación descendente (más recientes primero)
        List<Producto> productos = todosProductos.stream()
                .sorted((p1, p2) -> p2.getCreatedat().compareTo(p1.getCreatedat()))
                .limit(limite)
                .collect(java.util.stream.Collectors.toList());

        // Crear una lista mutable para evitar problemas de tipo
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Producto p : productos) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getProductoId());
            map.put("nombre", p.getProductoNombre());
            map.put("precio", p.getPrecio());
            map.put("stock", p.getStock());
            map.put("categoria", p.getCategoria().getNombre());
            resultado.add(map);
        }

        return resultado;
    }

    public long contarVentasPendientesPorVendedor(Long vendedorId) {
        log.info("💰 Contando ventas pendientes del vendedor ID: {}", vendedorId);
        // Implementar cuando se tenga el módulo de órdenes
        return 0L;
    }

    public List<Object[]> obtenerProductosMasVendidos() {
        log.info("📊 Obteniendo productos más vendidos del sistema");
        // Implementar cuando se tenga el módulo de órdenes
        return List.of();
    }

    // === MÉTODOS ADICIONALES PARA PRODUCTOS ===

    public List<Producto> obtenerProductosRecientes() {
        log.info("🆕 Obteniendo productos recientes");
        return productoRepository.findProductosActivosRecientes();
    }

    public List<Producto> obtenerProductosDestacados() {
        log.info("⭐ Obteniendo productos destacados");
        return productoRepository.findByDestacadoTrueAndActivoTrue();
    }

    public Producto obtenerProductoPorSlug(String slug) {
        log.info("🔍 Obteniendo producto por slug: {}", slug);
        return productoRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con slug: " + slug));
    }

    public Producto obtenerProductoPorId(Long productoId) {
        log.info("🔍 Obteniendo producto por ID: {}", productoId);
        return productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));
    }

    // Este método ya existe en el servicio original, solo verificamos que esté
    public Page<Producto> obtenerProductosActivos(Pageable pageable) {
        log.info("📋 Obteniendo productos activos con paginación");
        return productoRepository.findByActivoTrue(pageable);
    }
}
