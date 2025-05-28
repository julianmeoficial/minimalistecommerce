package com.digital.mecommerces.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Aspecto para auditoría de acciones del sistema
 * Optimizado para el sistema medbcommerce 3.0
 */
@Aspect
@Component
@Slf4j
public class AuditingAspect {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // === AUDITORÍA DE CONTROLLERS ===

    @After("execution(* com.digital.mecommerces.controller.*.*(..))")
    public void auditControllerCall(JoinPoint joinPoint) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : "ANÓNIMO";
            String userRole = authentication != null && authentication.getAuthorities() != null
                    ? authentication.getAuthorities().toString() : "SIN_ROL";

            String controller = joinPoint.getSignature().getDeclaringTypeName();
            String method = joinPoint.getSignature().getName();
            String timestamp = LocalDateTime.now().format(FORMATTER);

            // Log de auditoría detallado
            log.info("🔍 AUDIT | {} | Usuario: {} ({}) | Acción: {}.{}",
                    timestamp, username, userRole, getSimpleClassName(controller), method);

            // Log adicional para acciones críticas
            if (isCriticalAction(controller, method)) {
                log.warn("🚨 AUDIT_CRITICAL | {} | Usuario: {} | Acción CRÍTICA: {}.{} | Args: {}",
                        timestamp, username, getSimpleClassName(controller), method,
                        Arrays.toString(joinPoint.getArgs()));
            }

        } catch (Exception e) {
            log.error("❌ Error en auditoría de controller: {}", e.getMessage());
        }
    }

    // === AUDITORÍA DE SERVICIOS ===

    @Before("execution(* com.digital.mecommerces.service.*.*(..))")
    public void auditServiceCallBefore(JoinPoint joinPoint) {
        try {
            String service = joinPoint.getSignature().getDeclaringTypeName();
            String method = joinPoint.getSignature().getName();

            if (isImportantServiceOperation(service, method)) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication != null ? authentication.getName() : "SISTEMA";

                log.debug("⚡ SERVICE_START | Usuario: {} | Servicio: {}.{}",
                        username, getSimpleClassName(service), method);
            }

        } catch (Exception e) {
            log.error("❌ Error en auditoría de servicio (before): {}", e.getMessage());
        }
    }

    @AfterReturning(pointcut = "execution(* com.digital.mecommerces.service.*.*(..))", returning = "result")
    public void auditServiceCallAfter(JoinPoint joinPoint, Object result) {
        try {
            String service = joinPoint.getSignature().getDeclaringTypeName();
            String method = joinPoint.getSignature().getName();

            if (isImportantServiceOperation(service, method)) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication != null ? authentication.getName() : "SISTEMA";

                String resultInfo = getResultInfo(result);
                log.debug("✅ SERVICE_SUCCESS | Usuario: {} | Servicio: {}.{} | Resultado: {}",
                        username, getSimpleClassName(service), method, resultInfo);
            }

        } catch (Exception e) {
            log.error("❌ Error en auditoría de servicio (after): {}", e.getMessage());
        }
    }

    // === AUDITORÍA DE AUTENTICACIÓN ===

    @After("execution(* com.digital.mecommerces.controller.AuthController.autenticarUsuario(..))")
    public void auditLogin(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                // Obtener email del LoginDTO (primer argumento)
                String email = extractEmailFromLoginDTO(args[0]);
                String timestamp = LocalDateTime.now().format(FORMATTER);

                log.info("🔐 LOGIN_ATTEMPT | {} | Email: {} | IP: {}",
                        timestamp, email, getCurrentUserIP());
            }
        } catch (Exception e) {
            log.error("❌ Error en auditoría de login: {}", e.getMessage());
        }
    }

    @After("execution(* com.digital.mecommerces.controller.AuthController.registrarUsuario(..))")
    public void auditRegistration(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                String email = extractEmailFromRegistroDTO(args[0]);
                String timestamp = LocalDateTime.now().format(FORMATTER);

                log.info("📝 REGISTRATION_ATTEMPT | {} | Email: {} | IP: {}",
                        timestamp, email, getCurrentUserIP());
            }
        } catch (Exception e) {
            log.error("❌ Error en auditoría de registro: {}", e.getMessage());
        }
    }

    // === AUDITORÍA DE OPERACIONES ADMINISTRATIVAS ===

    @After("execution(* com.digital.mecommerces.controller.AdminController.*(..))")
    public void auditAdminOperation(JoinPoint joinPoint) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : "DESCONOCIDO";
            String method = joinPoint.getSignature().getName();
            String timestamp = LocalDateTime.now().format(FORMATTER);

            log.warn("👑 ADMIN_ACTION | {} | Admin: {} | Acción: {} | Args: {}",
                    timestamp, username, method, Arrays.toString(joinPoint.getArgs()));

        } catch (Exception e) {
            log.error("❌ Error en auditoría de admin: {}", e.getMessage());
        }
    }

    // === AUDITORÍA DE OPERACIONES DE PRODUCTOS ===

    @After("execution(* com.digital.mecommerces.service.ProductoService.crear*(..))")
    public void auditProductCreation(JoinPoint joinPoint) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : "SISTEMA";
            String timestamp = LocalDateTime.now().format(FORMATTER);

            log.info("🛍️ PRODUCT_CREATED | {} | Usuario: {} | Método: {}",
                    timestamp, username, joinPoint.getSignature().getName());

        } catch (Exception e) {
            log.error("❌ Error en auditoría de creación de producto: {}", e.getMessage());
        }
    }

    @After("execution(* com.digital.mecommerces.service.ProductoService.eliminar*(..))")
    public void auditProductDeletion(JoinPoint joinPoint) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : "SISTEMA";
            String timestamp = LocalDateTime.now().format(FORMATTER);

            log.warn("🗑️ PRODUCT_DELETED | {} | Usuario: {} | Args: {}",
                    timestamp, username, Arrays.toString(joinPoint.getArgs()));

        } catch (Exception e) {
            log.error("❌ Error en auditoría de eliminación de producto: {}", e.getMessage());
        }
    }

    // === MÉTODOS AUXILIARES ===

    private boolean isCriticalAction(String controller, String method) {
        String controllerName = getSimpleClassName(controller);

        // Acciones críticas que requieren auditoría especial
        return (controllerName.contains("Admin") &&
                (method.contains("eliminar") || method.contains("crear") || method.contains("actualizar"))) ||
                (controllerName.contains("Usuario") && method.contains("eliminar")) ||
                (controllerName.contains("Rol") &&
                        (method.contains("crear") || method.contains("eliminar") || method.contains("asignar"))) ||
                (controllerName.contains("Permiso") &&
                        (method.contains("crear") || method.contains("eliminar")));
    }

    private boolean isImportantServiceOperation(String service, String method) {
        String serviceName = getSimpleClassName(service);

        return serviceName.contains("Usuario") ||
                serviceName.contains("Producto") ||
                serviceName.contains("Carrito") ||
                serviceName.contains("Admin") ||
                method.contains("crear") ||
                method.contains("actualizar") ||
                method.contains("eliminar");
    }

    private String getSimpleClassName(String fullClassName) {
        return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
    }

    private String extractEmailFromLoginDTO(Object loginDTO) {
        try {
            return loginDTO.getClass().getMethod("getEmail").invoke(loginDTO).toString();
        } catch (Exception e) {
            return "EMAIL_NO_DISPONIBLE";
        }
    }

    private String extractEmailFromRegistroDTO(Object registroDTO) {
        try {
            return registroDTO.getClass().getMethod("getEmail").invoke(registroDTO).toString();
        } catch (Exception e) {
            return "EMAIL_NO_DISPONIBLE";
        }
    }

    private String getCurrentUserIP() {
        // En un entorno real, esto se obtendría del HttpServletRequest
        return "IP_NO_DISPONIBLE";
    }

    private String getResultInfo(Object result) {
        if (result == null) {
            return "NULL";
        } else if (result instanceof String) {
            return (String) result;
        } else if (result instanceof Number) {
            return result.toString();
        } else if (result instanceof Boolean) {
            return result.toString();
        } else {
            return result.getClass().getSimpleName();
        }
    }
}
