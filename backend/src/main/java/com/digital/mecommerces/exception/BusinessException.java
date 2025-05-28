package com.digital.mecommerces.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Excepción personalizada para errores de lógica de negocio
 * Optimizada para el sistema medbcommerce 3.0
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final Object data;

    // Constructor básico
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.data = null;
    }

    // Constructor con código de error
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.data = null;
    }

    // Constructor completo
    public BusinessException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.data = null;
    }

    // Constructor con datos adicionales
    public BusinessException(String message, String errorCode, HttpStatus httpStatus, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.data = data;
    }

    // Constructor con causa
    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.data = null;
    }

    // === MÉTODOS ESTÁTICOS PARA CREAR EXCEPCIONES ESPECÍFICAS ===

    public static BusinessException usuarioYaExiste(String email) {
        return new BusinessException(
                "El usuario con email '" + email + "' ya existe en el sistema",
                "USER_ALREADY_EXISTS",
                HttpStatus.CONFLICT
        );
    }

    public static BusinessException productoSinStock(String nombreProducto) {
        return new BusinessException(
                "El producto '" + nombreProducto + "' no tiene stock disponible",
                "PRODUCT_OUT_OF_STOCK",
                HttpStatus.BAD_REQUEST
        );
    }

    public static BusinessException stockInsuficiente(String nombreProducto, int stockDisponible, int cantidadSolicitada) {
        return new BusinessException(
                String.format("Stock insuficiente para '%s'. Disponible: %d, Solicitado: %d",
                        nombreProducto, stockDisponible, cantidadSolicitada),
                "INSUFFICIENT_STOCK",
                HttpStatus.BAD_REQUEST,
                Map.of(
                        "stockDisponible", stockDisponible,
                        "cantidadSolicitada", cantidadSolicitada
                )
        );
    }

    public static BusinessException rolInvalido(String rolNombre) {
        return new BusinessException(
                "El rol '" + rolNombre + "' no es válido o no existe",
                "INVALID_ROLE",
                HttpStatus.BAD_REQUEST
        );
    }

    public static BusinessException permisosDenegados(String accion) {
        return new BusinessException(
                "No tienes permisos para realizar la acción: " + accion,
                "ACCESS_DENIED",
                HttpStatus.FORBIDDEN
        );
    }

    public static BusinessException carritoVacio() {
        return new BusinessException(
                "El carrito está vacío. Agrega productos antes de continuar",
                "EMPTY_CART",
                HttpStatus.BAD_REQUEST
        );
    }

    public static BusinessException vendedorNoVerificado() {
        return new BusinessException(
                "El vendedor debe estar verificado para realizar esta acción",
                "SELLER_NOT_VERIFIED",
                HttpStatus.FORBIDDEN
        );
    }

    public static BusinessException categoriaEnUso(String categoriaNombre) {
        return new BusinessException(
                "La categoría '" + categoriaNombre + "' no puede ser eliminada porque tiene productos asociados",
                "CATEGORY_IN_USE",
                HttpStatus.CONFLICT
        );
    }

    public static BusinessException emailYaRegistrado(String email) {
        return new BusinessException(
                "El email '" + email + "' ya está registrado en el sistema",
                "EMAIL_ALREADY_REGISTERED",
                HttpStatus.CONFLICT
        );
    }

    public static BusinessException credencialesInvalidas() {
        return new BusinessException(
                "Las credenciales proporcionadas son incorrectas",
                "INVALID_CREDENTIALS",
                HttpStatus.UNAUTHORIZED
        );
    }

    public static BusinessException operacionNoPermitida(String operacion, String motivo) {
        return new BusinessException(
                "La operación '" + operacion + "' no está permitida: " + motivo,
                "OPERATION_NOT_ALLOWED",
                HttpStatus.FORBIDDEN
        );
    }

    public static BusinessException limiteExcedido(String limite, int valorActual, int valorMaximo) {
        return new BusinessException(
                String.format("Límite excedido para %s. Actual: %d, Máximo: %d", limite, valorActual, valorMaximo),
                "LIMIT_EXCEEDED",
                HttpStatus.BAD_REQUEST,
                Map.of(
                        "limite", limite,
                        "valorActual", valorActual,
                        "valorMaximo", valorMaximo
                )
        );
    }

    // === GETTERS ===

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "message='" + getMessage() + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", httpStatus=" + httpStatus +
                ", data=" + data +
                '}';
    }
}
