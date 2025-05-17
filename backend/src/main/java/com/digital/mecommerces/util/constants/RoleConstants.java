package com.digital.mecommerces.util.constants;

/**
 * Constantes para los roles de usuario en el sistema
 */
public class RoleConstants {

    /**
     * Rol de administrador con acceso total al sistema
     */
    public static final String ROLE_ADMIN = "ADMINISTRADOR";

    /**
     * Rol de comprador que puede realizar compras
     */
    public static final String ROLE_COMPRADOR = "COMPRADOR";

    /**
     * Rol de vendedor que puede gestionar productos
     */
    public static final String ROLE_VENDEDOR = "VENDEDOR";

    // Constructor privado para evitar instanciación
    private RoleConstants() {
        throw new IllegalStateException("Utility class");
    }
}

