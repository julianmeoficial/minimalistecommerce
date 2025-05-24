package com.digital.mecommerces.controller;

import com.digital.mecommerces.dto.PasswordResetTokensDTO;
import com.digital.mecommerces.service.PasswordResetTokensService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/password-reset")
@Slf4j
public class PasswordResetTokensController {

    private final PasswordResetTokensService passwordResetService;

    public PasswordResetTokensController(PasswordResetTokensService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/solicitar")
    public ResponseEntity<Map<String, Object>> solicitarReset(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");

            if (email == null || email.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("mensaje", "El email es obligatorio");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar si puede generar token
            if (!passwordResetService.puedeGenerarToken(email)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("mensaje", "Ha alcanzado el límite de solicitudes de reset para este email");
                return ResponseEntity.badRequest().body(response);
            }

            PasswordResetTokensDTO token = passwordResetService.solicitarResetPassword(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Se ha enviado un enlace de recuperación a su email");
            response.put("tokenId", token.getId());
            response.put("expiraEn", "24 horas");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en solicitud de reset: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarReset(@Valid @RequestBody PasswordResetTokensDTO resetDTO) {
        try {
            passwordResetService.resetPassword(resetDTO.getResetToken(), resetDTO.getNuevaPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Contraseña actualizada exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en confirmación de reset: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/validar/{token}")
    public ResponseEntity<Map<String, Object>> validarToken(@PathVariable String token) {
        try {
            boolean esValido = passwordResetService.validarToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("valido", esValido);

            if (esValido) {
                PasswordResetTokensDTO tokenInfo = passwordResetService.obtenerTokenInfo(token);
                response.put("usuarioEmail", tokenInfo.getUsuarioEmail());
                response.put("expiracion", tokenInfo.getFechaExpiracion());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("valido", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PasswordResetTokensDTO>> obtenerTokensUsuario(@PathVariable Long usuarioId) {
        List<PasswordResetTokensDTO> tokens = passwordResetService.obtenerTokensPorUsuario(usuarioId);
        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/usuario/{usuarioId}/validos")
    public ResponseEntity<List<PasswordResetTokensDTO>> obtenerTokensValidosUsuario(@PathVariable Long usuarioId) {
        List<PasswordResetTokensDTO> tokens = passwordResetService.obtenerTokensValidos(usuarioId);
        return ResponseEntity.ok(tokens);
    }

    @DeleteMapping("/usuario/{usuarioId}/invalidar")
    public ResponseEntity<Map<String, String>> invalidarTokensUsuario(@PathVariable Long usuarioId) {
        passwordResetService.invalidarTokensUsuario(usuarioId);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Todos los tokens del usuario han sido invalidados");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/limpieza/expirados")
    public ResponseEntity<Map<String, String>> limpiarTokensExpirados() {
        passwordResetService.limpiarTokensExpirados();

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Tokens expirados eliminados exitosamente");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/limpieza/antiguos")
    public ResponseEntity<Map<String, String>> limpiarTokensAntiguos(
            @RequestParam(defaultValue = "30") int dias) {

        passwordResetService.limpiarTokensUsadosAntiguos(dias);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Tokens usados antiguos eliminados exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info/{token}")
    public ResponseEntity<PasswordResetTokensDTO> obtenerInfoToken(@PathVariable String token) {
        PasswordResetTokensDTO tokenInfo = passwordResetService.obtenerTokenInfo(token);
        return ResponseEntity.ok(tokenInfo);
    }

    @GetMapping("/puede-generar/{email}")
    public ResponseEntity<Map<String, Object>> puedeGenerarToken(@PathVariable String email) {
        boolean puede = passwordResetService.puedeGenerarToken(email);

        Map<String, Object> response = new HashMap<>();
        response.put("puedeGenerar", puede);
        response.put("email", email);

        return ResponseEntity.ok(response);
    }
}
