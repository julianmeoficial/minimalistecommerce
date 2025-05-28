package com.digital.mecommerces.service;

import com.digital.mecommerces.model.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@mecommerces.com}")
    private String fromEmail;

    @Value("${app.mail.enabled:false}")
    private boolean emailEnabled;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailBienvenida(Usuario usuario) {
        if (!emailEnabled) {
            log.info("📧 Email deshabilitado - Simulando envío de bienvenida para: {}", usuario.getEmail());
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                log.info("📧 Enviando email de bienvenida a: {}", usuario.getEmail());

                String asunto = "¡Bienvenido a MeCommerces! 🎉";
                String contenido = generarContenidoBienvenida(usuario);

                enviarEmailHtml(usuario.getEmail(), asunto, contenido);
                log.info("✅ Email de bienvenida enviado exitosamente");

            } catch (Exception e) {
                log.error("❌ Error enviando email de bienvenida: {}", e.getMessage());
            }
        });
    }

    public void enviarEmailResetPassword(Usuario usuario, String token) {
        if (!emailEnabled) {
            log.info("📧 Email deshabilitado - Simulando envío de reset para: {}", usuario.getEmail());
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                log.info("🔑 Enviando email de reset de contraseña a: {}", usuario.getEmail());

                String asunto = "Restablecer contraseña - MeCommerces";
                String contenido = generarContenidoResetPassword(usuario, token);

                enviarEmailHtml(usuario.getEmail(), asunto, contenido);
                log.info("✅ Email de reset de contraseña enviado exitosamente");

            } catch (Exception e) {
                log.error("❌ Error enviando email de reset: {}", e.getMessage());
            }
        });
    }

    public void enviarEmailConfirmacionOrden(Usuario usuario, Long ordenId, Double total) {
        if (!emailEnabled) {
            log.info("📧 Email deshabilitado - Simulando confirmación de orden para: {}", usuario.getEmail());
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                log.info("📦 Enviando confirmación de orden {} a: {}", ordenId, usuario.getEmail());

                String asunto = "Confirmación de orden #" + ordenId + " - MeCommerces";
                String contenido = generarContenidoConfirmacionOrden(usuario, ordenId, total);

                enviarEmailHtml(usuario.getEmail(), asunto, contenido);
                log.info("✅ Email de confirmación de orden enviado exitosamente");

            } catch (Exception e) {
                log.error("❌ Error enviando confirmación de orden: {}", e.getMessage());
            }
        });
    }

    public void enviarEmailCambioEstadoOrden(Usuario usuario, Long ordenId, String nuevoEstado) {
        if (!emailEnabled) {
            log.info("📧 Email deshabilitado - Simulando cambio de estado para: {}", usuario.getEmail());
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                log.info("📋 Enviando cambio de estado de orden {} a: {}", ordenId, usuario.getEmail());

                String asunto = "Actualización de orden #" + ordenId + " - MeCommerces";
                String contenido = generarContenidoCambioEstado(usuario, ordenId, nuevoEstado);

                enviarEmailHtml(usuario.getEmail(), asunto, contenido);
                log.info("✅ Email de cambio de estado enviado exitosamente");

            } catch (Exception e) {
                log.error("❌ Error enviando cambio de estado: {}", e.getMessage());
            }
        });
    }

    public void enviarEmailVendedorVerificado(Usuario usuario) {
        if (!emailEnabled) {
            log.info("📧 Email deshabilitado - Simulando verificación de vendedor para: {}", usuario.getEmail());
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                log.info("✅ Enviando notificación de verificación de vendedor a: {}", usuario.getEmail());

                String asunto = "¡Tu cuenta de vendedor ha sido verificada! ✅ - MeCommerces";
                String contenido = generarContenidoVendedorVerificado(usuario);

                enviarEmailHtml(usuario.getEmail(), asunto, contenido);
                log.info("✅ Email de verificación de vendedor enviado exitosamente");

            } catch (Exception e) {
                log.error("❌ Error enviando email de verificación: {}", e.getMessage());
            }
        });
    }

    public void enviarEmailSimple(String destinatario, String asunto, String mensaje) {
        if (!emailEnabled) {
            log.info("📧 Email deshabilitado - Simulando envío simple a: {}", destinatario);
            return;
        }

        try {
            log.info("📧 Enviando email simple a: {}", destinatario);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinatario);
            message.setSubject(asunto);
            message.setText(mensaje);

            mailSender.send(message);
            log.info("✅ Email simple enviado exitosamente");

        } catch (Exception e) {
            log.error("❌ Error enviando email simple: {}", e.getMessage());
        }
    }

    private void enviarEmailHtml(String destinatario, String asunto, String contenidoHtml) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(contenidoHtml, true);

        mailSender.send(message);
    }

    private String generarContenidoBienvenida(Usuario usuario) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Bienvenido a MeCommerces</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2563eb;">¡Bienvenido a MeCommerces! 🎉</h2>
                    
                    <p>Hola <strong>%s</strong>,</p>
                    
                    <p>¡Nos complace darte la bienvenida a MeCommerces! Tu cuenta ha sido creada exitosamente.</p>
                    
                    <p><strong>Detalles de tu cuenta:</strong></p>
                    <ul>
                        <li>Nombre: %s</li>
                        <li>Email: %s</li>
                        <li>Rol: %s</li>
                    </ul>
                    
                    <p>Ahora puedes:</p>
                    <ul>
                        <li>Explorar nuestro catálogo de productos</li>
                        <li>Gestionar tu perfil</li>
                        <li>Realizar compras de forma segura</li>
                    </ul>
                    
                    <div style="margin: 30px 0;">
                        <a href="%s" style="background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px;">
                            Acceder a mi cuenta
                        </a>
                    </div>
                    
                    <p>Si tienes alguna pregunta, no dudes en contactarnos.</p>
                    
                    <p>¡Gracias por elegir MeCommerces!</p>
                    
                    <hr style="margin: 30px 0;">
                    <p style="font-size: 12px; color: #666;">
                        Este es un email automático, por favor no respondas a este mensaje.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(
                usuario.getUsuarioNombre(),
                usuario.getUsuarioNombre(),
                usuario.getEmail(),
                usuario.getRol() != null ? usuario.getRol().getDescripcion() : "Usuario",
                frontendUrl + "/login"
        );
    }

    private String generarContenidoResetPassword(Usuario usuario, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Restablecer Contraseña</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #dc2626;">Restablecer contraseña 🔑</h2>
                    
                    <p>Hola <strong>%s</strong>,</p>
                    
                    <p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en MeCommerces.</p>
                    
                    <p>Si no realizaste esta solicitud, puedes ignorar este email. Tu contraseña no será cambiada.</p>
                    
                    <p>Para restablecer tu contraseña, haz clic en el siguiente enlace:</p>
                    
                    <div style="margin: 30px 0;">
                        <a href="%s" style="background-color: #dc2626; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px;">
                            Restablecer contraseña
                        </a>
                    </div>
                    
                    <p style="font-size: 14px; color: #666;">
                        Este enlace expirará en 1 hora por seguridad.
                    </p>
                    
                    <p>Si el botón no funciona, copia y pega este enlace en tu navegador:</p>
                    <p style="word-break: break-all; background-color: #f5f5f5; padding: 10px; border-radius: 3px;">
                        %s
                    </p>
                    
                    <hr style="margin: 30px 0;">
                    <p style="font-size: 12px; color: #666;">
                        Este es un email automático, por favor no respondas a este mensaje.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(
                usuario.getUsuarioNombre(),
                resetUrl,
                resetUrl
        );
    }

    private String generarContenidoConfirmacionOrden(Usuario usuario, Long ordenId, Double total) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Confirmación de Orden</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #16a34a;">¡Orden confirmada! 📦</h2>
                    
                    <p>Hola <strong>%s</strong>,</p>
                    
                    <p>Tu orden ha sido confirmada y está siendo procesada.</p>
                    
                    <div style="background-color: #f0f9ff; padding: 20px; border-radius: 5px; margin: 20px 0;">
                        <h3 style="margin-top: 0;">Detalles de la orden:</h3>
                        <p><strong>Número de orden:</strong> #%d</p>
                        <p><strong>Total:</strong> $%.2f</p>
                        <p><strong>Estado:</strong> Pendiente de procesamiento</p>
                    </div>
                    
                    <p>Te notificaremos cuando tu orden sea enviada.</p>
                    
                    <div style="margin: 30px 0;">
                        <a href="%s" style="background-color: #16a34a; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px;">
                            Ver mis órdenes
                        </a>
                    </div>
                    
                    <p>¡Gracias por tu compra!</p>
                    
                    <hr style="margin: 30px 0;">
                    <p style="font-size: 12px; color: #666;">
                        Este es un email automático, por favor no respondas a este mensaje.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(
                usuario.getUsuarioNombre(),
                ordenId,
                total,
                frontendUrl + "/mis-ordenes"
        );
    }

    private String generarContenidoCambioEstado(Usuario usuario, Long ordenId, String nuevoEstado) {
        String estadoDescriptivo = obtenerEstadoDescriptivo(nuevoEstado);
        String color = obtenerColorEstado(nuevoEstado);

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Actualización de Orden</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: %s;">Actualización de orden 📋</h2>
                    
                    <p>Hola <strong>%s</strong>,</p>
                    
                    <p>Tu orden #%d ha sido actualizada.</p>
                    
                    <div style="background-color: #f0f9ff; padding: 20px; border-radius: 5px; margin: 20px 0;">
                        <h3 style="margin-top: 0;">Estado actual:</h3>
                        <p style="font-size: 18px; color: %s;"><strong>%s</strong></p>
                    </div>
                    
                    <div style="margin: 30px 0;">
                        <a href="%s" style="background-color: %s; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px;">
                            Ver detalles de la orden
                        </a>
                    </div>
                    
                    <hr style="margin: 30px 0;">
                    <p style="font-size: 12px; color: #666;">
                        Este es un email automático, por favor no respondas a este mensaje.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(
                color,
                usuario.getUsuarioNombre(),
                ordenId,
                color,
                estadoDescriptivo,
                frontendUrl + "/orden/" + ordenId,
                color
        );
    }

    private String generarContenidoVendedorVerificado(Usuario usuario) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Cuenta Verificada</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #16a34a;">¡Cuenta verificada! ✅</h2>
                    
                    <p>Hola <strong>%s</strong>,</p>
                    
                    <p>¡Excelentes noticias! Tu cuenta de vendedor ha sido verificada exitosamente.</p>
                    
                    <p>Ahora puedes:</p>
                    <ul>
                        <li>Publicar productos en nuestro marketplace</li>
                        <li>Gestionar tu inventario</li>
                        <li>Recibir y procesar órdenes</li>
                        <li>Acceder a herramientas de vendedor</li>
                    </ul>
                    
                    <div style="margin: 30px 0;">
                        <a href="%s" style="background-color: #16a34a; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px;">
                            Acceder al panel de vendedor
                        </a>
                    </div>
                    
                    <p>¡Bienvenido a la comunidad de vendedores de MeCommerces!</p>
                    
                    <hr style="margin: 30px 0;">
                    <p style="font-size: 12px; color: #666;">
                        Este es un email automático, por favor no respondas a este mensaje.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(
                usuario.getUsuarioNombre(),
                frontendUrl + "/vendedor/dashboard"
        );
    }

    private String obtenerEstadoDescriptivo(String estado) {
        return switch (estado.toUpperCase()) {
            case "PENDIENTE" -> "Pendiente de pago";
            case "PAGADA" -> "Pagada - Preparando envío";
            case "ENVIADA" -> "En camino";
            case "ENTREGADA" -> "Entregada";
            case "CANCELADA" -> "Cancelada";
            case "DEVUELTA" -> "Devuelta";
            default -> estado;
        };
    }

    private String obtenerColorEstado(String estado) {
        return switch (estado.toUpperCase()) {
            case "PENDIENTE" -> "#f59e0b";
            case "PAGADA" -> "#3b82f6";
            case "ENVIADA" -> "#8b5cf6";
            case "ENTREGADA" -> "#16a34a";
            case "CANCELADA", "DEVUELTA" -> "#dc2626";
            default -> "#6b7280";
        };
    }

    // Método para testing
    public void setEmailEnabled(boolean enabled) {
        this.emailEnabled = enabled;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }
}
