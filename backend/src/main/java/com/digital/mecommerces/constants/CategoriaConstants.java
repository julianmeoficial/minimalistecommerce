package com.digital.mecommerces.constants;

import java.util.Arrays;
import java.util.List;

/**
 * Constantes de categorías del sistema
 * Optimizado para el sistema medbcommerce 3.0
 */
public final class CategoriaConstants {

    // === CATEGORÍAS PRINCIPALES ===

    public static final String TECNOLOGIA = "TECNOLOGIA";
    public static final String ELECTRONICA = "ELECTRONICA";
    public static final String HOGAR = "HOGAR";
    public static final String MODA = "MODA";
    public static final String ROPA = "ROPA";
    public static final String MASCOTAS = "MASCOTAS";
    public static final String ARTEMANUALIDADES = "ARTEMANUALIDADES";
    public static final String DEPORTES = "DEPORTES";
    public static final String LIBROS = "LIBROS";
    public static final String JUGUETES = "JUGUETES";
    public static final String SALUD = "SALUD";
    public static final String BELLEZA = "BELLEZA";
    public static final String AUTOMOTRIZ = "AUTOMOTRIZ";
    public static final String ALIMENTOS = "ALIMENTOS";
    public static final String MUSICA = "MUSICA";

    // === CATEGORÍAS ESPECIALES ===

    public static final String GENERAL = "GENERAL";
    public static final String SIN_CATEGORIA = "SIN_CATEGORIA";

    // === SLUGS DE CATEGORÍAS ===

    public static final String SLUG_TECNOLOGIA = "tecnologia";
    public static final String SLUG_ELECTRONICA = "electronica";
    public static final String SLUG_HOGAR = "hogar";
    public static final String SLUG_MODA = "moda";
    public static final String SLUG_ROPA = "ropa";
    public static final String SLUG_MASCOTAS = "mascotas";
    public static final String SLUG_ARTEMANUALIDADES = "arte-manualidades";
    public static final String SLUG_DEPORTES = "deportes";
    public static final String SLUG_LIBROS = "libros";
    public static final String SLUG_JUGUETES = "juguetes";
    public static final String SLUG_SALUD = "salud";
    public static final String SLUG_BELLEZA = "belleza";
    public static final String SLUG_AUTOMOTRIZ = "automotriz";
    public static final String SLUG_ALIMENTOS = "alimentos";
    public static final String SLUG_MUSICA = "musica";
    public static final String SLUG_GENERAL = "general";
    public static final String SLUG_SIN_CATEGORIA = "sin-categoria";

    // === ICONOS DE CATEGORÍAS ===

    public static final String ICON_TECNOLOGIA = "💻";
    public static final String ICON_ELECTRONICA = "⚡";
    public static final String ICON_HOGAR = "🏠";
    public static final String ICON_MODA = "👗";
    public static final String ICON_ROPA = "👕";
    public static final String ICON_MASCOTAS = "🐕";
    public static final String ICON_ARTEMANUALIDADES = "🎨";
    public static final String ICON_DEPORTES = "⚽";
    public static final String ICON_LIBROS = "📚";
    public static final String ICON_JUGUETES = "🧸";
    public static final String ICON_SALUD = "🏥";
    public static final String ICON_BELLEZA = "💄";
    public static final String ICON_AUTOMOTRIZ = "🚗";
    public static final String ICON_ALIMENTOS = "🍎";
    public static final String ICON_MUSICA = "🎵";
    public static final String ICON_GENERAL = "📦";
    public static final String ICON_SIN_CATEGORIA = "❓";

    // === COLORES DE CATEGORÍAS ===

    public static final String COLOR_TECNOLOGIA = "#3b82f6";
    public static final String COLOR_ELECTRONICA = "#6366f1";
    public static final String COLOR_HOGAR = "#10b981";
    public static final String COLOR_MODA = "#ec4899";
    public static final String COLOR_ROPA = "#f59e0b";
    public static final String COLOR_MASCOTAS = "#8b5cf6";
    public static final String COLOR_ARTEMANUALIDADES = "#ef4444";
    public static final String COLOR_DEPORTES = "#059669";
    public static final String COLOR_LIBROS = "#7c3aed";
    public static final String COLOR_JUGUETES = "#f97316";
    public static final String COLOR_SALUD = "#06b6d4";
    public static final String COLOR_BELLEZA = "#e11d48";
    public static final String COLOR_AUTOMOTRIZ = "#374151";
    public static final String COLOR_ALIMENTOS = "#65a30d";
    public static final String COLOR_MUSICA = "#7c2d12";
    public static final String COLOR_GENERAL = "#6b7280";
    public static final String COLOR_SIN_CATEGORIA = "#9ca3af";

    // === LÍMITES Y CONFIGURACIÓN ===

