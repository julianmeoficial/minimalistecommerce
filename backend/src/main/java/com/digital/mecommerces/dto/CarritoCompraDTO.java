package com.digital.mecommerces.dto;

import com.digital.mecommerces.model.CarritoCompra;
import com.digital.mecommerces.model.CarritoItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para CarritoCompra
 * Sistema medbcommerce 3.0 - COMPLETO
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarritoCompraDTO {

    private Long carritoId;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private List<CarritoItemDTO> items;
    private Double total;
    private Double totalEstimado;
    private Integer totalItems;
    private Integer cantidadItems;
    private Boolean estaVacio;
    private Boolean puedeComprar;
    private String mensaje;
    private String estadoCarrito;
    private Double descuentoAplicado;
    private Double impuestos;
    private Double costoEnvio;
    private String moneda;

    // === CONSTRUCTORES ===

    public CarritoCompraDTO(Long carritoId, Long usuarioId, Boolean activo, LocalDateTime fechaCreacion) {
        this.carritoId = carritoId;
        this.usuarioId = usuarioId;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.items = new ArrayList<>();
        this.total = 0.0;
        this.totalItems = 0;
        this.estaVacio = true;
        this.puedeComprar = false;
        this.moneda = "USD";
    }

    // === MÉTODOS ESTÁTICOS DE CONVERSIÓN ===

    public static CarritoCompraDTO fromEntity(CarritoCompra carrito) {
        if (carrito == null) return null;

        CarritoCompraDTO dto = new CarritoCompraDTO();
        dto.setCarritoId(carrito.getCarritoId());
        dto.setUsuarioId(carrito.getUsuario().getUsuarioId());
        dto.setUsuarioNombre(carrito.getUsuario().getUsuarioNombre());
        dto.setUsuarioEmail(carrito.getUsuario().getEmail());
        dto.setActivo(carrito.getActivo());
        dto.setFechaCreacion(carrito.getFechaCreacion()); // ✅ CORREGIDO: usar getFechaCreacion()

        // Procesar items del carrito
        if (carrito.getItems() != null && !carrito.getItems().isEmpty()) {
            dto.setItems(carrito.getItems().stream()
                    .map(CarritoItemDTO::fromEntity)
                    .collect(Collectors.toList()));

            dto.setTotalItems(carrito.getItems().size());
            dto.setCantidadItems(carrito.getItems().stream()
                    .mapToInt(item -> item.getCantidad() != null ? item.getCantidad() : 0)
                    .sum());
            dto.setEstaVacio(false);
        } else {
            dto.setItems(new ArrayList<>());
            dto.setTotalItems(0);
            dto.setCantidadItems(0);
            dto.setEstaVacio(true);
        }

        // Calcular totales
        dto.calcularTotales();

        // Determinar estado del carrito
        dto.determinarEstadoCarrito();

        return dto;
    }

    // ✅ MÉTODO toSimple() AGREGADO - COMPLETO
    public CarritoCompraDTO toSimple() {
        CarritoCompraDTO simple = new CarritoCompraDTO();
        simple.setCarritoId(this.carritoId);
        simple.setUsuarioId(this.usuarioId);
        simple.setActivo(this.activo);
        simple.setFechaCreacion(this.fechaCreacion);
        simple.setTotal(this.total);
        simple.setTotalEstimado(this.totalEstimado);
        simple.setTotalItems(this.totalItems);
        simple.setCantidadItems(this.cantidadItems);
        simple.setEstaVacio(this.estaVacio);
        simple.setPuedeComprar(this.puedeComprar);
        simple.setMensaje(this.mensaje);
        simple.setEstadoCarrito(this.estadoCarrito);
        simple.setMoneda(this.moneda);
        // NO incluir items para versión simplificada
        simple.setItems(new ArrayList<>());
        return simple;
    }

    // ✅ MÉTODO toPublic() AGREGADO - COMPLETO
    public CarritoCompraDTO toPublic() {
        CarritoCompraDTO publicDto = new CarritoCompraDTO();
        publicDto.setCarritoId(this.carritoId);
        publicDto.setActivo(this.activo);
        publicDto.setFechaCreacion(this.fechaCreacion);
        publicDto.setTotal(this.total);
        publicDto.setTotalEstimado(this.totalEstimado);
        publicDto.setTotalItems(this.totalItems);
        publicDto.setCantidadItems(this.cantidadItems);
        publicDto.setEstaVacio(this.estaVacio);
        publicDto.setPuedeComprar(this.puedeComprar);
        publicDto.setMensaje(this.mensaje);
        publicDto.setEstadoCarrito(this.estadoCarrito);
        publicDto.setDescuentoAplicado(this.descuentoAplicado);
        publicDto.setImpuestos(this.impuestos);
        publicDto.setCostoEnvio(this.costoEnvio);
        publicDto.setMoneda(this.moneda);

        if (this.items != null && !this.items.isEmpty()) {
            publicDto.setItems(this.items.stream()
                    .map(CarritoItemDTO::toPublic)
                    .collect(Collectors.toList()));
        } else {
            publicDto.setItems(new ArrayList<>());
        }

        return publicDto;
    }

    public CarritoCompraDTO toMinimal() {
        CarritoCompraDTO minimal = new CarritoCompraDTO();
        minimal.setCarritoId(this.carritoId);
        minimal.setTotalItems(this.totalItems);
        minimal.setTotal(this.total);
        minimal.setEstaVacio(this.estaVacio);
        minimal.setMoneda(this.moneda);
        return minimal;
    }

    // === MÉTODOS DE CÁLCULO ===

    public void calcularTotales() {
        if (this.items == null || this.items.isEmpty()) {
            this.total = 0.0;
            this.totalEstimado = 0.0;
            this.descuentoAplicado = 0.0;
            this.impuestos = 0.0;
            this.costoEnvio = 0.0;
            return;
        }

        // Calcular subtotal de items
        double subtotal = this.items.stream()
                .mapToDouble(item -> {
                    if (item.getPrecioUnitario() != null && item.getCantidad() != null) {
                        return item.getPrecioUnitario() * item.getCantidad();
                    }
                    return 0.0;
                })
                .sum();

        // Aplicar descuentos (si los hay)
        this.descuentoAplicado = this.descuentoAplicado != null ? this.descuentoAplicado : 0.0;
        double subtotalConDescuento = subtotal - this.descuentoAplicado;

        // Calcular impuestos (ejemplo: 10%)
        this.impuestos = subtotalConDescuento * 0.10;

        // Calcular costo de envío (ejemplo: gratis si > $50)
        this.costoEnvio = subtotalConDescuento > 50.0 ? 0.0 : 5.99;

        // Total final
        this.total = subtotalConDescuento + this.impuestos + this.costoEnvio;
        this.totalEstimado = this.total;
    }

    public void determinarEstadoCarrito() {
        if (this.items == null || this.items.isEmpty()) {
            this.estadoCarrito = "VACIO";
            this.puedeComprar = false;
            this.mensaje = "El carrito está vacío";
            return;
        }

        // Verificar disponibilidad de productos
        boolean todosDisponibles = this.items.stream()
                .allMatch(item -> Boolean.TRUE.equals(item.getDisponible()));

        if (!todosDisponibles) {
            this.estadoCarrito = "CON_PRODUCTOS_NO_DISPONIBLES";
            this.puedeComprar = false;
            this.mensaje = "Algunos productos no están disponibles";
            return;
        }

        // Verificar stock suficiente
        boolean stockSuficiente = this.items.stream()
                .allMatch(item -> {
                    if (item.getStockDisponible() != null && item.getCantidad() != null) {
                        return item.getStockDisponible() >= item.getCantidad();
                    }
                    return true; // Si no hay info de stock, asumir que está bien
                });

        if (!stockSuficiente) {
            this.estadoCarrito = "STOCK_INSUFICIENTE";
            this.puedeComprar = false;
            this.mensaje = "Stock insuficiente para algunos productos";
            return;
        }

        // Todo está bien
        this.estadoCarrito = "LISTO_PARA_COMPRAR";
        this.puedeComprar = true;
        this.mensaje = "Carrito listo para proceder al checkout";
    }

    // === MÉTODOS DE UTILIDAD ===

    public boolean tieneItems() {
        return this.items != null && !this.items.isEmpty();
    }

    public boolean esValido() {
        return Boolean.TRUE.equals(this.puedeComprar) && this.tieneItems();
    }

    public int obtenerCantidadTotalProductos() {
        if (this.items == null) return 0;
        return this.items.stream()
                .mapToInt(item -> item.getCantidad() != null ? item.getCantidad() : 0)
                .sum();
    }

    public List<CarritoItemDTO> obtenerItemsDisponibles() {
        if (this.items == null) return new ArrayList<>();
        return this.items.stream()
                .filter(item -> Boolean.TRUE.equals(item.getDisponible()))
                .collect(Collectors.toList());
    }

    public Double obtenerSubtotal() {
        if (this.items == null) return 0.0;
        return this.items.stream()
                .mapToDouble(item -> {
                    if (item.getPrecioUnitario() != null && item.getCantidad() != null) {
                        return item.getPrecioUnitario() * item.getCantidad();
                    }
                    return 0.0;
                })
                .sum();
    }

    public String obtenerResumenCarrito() {
        if (this.estaVacio) {
            return "Carrito vacío";
        }
        return String.format("%d item(s) - Total: $%.2f %s",
                this.cantidadItems, this.total, this.moneda);
    }

    // === MÉTODOS DE VALIDACIÓN ===

    public boolean validarLimitesCarrito() {
        // Máximo 50 items por carrito
        if (this.cantidadItems != null && this.cantidadItems > 50) {
            return false;
        }

        // Máximo $10,000 por carrito
        if (this.total != null && this.total > 10000.0) {
            return false;
        }

        return true;
    }

    public List<String> obtenerMensajesValidacion() {
        List<String> mensajes = new ArrayList<>();

        if (this.estaVacio) {
            mensajes.add("El carrito está vacío");
        }

        if (!validarLimitesCarrito()) {
            if (this.cantidadItems > 50) {
                mensajes.add("Máximo 50 items permitidos por carrito");
            }
            if (this.total > 10000.0) {
                mensajes.add("El total del carrito no puede exceder $10,000");
            }
        }

        return mensajes;
    }

    // === MÉTODOS PARA COMPARACIÓN Y ORDENAMIENTO ===

    public static java.util.Comparator<CarritoCompraDTO> porFechaCreacion() {
        return (c1, c2) -> {
            if (c1.getFechaCreacion() == null && c2.getFechaCreacion() == null) return 0;
            if (c1.getFechaCreacion() == null) return 1;
            if (c2.getFechaCreacion() == null) return -1;
            return c2.getFechaCreacion().compareTo(c1.getFechaCreacion()); // Más reciente primero
        };
    }

    public static java.util.Comparator<CarritoCompraDTO> porTotal() {
        return (c1, c2) -> {
            Double total1 = c1.getTotal() != null ? c1.getTotal() : 0.0;
            Double total2 = c2.getTotal() != null ? c2.getTotal() : 0.0;
            return total2.compareTo(total1); // Mayor total primero
        };
    }

    // === GETTERS Y SETTERS ADICIONALES ===

    public void setItems(List<CarritoItemDTO> items) {
        this.items = items != null ? items : new ArrayList<>();
        // Recalcular totales cuando se establecen nuevos items
        this.calcularTotales();
        this.determinarEstadoCarrito();
    }

    @Override
    public String toString() {
        return String.format("CarritoCompraDTO{carritoId=%d, usuarioId=%d, items=%d, total=%.2f, estado='%s'}",
                carritoId, usuarioId,
                totalItems != null ? totalItems : 0,
                total != null ? total : 0.0,
                estadoCarrito);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarritoCompraDTO that = (CarritoCompraDTO) o;
        return carritoId != null && carritoId.equals(that.carritoId);
    }

    @Override
    public int hashCode() {
        return carritoId != null ? carritoId.hashCode() : 0;
    }
}
