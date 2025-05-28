package com.digital.mecommerces.controller;

import com.digital.mecommerces.dto.PasswordResetTokensDTO;
import com.digital.mecommerces.service.PasswordResetTokensService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador para gestión de tokens de reset de contraseña
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/password-reset")
@Tag(name = "Reset de Contraseña", description = "APIs para gestión de reset de contraseñas")
@Slf4j
public class PasswordResetTokensController {

    private final PasswordResetTokensService passwordResetTokensService;

    public PasswordResetTokensController(PasswordResetTokensService passwordResetTokensService) {
        this.passwordResetTokensService = passwordResetTokensService;
    }

    @PostMapping("/solicitar")
    @Operation(summary = "Solicitar reset de contraseña", description = "Genera token y envía email para reset")
    public ResponseEntity<PasswordResetTokensDTO.ResetPasswordResponseDTO> solicitarResetPassword(
            @Valid @RequestBody PasswordResetTokensDTO.ResetPasswordRequestDTO request,
            HttpServletRequest httpRequest) {

        log.info("🔒 Solicitud de reset de contraseña para: {}", request.getEmail());

        try {
            // Capturar información del request
            String ipAddress = obtenerIpCliente(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            request.setIpSolicitante(ipAddress);
            request.setUserAgent(userAgent);

            // Procesar solicitud
            PasswordResetTokensDTO.ResetPasswordResponseDTO response =
                    passwordResetTokensService.solicitarResetPassword(request);

            log.info("✅ Solicitud de reset procesada para: {}", request.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error procesando solicitud de reset para {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(PasswordResetTokensDTO.ResetPasswordResponseDTO.error(
                            "Error procesando solicitud: " + e.getMessage()));
        }
    }

    @PostMapping("/validar-token")
    @Operation(summary = "Validar token de reset", description = "Verifica si un token es válido y no ha expirado")
    public ResponseEntity<PasswordResetTokensDTO.TokenValidationResponseDTO> validarToken(
            @Valid @RequestBody PasswordResetTokensDTO.ValidateTokenRequestDTO request) {

        log.info("🔍 Validando token de reset");

        try {
            PasswordResetTokensDTO.TokenValidationResponseDTO response =
                    passwordResetTokensService.validarToken(request.getToken());

            if (response.getValid()) {
                log.info("✅ Token válido para usuario: {}", response.getUserEmail());
            } else {
                log.warn("❌ Token inválido: {}", response.getMessage());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error validando token: {}", e.getMessage());
            return ResponseEntity.ok(
                    PasswordResetTokensDTO.TokenValidationResponseDTO.invalid("Error validando token"));
        }
    }

    @PostMapping("/confirmar")
    @Operation(summary = "Confirmar reset de contraseña", description = "Cambia la contraseña usando el token válido")
    public ResponseEntity<PasswordResetTokensDTO.ResetPasswordResponseDTO> confirmarResetPassword(
            @Valid @RequestBody PasswordResetTokensDTO.ResetPasswordConfirmDTO request) {

        log.info("🔒 Confirmando reset de contraseña");

        try {
            // Validar que las contraseñas coincidan
            if (!request.passwordsMatch()) {
                return ResponseEntity.badRequest()
                        .body(PasswordResetTokensDTO.ResetPasswordResponseDTO.error(
                                "Las contraseñas no coinciden"));
            }

            // Procesar cambio de contraseña
            PasswordResetTokensDTO.ResetPasswordResponseDTO response =
                    passwordResetTokensService.confirmarResetPassword(request);

            log.info("✅ Reset de contraseña confirmado exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error confirmando reset de contraseña: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(PasswordResetTokensDTO.ResetPasswordResponseDTO.error(
                            "Error confirmando reset: " + e.getMessage()));
        }
    }

    @GetMapping("/info/{token}")
    @Operation(summary = "Obtener información del token", description = "Obtiene información detallada de un token")
    public ResponseEntity<PasswordResetTokensDTO.TokenInfoDTO> obtenerInfoToken(@PathVariable String token) {
        log.info("ℹ️ Obteniendo información del token");

        try {
            PasswordResetTokensDTO.TokenInfoDTO info = passwordResetTokensService.obtenerInfoToken(token);

            if (info != null) {
                return ResponseEntity.ok(info);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            log.error("❌ Error obteniendo información del token: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/invalidar/{token}")
    @Operation(summary = "Invalidar token", description = "Invalida un token específico")
    public ResponseEntity<Map<String, Object>> invalidarToken(@PathVariable String token) {
        log.info("🚫 Invalidando token");

        try {
            boolean invalidado = passwordResetTokensService.invalidarToken(token);

            Map<String, Object> response = Map.of(
                    "success", invalidado,
                    "mensaje", invalidado ? "Token invalidado exitosamente" : "Token no encontrado",
                    "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error invalidando token: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "mensaje", "Error invalidando token: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/limpiar-expirados")
    @Operation(summary = "Limpiar tokens expirados", description = "Elimina tokens expirados del sistema")
    public ResponseEntity<Map<String, Object>> limpiarTokensExpirados() {
        log.info("🧹 Limpiando tokens expirados");

        try {
            int tokensEliminados = passwordResetTokensService.limpiarTokensExpirados();

            Map<String, Object> response = Map.of(
                    "success", true,
                    "tokensEliminados", tokensEliminados,
                    "mensaje", "Limpieza completada exitosamente",
                    "timestamp", LocalDateTime.now()
            );

            log.info("✅ Limpieza completada: {} tokens eliminados", tokensEliminados);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error limpiando tokens expirados: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "mensaje", "Error en limpieza: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de tokens", description = "Estadísticas del sistema de reset")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.info("📊 Obteniendo estadísticas de tokens de reset");

        try {
            Map<String, Object> estadisticas = passwordResetTokensService.obtenerEstadisticas();
            estadisticas.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(estadisticas);

        } catch (Exception e) {
            log.error("❌ Error obteniendo estadísticas: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error obteniendo estadísticas",
                    "detalle", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    // Método auxiliar para obtener IP del cliente
    private String obtenerIpCliente(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // En caso de múltiples IPs, tomar la primera
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }
}
