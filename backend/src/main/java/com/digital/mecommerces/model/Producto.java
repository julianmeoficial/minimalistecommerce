package com.digital.mecommerces.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producto")
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
    private Integer stock = 0;

    @Column(name = "categoriaid", nullable = false)
    private Long categoriaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoriaid", insertable = false, updatable = false)
    private CategoriaProducto categoria;

    @Column(name = "vendedorid", nullable = false)
    private Long vendedorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedorid", insertable = false, updatable = false)
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

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductoImagen> imagenes = new ArrayList<>();

    // Constructor vacío
    public Producto() {
        this.createdat = LocalDateTime.now();
        this.updatedat = LocalDateTime.now();
        this.activo = true;
        this.destacado = false;
        this.stock = 0;
    }

    // Constructor con parámetros (incluyendo stock)
    public Producto(String productoNombre, String descripcion, Double precio, Integer stock,
                    CategoriaProducto categoria, Usuario vendedor) {
        this.productoNombre = productoNombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.categoriaId = categoria.getCategoriaId();
        this.vendedor = vendedor;
        this.vendedorId = vendedor.getUsuarioId();
        this.createdat = LocalDateTime.now();
        this.updatedat = LocalDateTime.now();
        this.activo = true;
        this.destacado = false;
    }

    // Constructor alternativo sin stock (para compatibilidad)
    public Producto(String productoNombre, String descripcion, Double precio,
                    CategoriaProducto categoria, Usuario vendedor) {
        this.productoNombre = productoNombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = 0;
        this.categoria = categoria;
        this.categoriaId = categoria.getCategoriaId();
        this.vendedor = vendedor;
        this.vendedorId = vendedor.getUsuarioId();
        this.createdat = LocalDateTime.now();
        this.updatedat = LocalDateTime.now();
        this.activo = true;
        this.destacado = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedat = LocalDateTime.now();
    }

    // Getters y Setters
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
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
        if (categoria != null) {
            this.categoriaId = categoria.getCategoriaId();
        }
    }

    public Long getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Long vendedorId) {
        this.vendedorId = vendedorId;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
        if (vendedor != null) {
            this.vendedorId = vendedor.getUsuarioId();
        }
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
    }

    public Boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    public List<ProductoImagen> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ProductoImagen> imagenes) {
        this.imagenes = imagenes;
    }

    // Métodos de utilidad
    public void addImagen(ProductoImagen imagen) {
        imagenes.add(imagen);
        imagen.setProducto(this);
    }

    public void removeImagen(ProductoImagen imagen) {
        imagenes.remove(imagen);
        imagen.setProducto(null);
    }

    public boolean isActivo() {
        return activo != null && activo;
    }

    public boolean isDestacado() {
        return destacado != null && destacado;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "productoId=" + productoId +
                ", productoNombre='" + productoNombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", activo=" + activo +
                ", slug='" + slug + '\'' +
                '}';
    }
}