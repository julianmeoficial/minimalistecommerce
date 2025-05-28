package com.digital.mecommerces.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // === MANEJO DE RESOURCE NOT FOUND ===

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        log.warn("🔍 Recurso no encontrado: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.notFound(ex.getResourceName(), String.valueOf(ex.getFieldValue()))
                .withPath(request.getDescription(false))
                .withData("resourceName", ex.getResourceName())
                .withData("fieldName", ex.getFieldName())
                .withData("fieldValue", ex.getFieldValue());

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // === MANEJO DE BUSINESS EXCEPTIONS ===

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDetails> handleBusinessException(
            BusinessException ex, WebRequest request) {

        log.warn("💼 Error de negocio: {} - Código: {}", ex.getMessage(), ex.getErrorCode());

        ErrorDetails errorDetails = ErrorDetails.businessError(ex.getMessage(), ex.getErrorCode())
                .withPath(request.getDescription(false));

        errorDetails.setStatus(ex.getHttpStatus().value());
        errorDetails.setError(ex.getHttpStatus().getReasonPhrase());

        if (ex.getData() != null) {
            errorDetails.setData((Map<String, Object>) ex.getData());
        }

        return new ResponseEntity<>(errorDetails, ex.getHttpStatus());
    }

    // === MANEJO DE ERRORES DE VALIDACIÓN ===

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDetails> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.warn("✅ Errores de validación encontrados: {} errores", ex.getBindingResult().getErrorCount());

        Map<String, String> errors = new HashMap<>();
        Map<String, Object> rejectedValues = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            Object rejectedValue = ((FieldError) error).getRejectedValue();

            errors.put(fieldName, errorMessage);
            rejectedValues.put(fieldName, rejectedValue);
        });

        ValidationErrorDetails validationErrorDetails = new ValidationErrorDetails(
                LocalDateTime.now(),
                "Error de validación en " + errors.size() + " campo(s)",
                "VALIDATION_ERROR",
                "Uno o más campos no son válidos",
                request.getDescription(false),
                errors,
                rejectedValues
        );

        return new ResponseEntity<>(validationErrorDetails, HttpStatus.BAD_REQUEST);
    }

    // === MANEJO DE CONSTRAINT VIOLATIONS ===

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorDetails> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {

        log.warn("🚫 Violaciones de constraint encontradas: {} violaciones", ex.getConstraintViolations().size());

        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }

        ValidationErrorDetails validationErrorDetails = ValidationErrorDetails.multipleFieldErrors(errors)
                .withPath(request.getDescription(false));

        return new ResponseEntity<>(validationErrorDetails, HttpStatus.BAD_REQUEST);
    }

    // === MANEJO DE ERRORES DE AUTENTICACIÓN ===

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorDetails> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        log.warn("🔐 Error de autenticación: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.authenticationError()
                .withPath(request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // === MANEJO DE ERRORES DE AUTORIZACIÓN ===

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        log.warn("🚫 Acceso denegado: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.authorizationError("acceso al recurso")
                .withPath(request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    // === MANEJO DE TOKEN EXCEPTIONS ===

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorDetails> handleTokenExpiredException(
            TokenExpiredException ex, WebRequest request) {

        log.warn("⏰ Token expirado: {} - Tipo: {}", ex.getMessage(), ex.getTokenType());

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                "TOKEN_EXPIRED",
                "El token ha expirado",
                request.getDescription(false),
                401,
                "Unauthorized"
        );

        errorDetails.withData("tokenType", ex.getTokenType());
        if (ex.getExpirationTime() != null) {
            errorDetails.withData("expirationTime", ex.getExpirationTime());
        }

        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ErrorDetails> handleTokenInvalidException(
            TokenInvalidException ex, WebRequest request) {

        log.warn("❌ Token inválido: {} - Tipo: {} - Razón: {}", ex.getMessage(), ex.getTokenType(), ex.getReason());

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                ex.getMessage(),
                "TOKEN_INVALID",
                "El token proporcionado es inválido",
                request.getDescription(false),
                401,
                "Unauthorized"
        );

        errorDetails.withData("tokenType", ex.getTokenType())
                .withData("reason", ex.getReason());

        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // === MANEJO DE ERRORES DE TIPO DE ARGUMENTO ===

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDetails> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        log.warn("🔄 Error de tipo de argumento: {}", ex.getMessage());

        String message = String.format("El parámetro '%s' debe ser de tipo %s",
                ex.getName(),
                ex.getRequiredType().getSimpleName());

        ErrorDetails errorDetails = ErrorDetails.badRequest(message)
                .withPath(request.getDescription(false))
                .withData("parameter", ex.getName())
                .withData("providedValue", ex.getValue())
                .withData("requiredType", ex.getRequiredType().getSimpleName());

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // === MANEJO DE ERRORES DE UPLOAD ===

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorDetails> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, WebRequest request) {

        log.warn("📁 Archivo demasiado grande: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "El archivo excede el tamaño máximo permitido",
                "FILE_TOO_LARGE",
                "El tamaño del archivo supera el límite establecido",
                request.getDescription(false),
                413,
                "Payload Too Large"
        );

        errorDetails.withData("maxFileSize", ex.getMaxUploadSize());

        return new ResponseEntity<>(errorDetails, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    // === MANEJO DE ILLEGAL ARGUMENT EXCEPTION ===

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("⚠️ Argumento ilegal: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.badRequest(ex.getMessage())
                .withPath(request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // === MANEJO DE ILLEGAL STATE EXCEPTION ===

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDetails> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {

        log.warn("🔴 Estado ilegal: {}", ex.getMessage());

        ErrorDetails errorDetails = ErrorDetails.conflict("estado del sistema", ex.getMessage())
                .withPath(request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    // === MANEJO DE EXCEPCIONES GENERALES ===

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("💥 Error inesperado: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = ErrorDetails.internalServerError()
                .withPath(request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // === MANEJO DE RUNTIME EXCEPTIONS ===

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetails> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        log.error("⚡ Error de runtime: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Error interno en tiempo de ejecución",
                "RUNTIME_ERROR",
                ex.getMessage(),
                request.getDescription(false),
                500,
                "Internal Server Error"
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // === MANEJO DE NULL POINTER EXCEPTION ===

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorDetails> handleNullPointerException(
            NullPointerException ex, WebRequest request) {

        log.error("🎯 Null pointer exception: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                "Error interno: referencia nula detectada",
                "NULL_POINTER",
                "Se ha detectado una referencia nula en el sistema",
                request.getDescription(false),
                500,
                "Internal Server Error"
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
