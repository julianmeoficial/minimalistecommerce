package com.digital.mecommerces.controller;

import com.digital.mecommerces.dto.AuthResponseDTO;
import com.digital.mecommerces.dto.LoginDTO;
import com.digital.mecommerces.dto.RegistroDTO;
import com.digital.mecommerces.exception.BusinessException;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.security.JwtTokenProvider;
import com.digital.mecommerces.service.RolUsuarioService;
import com.digital.mecommerces.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para autenticación y registro de usuarios
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "APIs para autenticación y registro de usuarios")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final RolUsuarioService rolUsuarioService;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioService usuarioService,
                          RolUsuarioService rolUsuarioService,
                          JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.rolUsuarioService = rolUsuarioService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario", description = "Autentica usuario y devuelve token JWT")
    public ResponseEntity<AuthResponseDTO> autenticarUsuario(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            log.info("🔐 Intento de login para: {}", loginDTO.getEmail());

            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar tokens
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = tokenProvider.generateToken(userDetails.getUsername());
            String refreshToken = tokenProvider.generateRefreshToken(userDetails.getUsername());

            // Obtener información del usuario
            Usuario usuario = usuarioService.obtenerUsuarioPorEmail(loginDTO.getEmail());

            // Registrar login exitoso
            usuarioService.registrarLogin(loginDTO.getEmail());

            // Obtener autoridades
            List<String> authorities = userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList());

            // Crear respuesta
            AuthResponseDTO.UsuarioSimpleDTO usuarioDTO = new AuthResponseDTO.UsuarioSimpleDTO(
                    usuario.getUsuarioId(),
                    usuario.getUsuarioNombre(),
                    usuario.getEmail(),
                    usuario.getRol().getNombre(),
                    usuario.getActivo(),
                    usuario.getUltimoLogin()
            );

            AuthResponseDTO response = AuthResponseDTO.loginSuccess(
                    token,
                    refreshToken,
                    usuarioDTO,
                    authorities,
                    tokenProvider.getJwtExpirationInMs() / 1000 // Convertir a segundos
            );

            log.info("✅ Login exitoso para: {}", loginDTO.getEmail());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("❌ Credenciales inválidas para: {}", loginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponseDTO.error("Credenciales inválidas"));

        } catch (Exception e) {
            log.error("❌ Error durante login para {}: {}", loginDTO.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponseDTO.error("Error interno del servidor"));
        }
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    public ResponseEntity<AuthResponseDTO> registrarUsuario(@Valid @RequestBody RegistroDTO registroDTO) {
        try {
            log.info("📝 Intento de registro para: {}", registroDTO.getEmail());

            // Verificar que el email no esté registrado
            if (usuarioService.existeEmail(registroDTO.getEmail())) {
                log.warn("❌ Email ya registrado: {}", registroDTO.getEmail());
                return ResponseEntity.badRequest()
                        .body(AuthResponseDTO.error("Este email ya está registrado"));
            }

            // Verificar que el rol existe
            RolUsuario rol = rolUsuarioService.obtenerRolPorId(registroDTO.getRolId());

            // Registrar usuario
            Usuario usuario = usuarioService.registrarUsuario(registroDTO);

            // Crear respuesta
            AuthResponseDTO.UsuarioSimpleDTO usuarioDTO = new AuthResponseDTO.UsuarioSimpleDTO(
                    usuario.getUsuarioId(),
                    usuario.getUsuarioNombre(),
                    usuario.getEmail(),
                    usuario.getRol().getNombre()
            );

            AuthResponseDTO response = AuthResponseDTO.registrationSuccess(usuarioDTO);

            log.info("✅ Usuario registrado exitosamente: {}", usuario.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (BusinessException e) {
            log.error("❌ Error de negocio en registro: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(AuthResponseDTO.error("Error en el registro: " + e.getMessage()));

        } catch (Exception e) {
            log.error("❌ Error durante registro para {}: {}", registroDTO.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponseDTO.error("Error interno del servidor"));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token", description = "Genera nuevo token usando refresh token")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            log.info("🔄 Solicitud de refresh token");

            // Extraer refresh token
            String refreshToken = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                refreshToken = authHeader.substring(7);
            }

            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(AuthResponseDTO.error("Refresh token requerido"));
            }

            // Generar nuevo token
            String newToken = tokenProvider.refreshToken(refreshToken);
            String username = tokenProvider.getUsernameFromToken(refreshToken);

            // Obtener información del usuario
            Usuario usuario = usuarioService.obtenerUsuarioPorEmail(username);

            AuthResponseDTO.UsuarioSimpleDTO usuarioDTO = new AuthResponseDTO.UsuarioSimpleDTO(
                    usuario.getUsuarioId(),
                    usuario.getUsuarioNombre(),
                    usuario.getEmail(),
                    usuario.getRol().getNombre(),
                    usuario.getActivo(),
                    usuario.getUltimoLogin()
            );

            AuthResponseDTO response = new AuthResponseDTO();
            response.setSuccess(true);
            response.setMessage("Token refrescado exitosamente");
            response.setToken(newToken);
            response.setRefreshToken(refreshToken); // Mantener el mismo refresh token
            response.setUsuario(usuarioDTO);
            response.setExpiresIn(tokenProvider.getJwtExpirationInMs() / 1000);
            response.setTimestamp(LocalDateTime.now());

            log.info("✅ Token refrescado exitosamente para: {}", username);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error refrescando token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponseDTO.error("Token de refresh inválido"));
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "Validar token", description = "Valida si un token JWT es válido")
    public ResponseEntity<AuthResponseDTO> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            log.debug("🔍 Validando token");

            // Extraer token
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(AuthResponseDTO.error("Token requerido"));
            }

            // Validar token
            if (tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromToken(token);
                Usuario usuario = usuarioService.obtenerUsuarioPorEmail(username);

                if (usuario != null && usuario.getActivo()) {
                    AuthResponseDTO.UsuarioSimpleDTO usuarioDTO = new AuthResponseDTO.UsuarioSimpleDTO(
                            usuario.getUsuarioId(),
                            usuario.getUsuarioNombre(),
                            usuario.getEmail(),
                            usuario.getRol().getNombre(),
                            usuario.getActivo(),
                            usuario.getUltimoLogin()
                    );

                    AuthResponseDTO response = new AuthResponseDTO();
                    response.setSuccess(true);
                    response.setMessage("Token válido");
                    response.setUsuario(usuarioDTO);
                    response.setTimestamp(LocalDateTime.now());

                    return ResponseEntity.ok(response);
                }
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponseDTO.error("Token inválido"));

        } catch (Exception e) {
            log.error("❌ Error validando token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponseDTO.error("Token inválido"));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Invalida la sesión actual del usuario")
    public ResponseEntity<AuthResponseDTO> logout() {
        try {
            log.info("🚪 Usuario cerrando sesión");

            // Limpiar contexto de seguridad
            SecurityContextHolder.clearContext();

            AuthResponseDTO response = new AuthResponseDTO();
            response.setSuccess(true);
            response.setMessage("Sesión cerrada exitosamente");
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error cerrando sesión: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponseDTO.error("Error cerrando sesión"));
        }
    }

    @GetMapping("/info")
    @Operation(summary = "Obtener información de registro", description = "Obtiene roles disponibles para registro")
    public ResponseEntity<AuthResponseDTO> obtenerInfoRegistro() {
        try {
            log.debug("ℹ️ Obteniendo información para registro");

            List<RolUsuario> roles = rolUsuarioService.obtenerRoles();

            AuthResponseDTO response = new AuthResponseDTO();
            response.setSuccess(true);
            response.setMessage("Información de registro obtenida");
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error obteniendo información de registro: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponseDTO.error("Error obteniendo información"));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener perfil del usuario autenticado")
    public ResponseEntity<AuthResponseDTO> obtenerPerfilUsuario() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(AuthResponseDTO.error("Usuario no autenticado"));
            }

            String email = authentication.getName();
            Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);

            AuthResponseDTO.UsuarioSimpleDTO usuarioDTO = new AuthResponseDTO.UsuarioSimpleDTO(
                    usuario.getUsuarioId(),
                    usuario.getUsuarioNombre(),
                    usuario.getEmail(),
                    usuario.getRol().getNombre(),
                    usuario.getActivo(),
                    usuario.getUltimoLogin()
            );

            AuthResponseDTO response = new AuthResponseDTO();
            response.setSuccess(true);
            response.setMessage("Perfil obtenido exitosamente");
            response.setUsuario(usuarioDTO);
            response.setTimestamp(LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error obteniendo perfil: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponseDTO.error("Error obteniendo perfil"));
        }
    }
}
