package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para items del carrito de compras (CarritoItem)
 * Optimizado para el sistema medbcommerce 3.0 - CORREGIDO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemDTO {

    @JsonProperty("itemId")
    private Long itemId;

    @JsonProperty("carritoCompraId")
    @NotNull(message = "El carrito de compra es obligatorio")
    private Long carritoCompraId;

    @JsonProperty("productoId")
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @JsonProperty("productoNombre")
    private String productoNombre;

    @JsonProperty("productoSlug")
    private String productoSlug;

    @JsonProperty("imagenPrincipal")
    private String imagenPrincipal;

    @JsonProperty("cantidad")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @JsonProperty("precioUnitario")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precioUnitario;

    @JsonProperty("subtotal")
    private Double subtotal;

    @JsonProperty("disponible")
    private Boolean disponible = true;

    @JsonProperty("stockDisponible")
    @PositiveOrZero(message = "El stock disponible no puede ser negativo")
    private Integer stockDisponible;

    @JsonProperty("guardadoDespues")
    private Boolean guardadoDespues = false;

    @JsonProperty("fechaAgregado")
    private LocalDateTime fechaAgregado;

    @JsonProperty("fechaActualizado")
    private LocalDateTime fechaActualizado;

    // Información adicional del producto
    @JsonProperty("categoriaNombre")
    private String categoriaNombre;

    @JsonProperty("vendedorNombre")
    private String vendedorNombre;

    @JsonProperty("vendedorId")
    private Long vendedorId;

    // Campos calculados
    @JsonProperty("puedeSerComprado")
    private Boolean puedeSerComprado;

    @JsonProperty("requiereActualizacion")
    private Boolean requiereActualizacion;

    @JsonProperty("mensaje")
    private String mensaje;

    @JsonProperty("descuentoAplicado")
    private Double descuentoAplicado = 0.0;

    @JsonProperty("subtotalConDescuento")
    private Double subtotalConDescuento;

    // Constructor básico para creación
    public CarritoItemDTO(Long productoId, Integer cantidad, Double precioUnitario) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.fechaAgregado = LocalDateTime.now();
        this.disponible = true;
        this.guardadoDespues = false;
        this.descuentoAplicado = 0.0;
        calcularSubtotales();
    }

    // Constructor con carrito
    public CarritoItemDTO(Long carritoCompraId, Long productoId, Integer cantidad, Double precioUnitario) {
        this.carritoCompraId = carritoCompraId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.fechaAgregado = LocalDateTime.now();
        this.disponible = true;
        this.guardadoDespues = false;
        this.descuentoAplicado = 0.0;
        calcularSubtotales();
    }

    // Métodos de validación
    public boolean isValid() {
        return productoId != null && productoId > 0 &&
                cantidad != null && cantidad > 0 &&
                precioUnitario != null && precioUnitario > 0;
    }

    public boolean estaDisponible() {
        return Boolean.TRUE.equals(disponible) &&
                stockDisponible != null && stockDisponible >= cantidad;
    }

    // Métodos de negocio
    public void calcularSubtotales() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = cantidad * precioUnitario;

            if (descuentoAplicado != null && descuentoAplicado > 0) {
                this.subtotalConDescuento = subtotal - descuentoAplicado;
            } else {
                this.subtotalConDescuento = subtotal;
            }
        } else {
            this.subtotal = 0.0;
            this.subtotalConDescuento = 0.0;
        }
    }

    public void calcularCamposDerivados() {
        calcularSubtotales();

        this.puedeSerComprado = estaDisponible() &&
                !Boolean.TRUE.equals(guardadoDespues) &&
                Boolean.TRUE.equals(disponible);

        this.requiereActualizacion = stockDisponible != null &&
                stockDisponible < cantidad;

        this.mensaje = generarMensaje();
    }

    private String generarMensaje() {
        if (!Boolean.TRUE.equals(disponible)) {
            return "Producto no disponible";
        }

        if (Boolean.TRUE.equals(guardadoDespues)) {
            return "Guardado para después";
        }

        if (stockDisponible != null && stockDisponible == 0) {
            return "Producto agotado";
        }

        if (stockDisponible != null && stockDisponible < cantidad) {
            return "Stock insuficiente. Disponible: " + stockDisponible;
        }

        return "Producto disponible";
    }

    public void actualizarCantidad(Integer nuevaCantidad) {
        if (nuevaCantidad != null && nuevaCantidad > 0) {
            this.cantidad = nuevaCantidad;
            this.fechaActualizado = LocalDateTime.now();
            calcularCamposDerivados();
        }
    }

    public void marcarComoGuardadoDespues() {
        this.guardadoDespues = true;
        this.puedeSerComprado = false;
        this.mensaje = "Guardado para después";
        this.fechaActualizado = LocalDateTime.now();
    }

    // Setter personalizado para carritoId (compatibilidad)
    public void setCarritoId(Long carritoId) {
        this.carritoCompraId = carritoId;
    }

    public Long getCarritoId() {
        return this.carritoCompraId;
    }

    // Método para crear desde entidad CarritoItem
    public static CarritoItemDTO fromEntity(com.digital.mecommerces.model.CarritoItem item) {
        if (item == null) return null;

        CarritoItemDTO dto = new CarritoItemDTO();
        dto.setItemId(item.getItemId());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setGuardadoDespues(item.getGuardadoDespues());
        dto.setFechaAgregado(item.getFechaAgregado());

        if (item.getCarritoCompra() != null) {
            dto.setCarritoCompraId(item.getCarritoCompra().getCarritoId());
        }

        if (item.getProducto() != null) {
            dto.setProductoId(item.getProducto().getProductoId());
            dto.setProductoNombre(item.getProducto().getProductoNombre());
            dto.setProductoSlug(item.getProducto().getSlug());
            dto.setStockDisponible(item.getProducto().getStock());
            dto.setDisponible(item.getProducto().getActivo() &&
                    item.getProducto().getStock() != null &&
                    item.getProducto().getStock() > 0);

            if (item.getProducto().getCategoria() != null) {
                dto.setCategoriaNombre(item.getProducto().getCategoria().getNombre());
            }

            if (item.getProducto().getVendedor() != null) {
                dto.setVendedorId(item.getProducto().getVendedor().getUsuarioId());
                dto.setVendedorNombre(item.getProducto().getVendedor().getUsuarioNombre());
            }

            if (item.getProducto().getImagenes() != null && !item.getProducto().getImagenes().isEmpty()) {
                dto.setImagenPrincipal(item.getProducto().getImagenes().stream()
                        .filter(img -> Boolean.TRUE.equals(img.getEsPrincipal()))
                        .map(com.digital.mecommerces.model.ProductoImagen::getUrl)
                        .findFirst()
                        .orElse(item.getProducto().getImagenes().get(0).getUrl()));
            }
        }

        dto.calcularCamposDerivados();
        return dto;
    }

    /**
     * Método para crear una versión pública del DTO con solo los campos necesarios para el frontend
     * @return Una versión simplificada del DTO con campos públicos
     */
    public static CarritoItemDTO toPublic(CarritoItemDTO item) {
        if (item == null) return null;

        CarritoItemDTO publicDto = new CarritoItemDTO();
        publicDto.setItemId(item.getItemId());
        publicDto.setProductoId(item.getProductoId());
        publicDto.setProductoNombre(item.getProductoNombre());
        publicDto.setProductoSlug(item.getProductoSlug());
        publicDto.setImagenPrincipal(item.getImagenPrincipal());
        publicDto.setCantidad(item.getCantidad());
        publicDto.setPrecioUnitario(item.getPrecioUnitario());
        publicDto.setSubtotal(item.getSubtotal());
        publicDto.setDisponible(item.getDisponible());
        publicDto.setStockDisponible(item.getStockDisponible());
        publicDto.setCategoriaNombre(item.getCategoriaNombre());
        publicDto.setPuedeSerComprado(item.getPuedeSerComprado());
        publicDto.setMensaje(item.getMensaje());
        publicDto.setDescuentoAplicado(item.getDescuentoAplicado());
        publicDto.setSubtotalConDescuento(item.getSubtotalConDescuento());

        return publicDto;
    }

    @Override
    public String toString() {
        return "CarritoItemDTO{" +
                "itemId=" + itemId +
                ", carritoCompraId=" + carritoCompraId +
                ", productoId=" + productoId +
                ", productoNombre='" + productoNombre + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                ", disponible=" + disponible +
                ", puedeSerComprado=" + puedeSerComprado +
                '}';
    }
}
