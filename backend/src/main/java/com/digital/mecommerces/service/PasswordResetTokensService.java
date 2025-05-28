package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.BusinessException;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.exception.TokenExpiredException;
import com.digital.mecommerces.exception.TokenInvalidException;
import com.digital.mecommerces.model.PasswordResetTokens;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.PasswordResetTokensRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PasswordResetTokensService {

    private final PasswordResetTokensRepository passwordResetTokensRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.password-reset.token-expiration-hours:1}")
    private int tokenExpirationHours;

    @Value("${app.password-reset.max-attempts:3}")
    private int maxAttempts;

    @Value("${app.password-reset.rate-limit-minutes:15}")
    private int rateLimitMinutes;

    @Value("${app.password-reset.max-tokens-per-user:3}")
    private int maxTokensPerUser;

    public PasswordResetTokensService(PasswordResetTokensRepository passwordResetTokensRepository,
                                      UsuarioRepository usuarioRepository,
                                      PasswordEncoder passwordEncoder,
                                      EmailService emailService) {
        this.passwordResetTokensRepository = passwordResetTokensRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void crearTokenResetPassword(String email, String ipSolicitante, String userAgent) {
        log.info("🔑 Creando token de reset de contraseña para email: {}", email);

        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        // Verificar rate limiting por email
        verificarRateLimitingPorEmail(email);

        // Verificar rate limiting por IP
        verificarRateLimitingPorIp(ipSolicitante);

        // Verificar límite de tokens activos por usuario
        verificarLimiteTokensPorUsuario(usuario.getUsuarioId());

        // Desactivar tokens anteriores del usuario
        desactivarTokensActivosDeUsuario(usuario.getUsuarioId());

        // Generar nuevo token
        String token = generarTokenSeguro();
        LocalDateTime expiracion = LocalDateTime.now().plusHours(tokenExpirationHours);

        // Crear nuevo token
        PasswordResetTokens resetToken = new PasswordResetTokens(usuario, token, expiracion, ipSolicitante);
        resetToken.setUserAgent(userAgent);

        passwordResetTokensRepository.save(resetToken);

        // Enviar email
        emailService.enviarEmailResetPassword(usuario, token);

        log.info("✅ Token de reset creado exitosamente para: {}", email);
    }

    @Transactional
    public void resetearPassword(String token, String nuevaPassword) {
        log.info("🔄 Procesando reset de contraseña con token");

        // Validar token
        PasswordResetTokens resetToken = validarToken(token);

        // Verificar que la nueva contraseña sea válida
        validarNuevaPassword(nuevaPassword);

        // Actualizar contraseña del usuario
        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        // Marcar token como usado
        resetToken.marcarComoUsado();
        passwordResetTokensRepository.save(resetToken);

        // Desactivar otros tokens del usuario
        desactivarTokensActivosDeUsuario(usuario.getUsuarioId());

        log.info("✅ Contraseña actualizada exitosamente para usuario: {}", usuario.getEmail());
    }

    public void verificarValidezToken(String token) {
        log.info("🔍 Validando token de reset");

        PasswordResetTokens resetToken = passwordResetTokensRepository.findByToken(token)
                .orElseThrow(() -> new TokenInvalidException("Token de reset no válido"));

        if (!resetToken.puedeSerUtilizado()) {
            String estado = resetToken.getEstadoToken();

            switch (estado) {
                case "EXPIRADO" -> {
                    log.warn("⏰ Token expirado: {}", token);
                    throw new TokenExpiredException("El token ha expirado. Solicita un nuevo reset de contraseña.");
                }
                case "USADO" -> {
                    log.warn("🔄 Token ya usado: {}", token);
                    throw new TokenInvalidException("Este token ya ha sido utilizado");
                }
                case "BLOQUEADO" -> {
                    log.warn("🚫 Token bloqueado por demasiados intentos: {}", token);
                    throw new TokenInvalidException("Token bloqueado por demasiados intentos fallidos");
                }
                case "INACTIVO" -> {
                    log.warn("❌ Token inactivo: {}", token);
                    throw new TokenInvalidException("Token inactivo");
                }
                default -> {
                    log.warn("⚠️ Token en estado no válido: {}", estado);
                    throw new TokenInvalidException("Token no válido");
                }
            }
        }

        log.info("✅ Token validado exitosamente");
    }

    @Transactional
    public void incrementarIntentosToken(String token) {
        log.info("📊 Incrementando intentos para token");

        PasswordResetTokens resetToken = passwordResetTokensRepository.findByToken(token)
                .orElseThrow(() -> new TokenInvalidException("Token no encontrado"));

        resetToken.incrementarIntentos();
        passwordResetTokensRepository.save(resetToken);

        if (resetToken.alcanzóLimiteIntentos()) {
            log.warn("⚠️ Token bloqueado por demasiados intentos: {}", token);
        }
    }

    public PasswordResetTokens obtenerInformacionToken(String token) {
        return passwordResetTokensRepository.findByToken(token)
                .orElseThrow(() -> new TokenInvalidException("Token no encontrado"));
    }

    public List<PasswordResetTokens> obtenerTokensActivosPorUsuario(Long usuarioId) {
        return passwordResetTokensRepository.findTokensValidosPorUsuario(usuarioId, LocalDateTime.now());
    }

    public List<PasswordResetTokens> obtenerTokensExpirados() {
        return passwordResetTokensRepository.findTokensExpirados(LocalDateTime.now());
    }

    @Transactional
    public void limpiarTokensExpirados() {
        log.info("🧹 Limpiando tokens expirados");

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaLimite = ahora.minusDays(7); // Eliminar tokens de más de 7 días

        List<PasswordResetTokens> tokensParaLimpiar = passwordResetTokensRepository
                .findTokensParaLimpiar(ahora, fechaLimite);

        passwordResetTokensRepository.deleteAll(tokensParaLimpiar);

        log.info("✅ Limpieza completada. {} tokens eliminados", tokensParaLimpiar.size());
    }

    public long contarTokensActivos() {
        return passwordResetTokensRepository.countTokensUsados();
    }

    public long contarTokensExpirados() {
        return passwordResetTokensRepository.countTokensExpiradosNoUsados(LocalDateTime.now());
    }

    public List<Object[]> obtenerEstadisticasPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return passwordResetTokensRepository.findEstadisticasPorFecha(inicio, fin);
    }

    // Métodos privados de validación y utilidad

    private PasswordResetTokens validarToken(String token) {
        PasswordResetTokens resetToken = passwordResetTokensRepository.findByToken(token)
                .orElseThrow(() -> new TokenInvalidException("Token de reset no válido"));

        if (!resetToken.puedeSerUtilizado()) {
            String estado = resetToken.getEstadoToken();

            switch (estado) {
                case "EXPIRADO" -> throw new TokenExpiredException("El token ha expirado");
                case "USADO" -> throw new TokenInvalidException("Este token ya ha sido utilizado");
                case "BLOQUEADO" -> throw new TokenInvalidException("Token bloqueado por demasiados intentos");
                case "INACTIVO" -> throw new TokenInvalidException("Token inactivo");
                default -> throw new TokenInvalidException("Token no válido");
            }
        }

        return resetToken;
    }

    private void verificarRateLimitingPorEmail(String email) {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(rateLimitMinutes);
        long tokenCount = passwordResetTokensRepository.countTokensRecientesPorEmail(email, limite);

        if (tokenCount >= maxTokensPerUser) {
            log.warn("⚠️ Rate limit excedido para email: {}", email);
            throw new BusinessException("Demasiadas solicitudes de reset. Intenta nuevamente en " +
                    rateLimitMinutes + " minutos");
        }
    }

    private void verificarRateLimitingPorIp(String ip) {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(rateLimitMinutes);
        long tokenCount = passwordResetTokensRepository.countTokensRecientesPorIp(ip, limite);

        if (tokenCount >= 5) { // Máximo 5 solicitudes por IP en el período
            log.warn("⚠️ Rate limit excedido para IP: {}", ip);
            throw new BusinessException("Demasiadas solicitudes desde esta dirección. Intenta nuevamente en " +
                    rateLimitMinutes + " minutos");
        }
    }

    private void verificarLimiteTokensPorUsuario(Long usuarioId) {
        List<PasswordResetTokens> tokensActivos = passwordResetTokensRepository
                .findTokensValidosPorUsuario(usuarioId, LocalDateTime.now());

        if (tokensActivos.size() >= maxTokensPerUser) {
            log.warn("⚠️ Límite de tokens activos excedido para usuario ID: {}", usuarioId);
            throw new BusinessException("Ya tienes tokens de reset activos. Usa uno existente o espera a que expiren");
        }
    }

    private void desactivarTokensActivosDeUsuario(Long usuarioId) {
        log.info("🔄 Desactivando tokens activos para usuario ID: {}", usuarioId);

        List<PasswordResetTokens> tokensActivos = passwordResetTokensRepository
                .findTokensActivosPorUsuario(usuarioId);

        for (PasswordResetTokens token : tokensActivos) {
            token.marcarComoInactivo();
        }

        if (!tokensActivos.isEmpty()) {
            passwordResetTokensRepository.saveAll(tokensActivos);
            log.info("✅ {} tokens anteriores desactivados", tokensActivos.size());
        }
    }

    private String generarTokenSeguro() {
        // Combinar UUID con números aleatorios para mayor seguridad
        String uuid = UUID.randomUUID().toString().replace("-", "");

        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();

        // Agregar 8 caracteres aleatorios adicionales
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 8; i++) {
            builder.append(chars.charAt(random.nextInt(chars.length())));
        }

        return uuid + builder.toString();
    }

    private void validarNuevaPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException("La nueva contraseña no puede estar vacía");
        }

        if (password.length() < 6) {
            throw new BusinessException("La contraseña debe tener al menos 6 caracteres");
        }

        if (password.length() > 100) {
            throw new BusinessException("La contraseña es demasiado larga");
        }

        // Validar que contenga al menos una letra y un número
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
            throw new BusinessException("La contraseña debe contener al menos una letra y un número");
        }
    }

    // Métodos para estadísticas y monitoreo
    public List<Object[]> obtenerPatronesDeUso() {
        return passwordResetTokensRepository.findPatronesDeUso();
    }

    public List<Object[]> obtenerEstadisticasPorUserAgent() {
        return passwordResetTokensRepository.findEstadisticasPorUserAgent();
    }

    public Double obtenerPromedioIntentos() {
        return passwordResetTokensRepository.findPromedioIntentosPorToken();
    }
}