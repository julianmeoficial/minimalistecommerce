package com.digital.mecommerces.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Entidad ProductoImagen para gestión de imágenes de productos
 * Optimizada para el sistema medbcommerce 3.0
 */
@Entity
@Table(name = "productoimagen")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"producto"})
@ToString(exclude = {"producto"})
public class ProductoImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imagenid", nullable = false)
    private Long imagenId;

    @Column(name = "url", nullable = false, length = 255)
    private String url;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "esprincipal", nullable = false)
    private Boolean esPrincipal = false;

    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "activa", nullable = false)
    private Boolean activa = true;

    @Column(name = "tamanio")
    private Integer tamanio;

    // ✅ CAMPO REAL DE LA BD (según documentación - lowercase)
    @Column(name = "createdat", nullable = false)
    private LocalDateTime createdat;

    // Relación con Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productoid", nullable = false)
    @JsonBackReference
    private Producto producto;

    // === CONSTRUCTORES ===

    public ProductoImagen(String url, String descripcion, Boolean esPrincipal, Producto producto) {
        this.url = url;
        this.descripcion = descripcion;
        this.esPrincipal = esPrincipal != null ? esPrincipal : false;
        this.producto = producto;
        this.createdat = LocalDateTime.now();
        this.activa = true;
        this.tipo = "principal";
        this.orden = 1;
    }

    public ProductoImagen(String url, String descripcion, Boolean esPrincipal, String tipo, Integer orden, Producto producto) {
        this.url = url;
        this.descripcion = descripcion;
        this.esPrincipal = esPrincipal != null ? esPrincipal : false;
        this.tipo = tipo;
        this.orden = orden;
        this.producto = producto;
        this.createdat = LocalDateTime.now();
        this.activa = true;
    }

    // === MÉTODOS DE COMPATIBILIDAD PARA EL SERVICIO ===

    /**
     * ✅ MÉTODO AGREGADO - Compatibilidad con ProductoImagenService
     * Mapea al campo real createdat de la BD
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdat = createdAt;
    }

    /**
     * ✅ MÉTODO AGREGADO - Compatibilidad con ProductoImagenService
     * Retorna el campo real createdat de la BD
     */
    public LocalDateTime getCreatedAt() {
        return this.createdat;
    }

    /**
     * ✅ MÉTODO AGREGADO - Compatibilidad con ProductoImagenService
     * Como la BD no tiene updatedat, mapea a createdat
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.createdat = updatedAt;
    }

    /**
     * ✅ MÉTODO AGREGADO - Compatibilidad con ProductoImagenService
     * Retorna createdat como "updated at"
     */
    public LocalDateTime getUpdatedAt() {
        return this.createdat;
    }

    // === MÉTODOS JPA LIFECYCLE ===

    @PrePersist
    protected void onCreate() {
        if (this.createdat == null) {
            this.createdat = LocalDateTime.now();
        }
        if (this.activa == null) {
            this.activa = true;
        }
        if (this.esPrincipal == null) {
            this.esPrincipal = false;
        }
        if (this.orden == null) {
            this.orden = 1;
        }
        if (this.tipo == null) {
            this.tipo = "principal";
        }
    }

    // === MÉTODOS DE VALIDACIÓN Y UTILIDAD ===

    public boolean esImagenValida() {
        return url != null && !url.trim().isEmpty() &&
                url.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|webp|svg)$");
    }

    public boolean esImagenPrincipal() {
        return Boolean.TRUE.equals(this.esPrincipal);
    }

    public boolean esImagenActiva() {
        return Boolean.TRUE.equals(this.activa);
    }

    public boolean perteneceAProducto(Long productoId) {
        return this.producto != null &&
                this.producto.getProductoId() != null &&
                this.producto.getProductoId().equals(productoId);
    }

    public String obtenerExtension() {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }
        int lastDot = url.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return url.substring(lastDot + 1).toLowerCase();
    }

    public boolean esFormatoSoportado() {
        String extension = obtenerExtension();
        return extension.matches("jpg|jpeg|png|gif|webp|svg");
    }

    // === MÉTODOS DE COMPARACIÓN ===

    public static java.util.Comparator<ProductoImagen> porOrden() {
        return (img1, img2) -> {
            // Imagen principal siempre primero
            if (Boolean.TRUE.equals(img1.getEsPrincipal()) && !Boolean.TRUE.equals(img2.getEsPrincipal())) {
                return -1;
            }
            if (!Boolean.TRUE.equals(img1.getEsPrincipal()) && Boolean.TRUE.equals(img2.getEsPrincipal())) {
                return 1;
            }

            // Luego por orden numérico
            Integer orden1 = img1.getOrden() != null ? img1.getOrden() : 999;
            Integer orden2 = img2.getOrden() != null ? img2.getOrden() : 999;
            return orden1.compareTo(orden2);
        };
    }

    public static java.util.Comparator<ProductoImagen> porFechaCreacion() {
        return (img1, img2) -> {
            if (img1.getCreatedAt() == null && img2.getCreatedAt() == null) return 0;
            if (img1.getCreatedAt() == null) return 1;
            if (img2.getCreatedAt() == null) return -1;
            return img2.getCreatedAt().compareTo(img1.getCreatedAt()); // Más reciente primero
        };
    }

    // === VALIDACIONES ESPECÍFICAS ===

    public java.util.List<String> validar() {
        java.util.List<String> errores = new java.util.ArrayList<>();

        if (url == null || url.trim().isEmpty()) {
            errores.add("La URL de la imagen es obligatoria");
        } else if (!esImagenValida()) {
            errores.add("La URL no corresponde a un formato de imagen válido");
        }

        if (producto == null) {
            errores.add("La imagen debe estar asociada a un producto");
        }

        if (orden != null && orden < 1) {
            errores.add("El orden debe ser mayor a 0");
        }

        if (tamanio != null && tamanio < 0) {
            errores.add("El tamaño no puede ser negativo");
        }

        return errores;
    }

    public boolean esValida() {
        return validar().isEmpty();
    }

    // === MÉTODOS DE COPIA ===

    public ProductoImagen copiar() {
        ProductoImagen copia = new ProductoImagen();
        copia.setUrl(this.url);
        copia.setDescripcion(this.descripcion);
        copia.setEsPrincipal(false); // Las copias no son principales por defecto
        copia.setTipo(this.tipo);
        copia.setOrden(this.orden);
        copia.setActiva(this.activa);
        copia.setTamanio(this.tamanio);
        copia.setCreatedAt(LocalDateTime.now());
        return copia;
    }

    public ProductoImagen copiarPara(Producto nuevoProducto) {
        ProductoImagen copia = copiar();
        copia.setProducto(nuevoProducto);
        return copia;
    }

    // === GETTERS Y SETTERS CON VALIDACIÓN ===

    public void setUrl(String url) {
        if (url != null) {
            this.url = url.trim();
        } else {
            this.url = null;
        }
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal != null ? esPrincipal : false;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa != null ? activa : true;
    }

    public void setOrden(Integer orden) {
        if (orden != null && orden < 1) {
            this.orden = 1;
        } else {
            this.orden = orden;
        }
    }

    public void setTipo(String tipo) {
        if (tipo != null && !tipo.trim().isEmpty()) {
            this.tipo = tipo.trim().toLowerCase();
        } else {
            this.tipo = "principal";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoImagen that = (ProductoImagen) o;
        return imagenId != null && imagenId.equals(that.imagenId);
    }

    @Override
    public int hashCode() {
        return imagenId != null ? imagenId.hashCode() : 0;
    }
}
