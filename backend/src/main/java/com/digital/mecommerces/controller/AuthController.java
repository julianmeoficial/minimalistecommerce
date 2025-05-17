package com.digital.mecommerces.controller;

import com.digital.mecommerces.dto.AuthResponseDTO;
import com.digital.mecommerces.dto.LoginDTO;
import com.digital.mecommerces.dto.RegistroDTO;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.UsuarioRepository;
import com.digital.mecommerces.security.JwtTokenProvider;
import com.digital.mecommerces.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioService usuarioService,
                          JwtTokenProvider tokenProvider,
                          UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.tokenProvider = tokenProvider;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication.getName());

        return ResponseEntity.ok(new AuthResponseDTO("Usuario autenticado exitosamente", true, token));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroDTO registroDTO) {
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            return new ResponseEntity<>(new AuthResponseDTO("Este email ya está en uso", false), HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = usuarioService.registrarUsuario(registroDTO);

        return new ResponseEntity<>(new AuthResponseDTO("Usuario registrado exitosamente", true), HttpStatus.CREATED);
    }
}
