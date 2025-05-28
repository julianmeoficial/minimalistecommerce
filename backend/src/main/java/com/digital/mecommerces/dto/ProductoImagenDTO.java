package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para gestión de imágenes de productos
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoImagenDTO {

    @JsonProperty("imagenId")
    private Long imagenId;

    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Size(max = 255, message = "La URL no puede exceder 255 caracteres")
    @JsonProperty("url")
    private String url;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("esPrincipal")
    private Boolean esPrincipal = false;

    @NotNull(message = "El producto es obligatorio")
    @JsonProperty("productoId")
    private Long productoId;

    @JsonProperty("tipo")
    @Size(max = 50, message = "El tipo no puede exceder 50 caracteres")
    private String tipo = "galeria";

    @JsonProperty("orden")
    private Integer orden = 0;

    @JsonProperty("tamanio")
    private Integer tamanio;

    @JsonProperty("activa")
    private Boolean activa = true;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    // Información adicional del producto asociado
    @JsonProperty("productoNombre")
    private String productoNombre;

    @JsonProperty("productoSlug")
    private String productoSlug;

    // Constructor básico para creación
    public ProductoImagenDTO(String url, String descripcion, Boolean esPrincipal, Long productoId) {
        this.url = url;
        this.descripcion = descripcion;
        this.esPrincipal = esPrincipal != null ? esPrincipal : false;
        this.productoId = productoId;
        this.tipo = "galeria";
        this.orden = 0;
        this.activa = true;
    }

    // Constructor con tipo específico
    public ProductoImagenDTO(String url, String descripcion, Boolean esPrincipal,
                             Long productoId, String tipo, Integer orden) {
        this.url = url;
        this.descripcion = descripcion;
        this.esPrincipal = esPrincipal != null ? esPrincipal : false;
        this.productoId = productoId;
        this.tipo = tipo != null ? tipo : "galeria";
        this.orden = orden != null ? orden : 0;
        this.activa = true;
    }

    // Métodos de validación
    public boolean isValid() {
        return url != null && !url.trim().isEmpty() &&
                productoId != null && productoId > 0;
    }

    public boolean esImagenPrincipal() {
        return Boolean.TRUE.equals(esPrincipal);
    }

    public boolean esImagenSecundaria() {
        return !Boolean.TRUE.equals(esPrincipal);
    }

    public boolean esImagenActiva() {
        return Boolean.TRUE.equals(activa);
    }

    // Métodos de negocio
    public String obtenerTipoImagen() {
        if (tipo == null || tipo.trim().isEmpty()) {
            return "galeria";
        }
        return tipo.toLowerCase();
    }

    public boolean esTipoValido() {
        String tipoLimpio = obtenerTipoImagen();
        return "principal".equals(tipoLimpio) ||
                "galeria".equals(tipoLimpio) ||
                "miniatura".equals(tipoLimpio) ||
                "zoom".equals(tipoLimpio) ||
                "detalle".equals(tipoLimpio);
    }

    public boolean tieneDescripcion() {
        return descripcion != null && !descripcion.trim().isEmpty();
    }

    public String obtenerDescripcionODefault() {
        if (tieneDescripcion()) {
            return descripcion;
        }

        if (Boolean.TRUE.equals(esPrincipal)) {
            return "Imagen principal del producto";
        }

        return switch (obtenerTipoImagen()) {
            case "miniatura" -> "Imagen miniatura";
            case "zoom" -> "Imagen para zoom";
            case "detalle" -> "Imagen de detalle";
            default -> "Imagen del producto";
        };
    }

    // Métodos para manejo de URLs
    public boolean esUrlValida() {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        String urlLimpia = url.trim().toLowerCase();
        return urlLimpia.startsWith("http://") ||
                urlLimpia.startsWith("https://") ||
                urlLimpia.startsWith("/api/imagenes/") ||
                urlLimpia.startsWith("data:image/"); // Para base64
    }

    public boolean esImagenLocal() {
        if (url == null) return false;
        return url.startsWith("/api/imagenes/") || url.startsWith("/uploads/");
    }

    public boolean esImagenExterna() {
        if (url == null) return false;
        String urlLimpia = url.trim().toLowerCase();
        return urlLimpia.startsWith("http://") || urlLimpia.startsWith("https://");
    }

    public String obtenerNombreArchivo() {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }

        try {
            String[] partes = url.split("/");
            return partes[partes.length - 1];
        } catch (Exception e) {
            return "imagen";
        }
    }

    public String obtenerExtension() {
        String nombreArchivo = obtenerNombreArchivo();
        try {
            int ultimoPunto = nombreArchivo.lastIndexOf('.');
            if (ultimoPunto > 0 && ultimoPunto < nombreArchivo.length() - 1) {
                return nombreArchivo.substring(ultimoPunto + 1).toLowerCase();
            }
        } catch (Exception e) {
            // Ignorar errores
        }
        return "jpg"; // Default
    }

    public boolean esExtensionValida() {
        String extension = obtenerExtension();
        return "jpg".equals(extension) || "jpeg".equals(extension) ||
                "png".equals(extension) || "gif".equals(extension) ||
                "webp".equals(extension) || "svg".equals(extension);
    }

    // Método para crear desde entidad
    public static ProductoImagenDTO fromEntity(com.digital.mecommerces.model.ProductoImagen imagen) {
        if (imagen == null) return null;

        ProductoImagenDTO dto = new ProductoImagenDTO();
        dto.setImagenId(imagen.getImagenId());
        dto.setUrl(imagen.getUrl());
        dto.setDescripcion(imagen.getDescripcion());
        dto.setEsPrincipal(imagen.getEsPrincipal());
        dto.setTipo(imagen.getTipo());
        dto.setOrden(imagen.getOrden());
        dto.setTamanio(imagen.getTamanio());
        dto.setActiva(imagen.getActiva());
        dto.setCreatedAt(imagen.getCreatedAt());
        dto.setUpdatedAt(imagen.getUpdatedAt());

        if (imagen.getProducto() != null) {
            dto.setProductoId(imagen.getProducto().getProductoId());
            dto.setProductoNombre(imagen.getProducto().getProductoNombre());
            dto.setProductoSlug(imagen.getProducto().getSlug());
        }

        return dto;
    }

    // Método para crear versión simplificada
    public ProductoImagenDTO toSimple() {
        ProductoImagenDTO simpleDto = new ProductoImagenDTO();
        simpleDto.setImagenId(this.imagenId);
        simpleDto.setUrl(this.url);
        simpleDto.setDescripcion(this.descripcion);
        simpleDto.setEsPrincipal(this.esPrincipal);
        simpleDto.setTipo(this.tipo);
        simpleDto.setOrden(this.orden);
        simpleDto.setActiva(this.activa);
        return simpleDto;
    }

    // Método para crear versión pública (sin información interna)
    public ProductoImagenDTO toPublic() {
        ProductoImagenDTO publicDto = new ProductoImagenDTO();
        publicDto.setUrl(this.url);
        publicDto.setDescripcion(this.obtenerDescripcionODefault());
        publicDto.setEsPrincipal(this.esPrincipal);
        publicDto.setTipo(this.tipo);
        publicDto.setOrden(this.orden);
        // No incluir IDs internos, fechas, etc.
        return publicDto;
    }

    // Métodos para ordenamiento
    public static java.util.Comparator<ProductoImagenDTO> porOrden() {
        return java.util.Comparator.comparing(
                img -> img.getOrden() != null ? img.getOrden() : 999,
                java.util.Comparator.naturalOrder()
        );
    }

    public static java.util.Comparator<ProductoImagenDTO> principalPrimero() {
        return java.util.Comparator.<ProductoImagenDTO, Boolean>comparing(
                img -> !Boolean.TRUE.equals(img.getEsPrincipal())
        ).thenComparing(porOrden());
    }

    // Métodos de utilidad
    public String generarAltText() {
        if (tieneDescripcion()) {
            return descripcion;
        }

        String producto = productoNombre != null ? productoNombre : "Producto";

        if (Boolean.TRUE.equals(esPrincipal)) {
            return producto + " - Imagen principal";
        }

        return producto + " - " + obtenerDescripcionODefault();
    }

    public boolean requiereActualizacion() {
        return !esUrlValida() || !esTipoValido() || !esExtensionValida();
    }

    @Override
    public String toString() {
        return "ProductoImagenDTO{" +
                "imagenId=" + imagenId +
                ", url='" + url + '\'' +
                ", esPrincipal=" + esPrincipal +
                ", tipo='" + tipo + '\'' +
                ", orden=" + orden +
                ", productoId=" + productoId +
                ", activa=" + activa +
                '}';
    }
}
