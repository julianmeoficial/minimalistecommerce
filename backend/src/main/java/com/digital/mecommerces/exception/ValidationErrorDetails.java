package com.digital.mecommerces.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase especializada para errores de validación
 * Extiende ErrorDetails para incluir información específica de validación
 * Optimizada para el sistema medbcommerce 3.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationErrorDetails extends ErrorDetails {

    private Map<String, String> validationErrors;
    private Map<String, Object> rejectedValues;
    private Integer errorCount;

    // Constructor básico
    public ValidationErrorDetails(LocalDateTime timestamp, String message, String errorCode,
                                  String details, Map<String, String> validationErrors) {
        super(timestamp, message, errorCode, details);
        this.validationErrors = validationErrors != null ? validationErrors : new HashMap<>();
        this.rejectedValues = new HashMap<>();
        this.errorCount = this.validationErrors.size();
        setStatus(400);
        setError("Bad Request");
    }

    // Constructor completo
    public ValidationErrorDetails(LocalDateTime timestamp, String message, String errorCode,
                                  String details, String path, Map<String, String> validationErrors) {
        super(timestamp, message, errorCode, details, path, 400, "Bad Request");
        this.validationErrors = validationErrors != null ? validationErrors : new HashMap<>();
        this.rejectedValues = new HashMap<>();
        this.errorCount = this.validationErrors.size();
    }

    // Constructor con valores rechazados
    public ValidationErrorDetails(LocalDateTime timestamp, String message, String errorCode,
                                  String details, String path, Map<String, String> validationErrors,
                                  Map<String, Object> rejectedValues) {
        super(timestamp, message, errorCode, details, path, 400, "Bad Request");
        this.validationErrors = validationErrors != null ? validationErrors : new HashMap<>();
        this.rejectedValues = rejectedValues != null ? rejectedValues : new HashMap<>();
        this.errorCount = this.validationErrors.size();
    }

    // === MÉTODOS ESTÁTICOS PARA CREAR ERRORES DE VALIDACIÓN ===

    public static ValidationErrorDetails singleFieldError(String field, String error) {
        Map<String, String> errors = Map.of(field, error);
        return new ValidationErrorDetails(
                LocalDateTime.now(),
                "Error de validación en el campo: " + field,
                "VALIDATION_ERROR",
                "Uno o más campos no son válidos",
                errors
        );
    }

    public static ValidationErrorDetails multipleFieldErrors(Map<String, String> errors) {
        return new ValidationErrorDetails(
                LocalDateTime.now(),
                "Errores de validación en múltiples campos",
                "VALIDATION_ERROR",
                "Uno o más campos no son válidos",
                errors
        );
    }

    public static ValidationErrorDetails requiredFieldMissing(String field) {
        return singleFieldError(field, "Este campo es obligatorio");
    }

    public static ValidationErrorDetails invalidFormat(String field, String expectedFormat) {
        return singleFieldError(field, "Formato inválido. Formato esperado: " + expectedFormat);
    }

    public static ValidationErrorDetails valueTooLong(String field, int maxLength) {
        return singleFieldError(field, "El valor excede la longitud máxima de " + maxLength + " caracteres");
    }

    public static ValidationErrorDetails valueTooShort(String field, int minLength) {
        return singleFieldError(field, "El valor debe tener al menos " + minLength + " caracteres");
    }

    public static ValidationErrorDetails invalidRange(String field, Object min, Object max) {
        return singleFieldError(field, "El valor debe estar entre " + min + " y " + max);
    }

    public static ValidationErrorDetails invalidEmail(String field) {
        return singleFieldError(field, "Debe ser una dirección de email válida");
    }

    public static ValidationErrorDetails invalidUrl(String field) {
        return singleFieldError(field, "Debe ser una URL válida");
    }

    public static ValidationErrorDetails invalidDate(String field) {
        return singleFieldError(field, "Debe ser una fecha válida");
    }

    public static ValidationErrorDetails invalidNumber(String field) {
        return singleFieldError(field, "Debe ser un número válido");
    }

    public static ValidationErrorDetails valueNotPositive(String field) {
        return singleFieldError(field, "El valor debe ser positivo");
    }

    public static ValidationErrorDetails customValidationError(String field, String message, Object rejectedValue) {
        ValidationErrorDetails error = singleFieldError(field, message);
        error.addRejectedValue(field, rejectedValue);
        return error;
    }

    // === MÉTODOS PARA AGREGAR ERRORES ===

    public void addValidationError(String field, String error) {
        if (this.validationErrors == null) {
            this.validationErrors = new HashMap<>();
        }
        this.validationErrors.put(field, error);
        updateErrorCount();
    }

    public void addValidationErrors(Map<String, String> errors) {
        if (this.validationErrors == null) {
            this.validationErrors = new HashMap<>();
        }
        this.validationErrors.putAll(errors);
        updateErrorCount();
    }

    public void addRejectedValue(String field, Object value) {
        if (this.rejectedValues == null) {
            this.rejectedValues = new HashMap<>();
        }
        this.rejectedValues.put(field, value);
    }

    public void addRejectedValues(Map<String, Object> values) {
        if (this.rejectedValues == null) {
            this.rejectedValues = new HashMap<>();
        }
        this.rejectedValues.putAll(values);
    }

    // === MÉTODOS DE UTILIDAD ===

    public boolean hasValidationErrors() {
        return validationErrors != null && !validationErrors.isEmpty();
    }

    public boolean hasRejectedValues() {
        return rejectedValues != null && !rejectedValues.isEmpty();
    }

    public boolean hasErrorForField(String field) {
        return validationErrors != null && validationErrors.containsKey(field);
    }

    public String getErrorForField(String field) {
        return validationErrors != null ? validationErrors.get(field) : null;
    }

    public Object getRejectedValueForField(String field) {
        return rejectedValues != null ? rejectedValues.get(field) : null;
    }

    public boolean isMultipleFieldError() {
        return errorCount != null && errorCount > 1;
    }

    private void updateErrorCount() {
        this.errorCount = validationErrors != null ? validationErrors.size() : 0;
    }

    // === MÉTODOS PARA CREAR RESPUESTAS ESPECÍFICAS ===

    public ValidationErrorDetails withPath(String path) {
        setPath(path);
        return this;
    }

    public ValidationErrorDetails withAdditionalData(String key, Object value) {
        Map<String, Object> data = getData();
        if (data == null) {
            data = new HashMap<>();
            setData(data);
        }
        data.put(key, value);
        return this;
    }

    // === GETTERS Y SETTERS ESPECÍFICOS ===

    public Map<String, String> getValidationErrors() {
        return validationErrors != null ? validationErrors : new HashMap<>();
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
        updateErrorCount();
    }

    public Map<String, Object> getRejectedValues() {
        return rejectedValues != null ? rejectedValues : new HashMap<>();
    }

    public void setRejectedValues(Map<String, Object> rejectedValues) {
        this.rejectedValues = rejectedValues;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    @Override
    public String toString() {
        return "ValidationErrorDetails{" +
                "message='" + getMessage() + '\'' +
                ", errorCount=" + errorCount +
                ", validationErrors=" + validationErrors +
                ", timestamp=" + getTimestamp() +
                ", path='" + getPath() + '\'' +
                '}';
    }
}
