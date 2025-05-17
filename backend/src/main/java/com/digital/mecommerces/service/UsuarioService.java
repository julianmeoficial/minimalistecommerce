package com.digital.mecommerces.service;

import com.digital.mecommerces.dto.RegistroDTO;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final AdminDetallesRepository adminDetallesRepository;
    private final CompradorDetallesRepository compradorDetallesRepository;
    private final VendedorDetallesRepository vendedorDetallesRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolUsuarioRepository rolUsuarioRepository,
            AdminDetallesRepository adminDetallesRepository,
            CompradorDetallesRepository compradorDetallesRepository,
            VendedorDetallesRepository vendedorDetallesRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.adminDetallesRepository = adminDetallesRepository;
        this.compradorDetallesRepository = compradorDetallesRepository;
        this.vendedorDetallesRepository = vendedorDetallesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Transactional
    public Usuario registrarUsuario(RegistroDTO registroDTO) {
        // Buscar el rol de usuario
        RolUsuario rol = rolUsuarioRepository.findById(registroDTO.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + registroDTO.getRolId()));

        // Crear y guardar el nuevo usuario
        Usuario usuario = new Usuario(
                registroDTO.getNombre(),
                registroDTO.getEmail(),
                passwordEncoder.encode(registroDTO.getPassword()),
                rol
        );

        usuario = usuarioRepository.save(usuario);

        // Crear y guardar detalles específicos según el rol
        String rolNombre = rol.getNombre();
        if ("ADMINISTRADOR".equals(rolNombre)) {
            AdminDetalles adminDetalles = new AdminDetalles();
            adminDetalles.setUsuario(usuario);
            adminDetalles.setRegion(registroDTO.getRegion() != null ? registroDTO.getRegion() : "Default");
            adminDetalles.setNivelAcceso(registroDTO.getNivelAcceso() != null ? registroDTO.getNivelAcceso() : "BASICO");
            adminDetallesRepository.save(adminDetalles);
        } else if ("COMPRADOR".equals(rolNombre)) {
            CompradorDetalles compradorDetalles = new CompradorDetalles();
            compradorDetalles.setUsuario(usuario);
            compradorDetalles.setDireccionEnvio(registroDTO.getDireccionEnvio());
            compradorDetalles.setTelefono(registroDTO.getTelefono());
            compradorDetallesRepository.save(compradorDetalles);
        } else if ("VENDEDOR".equals(rolNombre)) {
            VendedorDetalles vendedorDetalles = new VendedorDetalles();
            vendedorDetalles.setUsuario(usuario);
            vendedorDetalles.setNumRegistroFiscal(registroDTO.getNumRegistroFiscal());
            vendedorDetalles.setEspecialidad(registroDTO.getEspecialidad());
            vendedorDetalles.setDireccionComercial(registroDTO.getDireccionComercial());
            vendedorDetallesRepository.save(vendedorDetalles);
        }

        return usuario;
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario usuarioDetails) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setUsuarioNombre(usuarioDetails.getUsuarioNombre());
        usuario.setEmail(usuarioDetails.getEmail());

        if (usuarioDetails.getPassword() != null && !usuarioDetails.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDetails.getPassword()));
        }

        if (usuarioDetails.getRol() != null) {
            RolUsuario rol = rolUsuarioRepository.findById(usuarioDetails.getRol().getRolId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
            usuario.setRol(rol);
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuarioRepository.delete(usuario);
    }
}
