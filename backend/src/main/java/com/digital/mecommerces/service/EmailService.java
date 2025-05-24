package com.digital.mecommerces.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.email.from:noreply@mecommerces.com}")
    private String fromEmail;

    public void enviarEmailResetPassword(String email, String nombreUsuario, String token) {
        log.info("Enviando email de reset de password a: {}", email);

        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        // Aquí implementaremos la lógica real de envío de email
        // Por ahora solo logging para desarrollo
        log.info("=== EMAIL DE RESET DE PASSWORD ===");
        log.info("Para: {}", email);
        log.info("Usuario: {}", nombreUsuario);
        log.info("URL de reset: {}", resetUrl);
        log.info("Token: {}", token);
        log.info("================================");

        // En producción se debe usar un servicio como:
        // - Spring Mail
        // - SendGrid
        // - Amazon SES
    }

    public void enviarEmailConfirmacionReset(String email, String nombreUsuario) {
        log.info("Enviando email de confirmación de reset a: {}", email);

        // Aquí implementarías la lógica real de envío de email
        log.info("=== EMAIL DE CONFIRMACIÓN ===");
        log.info("Para: {}", email);
        log.info("Usuario: {}", nombreUsuario);
        log.info("Mensaje: Su contraseña ha sido actualizada exitosamente");
        log.info("============================");
    }
}
