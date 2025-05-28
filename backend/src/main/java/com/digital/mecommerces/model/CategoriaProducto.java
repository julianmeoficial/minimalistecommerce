package com.digital.mecommerces.model;

import com.digital.mecommerces.enums.TipoCategoria;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categoriaproducto")
@Slf4j
public class CategoriaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoriaid", nullable = false)
    private Long categoriaId;

    @Column(name = "nombre", nullable = false, length = 50, unique = true)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "categoriapadreid")
    private Long categoriapadreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoriapadreid", insertable = false, updatable = false)
    private CategoriaProducto categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategoriaProducto> subcategorias = new ArrayList<>();

    @Column(name = "slug", length = 100, unique = true)
    private String slug;

    @Column(name = "imagen", length = 255)
    private String imagen;

    @Column(name = "activo")
    private Boolean activo = true;

    // Constructor vacío requerido por JPA
    public CategoriaProducto() {
        this.activo = true;
    }

    // Constructor optimizado con validación de enum
    public CategoriaProducto(String nombre, String descripcion) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;

        // Validar si es una categoría del sistema usando enum
        try {
            TipoCategoria tipo = TipoCategoria.fromCodigo(nombre.toUpperCase());
            log.debug("✅ Categoría del sistema creada: {}", tipo.getDescripcion());
        } catch (IllegalArgumentException e) {
            log.debug("📝 Categoría personalizada creada: {}", nombre);
        }
    }

    // Constructor con categoría padre
    public CategoriaProducto(String nombre, String descripcion, CategoriaProducto categoriaPadre) {
        this(nombre, descripcion);
        this.categoriaPadre = categoriaPadre;

        if (categoriaPadre != null) {
            this.categoriapadreId = categoriaPadre.getCategoriaId();
        }
    }

    // Métodos de validación optimizados
    @PrePersist
    public void prePersist() {
        if (this.activo == null) {
            this.activo = true;
        }

        // Generar slug si no existe
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = generarSlug(this.nombre);
        }

        // Verificar si es una categoría del sistema
        verificarTipoCategoria();

        log.debug("✅ CategoriaProducto preparada para persistir: {}", this.nombre);
    }

    @PreUpdate
    public void preUpdate() {
        // Actualizar slug si cambió el nombre
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = generarSlug(this.nombre);
        }

        verificarTipoCategoria();
        log.debug("🔄 CategoriaProducto actualizada: {}", this.nombre);
    }

    // Métodos de utilidad optimizados
    public boolean esCategoriaPrincipal() {
        return this.categoriaPadre == null && this.categoriapadreId == null;
    }

    public boolean esSubcategoria() {
        return !esCategoriaPrincipal();
    }

    public boolean esCategoriaDelSistema() {
        try {
            TipoCategoria.fromCodigo(this.nombre.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public TipoCategoria getTipoCategoria() {
        try {
            return TipoCategoria.fromCodigo(this.nombre.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean tieneSubcategorias() {
        return subcategorias != null && !subcategorias.isEmpty();
    }

    public int getNivelJerarquia() {
        if (esCategoriaPrincipal()) {
            return 0;
        } else if (categoriaPadre != null) {
            return categoriaPadre.getNivelJerarquia() + 1;
        }
        return 1; // Fallback
    }

    public void agregarSubcategoria(CategoriaProducto subcategoria) {
        if (subcategoria != null) {
            subcategorias.add(subcategoria);
            subcategoria.setCategoriaPadre(this);
            subcategoria.setCategoriapadreId(this.categoriaId);
            log.debug("➕ Subcategoría agregada: {} -> {}", this.nombre, subcategoria.getNombre());
        }
    }

    public void removerSubcategoria(CategoriaProducto subcategoria) {
        if (subcategoria != null && subcategorias.remove(subcategoria)) {
            subcategoria.setCategoriaPadre(null);
            subcategoria.setCategoriapadreId(null);
            log.debug("➖ Subcategoría removida: {} <- {}", this.nombre, subcategoria.getNombre());
        }
    }

    private void verificarTipoCategoria() {
        if (esCategoriaDelSistema()) {
            TipoCategoria tipo = getTipoCategoria();
            log.debug("✅ Categoría del sistema: {} - {}", tipo.getCodigo(), tipo.getDescripcion());
        } else {
            log.debug("📝 Categoría personalizada: {}", this.nombre);
        }
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

    // Getters y Setters optimizados
    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        // Regenerar slug cuando cambia el nombre
        if (nombre != null && !nombre.isEmpty()) {
            this.slug = generarSlug(nombre);
        }
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getCategoriapadreId() {
        return categoriapadreId;
    }

    public void setCategoriapadreId(Long categoriapadreId) {
        this.categoriapadreId = categoriapadreId;
    }

    public CategoriaProducto getCategoriaPadre() {
        return categoriaPadre;
    }

    public void setCategoriaPadre(CategoriaProducto categoriaPadre) {
        this.categoriaPadre = categoriaPadre;
        if (categoriaPadre != null) {
            this.categoriapadreId = categoriaPadre.getCategoriaId();
        } else {
            this.categoriapadreId = null;
        }
    }

    public List<CategoriaProducto> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<CategoriaProducto> subcategorias) {
        this.subcategorias = subcategorias != null ? subcategorias : new ArrayList<>();
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "CategoriaProducto{" +
                "categoriaId=" + categoriaId +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                ", esCategoriaPrincipal=" + esCategoriaPrincipal() +
                ", esCategoriaDelSistema=" + esCategoriaDelSistema() +
                ", slug='" + slug + '\'' +
                '}';
    }
}
