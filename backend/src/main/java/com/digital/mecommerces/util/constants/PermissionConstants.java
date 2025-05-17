package com.digital.mecommerces.util.constants;

/**
 * Constantes para los permisos en el sistema
 */
public class PermissionConstants {

    /**
     * Permiso de administración total del sistema
     */
    public static final String PERM_ADMIN_TOTAL = "ADMIN_TOTAL";

    /**
     * Permiso para comprar productos
     */
    public static final String PERM_COMPRAR = "COMPRAR_PRODUCTOS";

    /**
     * Permiso para vender productos
     */
    public static final String PERM_VENDER = "VENDER_PRODUCTOS";

    /**
     * Permiso para gestionar usuarios
     */
    public static final String PERM_GESTIONAR_USUARIOS = "GESTIONAR_USUARIOS";

    /**
     * Permiso para gestionar categorías
     */
    public static final String PERM_GESTIONAR_CATEGORIAS = "GESTIONAR_CATEGORIAS";

    /**
     * Prefijo para los permisos en el sistema de seguridad
     */
    public static final String PERMISSION_PREFIX = "PERM_";

    // Constructor privado para evitar instanciación
    private PermissionConstants() {
        throw new IllegalStateException("Utility class");
    }
}
