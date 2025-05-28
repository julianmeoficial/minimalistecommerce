package com.digital.mecommerces.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para gestión de categorías de productos
 * Optimizado para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaProductoDTO {

    @JsonProperty("categoriaId")
    private Long categoriaId;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    @JsonProperty("nombre")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("categoriaPadreId")
    private Long categoriaPadreId;

    @JsonProperty("categoriaPadreNombre")
    private String categoriaPadreNombre;

    @JsonProperty("slug")
    @Size(max = 100, message = "El slug no puede exceder 100 caracteres")
    private String slug;

    @JsonProperty("imagen")
    @Size(max = 255, message = "La URL de imagen no puede exceder 255 caracteres")
    private String imagen;

    @JsonProperty("activo")
    private Boolean activo = true;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    // Campos calculados
    @JsonProperty("subcategorias")
    private List<CategoriaProductoDTO> subcategorias = new ArrayList<>();

    @JsonProperty("numeroProductos")
    private Long numeroProductos;

    @JsonProperty("tieneSubcategorias")
    private Boolean tieneSubcategorias;

    @JsonProperty("esPrincipal")
    private Boolean esPrincipal;

    @JsonProperty("nivel")
    private Integer nivel;

    @JsonProperty("rutaCompleta")
    private String rutaCompleta;

    @JsonProperty("esDelSistema")
    private Boolean esDelSistema;

    // Constructor básico para creación
    public CategoriaProductoDTO(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = true;
        this.subcategorias = new ArrayList<>();
    }

    // Constructor con categoría padre
    public CategoriaProductoDTO(String nombre, String descripcion, Long categoriaPadreId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoriaPadreId = categoriaPadreId;
        this.activo = true;
        this.subcategorias = new ArrayList<>();
    }

    // Constructor completo para listado
    public CategoriaProductoDTO(Long categoriaId, String nombre, String descripcion,
                                String slug, Boolean activo, Long numeroProductos) {
        this.categoriaId = categoriaId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.slug = slug;
        this.activo = activo;
        this.numeroProductos = numeroProductos;
        this.subcategorias = new ArrayList<>();
    }

    // Métodos de validación
    public boolean isValid() {
        return nombre != null && !nombre.trim().isEmpty() &&
                (slug == null || !slug.trim().isEmpty());
    }

    public boolean esCategoriaPrincipal() {
        return categoriaPadreId == null;
    }

    public boolean esSubcategoria() {
        return categoriaPadreId != null && categoriaPadreId > 0;
    }

    public boolean tieneProductos() {
        return numeroProductos != null && numeroProductos > 0;
    }

    // Métodos de negocio
    public void calcularCamposDerivados() {
        // Calcular si es principal
        this.esPrincipal = esCategoriaPrincipal();

        // Calcular si tiene subcategorías
        this.tieneSubcategorias = subcategorias != null && !subcategorias.isEmpty();

        // Calcular nivel en la jerarquía
        this.nivel = calcularNivel();

        // Verificar si es del sistema
        this.esDelSistema = verificarSiEsDelSistema();

        // Generar ruta completa
        this.rutaCompleta = generarRutaCompleta();
    }

    private Integer calcularNivel() {
        if (esCategoriaPrincipal()) {
            return 1;
        } else {
            return 2; // Simplificado - en una implementación real podría ser recursivo
        }
    }

    private Boolean verificarSiEsDelSistema() {
        if (nombre == null) return false;

        // Categorías del sistema optimizado
        String[] categoriasDelSistema = {
                "ELECTRONICA", "ROPA", "HOGAR", "DEPORTES", "LIBROS",
                "JUGUETES", "BELLEZA", "SALUD", "AUTOMOTRIZ", "JARDIN"
        };

        String nombreUpper = nombre.toUpperCase();
        for (String categoriaSistema : categoriasDelSistema) {
            if (categoriaSistema.equals(nombreUpper)) {
                return true;
            }
        }
        return false;
    }

    private String generarRutaCompleta() {
        if (categoriaPadreNombre != null && !categoriaPadreNombre.trim().isEmpty()) {
            return categoriaPadreNombre + " > " + nombre;
        }
        return nombre;
    }

    public String obtenerSlugOGenerado() {
        if (slug != null && !slug.trim().isEmpty()) {
            return slug;
        }
        return generarSlugDesdeNombre();
    }

    public String generarSlugDesdeNombre() {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "";
        }

        return nombre.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    public boolean tieneImagen() {
        return imagen != null && !imagen.trim().isEmpty();
    }

    public String obtenerImagenODefault() {
        if (tieneImagen()) {
            return imagen;
        }

        // Imagen por defecto basada en la categoría
        if (Boolean.TRUE.equals(esDelSistema)) {
            return "/api/imagenes/categorias/default-" +
                    nombre.toLowerCase().replaceAll("[^a-z0-9]", "") + ".jpg";
        }

        return "/api/imagenes/categorias/default-categoria.jpg";
    }

    // Métodos para manejo de subcategorías
    public void agregarSubcategoria(CategoriaProductoDTO subcategoria) {
        if (this.subcategorias == null) {
            this.subcategorias = new ArrayList<>();
        }
        this.subcategorias.add(subcategoria);
        this.tieneSubcategorias = true;
    }

    public void eliminarSubcategoria(Long subcategoriaId) {
        if (subcategorias != null) {
            subcategorias.removeIf(sub -> subcategoriaId.equals(sub.getCategoriaId()));
            this.tieneSubcategorias = !subcategorias.isEmpty();
        }
    }

    public List<CategoriaProductoDTO> obtenerSubcategoriasActivas() {
        if (subcategorias == null) {
            return new ArrayList<>();
        }
        return subcategorias.stream()
                .filter(sub -> Boolean.TRUE.equals(sub.getActivo()))
                .toList();
    }

    public long contarSubcategoriasActivas() {
        return obtenerSubcategoriasActivas().size();
    }

    public boolean puedeSerEliminada() {
        return !tieneProductos() && !Boolean.TRUE.equals(tieneSubcategorias);
    }

    // Método para crear desde entidad
    public static CategoriaProductoDTO fromEntity(com.digital.mecommerces.model.CategoriaProducto categoria) {
        if (categoria == null) return null;

        CategoriaProductoDTO dto = new CategoriaProductoDTO();
        dto.setCategoriaId(categoria.getCategoriaId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setSlug(categoria.getSlug());
        dto.setImagen(categoria.getImagen());
        dto.setActivo(categoria.getActivo());
        // Removed non-existent field access: createdAt and updatedAt don't exist in the entity
        // dto.setCreatedAt(categoria.getCreatedAt());
        // dto.setUpdatedAt(categoria.getUpdatedAt());

        if (categoria.getCategoriaPadre() != null) {
            dto.setCategoriaPadreId(categoria.getCategoriaPadre().getCategoriaId());
            dto.setCategoriaPadreNombre(categoria.getCategoriaPadre().getNombre());
        }

        // Convertir subcategorías si existen
        if (categoria.getSubcategorias() != null) {
            List<CategoriaProductoDTO> subcategoriasDto = categoria.getSubcategorias().stream()
                    .map(CategoriaProductoDTO::fromEntity)
                    .toList();
            dto.setSubcategorias(subcategoriasDto);
        }

        // Calcular campos derivados
        dto.calcularCamposDerivados();

        return dto;
    }

    // Método para crear versión simplificada (para listados)
    public CategoriaProductoDTO toSimple() {
        CategoriaProductoDTO simpleDto = new CategoriaProductoDTO();
        simpleDto.setCategoriaId(this.categoriaId);
        simpleDto.setNombre(this.nombre);
        simpleDto.setDescripcion(this.descripcion);
        simpleDto.setSlug(this.slug);
        simpleDto.setActivo(this.activo);
        simpleDto.setNumeroProductos(this.numeroProductos);
        simpleDto.setEsPrincipal(this.esPrincipal);
        simpleDto.setTieneSubcategorias(this.tieneSubcategorias);
        simpleDto.setNivel(this.nivel);
        simpleDto.setEsDelSistema(this.esDelSistema);
        return simpleDto;
    }

    // Método para crear versión pública (sin información administrativa)
    public CategoriaProductoDTO toPublic() {
        CategoriaProductoDTO publicDto = new CategoriaProductoDTO();
        publicDto.setCategoriaId(this.categoriaId);
        publicDto.setNombre(this.nombre);
        publicDto.setDescripcion(this.descripcion);
        publicDto.setSlug(this.slug);
        publicDto.setImagen(this.obtenerImagenODefault());
        publicDto.setActivo(this.activo);
        publicDto.setCategoriaPadreId(this.categoriaPadreId);
        publicDto.setCategoriaPadreNombre(this.categoriaPadreNombre);

        // Solo subcategorías activas
        if (subcategorias != null) {
            List<CategoriaProductoDTO> subcategoriasPublicas = subcategorias.stream()
                    .filter(sub -> Boolean.TRUE.equals(sub.getActivo()))
                    .map(CategoriaProductoDTO::toPublic)
                    .toList();
            publicDto.setSubcategorias(subcategoriasPublicas);
        }

        publicDto.calcularCamposDerivados();
        return publicDto;
    }

    // Método para crear versión jerárquica (con toda la estructura)
    public CategoriaProductoDTO toHierarchical() {
        CategoriaProductoDTO hierarchicalDto = new CategoriaProductoDTO();
        hierarchicalDto.setCategoriaId(this.categoriaId);
        hierarchicalDto.setNombre(this.nombre);
        hierarchicalDto.setDescripcion(this.descripcion);
        hierarchicalDto.setSlug(this.slug);
        hierarchicalDto.setImagen(this.imagen);
        hierarchicalDto.setActivo(this.activo);
        hierarchicalDto.setNumeroProductos(this.numeroProductos);

        // Incluir toda la jerarquía
        if (subcategorias != null) {
            List<CategoriaProductoDTO> subcategoriasJerarquicas = subcategorias.stream()
                    .map(CategoriaProductoDTO::toHierarchical)
                    .toList();
            hierarchicalDto.setSubcategorias(subcategoriasJerarquicas);
        }

        hierarchicalDto.calcularCamposDerivados();
        return hierarchicalDto;
    }

    // Métodos para navegación y SEO
    public String generarTituloSEO() {
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            return nombre + " - " + descripcion;
        }
        return nombre + " - Categoría de productos";
    }

    public String generarMetaDescripcion() {
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            return "Explora nuestra categoría de " + descripcion.toLowerCase() +
                    ". Encuentra los mejores productos en " + nombre.toLowerCase() + ".";
        }
        return "Descubre productos en la categoría " + nombre.toLowerCase() +
                ". Amplia selección y mejores precios.";
    }

    public List<String> generarPalabrasClave() {
        List<String> palabras = new ArrayList<>();
        if (nombre != null) {
            palabras.add(nombre.toLowerCase());
        }
        if (categoriaPadreNombre != null) {
            palabras.add(categoriaPadreNombre.toLowerCase());
        }
        if (descripcion != null) {
            String[] palabrasDesc = descripcion.toLowerCase().split("\\s+");
            for (String palabra : palabrasDesc) {
                if (palabra.length() > 3) {
                    palabras.add(palabra);
                }
            }
        }
        return palabras;
    }

    @Override
    public String toString() {
        return "CategoriaProductoDTO{" +
                "categoriaId=" + categoriaId +
                ", nombre='" + nombre + '\'' +
                ", slug='" + slug + '\'' +
                ", activo=" + activo +
                ", esPrincipal=" + esPrincipal +
                ", tieneSubcategorias=" + tieneSubcategorias +
                ", numeroProductos=" + numeroProductos +
                ", nivel=" + nivel +
                '}';
    }
}
