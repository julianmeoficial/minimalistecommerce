package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para gestión de productos del sistema
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    @JsonProperty("productoId")
    private Long productoId;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @JsonProperty("productoNombre")
    private String productoNombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @JsonProperty("descripcion")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    @DecimalMax(value = "999999.99", message = "El precio no puede exceder $999,999.99")
    @JsonProperty("precio")
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock debe ser mayor o igual a cero")
    @Max(value = 999999, message = "El stock no puede exceder 999,999 unidades")
    @JsonProperty("stock")
    private Integer stock;

    @NotNull(message = "La categoría del producto es obligatoria")
    @JsonProperty("categoriaId")
    private Long categoriaId;

    @JsonProperty("categoriaNombre")
    private String categoriaNombre;

    @NotNull(message = "El vendedor es obligatorio")
    @JsonProperty("vendedorId")
    private Long vendedorId;

    @JsonProperty("vendedorNombre")
    private String vendedorNombre;

    @JsonProperty("vendedorEmail")
    private String vendedorEmail;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("activo")
    private Boolean activo = true;

    @JsonProperty("destacado")
    private Boolean destacado = false;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @JsonProperty("imagenes")
    private List<ProductoImagenDTO> imagenes = new ArrayList<>();

    // Campos calculados
    @JsonProperty("imagenPrincipal")
    private String imagenPrincipal;

    @JsonProperty("tieneStock")
    private Boolean tieneStock;

    @JsonProperty("estadoStock")
    private String estadoStock;

    @JsonProperty("diasDesdeCreacion")
    private Long diasDesdeCreacion;

    @JsonProperty("esNuevo")
    private Boolean esNuevo;

    @JsonProperty("precioFormateado")
    private String precioFormateado;

    // Constructor básico para creación
    public ProductoDTO(String productoNombre, String descripcion, Double precio,
                       Integer stock, Long categoriaId, Long vendedorId) {
        this.productoNombre = productoNombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoriaId = categoriaId;
        this.vendedorId = vendedorId;
        this.activo = true;
        this.destacado = false;
        this.imagenes = new ArrayList<>();
    }

    // Constructor completo para listado
    public ProductoDTO(Long productoId, String productoNombre, String descripcion,
                       Double precio, Integer stock, Long categoriaId, String categoriaNombre,
                       Long vendedorId, String vendedorNombre, Boolean activo) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.vendedorId = vendedorId;
        this.vendedorNombre = vendedorNombre;
        this.activo = activo;
        this.imagenes = new ArrayList<>();
    }

    // Métodos de validación
    public boolean isValid() {
        return productoNombre != null && !productoNombre.trim().isEmpty() &&
                precio != null && precio > 0 &&
                stock != null && stock >= 0 &&
                categoriaId != null && categoriaId > 0 &&
                vendedorId != null && vendedorId > 0;
    }

    public boolean tieneImagenes() {
        return imagenes != null && !imagenes.isEmpty();
    }

    public boolean tieneImagenPrincipal() {
        return imagenes != null && imagenes.stream()
                .anyMatch(img -> Boolean.TRUE.equals(img.getEsPrincipal()));
    }

    // Métodos de negocio
    public void calcularCamposDerivados() {
        // Calcular si tiene stock
        this.tieneStock = stock != null && stock > 0;

        // Calcular estado del stock
        if (stock == null || stock == 0) {
            this.estadoStock = "AGOTADO";
        } else if (stock <= 5) {
            this.estadoStock = "POCO_STOCK";
        } else if (stock <= 20) {
            this.estadoStock = "STOCK_BAJO";
        } else {
            this.estadoStock = "DISPONIBLE";
        }

        // Calcular días desde creación
        if (createdAt != null) {
            this.diasDesdeCreacion = java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
            this.esNuevo = this.diasDesdeCreacion <= 30; // Nuevo si tiene menos de 30 días
        }

        // Formatear precio
        if (precio != null) {
            this.precioFormateado = String.format("$%.2f", precio);
        }

        // Obtener imagen principal
        if (imagenes != null) {
            this.imagenPrincipal = imagenes.stream()
                    .filter(img -> Boolean.TRUE.equals(img.getEsPrincipal()))
                    .map(ProductoImagenDTO::getUrl)
                    .findFirst()
                    .orElse(imagenes.isEmpty() ? null : imagenes.get(0).getUrl());
        }
    }

    public String obtenerDescripcionCorta(int maxCaracteres) {
        if (descripcion == null || descripcion.length() <= maxCaracteres) {
            return descripcion;
        }
        return descripcion.substring(0, maxCaracteres) + "...";
    }

    public boolean esProductoEconomico() {
        return precio != null && precio < 50.0;
    }

    public boolean esProductoPremium() {
        return precio != null && precio > 200.0;
    }

    public boolean requiereReabastecimiento() {
        return stock != null && stock <= 5;
    }

    public double calcularValorInventario() {
        if (precio == null || stock == null) {
            return 0.0;
        }
        return precio * stock;
    }

    // Métodos para manejo de imágenes
    public void agregarImagen(ProductoImagenDTO imagen) {
        if (this.imagenes == null) {
            this.imagenes = new ArrayList<>();
        }
        this.imagenes.add(imagen);
    }

    public void establecerImagenPrincipal(String url) {
        if (imagenes != null) {
            // Quitar el estado principal de todas las imágenes
            imagenes.forEach(img -> img.setEsPrincipal(false));

            // Establecer como principal la imagen con la URL especificada
            imagenes.stream()
                    .filter(img -> url.equals(img.getUrl()))
                    .findFirst()
                    .ifPresent(img -> img.setEsPrincipal(true));
        }
    }

    public List<ProductoImagenDTO> obtenerImagenesSecundarias() {
        if (imagenes == null) {
            return new ArrayList<>();
        }
        return imagenes.stream()
                .filter(img -> !Boolean.TRUE.equals(img.getEsPrincipal()))
                .toList();
    }

    // Método para crear desde entidad
    public static ProductoDTO fromEntity(com.digital.mecommerces.model.Producto producto) {
        if (producto == null) return null;

        ProductoDTO dto = new ProductoDTO();
        dto.setProductoId(producto.getProductoId());
        dto.setProductoNombre(producto.getProductoNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setSlug(producto.getSlug());
        dto.setActivo(producto.getActivo());
        dto.setDestacado(producto.getDestacado());
        dto.setCreatedAt(producto.getCreatedat());
        dto.setUpdatedAt(producto.getUpdatedat());

        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getCategoriaId());
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }

        if (producto.getVendedor() != null) {
            dto.setVendedorId(producto.getVendedor().getUsuarioId());
            dto.setVendedorNombre(producto.getVendedor().getUsuarioNombre());
            dto.setVendedorEmail(producto.getVendedor().getEmail());
        }

        // Convertir imágenes
        if (producto.getImagenes() != null) {
            List<ProductoImagenDTO> imagenesDto = producto.getImagenes().stream()
                    .map(ProductoImagenDTO::fromEntity)
                    .toList();
            dto.setImagenes(imagenesDto);
        }

        // Calcular campos derivados
        dto.calcularCamposDerivados();

        return dto;
    }

    // Método para crear versión simplificada (para listados)
    public ProductoDTO toSimple() {
        ProductoDTO simpleDto = new ProductoDTO();
        simpleDto.setProductoId(this.productoId);
        simpleDto.setProductoNombre(this.productoNombre);
        simpleDto.setPrecio(this.precio);
        simpleDto.setStock(this.stock);
        simpleDto.setCategoriaNombre(this.categoriaNombre);
        simpleDto.setVendedorNombre(this.vendedorNombre);
        simpleDto.setActivo(this.activo);
        simpleDto.setDestacado(this.destacado);
        simpleDto.setImagenPrincipal(this.imagenPrincipal);
        simpleDto.setTieneStock(this.tieneStock);
        simpleDto.setEstadoStock(this.estadoStock);
        simpleDto.setPrecioFormateado(this.precioFormateado);
        simpleDto.setEsNuevo(this.esNuevo);
        return simpleDto;
    }

    // Método para crear versión pública (sin información del vendedor sensible)
    public ProductoDTO toPublic() {
        ProductoDTO publicDto = new ProductoDTO();
        publicDto.setProductoId(this.productoId);
        publicDto.setProductoNombre(this.productoNombre);
        publicDto.setDescripcion(this.descripcion);
        publicDto.setPrecio(this.precio);
        publicDto.setStock(this.stock);
        publicDto.setCategoriaId(this.categoriaId);
        publicDto.setCategoriaNombre(this.categoriaNombre);
        publicDto.setVendedorNombre(this.vendedorNombre); // Solo nombre, no email
        publicDto.setSlug(this.slug);
        publicDto.setActivo(this.activo);
        publicDto.setDestacado(this.destacado);
        publicDto.setImagenes(this.imagenes);
        publicDto.calcularCamposDerivados();
        return publicDto;
    }

    @Override
    public String toString() {
        return "ProductoDTO{" +
                "productoId=" + productoId +
                ", productoNombre='" + productoNombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", categoriaNombre='" + categoriaNombre + '\'' +
                ", activo=" + activo +
                ", tieneStock=" + tieneStock +
                '}';
    }
}