    public static final int MAX_NOMBRE_LENGTH = 50;
    public static final int MAX_DESCRIPCION_LENGTH = 255;
    public static final int MAX_SLUG_LENGTH = 100;
    public static final int MAX_IMAGEN_URL_LENGTH = 255;
    public static final int MAX_NIVEL_JERARQUIA = 5;
    public static final int MAX_SUBCATEGORIAS_POR_CATEGORIA = 20;

    // === PATRONES DE VALIDACIÓN ===

    public static final String PATRON_SLUG = "^[a-z0-9]+(?:-[a-z0-9]+)*$";
    public static final String PATRON_COLOR_HEX = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
    public static final String PATRON_NOMBRE_CATEGORIA = "^[A-ZÁÉÍÓÚÑ][A-Za-záéíóúñ\\s]{1,49}$";

    // === LISTAS DE CATEGORÍAS ===

    public static final List<String> CATEGORIAS_PRINCIPALES = Arrays.asList(
            TECNOLOGIA, ELECTRONICA, HOGAR, MODA, ROPA, MASCOTAS,
            ARTEMANUALIDADES, DEPORTES, LIBROS, JUGUETES, SALUD,
            BELLEZA, AUTOMOTRIZ, ALIMENTOS, MUSICA
    );

    public static final List<String> CATEGORIAS_ESPECIALES = Arrays.asList(
            GENERAL, SIN_CATEGORIA
    );

    public static final List<String> CATEGORIAS_ELECTRONICAS = Arrays.asList(
            TECNOLOGIA, ELECTRONICA
    );

    public static final List<String> CATEGORIAS_VESTIMENTA = Arrays.asList(
            MODA, ROPA
    );

    public static final List<String> CATEGORIAS_PERSONALES = Arrays.asList(
            SALUD, BELLEZA, DEPORTES
    );

    public static final List<String> CATEGORIAS_CREATIVAS = Arrays.asList(
            ARTEMANUALIDADES, LIBROS, MUSICA
    );

    // === AGRUPACIONES POR CARACTERÍSTICAS ===

    public static final List<String> CATEGORIAS_QUE_REQUIEREN_VERIFICACION = Arrays.asList(
            SALUD, ALIMENTOS, AUTOMOTRIZ
    );

    public static final List<String> CATEGORIAS_CON_SUBCATEGORIAS = Arrays.asList(
            TECNOLOGIA, ELECTRONICA, ROPA, MODA, HOGAR, DEPORTES
    );

    public static final List<String> CATEGORIAS_POPULARES = Arrays.asList(
            TECNOLOGIA, ELECTRONICA, ROPA, MODA, HOGAR
    );

    // === ÓRDENES DE VISUALIZACIÓN ===

    public static final int ORDEN_TECNOLOGIA = 1;
    public static final int ORDEN_ELECTRONICA = 2;
    public static final int ORDEN_MODA = 3;
    public static final int ORDEN_ROPA = 4;
    public static final int ORDEN_HOGAR = 5;
    public static final int ORDEN_DEPORTES = 6;
    public static final int ORDEN_SALUD = 7;
    public static final int ORDEN_BELLEZA = 8;
    public static final int ORDEN_MASCOTAS = 9;
    public static final int ORDEN_JUGUETES = 10;
    public static final int ORDEN_LIBROS = 11;
    public static final int ORDEN_MUSICA = 12;
    public static final int ORDEN_ARTEMANUALIDADES = 13;
    public static final int ORDEN_ALIMENTOS = 14;
    public static final int ORDEN_AUTOMOTRIZ = 15;
    public static final int ORDEN_GENERAL = 98;
    public static final int ORDEN_SIN_CATEGORIA = 99;

    // === MÉTODOS DE VALIDACIÓN ===

    public static boolean esCategoriaValida(String categoria) {
        return CATEGORIAS_PRINCIPALES.contains(categoria) ||
                CATEGORIAS_ESPECIALES.contains(categoria);
    }

    public static boolean esCategoriaEspecial(String categoria) {
        return CATEGORIAS_ESPECIALES.contains(categoria);
    }

    public static boolean esCategoriaPrincipal(String categoria) {
        return CATEGORIAS_PRINCIPALES.contains(categoria);
    }

    public static boolean requiereVerificacion(String categoria) {
        return CATEGORIAS_QUE_REQUIEREN_VERIFICACION.contains(categoria);
    }

    public static boolean permiteSubcategorias(String categoria) {
        return CATEGORIAS_CON_SUBCATEGORIAS.contains(categoria);
    }

    // === MÉTODOS DE UTILIDAD ===

