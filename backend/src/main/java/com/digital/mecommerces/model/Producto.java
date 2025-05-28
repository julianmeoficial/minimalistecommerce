package com.digital.mecommerces.model;

import com.digital.mecommerces.enums.TipoCategoria;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producto")
@Slf4j
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productoid", nullable = false)
    private Long productoId;

    @Column(name = "productonombre", nullable = false, length = 100)
    private String productoNombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "precio", nullable = false)
    private Double precio;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoriaid", nullable = false)
    private CategoriaProducto categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedorid", nullable = false)
    private Usuario vendedor;

    @Column(name = "slug", length = 150, unique = true)
    private String slug;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "updatedat")
    private LocalDateTime updatedat;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "destacado")
    private Boolean destacado = false;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductoImagen> imagenes = new ArrayList<>();

    // Constructor vacío requerido por JPA
    public Producto() {
        this.createdat = LocalDateTime.now();
        this.updatedat = LocalDateTime.now();
        this.activo = true;
        this.destacado = false;
    }

    // Constructor optimizado con validación de categoría
    public Producto(String productoNombre, String descripcion, Double precio, Integer stock,
                    CategoriaProducto categoria, Usuario vendedor) {
        this();
        this.productoNombre = productoNombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.vendedor = vendedor;

        // Validar que la categoría sea del sistema optimizado
        if (categoria != null) {
            try {
                TipoCategoria.fromCodigo(categoria.getNombre().toUpperCase());
                log.debug("✅ Producto creado en categoría del sistema: {}", categoria.getNombre());
            } catch (IllegalArgumentException e) {
                log.debug("📝 Producto creado en categoría personalizada: {}", categoria.getNombre());
            }
        }

        // Generar slug automáticamente
        this.slug = generarSlug(productoNombre);
    }

    // Métodos de ciclo de vida optimizados
    @PrePersist
    public void prePersist() {
        if (this.createdat == null) {
            this.createdat = LocalDateTime.now();
        }

        if (this.updatedat == null) {
            this.updatedat = LocalDateTime.now();
        }

        if (this.activo == null) {
            this.activo = true;
        }

        if (this.destacado == null) {
            this.destacado = false;
        }

        // Generar slug si no existe
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = generarSlug(this.productoNombre);
        }

        log.debug("✅ Producto preparado para persistir: {} - Categoría: {}",
                this.productoNombre, this.categoria != null ? this.categoria.getNombre() : "null");
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedat = LocalDateTime.now();

        // Actualizar slug si cambió el nombre
        if (this.productoNombre != null) {
            String nuevoSlug = generarSlug(this.productoNombre);
            if (!nuevoSlug.equals(this.slug)) {
                this.slug = nuevoSlug;
            }
        }

        log.debug("🔄 Producto actualizado: {} - Stock: {}", this.productoNombre, this.stock);
    }

    // Métodos de gestión de imágenes optimizados
    public void addImagen(ProductoImagen imagen) {
        if (imagen != null) {
            imagenes.add(imagen);
            imagen.setProducto(this);
            log.debug("➕ Imagen agregada al producto {}: {}", this.productoId, imagen.getUrl());
        }
    }

    public void removeImagen(ProductoImagen imagen) {
        if (imagen != null && imagenes.remove(imagen)) {
            imagen.setProducto(null);
            log.debug("➖ Imagen removida del producto {}", this.productoId);
        }
    }

    public ProductoImagen getImagenPrincipal() {
        return imagenes.stream()
                .filter(img -> img.getEsPrincipal() != null && img.getEsPrincipal())
                .findFirst()
                .orElse(imagenes.isEmpty() ? null : imagenes.get(0));
    }

    public List<ProductoImagen> getImagenesSecundarias() {
        return imagenes.stream()
                .filter(img -> img.getEsPrincipal() == null || !img.getEsPrincipal())
                .toList();
    }

    // Métodos de validación optimizados
    public boolean estaDisponible() {
        return this.activo && this.stock != null && this.stock > 0;
    }

    public boolean puedeVenderse(Integer cantidad) {
        return estaDisponible() && this.stock >= cantidad;
    }

    public boolean perteneceACategoriaDelSistema() {
        if (this.categoria == null) return false;

        try {
            TipoCategoria.fromCodigo(this.categoria.getNombre().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public TipoCategoria getTipoCategoria() {
        if (this.categoria == null) return null;

        try {
            return TipoCategoria.fromCodigo(this.categoria.getNombre().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Métodos de gestión de stock optimizados
    public void reducirStock(Integer cantidad) {
        if (cantidad > 0 && this.stock >= cantidad) {
            this.stock -= cantidad;
            this.updatedat = LocalDateTime.now();
            log.debug("📦 Stock reducido para producto {}: -{} (Stock actual: {})",
                    this.productoId, cantidad, this.stock);
        } else {
            log.warn("⚠️ No se puede reducir stock del producto {}: cantidad={}, stock actual={}",
                    this.productoId, cantidad, this.stock);
        }
    }

    public void aumentarStock(Integer cantidad) {
        if (cantidad > 0) {
            this.stock += cantidad;
            this.updatedat = LocalDateTime.now();
            log.debug("📦 Stock aumentado para producto {}: +{} (Stock actual: {})",
                    this.productoId, cantidad, this.stock);
        }
    }

    public void marcarComoDestacado() {
        this.destacado = true;
        this.updatedat = LocalDateTime.now();
        log.debug("⭐ Producto {} marcado como destacado", this.productoId);
    }

    public void quitarDestacado() {
        this.destacado = false;
        this.updatedat = LocalDateTime.now();
        log.debug("⭐ Producto {} ya no está destacado", this.productoId);
    }

    public void activar() {
        this.activo = true;
        this.updatedat = LocalDateTime.now();
        log.debug("✅ Producto {} activado", this.productoId);
    }

    public void desactivar() {
        this.activo = false;
        this.updatedat = LocalDateTime.now();
        log.debug("❌ Producto {} desactivado", this.productoId);
    }

    // Métodos de utilidad optimizados
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

    public String getEstadoStock() {
        if (this.stock == null || this.stock <= 0) {
            return "AGOTADO";
        } else if (this.stock <= 5) {
            return "POCO_STOCK";
        } else {
            return "DISPONIBLE";
        }
    }

    public boolean necesitaReabastecimiento() {
        return this.stock != null && this.stock <= 5;
    }

    // Getters y Setters optimizados
    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
        // Regenerar slug cuando cambia el nombre
        if (productoNombre != null && !productoNombre.isEmpty()) {
            this.slug = generarSlug(productoNombre);
        }
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
        this.updatedat = LocalDateTime.now();
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
        this.updatedat = LocalDateTime.now();
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
        this.updatedat = LocalDateTime.now();
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public LocalDateTime getUpdatedat() {
        return updatedat;
    }

    public void setUpdatedat(LocalDateTime updatedat) {
        this.updatedat = updatedat;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
        this.updatedat = LocalDateTime.now();
    }

    public Boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
        this.updatedat = LocalDateTime.now();
    }

    public List<ProductoImagen> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ProductoImagen> imagenes) {
        this.imagenes = imagenes != null ? imagenes : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Producto{" +
                "productoId=" + productoId +
                ", productoNombre='" + productoNombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", categoria=" + (categoria != null ? categoria.getNombre() : "null") +
                ", vendedor=" + (vendedor != null ? vendedor.getEmail() : "null") +
                ", activo=" + activo +
                ", destacado=" + destacado +
                ", slug='" + slug + '\'' +
                ", estadoStock='" + getEstadoStock() + '\'' +
                '}';
    }
}
