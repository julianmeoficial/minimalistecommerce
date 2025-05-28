package com.digital.mecommerces.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum para tipos de categoría de productos
 * Optimizado para el sistema medbcommerce 3.0
 */
public enum TipoCategoria {

    // Categorías principales del sistema
    TECNOLOGIA("TECNOLOGIA", "Productos tecnológicos y electrónicos", "tecnologia", "💻", "#3b82f6", true),
    ELECTRONICA("ELECTRONICA", "Dispositivos electrónicos y gadgets", "electronica", "⚡", "#6366f1", true),
    HOGAR("HOGAR", "Artículos para el hogar y decoración", "hogar", "🏠", "#10b981", true),
    MODA("MODA", "Ropa, calzado y accesorios de moda", "moda", "👗", "#ec4899", true),
    ROPA("ROPA", "Prendas de vestir para toda la familia", "ropa", "👕", "#f59e0b", true),
    MASCOTAS("MASCOTAS", "Productos para el cuidado de mascotas", "mascotas", "🐕", "#8b5cf6", true),
    ARTEMANUALIDADES("ARTEMANUALIDADES", "Arte, manualidades y creatividad", "arte-manualidades", "🎨", "#ef4444", true),
    DEPORTES("DEPORTES", "Artículos deportivos y fitness", "deportes", "⚽", "#059669", true),
    LIBROS("LIBROS", "Libros y material educativo", "libros", "📚", "#7c3aed", true),
    JUGUETES("JUGUETES", "Juguetes y entretenimiento infantil", "juguetes", "🧸", "#f97316", true),
    SALUD("SALUD", "Productos de salud y bienestar", "salud", "🏥", "#06b6d4", true),
    BELLEZA("BELLEZA", "Productos de belleza y cuidado personal", "belleza", "💄", "#e11d48", true),
    AUTOMOTRIZ("AUTOMOTRIZ", "Accesorios y partes automotrices", "automotriz", "🚗", "#374151", true),
    ALIMENTOS("ALIMENTOS", "Alimentos y bebidas", "alimentos", "🍎", "#65a30d", true),
    MUSICA("MUSICA", "Instrumentos musicales y audio", "musica", "🎵", "#7c2d12", true),

    // Categorías especiales del sistema
    GENERAL("GENERAL", "Categoría general para productos sin clasificar", "general", "📦", "#6b7280", false),
    SIN_CATEGORIA("SIN_CATEGORIA", "Productos sin categoría asignada", "sin-categoria", "❓", "#9ca3af", false);

    private final String codigo;
    private final String descripcion;
    private final String slug;
    private final String icono;
    private final String color;
    private final boolean esCategoriaPrincipal;

