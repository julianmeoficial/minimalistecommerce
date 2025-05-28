package com.digital.mecommerces.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para recursos no encontrados
 * Optimizada para el sistema medbcommerce 3.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    // Constructor básico
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = "Resource";
        this.fieldName = "id";
        this.fieldValue = null;
    }

    // Constructor con detalles del recurso
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    // Constructor con mensaje personalizado y detalles
    public ResourceNotFoundException(String message, String resourceName, String fieldName, Object fieldValue) {
        super(message);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    // === MÉTODOS ESTÁTICOS PARA CREAR EXCEPCIONES ESPECÍFICAS ===

    public static ResourceNotFoundException usuario(Long id) {
        return new ResourceNotFoundException("Usuario", "ID", id);
    }

    public static ResourceNotFoundException usuarioPorEmail(String email) {
        return new ResourceNotFoundException("Usuario", "email", email);
    }

    public static ResourceNotFoundException producto(Long id) {
        return new ResourceNotFoundException("Producto", "ID", id);
    }

    public static ResourceNotFoundException productoPorSlug(String slug) {
        return new ResourceNotFoundException("Producto", "slug", slug);
    }

    public static ResourceNotFoundException categoria(Long id) {
        return new ResourceNotFoundException("Categoría", "ID", id);
    }

    public static ResourceNotFoundException categoriaPorSlug(String slug) {
        return new ResourceNotFoundException("Categoría", "slug", slug);
    }

    public static ResourceNotFoundException rol(Long id) {
        return new ResourceNotFoundException("Rol", "ID", id);
    }

    public static ResourceNotFoundException rolPorNombre(String nombre) {
        return new ResourceNotFoundException("Rol", "nombre", nombre);
    }

    public static ResourceNotFoundException permiso(Long id) {
        return new ResourceNotFoundException("Permiso", "ID", id);
    }

    public static ResourceNotFoundException permisoPorCodigo(String codigo) {
        return new ResourceNotFoundException("Permiso", "código", codigo);
    }

    public static ResourceNotFoundException carrito(Long id) {
        return new ResourceNotFoundException("Carrito", "ID", id);
    }

    public static ResourceNotFoundException carritoItem(Long id) {
        return new ResourceNotFoundException("Item del carrito", "ID", id);
    }

    public static ResourceNotFoundException orden(Long id) {
        return new ResourceNotFoundException("Orden", "ID", id);
    }

    public static ResourceNotFoundException imagen(Long id) {
        return new ResourceNotFoundException("Imagen", "ID", id);
    }

    public static ResourceNotFoundException imagenPorNombre(String filename) {
        return new ResourceNotFoundException("Imagen", "nombre de archivo", filename);
    }

    public static ResourceNotFoundException detallesComprador(Long usuarioId) {
        return new ResourceNotFoundException("Detalles de comprador", "usuario ID", usuarioId);
    }

    public static ResourceNotFoundException detallesVendedor(Long usuarioId) {
        return new ResourceNotFoundException("Detalles de vendedor", "usuario ID", usuarioId);
    }

    public static ResourceNotFoundException detallesAdmin(Long usuarioId) {
        return new ResourceNotFoundException("Detalles de administrador", "usuario ID", usuarioId);
    }

    public static ResourceNotFoundException tokenReset(String token) {
        return new ResourceNotFoundException("Token de reset", "token", token);
    }

    // === GETTERS ===

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    @Override
    public String toString() {
        return "ResourceNotFoundException{" +
                "message='" + getMessage() + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", fieldValue=" + fieldValue +
                '}';
    }
}
