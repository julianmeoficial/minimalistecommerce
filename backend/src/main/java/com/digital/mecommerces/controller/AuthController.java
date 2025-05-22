package com.digital.mecommerces.controller;

import com.digital.mecommerces.dto.AuthResponseDTO;
import com.digital.mecommerces.dto.LoginDTO;
import com.digital.mecommerces.dto.RegistroDTO;
import com.digital.mecommerces.exception.BusinessException;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.RolUsuarioRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import com.digital.mecommerces.security.JwtTokenProvider;
import com.digital.mecommerces.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolUsuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioService usuarioService,
                          JwtTokenProvider tokenProvider,
                          UsuarioRepository usuarioRepository,
                          RolUsuarioRepository rolUsuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.tokenProvider = tokenProvider;
        this.usuarioRepository = usuarioRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            log.info("Intento de login para email: {}", loginDTO.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = tokenProvider.generateToken(authentication.getName());

            // Actualizar último login
            Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail()).orElse(null);
            if (usuario != null) {
                usuario.setUltimoLogin(LocalDateTime.now());
                usuarioRepository.save(usuario);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario autenticado exitosamente");
            response.put("success", true);
            response.put("token", token);
            response.put("usuario", Map.of(
                    "id", usuario.getUsuarioId(),
                    "nombre", usuario.getUsuarioNombre(),
                    "email", usuario.getEmail(),
                    "rol", usuario.getRol().getNombre()
            ));

            log.info("Login exitoso para usuario: {}", loginDTO.getEmail());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Credenciales inválidas para email: {}", loginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDTO("Credenciales inválidas", false));
        } catch (Exception e) {
            log.error("Error durante el login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponseDTO("Error interno del servidor", false));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroDTO registroDTO) {
        try {
            log.info("Intento de registro para email: {}", registroDTO.getEmail());

            // Verificar que el rol existe
            RolUsuario rol = rolUsuarioRepository.findById(registroDTO.getRolId())
                    .orElseThrow(() -> new BusinessException("Rol no válido", "INVALID_ROLE"));

            // Verificar que el email no esté registrado
            if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
                log.warn("Intento de registro con email ya existente: {}", registroDTO.getEmail());
                return ResponseEntity.badRequest()
                        .body(new AuthResponseDTO("Este email ya está registrado", false));
            }

            // Registrar el usuario
            Usuario usuario = usuarioService.registrarUsuario(registroDTO);

            log.info("Usuario registrado exitosamente: {}", usuario.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario registrado exitosamente");
            response.put("success", true);
            response.put("usuario", Map.of(
                    "id", usuario.getUsuarioId(),
                    "nombre", usuario.getUsuarioNombre(),
                    "email", usuario.getEmail(),
                    "rol", usuario.getRol().getNombre()
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (BusinessException e) {
            log.error("Error de negocio durante el registro: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new AuthResponseDTO("Error en el registro: " + e.getMessage(), false));
        } catch (Exception e) {
            log.error("Error durante el registro: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponseDTO("Error interno del servidor", false));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (tokenProvider.validateToken(token)) {
                String email = tokenProvider.getUsernameFromToken(token);
                Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

                if (usuario != null && usuario.getActivo()) {
                    return ResponseEntity.ok(Map.of(
                            "valid", true,
                            "usuario", Map.of(
                                    "id", usuario.getUsuarioId(),
                                    "nombre", usuario.getUsuarioNombre(),
                                    "email", usuario.getEmail(),
                                    "rol", usuario.getRol().getNombre()
                            )
                    ));
                }
            }

            return ResponseEntity.ok(Map.of("valid", false));
        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
}
