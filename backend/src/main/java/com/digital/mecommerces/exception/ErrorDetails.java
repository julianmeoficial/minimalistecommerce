package com.digital.mecommerces.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para encapsular detalles de errores en respuestas HTTP
 * Optimizada para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {

    private LocalDateTime timestamp;
    private String message;
    private String errorCode;
    private String details;
    private String path;
    private Integer status;
    private String error;
    private Map<String, Object> data;

    // Constructor básico
    public ErrorDetails(LocalDateTime timestamp, String message, String errorCode, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.errorCode = errorCode;
        this.details = details;
    }

    // Constructor completo
    public ErrorDetails(LocalDateTime timestamp, String message, String errorCode,
                        String details, String path, Integer status, String error) {
        this.timestamp = timestamp;
        this.message = message;
        this.errorCode = errorCode;
        this.details = details;
        this.path = path;
        this.status = status;
        this.error = error;
    }

    // Constructor con datos adicionales
    public ErrorDetails(LocalDateTime timestamp, String message, String errorCode,
                        String details, String path, Integer status, String error, Map<String, Object> data) {
        this.timestamp = timestamp;
        this.message = message;
        this.errorCode = errorCode;
        this.details = details;
        this.path = path;
        this.status = status;
        this.error = error;
        this.data = data;
    }

    // === MÉTODOS ESTÁTICOS PARA CREAR ERRORES ESPECÍFICOS ===

    public static ErrorDetails notFound(String resource, String identifier) {
        return new ErrorDetails(
                LocalDateTime.now(),
                resource + " no encontrado con identificador: " + identifier,
                "RESOURCE_NOT_FOUND",
                "El recurso solicitado no existe en el sistema"
        );
    }

    public static ErrorDetails validationError(String field, String message) {
        return new ErrorDetails(
                LocalDateTime.now(),
                "Error de validación en el campo: " + field,
                "VALIDATION_ERROR",
                message,
                null,
                400,
                "Bad Request"
        );
    }

    public static ErrorDetails authenticationError() {
        return new ErrorDetails(
                LocalDateTime.now(),
                "Error de autenticación",
                "AUTHENTICATION_ERROR",
                "Las credenciales proporcionadas son inválidas",
                null,
                401,
                "Unauthorized"
        );
    }

    public static ErrorDetails authorizationError(String action) {
        return new ErrorDetails(
                LocalDateTime.now(),
                "No tienes permisos para realizar esta acción: " + action,
                "AUTHORIZATION_ERROR",
                "Permisos insuficientes",
                null,
                403,
                "Forbidden"
        );
    }

    public static ErrorDetails businessError(String message, String errorCode) {
        return new ErrorDetails(
                LocalDateTime.now(),
                message,
                errorCode,
                "Error en la lógica de negocio",
                null,
                400,
                "Bad Request"
        );
    }

    public static ErrorDetails internalServerError() {
        return new ErrorDetails(
                LocalDateTime.now(),
                "Error interno del servidor",
                "INTERNAL_SERVER_ERROR",
                "Ha ocurrido un error inesperado en el servidor",
                null,
                500,
                "Internal Server Error"
        );
    }

    public static ErrorDetails badRequest(String message) {
        return new ErrorDetails(
                LocalDateTime.now(),
                message,
                "BAD_REQUEST",
                "La solicitud no es válida",
                null,
                400,
                "Bad Request"
        );
    }

    public static ErrorDetails conflict(String resource, String reason) {
        return new ErrorDetails(
                LocalDateTime.now(),
                "Conflicto con " + resource + ": " + reason,
                "CONFLICT",
                "El recurso no puede ser procesado debido a un conflicto",
                null,
                409,
                "Conflict"
        );
    }

    public static ErrorDetails serviceUnavailable(String service) {
        return new ErrorDetails(
                LocalDateTime.now(),
                "El servicio " + service + " no está disponible temporalmente",
                "SERVICE_UNAVAILABLE",
                "Servicio temporalmente no disponible",
                null,
                503,
                "Service Unavailable"
        );
    }

    public static ErrorDetails tooManyRequests() {
        return new ErrorDetails(
                LocalDateTime.now(),
                "Demasiadas solicitudes. Intenta nuevamente más tarde",
                "TOO_MANY_REQUESTS",
                "Límite de velocidad excedido",
                null,
                429,
                "Too Many Requests"
        );
    }

    // === MÉTODOS DE UTILIDAD ===

    public ErrorDetails withPath(String path) {
        this.path = path;
        return this;
    }

    public ErrorDetails withData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public ErrorDetails withData(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);
        return this;
    }

    public boolean hasData() {
        return data != null && !data.isEmpty();
    }

    public boolean isClientError() {
        return status != null && status >= 400 && status < 500;
    }

    public boolean isServerError() {
        return status != null && status >= 500;
    }

    @Override
    public String toString() {
        return "ErrorDetails{" +
                "timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", status=" + status +
                ", path='" + path + '\'' +
                '}';
    }
}
