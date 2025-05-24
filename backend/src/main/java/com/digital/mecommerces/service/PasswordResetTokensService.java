package com.digital.mecommerces.service;

import com.digital.mecommerces.dto.PasswordResetTokensDTO;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class PasswordResetTokensService {

    private final PasswordResetTokensRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.password-reset.token-expiration-hours:24}")
    private int tokenExpirationHours;

    @Value("${app.password-reset.max-tokens-per-user:3}")
    private int maxTokensPerUser;

    public PasswordResetTokensService(PasswordResetTokensRepository tokenRepository,
                                      UsuarioRepository usuarioRepository,
                                      PasswordEncoder passwordEncoder,
                                      EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public PasswordResetTokensDTO solicitarResetPassword(String email) {
        log.info("Solicitando reset de password para email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        // Verificar que el usuario esté activo
        if (!usuario.getActivo()) {
            throw new IllegalStateException("El usuario está inactivo");
        }

        // Verificar límite de tokens activos
        long tokensActivos = tokenRepository.countValidTokensByUsuario(usuario, LocalDateTime.now());
        if (tokensActivos >= maxTokensPerUser) {
            log.warn("Usuario {} ha alcanzado el límite de tokens activos", email);
            throw new IllegalStateException("Ha alcanzado el límite máximo de solicitudes de reset. " +
                    "Espere a que expiren los tokens existentes o use uno de los tokens ya enviados.");
        }

        // Generar nuevo token
        String token = generarToken();
        LocalDateTime fechaExpiracion = LocalDateTime.now().plusHours(tokenExpirationHours);

        PasswordResetTokens resetToken = new PasswordResetTokens(usuario, token, fechaExpiracion);
        resetToken = tokenRepository.save(resetToken);

        // Enviar email (asíncrono)
        try {
            emailService.enviarEmailResetPassword(usuario.getEmail(), usuario.getUsuarioNombre(), token);
            log.info("Email de reset enviado exitosamente a: {}", email);
        } catch (Exception e) {
            log.error("Error enviando email de reset a {}: {}", email, e.getMessage());
            // No fallar la operación si el email no se puede enviar
        }

        log.info("Token de reset creado exitosamente para usuario: {}", email);
        return convertirADTO(resetToken);
    }

    @Transactional
    public void resetPassword(String token, String nuevaPassword) {
        log.info("Intentando reset de password con token: {}", token.substring(0, 8) + "...");

        PasswordResetTokens resetToken = tokenRepository.findValidTokenByToken(token, LocalDateTime.now())
                .orElseThrow(() -> new TokenInvalidException("Token inválido o expirado"));

        // Verificar que el token no esté usado
        if (resetToken.getUsado()) {
            throw new TokenInvalidException("El token ya ha sido utilizado");
        }

        // Verificar que no esté expirado
        if (resetToken.isExpired()) {
            throw new TokenExpiredException("El token ha expirado");
        }

        Usuario usuario = resetToken.getUsuario();

        // Actualizar password
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setUpdatedat(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Marcar token como usado
        resetToken.setUsado(true);
        tokenRepository.save(resetToken);

        // Invalidar todos los otros tokens del usuario
        tokenRepository.markAllUserTokensAsUsed(usuario);

        log.info("Password actualizado exitosamente para usuario: {}", usuario.getEmail());

        // Enviar email de confirmación
        try {
            emailService.enviarEmailConfirmacionReset(usuario.getEmail(), usuario.getUsuarioNombre());
        } catch (Exception e) {
            log.error("Error enviando email de confirmación a {}: {}", usuario.getEmail(), e.getMessage());
        }
    }

    public boolean validarToken(String token) {
        log.debug("Validando token: {}", token.substring(0, 8) + "...");

        return tokenRepository.findValidTokenByToken(token, LocalDateTime.now()).isPresent();
    }

    public PasswordResetTokensDTO obtenerTokenInfo(String token) {
        log.debug("Obteniendo información del token: {}", token.substring(0, 8) + "...");

        PasswordResetTokens resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token no encontrado"));

        return convertirADTO(resetToken);
    }

    public List<PasswordResetTokensDTO> obtenerTokensPorUsuario(Long usuarioId) {
        log.debug("Obteniendo tokens para usuario: {}", usuarioId);

        List<PasswordResetTokens> tokens = tokenRepository.findByUsuarioUsuarioId(usuarioId);
        return tokens.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<PasswordResetTokensDTO> obtenerTokensValidos(Long usuarioId) {
        log.debug("Obteniendo tokens válidos para usuario: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<PasswordResetTokens> tokens = tokenRepository.findValidTokensByUsuario(usuario, LocalDateTime.now());
        return tokens.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void invalidarTokensUsuario(Long usuarioId) {
        log.info("Invalidando todos los tokens del usuario: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        tokenRepository.markAllUserTokensAsUsed(usuario);
        log.info("Tokens invalidados exitosamente para usuario: {}", usuarioId);
    }

    @Transactional
    public void limpiarTokensExpirados() {
        log.info("Iniciando limpieza de tokens expirados");

        LocalDateTime now = LocalDateTime.now();
        List<PasswordResetTokens> tokensExpirados = tokenRepository.findExpiredTokens(now);

        if (!tokensExpirados.isEmpty()) {
            tokenRepository.deleteExpiredTokens(now);
            log.info("Eliminados {} tokens expirados", tokensExpirados.size());
        } else {
            log.debug("No se encontraron tokens expirados para eliminar");
        }
    }

    @Transactional
    public void limpiarTokensUsadosAntiguos(int diasAntiguedad) {
        log.info("Iniciando limpieza de tokens usados antiguos (más de {} días)", diasAntiguedad);

        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(diasAntiguedad);
        tokenRepository.deleteOldUsedTokens(fechaLimite);

        log.info("Limpieza de tokens usados antiguos completada");
    }

    public boolean puedeGenerarToken(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuario == null || !usuario.getActivo()) {
            return false;
        }

        long tokensActivos = tokenRepository.countValidTokensByUsuario(usuario, LocalDateTime.now());
        return tokensActivos < maxTokensPerUser;
    }

    private String generarToken() {
        // Generar token seguro usando UUID + timestamp + random
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf(new SecureRandom().nextInt(10000));

        return uuid + timestamp + random;
    }

    private PasswordResetTokensDTO convertirADTO(PasswordResetTokens token) {
        PasswordResetTokensDTO dto = new PasswordResetTokensDTO();
        dto.setId(token.getId());
        dto.setUsuarioId(token.getUsuario().getUsuarioId());
        dto.setToken(token.getToken());
        dto.setFechaExpiracion(token.getFechaExpiracion());
        dto.setUsado(token.getUsado());
        dto.setCreatedat(token.getCreatedat());

        // Información del usuario
        Usuario usuario = token.getUsuario();
        dto.setUsuarioNombre(usuario.getUsuarioNombre());
        dto.setUsuarioEmail(usuario.getEmail());

        return dto;
    }
}
