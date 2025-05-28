package com.digital.mecommerces.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Aspecto para logging avanzado del sistema
 * Optimizado para el sistema medbcommerce 3.0
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // === MANEJO DE EXCEPCIONES ===

    @AfterThrowing(pointcut = "execution(* com.digital.mecommerces.service.*.*(..))", throwing = "exception")
    public void logServiceException(JoinPoint joinPoint, Throwable exception) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : "SISTEMA";
            String service = getSimpleClassName(joinPoint.getSignature().getDeclaringTypeName());
            String method = joinPoint.getSignature().getName();
            String timestamp = LocalDateTime.now().format(FORMATTER);

            log.error("💥 SERVICE_ERROR | {} | Usuario: {} | Servicio: {}.{} | Error: {} | Causa: {}",
                    timestamp, username, service, method,
                    exception.getClass().getSimpleName(),
                    exception.getMessage() != null ? exception.getMessage() : "SIN_MENSAJE");

            // Log adicional para excepciones críticas
            if (isCriticalException(exception)) {
                log.error("🚨 CRITICAL_ERROR | {} | Usuario: {} | Servicio: {}.{} | Args: {} | StackTrace: {}",
                        timestamp, username, service, method,
                        Arrays.toString(joinPoint.getArgs()),
                        getStackTraceString(exception));
            }

        } catch (Exception e) {
            log.error("❌ Error en logging de excepción de servicio: {}", e.getMessage());
        }
    }

    @AfterThrowing(pointcut = "execution(* com.digital.mecommerces.controller.*.*(..))", throwing = "exception")
    public void logControllerException(JoinPoint joinPoint, Throwable exception) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : "ANÓNIMO";
            String controller = getSimpleClassName(joinPoint.getSignature().getDeclaringTypeName());
            String method = joinPoint.getSignature().getName();
            String timestamp = LocalDateTime.now().format(FORMATTER);

            log.error("🔥 CONTROLLER_ERROR | {} | Usuario: {} | Controller: {}.{} | Error: {} | Mensaje: {}",
                    timestamp, username, controller, method,
                    exception.getClass().getSimpleName(),
                    exception.getMessage() != null ? exception.getMessage() : "SIN_MENSAJE");

        } catch (Exception e) {
            log.error("❌ Error en logging de excepción de controller: {}", e.getMessage());
        }
    }

    @AfterThrowing(pointcut = "execution(* com.digital.mecommerces.repository.*.*(..))", throwing = "exception")
    public void logRepositoryException(JoinPoint joinPoint, Throwable exception) {
        try {
            String repository = getSimpleClassName(joinPoint.getSignature().getDeclaringTypeName());
            String method = joinPoint.getSignature().getName();
            String timestamp = LocalDateTime.now().format(FORMATTER);

            log.error("💾 REPOSITORY_ERROR | {} | Repository: {}.{} | Error: {} | Mensaje: {}",
                    timestamp, repository, method,
                    exception.getClass().getSimpleName(),
                    exception.getMessage() != null ? exception.getMessage() : "SIN_MENSAJE");

        } catch (Exception e) {
            log.error("❌ Error en logging de excepción de repository: {}", e.getMessage());
        }
    }

    // === MEDICIÓN DE PERFORMANCE ===

    @Around("execution(* com.digital.mecommerces.service.*.*(..))")
    public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String service = getSimpleClassName(joinPoint.getSignature().getDeclaringTypeName());
        String method = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > 1000) { // Log si toma más de 1 segundo
                log.warn("🐌 SLOW_SERVICE | Servicio: {}.{} | Tiempo: {}ms",
                        service, method, executionTime);
            } else if (executionTime > 500) { // Log si toma más de 500ms
                log.info("⏱️ SERVICE_PERFORMANCE | Servicio: {}.{} | Tiempo: {}ms",
                        service, method, executionTime);
            }

            return result;

        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("💥 SERVICE_ERROR_TIME | Servicio: {}.{} | Tiempo antes del error: {}ms",
                    service, method, executionTime);
            throw throwable;
        }
    }

    @Around("execution(* com.digital.mecommerces.repository.*.*(..))")
    public Object logRepositoryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String repository = getSimpleClassName(joinPoint.getSignature().getDeclaringTypeName());
        String method = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > 2000) { // Log si la consulta toma más de 2 segundos
                log.warn("🐌 SLOW_QUERY | Repository: {}.{} | Tiempo: {}ms",
                        repository, method, executionTime);
            } else if (executionTime > 1000) {
                log.debug("⏱️ DB_PERFORMANCE | Repository: {}.{} | Tiempo: {}ms",
                        repository, method, executionTime);
            }

            return result;

        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("💾 DB_ERROR_TIME | Repository: {}.{} | Tiempo antes del error: {}ms",
                    repository, method, executionTime);
            throw throwable;
        }
    }

    // === LOGGING DE CACHE ===

    @Before("@annotation(org.springframework.cache.annotation.Cacheable)")
    public void logCacheableOperation(JoinPoint joinPoint) {
        try {
            String method = joinPoint.getSignature().getName();
            String className = getSimpleClassName(joinPoint.getSignature().getDeclaringTypeName());

            log.debug("📦 CACHE_LOOKUP | Método: {}.{} | Args: {}",
                    className, method, Arrays.toString(joinPoint.getArgs()));

        } catch (Exception e) {
            log.error("❌ Error en logging de cache: {}", e.getMessage());
        }
    }

    @After("@annotation(org.springframework.cache.annotation.CacheEvict)")
    public void logCacheEvictOperation(JoinPoint joinPoint) {
        try {
            String method = joinPoint.getSignature().getName();
            String className = getSimpleClassName(joinPoint.getSignature().getDeclaringTypeName());

            log.debug("🗑️ CACHE_EVICT | Método: {}.{} | Args: {}",
                    className, method, Arrays.toString(joinPoint.getArgs()));

        } catch (Exception e) {
            log.error("❌ Error en logging de cache evict: {}", e.getMessage());
        }
    }

    // === LOGGING DE SEGURIDAD ===

    @Before("execution(* com.digital.mecommerces.security.*.*(..))")
    public void logSecurityOperation(JoinPoint joinPoint) {
        try {
            String securityClass = getSimpleClassName(joinPoint.getSignature().getDeclaringTypeName());
            String method = joinPoint.getSignature().getName();
            String timestamp = LocalDateTime.now().format(FORMATTER);

            log.debug("🔒 SECURITY_OP | {} | Clase: {}.{}",
                    timestamp, securityClass, method);

        } catch (Exception e) {
            log.error("❌ Error en logging de seguridad: {}", e.getMessage());
        }
    }

    // === LOGGING DE TRANSACCIONES ===

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void logTransactionalOperation(JoinPoint joinPoint) {
        try {
            String method = joinPoint.getSignature().getName();
            String className = getSimpleClassName(joinPoint.getSignature().getDeclaringTypeName());

            log.debug("🔄 TRANSACTION_START | Método: {}.{}", className, method);

        } catch (Exception e) {
            log.error("❌ Error en logging de transacción: {}", e.getMessage());
        }
    }

    // === MÉTODOS AUXILIARES ===

    private boolean isCriticalException(Throwable exception) {
        return exception instanceof NullPointerException ||
                exception instanceof IllegalStateException ||
                exception instanceof SecurityException ||
                exception.getClass().getSimpleName().contains("DataAccess") ||
                exception.getClass().getSimpleName().contains("Database") ||
                exception.getClass().getSimpleName().contains("SQL");
    }

    private String getSimpleClassName(String fullClassName) {
        return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
    }

    private String getStackTraceString(Throwable exception) {
        if (exception.getStackTrace().length > 0) {
            StackTraceElement firstElement = exception.getStackTrace()[0];
            return String.format("%s.%s:%d",
                    firstElement.getClassName(),
                    firstElement.getMethodName(),
                    firstElement.getLineNumber());
        }
        return "STACK_TRACE_NO_DISPONIBLE";
    }
}
