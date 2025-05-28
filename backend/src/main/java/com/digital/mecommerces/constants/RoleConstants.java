package com.digital.mecommerces.constants;

/**
 * Constantes de roles y permisos del sistema
 * Optimizado para el sistema medbcommerce 3.0
 */
public final class RoleConstants {

    // === ROLES DEL SISTEMA ===

    public static final String ROLE_ADMINISTRADOR = "ADMINISTRADOR";
    public static final String ROLE_VENDEDOR = "VENDEDOR";
    public static final String ROLE_COMPRADOR = "COMPRADOR";

    // === PERMISOS PRINCIPALES ===

    // Administración total
    public static final String PERM_ADMIN_TOTAL = "ADMIN_TOTAL";

    // Gestión de usuarios
    public static final String PERM_GESTIONAR_USUARIOS = "GESTIONAR_USUARIOS";

    // Gestión de categorías
    public static final String PERM_GESTIONAR_CATEGORIAS = "GESTIONAR_CATEGORIAS";

    // Ventas y productos
    public static final String PERM_VENDER_PRODUCTOS = "VENDER_PRODUCTOS";
    public static final String PERM_GESTIONAR_INVENTARIO = "GESTIONAR_INVENTARIO";
    public static final String PERM_GESTIONAR_ORDENES = "GESTIONAR_ORDENES";

    // Compras
    public static final String PERM_COMPRAR_PRODUCTOS = "COMPRAR_PRODUCTOS";

    // Reportes y estadísticas
    public static final String PERM_VER_ESTADISTICAS = "VER_ESTADISTICAS";

    // Configuración del sistema
    public static final String PERM_CONFIGURAR_SISTEMA = "CONFIGURAR_SISTEMA";

    // Moderación de contenido
    public static final String PERM_MODERAR_CONTENIDO = "MODERAR_CONTENIDO";

    // Procesamiento de pagos
    public static final String PERM_PROCESAR_PAGOS = "PROCESAR_PAGOS";

    // === NIVELES DE PERMISOS ===

    public static final int NIVEL_SUPER_ADMIN = 1;
    public static final int NIVEL_ADMIN = 2;
    public static final int NIVEL_MODERADOR = 3;
    public static final int NIVEL_OPERADOR = 4;
    public static final int NIVEL_USUARIO = 5;

    // === CATEGORÍAS DE PERMISOS ===

    public static final String CATEGORIA_SISTEMA = "SISTEMA";
    public static final String CATEGORIA_USUARIOS = "USUARIOS";
    public static final String CATEGORIA_PRODUCTOS = "PRODUCTOS";
    public static final String CATEGORIA_CATEGORIAS = "CATEGORIAS";
    public static final String CATEGORIA_VENTAS = "VENTAS";
    public static final String CATEGORIA_COMPRAS = "COMPRAS";
    public static final String CATEGORIA_INVENTARIO = "INVENTARIO";
    public static final String CATEGORIA_REPORTES = "REPORTES";
    public static final String CATEGORIA_CONFIGURACION = "CONFIGURACION";
    public static final String CATEGORIA_MODERACION = "MODERACION";
    public static final String CATEGORIA_PAGOS = "PAGOS";

    // === CÓDIGOS DE ROL PARA BASE DE DATOS ===

    public static final Long ROLE_ID_ADMINISTRADOR = 1L;
    public static final Long ROLE_ID_VENDEDOR = 2L;
    public static final Long ROLE_ID_COMPRADOR = 3L;

    // === PATRONES DE AUTORIDAD SPRING SECURITY ===

    public static final String AUTHORITY_PREFIX = "ROLE_";
    public static final String PERMISSION_PREFIX = "PERM_";

    public static final String AUTHORITY_ADMIN = AUTHORITY_PREFIX + ROLE_ADMINISTRADOR;
    public static final String AUTHORITY_VENDEDOR = AUTHORITY_PREFIX + ROLE_VENDEDOR;
    public static final String AUTHORITY_COMPRADOR = AUTHORITY_PREFIX + ROLE_COMPRADOR;

    // === CONJUNTOS DE PERMISOS POR ROL ===