    TipoCategoria(String codigo, String descripcion, String slug, String icono, String color, boolean esCategoriaPrincipal) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.slug = slug;
        this.icono = icono;
        this.color = color;
        this.esCategoriaPrincipal = esCategoriaPrincipal;
    }

    // === GETTERS ===

    @JsonValue
    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getSlug() {
        return slug;
    }

    public String getIcono() {
        return icono;
    }

    public String getColor() {
        return color;
    }

    public boolean isEsCategoriaPrincipal() {
        return esCategoriaPrincipal;
    }

    // === MÉTODOS ESTÁTICOS ===

    @JsonCreator
    public static TipoCategoria fromCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de categoría no puede ser nulo o vacío");
        }

        for (TipoCategoria tipo : values()) {
            if (tipo.codigo.equalsIgnoreCase(codigo.trim())) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de categoría no válido: " + codigo);
    }

    public static TipoCategoria fromSlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new IllegalArgumentException("El slug de categoría no puede ser nulo o vacío");
        }

        for (TipoCategoria tipo : values()) {
            if (tipo.slug.equalsIgnoreCase(slug.trim())) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Slug de categoría no válido: " + slug);
    }

    public static List<TipoCategoria> obtenerCategoriasPrincipales() {
        return Arrays.stream(values())
                .filter(TipoCategoria::isEsCategoriaPrincipal)
                .collect(Collectors.toList());
    }

    public static List<TipoCategoria> obtenerCategoriasEspeciales() {
        return Arrays.stream(values())
                .filter(tipo -> !tipo.isEsCategoriaPrincipal())
                .collect(Collectors.toList());
    }

    public static List<String> obtenerCodigos() {
        return Arrays.stream(values())
                .map(TipoCategoria::getCodigo)
                .collect(Collectors.toList());
    }

    public static List<String> obtenerSlugs() {
        return Arrays.stream(values())
                .map(TipoCategoria::getSlug)
                .collect(Collectors.toList());
    }

    public static List<String> obtenerCodigosPrincipales() {
        return obtenerCategoriasPrincipales().stream()
                .map(TipoCategoria::getCodigo)
                .collect(Collectors.toList());
    }

    public static boolean existe(String codigo) {
        try {
            fromCodigo(codigo);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean existeSlug(String slug) {
        try {
            fromSlug(slug);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // === MÉTODOS DE AGRUPACIÓN ===

    public static List<TipoCategoria> obtenerCategoriasElectronicas() {
        return Arrays.asList(TECNOLOGIA, ELECTRONICA);
    }

    public static List<TipoCategoria> obtenerCategoriasVestimenta() {
        return Arrays.asList(MODA, ROPA);
    }

    public static List<TipoCategoria> obtenerCategoriasHogar() {
        return Arrays.asList(HOGAR);
    }

    public static List<TipoCategoria> obtenerCategoriasCreativas() {
        return Arrays.asList(ARTEMANUALIDADES, LIBROS, MUSICA);
    }

    public static List<TipoCategoria> obtenerCategoriasPersonales() {
        return Arrays.asList(SALUD, BELLEZA, DEPORTES);
    }

    // === MÉTODOS DE VALIDACIÓN Y UTILIDAD ===

    public boolean esTecnologia() {
        return this == TECNOLOGIA || this == ELECTRONICA;
    }

    public boolean esModa() {
        return this == MODA || this == ROPA;
    }

    public boolean esCreativa() {
        return this == ARTEMANUALIDADES || this == LIBROS || this == MUSICA;
    }

    public boolean esEspecial() {
        return !this.esCategoriaPrincipal;
    }

    public boolean esGeneral() {
        return this == GENERAL || this == SIN_CATEGORIA;
    }

    public boolean requiereVerificacionEspecial() {
        return this == SALUD || this == ALIMENTOS || this == AUTOMOTRIZ;
    }

    public boolean permiteSubcategorias() {
        return this.esCategoriaPrincipal && !this.esGeneral();
    }

    public int obtenerOrdenVisualizacion() {
        return switch (this) {
            case TECNOLOGIA -> 1;
            case ELECTRONICA -> 2;
            case MODA -> 3;
            case ROPA -> 4;
            case HOGAR -> 5;
            case DEPORTES -> 6;
            case SALUD -> 7;
            case BELLEZA -> 8;
            case MASCOTAS -> 9;
            case JUGUETES -> 10;
            case LIBROS -> 11;
            case MUSICA -> 12;
            case ARTEMANUALIDADES -> 13;
            case ALIMENTOS -> 14;
            case AUTOMOTRIZ -> 15;
            case GENERAL -> 98;
            case SIN_CATEGORIA -> 99;
        };
    }

    public List<String> obtenerPalabrasClaveRelacionadas() {
        return switch (this) {
            case TECNOLOGIA -> List.of("tech", "technology", "digital", "computadoras", "software");
            case ELECTRONICA -> List.of("electronics", "gadgets", "dispositivos", "aparatos");
            case MODA -> List.of("fashion", "style", "estilo", "tendencias", "diseño");
            case ROPA -> List.of("clothing", "clothes", "vestimenta", "prendas", "textil");
            case HOGAR -> List.of("home", "casa", "decoracion", "muebles", "domestico");
            case DEPORTES -> List.of("sports", "fitness", "ejercicio", "athletic", "entrenamiento");
            case SALUD -> List.of("health", "medicina", "wellness", "bienestar", "medical");
            case BELLEZA -> List.of("beauty", "cosmetics", "skincare", "makeup", "cuidado");
            case MASCOTAS -> List.of("pets", "animals", "mascota", "veterinario", "animal");
            case JUGUETES -> List.of("toys", "games", "juegos", "infantil", "kids");
            case LIBROS -> List.of("books", "literatura", "education", "lectura", "estudio");
            case MUSICA -> List.of("music", "instruments", "audio", "sonido", "musical");
            case ARTEMANUALIDADES -> List.of("art", "crafts", "creative", "manualidades", "artistico");
            case ALIMENTOS -> List.of("food", "comida", "bebidas", "nutrition", "gourmet");
            case AUTOMOTRIZ -> List.of("automotive", "cars", "vehiculos", "auto", "mecanica");
            default -> List.of("general", "varios", "otros");
        };
    }

    public String obtenerDescripcionSEO() {
        return switch (this) {
            case TECNOLOGIA -> "Encuentra los mejores productos tecnológicos: computadoras, smartphones, tablets y más";
            case ELECTRONICA -> "Dispositivos electrónicos de calidad: gadgets, accesorios y equipos tecnológicos";
            case MODA -> "Moda y estilo: las últimas tendencias en ropa y accesorios para toda ocasión";
            case ROPA -> "Ropa de calidad para hombre, mujer y niños. Encuentra tu estilo perfecto";
            case HOGAR -> "Todo para tu hogar: muebles, decoración y artículos para el hogar";
            case DEPORTES -> "Artículos deportivos y fitness para una vida activa y saludable";
            case SALUD -> "Productos de salud y bienestar para cuidar tu cuerpo y mente";
            case BELLEZA -> "Cosméticos y productos de belleza para lucir siempre radiante";
            case MASCOTAS -> "Todo lo que tu mascota necesita: alimento, juguetes y accesorios";
            case JUGUETES -> "Juguetes educativos y divertidos para niños de todas las edades";
            case LIBROS -> "Libros y material educativo para aprender y entretenerse";
            case MUSICA -> "Instrumentos musicales y equipos de audio de la mejor calidad";
            case ARTEMANUALIDADES -> "Materiales para arte y manualidades. Da rienda suelta a tu creatividad";
            case ALIMENTOS -> "Alimentos frescos y productos gourmet para tu mesa";
            case AUTOMOTRIZ -> "Accesorios y partes automotrices para mantener tu vehículo perfecto";
            default -> "Productos varios y artículos generales para todas tus necesidades";
        };
    }

    // === MÉTODOS DE COMPARACIÓN ===

    public static List<TipoCategoria> ordenarPorVisualizacion() {
        return Arrays.stream(values())
                .sorted((c1, c2) -> Integer.compare(c1.obtenerOrdenVisualizacion(), c2.obtenerOrdenVisualizacion()))
                .collect(Collectors.toList());
    }

    public static List<TipoCategoria> ordenarPorNombre() {
        return Arrays.stream(values())
                .sorted((c1, c2) -> c1.descripcion.compareToIgnoreCase(c2.descripcion))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.format("TipoCategoria{codigo='%s', descripcion='%s', slug='%s', principal=%s}",
                codigo, descripcion, slug, esCategoriaPrincipal);
    }
}