    public static String obtenerIcono(String categoria) {
        return switch (categoria) {
            case TECNOLOGIA -> ICON_TECNOLOGIA;
            case ELECTRONICA -> ICON_ELECTRONICA;
            case HOGAR -> ICON_HOGAR;
            case MODA -> ICON_MODA;
            case ROPA -> ICON_ROPA;
            case MASCOTAS -> ICON_MASCOTAS;
            case ARTEMANUALIDADES -> ICON_ARTEMANUALIDADES;
            case DEPORTES -> ICON_DEPORTES;
            case LIBROS -> ICON_LIBROS;
            case JUGUETES -> ICON_JUGUETES;
            case SALUD -> ICON_SALUD;
            case BELLEZA -> ICON_BELLEZA;
            case AUTOMOTRIZ -> ICON_AUTOMOTRIZ;
            case ALIMENTOS -> ICON_ALIMENTOS;
            case MUSICA -> ICON_MUSICA;
            case GENERAL -> ICON_GENERAL;
            case SIN_CATEGORIA -> ICON_SIN_CATEGORIA;
            default -> "❓";
        };
    }

    public static String obtenerColor(String categoria) {
        return switch (categoria) {
            case TECNOLOGIA -> COLOR_TECNOLOGIA;
            case ELECTRONICA -> COLOR_ELECTRONICA;
            case HOGAR -> COLOR_HOGAR;
            case MODA -> COLOR_MODA;
            case ROPA -> COLOR_ROPA;
            case MASCOTAS -> COLOR_MASCOTAS;
            case ARTEMANUALIDADES -> COLOR_ARTEMANUALIDADES;
            case DEPORTES -> COLOR_DEPORTES;
            case LIBROS -> COLOR_LIBROS;
            case JUGUETES -> COLOR_JUGUETES;
            case SALUD -> COLOR_SALUD;
            case BELLEZA -> COLOR_BELLEZA;
            case AUTOMOTRIZ -> COLOR_AUTOMOTRIZ;
            case ALIMENTOS -> COLOR_ALIMENTOS;
            case MUSICA -> COLOR_MUSICA;
            case GENERAL -> COLOR_GENERAL;
            case SIN_CATEGORIA -> COLOR_SIN_CATEGORIA;
            default -> COLOR_GENERAL;
        };
    }

    public static String obtenerSlug(String categoria) {
        return switch (categoria) {
            case TECNOLOGIA -> SLUG_TECNOLOGIA;
            case ELECTRONICA -> SLUG_ELECTRONICA;
            case HOGAR -> SLUG_HOGAR;
            case MODA -> SLUG_MODA;
            case ROPA -> SLUG_ROPA;
            case MASCOTAS -> SLUG_MASCOTAS;
            case ARTEMANUALIDADES -> SLUG_ARTEMANUALIDADES;
            case DEPORTES -> SLUG_DEPORTES;
            case LIBROS -> SLUG_LIBROS;
            case JUGUETES -> SLUG_JUGUETES;
            case SALUD -> SLUG_SALUD;
            case BELLEZA -> SLUG_BELLEZA;
            case AUTOMOTRIZ -> SLUG_AUTOMOTRIZ;
            case ALIMENTOS -> SLUG_ALIMENTOS;
            case MUSICA -> SLUG_MUSICA;
            case GENERAL -> SLUG_GENERAL;
            case SIN_CATEGORIA -> SLUG_SIN_CATEGORIA;
            default -> "general";
        };
    }

    public static int obtenerOrden(String categoria) {
        return switch (categoria) {
            case TECNOLOGIA -> ORDEN_TECNOLOGIA;
            case ELECTRONICA -> ORDEN_ELECTRONICA;
            case MODA -> ORDEN_MODA;
            case ROPA -> ORDEN_ROPA;
            case HOGAR -> ORDEN_HOGAR;
            case DEPORTES -> ORDEN_DEPORTES;
            case SALUD -> ORDEN_SALUD;
            case BELLEZA -> ORDEN_BELLEZA;
            case MASCOTAS -> ORDEN_MASCOTAS;
            case JUGUETES -> ORDEN_JUGUETES;
            case LIBROS -> ORDEN_LIBROS;
            case MUSICA -> ORDEN_MUSICA;
            case ARTEMANUALIDADES -> ORDEN_ARTEMANUALIDADES;
            case ALIMENTOS -> ORDEN_ALIMENTOS;
            case AUTOMOTRIZ -> ORDEN_AUTOMOTRIZ;
            case GENERAL -> ORDEN_GENERAL;
            case SIN_CATEGORIA -> ORDEN_SIN_CATEGORIA;
            default -> 999;
        };
    }

    // === MENSAJES DE ERROR ===

    public static final String ERROR_CATEGORIA_INVALIDA = "La categoría especificada no es válida";
    public static final String ERROR_SLUG_INVALIDO = "El slug de la categoría no tiene un formato válido";
    public static final String ERROR_NOMBRE_DEMASIADO_LARGO = "El nombre de la categoría excede la longitud máxima";
    public static final String ERROR_JERARQUIA_EXCEDIDA = "Se ha excedido el nivel máximo de jerarquía";
    public static final String ERROR_SUBCATEGORIAS_EXCEDIDAS = "Se ha excedido el número máximo de subcategorías";

    // Constructor privado para evitar instanciación
    private CategoriaConstants() {
        throw new UnsupportedOperationException("Esta es una clase de constantes y no puede ser instanciada");
    }
}