    public static final String[] PERMISOS_ADMINISTRADOR = {
            PERM_ADMIN_TOTAL,
            PERM_GESTIONAR_USUARIOS,
            PERM_GESTIONAR_CATEGORIAS,
            PERM_VENDER_PRODUCTOS,
            PERM_GESTIONAR_INVENTARIO,
            PERM_GESTIONAR_ORDENES,
            PERM_COMPRAR_PRODUCTOS,
            PERM_VER_ESTADISTICAS,
            PERM_CONFIGURAR_SISTEMA,
            PERM_MODERAR_CONTENIDO,
            PERM_PROCESAR_PAGOS
    };

    public static final String[] PERMISOS_VENDEDOR = {
            PERM_VENDER_PRODUCTOS,
            PERM_GESTIONAR_INVENTARIO,
            PERM_GESTIONAR_ORDENES,
            PERM_VER_ESTADISTICAS
    };

    public static final String[] PERMISOS_COMPRADOR = {
            PERM_COMPRAR_PRODUCTOS
    };

    // === VALIDACIONES ===

    public static boolean esRolValido(String rol) {
        return ROLE_ADMINISTRADOR.equals(rol) ||
                ROLE_VENDEDOR.equals(rol) ||
                ROLE_COMPRADOR.equals(rol);
    }

    public static boolean esPermisoValido(String permiso) {
        for (String permisoAdmin : PERMISOS_ADMINISTRADOR) {
            if (permisoAdmin.equals(permiso)) {
                return true;
            }
        }
        return false;
    }

    public static boolean rolTienePermiso(String rol, String permiso) {
        if (ROLE_ADMINISTRADOR.equals(rol)) {
            return true; // Admin tiene todos los permisos
        }

        if (ROLE_VENDEDOR.equals(rol)) {
            for (String permisoVendedor : PERMISOS_VENDEDOR) {
                if (permisoVendedor.equals(permiso)) {
                    return true;
                }
            }
        }

        if (ROLE_COMPRADOR.equals(rol)) {
            for (String permisoComprador : PERMISOS_COMPRADOR) {
                if (permisoComprador.equals(permiso)) {
                    return true;
                }
            }
        }

        return false;
    }

    // === MENSAJES DE ERROR ===

    public static final String ERROR_ROL_INVALIDO = "El rol especificado no es válido";
    public static final String ERROR_PERMISO_INVALIDO = "El permiso especificado no es válido";
    public static final String ERROR_ACCESO_DENEGADO = "Acceso denegado: permisos insuficientes";
    public static final String ERROR_ROL_NO_ENCONTRADO = "Rol no encontrado en el sistema";
    public static final String ERROR_PERMISO_NO_ENCONTRADO = "Permiso no encontrado en el sistema";

    // === DESCRIPCIÓN DE ROLES ===

    public static String getDescripcionRol(String rol) {
        return switch (rol) {
            case ROLE_ADMINISTRADOR -> "Administrador del sistema con acceso total";
            case ROLE_VENDEDOR -> "Usuario vendedor de productos";
            case ROLE_COMPRADOR -> "Usuario comprador de productos";
            default -> "Rol desconocido";
        };
    }

    public static String getDescripcionPermiso(String permiso) {
        return switch (permiso) {
            case PERM_ADMIN_TOTAL -> "Administración total del sistema";
            case PERM_GESTIONAR_USUARIOS -> "Gestionar usuarios del sistema";
            case PERM_GESTIONAR_CATEGORIAS -> "Gestionar categorías de productos";
            case PERM_VENDER_PRODUCTOS -> "Vender y gestionar productos";
            case PERM_GESTIONAR_INVENTARIO -> "Gestionar inventario de productos";
            case PERM_GESTIONAR_ORDENES -> "Gestionar órdenes y pedidos";
            case PERM_COMPRAR_PRODUCTOS -> "Comprar productos del sistema";
            case PERM_VER_ESTADISTICAS -> "Ver estadísticas y reportes";
            case PERM_CONFIGURAR_SISTEMA -> "Configurar parámetros del sistema";
            case PERM_MODERAR_CONTENIDO -> "Moderar contenido y reseñas";
            case PERM_PROCESAR_PAGOS -> "Procesar pagos y transacciones";
            default -> "Permiso desconocido";
        };
    }

    // Constructor privado para evitar instanciación
    private RoleConstants() {
        throw new UnsupportedOperationException("Esta es una clase de constantes y no puede ser instanciada");
    }
}
