package com.digital.mecommerces.service;

import com.digital.mecommerces.dto.RegistroDTO;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.Tipo;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.TipoRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TipoRepository tipoRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, TipoRepository tipoRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tipoRepository = tipoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método para encriptar contraseñas
    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    // Obtener un usuario por ID
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    // Registrar un nuevo usuario
    public Usuario registrarUsuario(RegistroDTO registroDTO) {
        // Buscar el tipo de usuario
        Tipo tipo = tipoRepository.findById(registroDTO.getTipoId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado con id: " + registroDTO.getTipoId()));

        // Crear y guardar el nuevo usuario
        Usuario usuario = new Usuario(
                registroDTO.getNombre(),
                registroDTO.getEmail(),
                passwordEncoder.encode(registroDTO.getPassword()),
                tipo
        );

        return usuarioRepository.save(usuario);
    }

    // Actualizar un usuario existente
    public Usuario actualizarUsuario(Long id, Usuario usuarioDetails) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuario.setUsuarioNombre(usuarioDetails.getUsuarioNombre());
        usuario.setEmail(usuarioDetails.getEmail());

        // Solo actualizar la contraseña si se proporciona una nueva
        if (usuarioDetails.getPassword() != null && !usuarioDetails.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDetails.getPassword()));
        }

        usuario.setTipo(usuarioDetails.getTipo());

        return usuarioRepository.save(usuario);
    }

    // Eliminar un usuario
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuarioRepository.delete(usuario);
    }
}
