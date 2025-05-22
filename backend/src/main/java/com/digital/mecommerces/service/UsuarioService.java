package com.digital.mecommerces.service;

import com.digital.mecommerces.dto.RegistroDTO;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.*;
import com.digital.mecommerces.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
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

    @Transactional
    public Usuario registrarUsuario(RegistroDTO registroDTO) {
        log.info("Registrando usuario: {}", registroDTO.getEmail());

        try {
            // Verificar que el email no esté en uso
            if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }

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

            usuario.setCreatedat(LocalDateTime.now());
            usuario.setUpdatedat(LocalDateTime.now());
            usuario.setActivo(true);

            usuario = usuarioRepository.save(usuario);
            log.info("Usuario guardado con ID: {}", usuario.getUsuarioId());

            // Crear y guardar detalles específicos según el rol
            String rolNombre = rol.getNombre();
            if ("ADMINISTRADOR".equals(rolNombre)) {
                crearDetallesAdmin(usuario, registroDTO);
            } else if ("COMPRADOR".equals(rolNombre)) {
                crearDetallesComprador(usuario, registroDTO);
            } else if ("VENDEDOR".equals(rolNombre)) {
                crearDetallesVendedor(usuario, registroDTO);
            }

            log.info("Usuario y detalles creados exitosamente para: {}", usuario.getEmail());
            return usuario;

        } catch (Exception e) {
            log.error("Error registrando usuario {}: {}", registroDTO.getEmail(), e.getMessage());
            throw e;
        }
    }

    private void crearDetallesComprador(Usuario usuario, RegistroDTO registroDTO) {
        try {
            log.info("Creando detalles de comprador para usuario ID: {}", usuario.getUsuarioId());

            // ✅ CRÍTICO: Crear la instancia con el usuario ya establecido
            CompradorDetalles compradorDetalles = new CompradorDetalles(usuario);

            // Establecer datos del registro
            compradorDetalles.setDireccionEnvio(registroDTO.getDireccionEnvio());
            compradorDetalles.setTelefono(registroDTO.getTelefono());

            // Guardar los detalles
            compradorDetallesRepository.save(compradorDetalles);
            log.info("Detalles de comprador creados exitosamente para usuario: {}", usuario.getUsuarioId());

        } catch (Exception e) {
            log.error("Error creando detalles de comprador para usuario {}: {}", usuario.getUsuarioId(), e.getMessage());
            throw new RuntimeException("Error al crear detalles del comprador: " + e.getMessage(), e);
        }
    }

    private void crearDetallesAdmin(Usuario usuario, RegistroDTO registroDTO) {
        try {
            log.info("Creando detalles de administrador para usuario ID: {}", usuario.getUsuarioId());

            AdminDetalles adminDetalles = new AdminDetalles();
            adminDetalles.setUsuario(usuario);
            adminDetalles.setUsuarioId(usuario.getUsuarioId());
            adminDetalles.setRegion(registroDTO.getRegion() != null ? registroDTO.getRegion() : "Default");
            adminDetalles.setNivelAcceso(registroDTO.getNivelAcceso() != null ? registroDTO.getNivelAcceso() : "BASICO");
            adminDetalles.setUltimaAccion("Registro de cuenta");
            adminDetalles.setUltimoLogin(LocalDateTime.now());

            adminDetallesRepository.save(adminDetalles);
            log.info("Detalles de administrador creados para usuario: {}", usuario.getUsuarioId());
        } catch (Exception e) {
            log.error("Error creando detalles de administrador: {}", e.getMessage());
            throw new RuntimeException("Error al crear detalles del administrador: " + e.getMessage(), e);
        }
    }

    private void crearDetallesVendedor(Usuario usuario, RegistroDTO registroDTO) {
        try {
            log.info("Creando detalles de vendedor para usuario ID: {}", usuario.getUsuarioId());

            VendedorDetalles vendedorDetalles = new VendedorDetalles();
            vendedorDetalles.setUsuario(usuario);
            vendedorDetalles.setUsuarioId(usuario.getUsuarioId());
            vendedorDetalles.setNumRegistroFiscal(registroDTO.getNumRegistroFiscal());
            vendedorDetalles.setEspecialidad(registroDTO.getEspecialidad());
            vendedorDetalles.setDireccionComercial(registroDTO.getDireccionComercial());
            vendedorDetalles.setVerificado(false);
            vendedorDetalles.setFechaVerificacion(null);

            vendedorDetallesRepository.save(vendedorDetalles);
            log.info("Detalles de vendedor creados para usuario: {}", usuario.getUsuarioId());
        } catch (Exception e) {
            log.error("Error creando detalles de vendedor: {}", e.getMessage());
            throw new RuntimeException("Error al crear detalles del vendedor: " + e.getMessage(), e);
        }
    }

    // Resto de métodos del servicio...
    public List<Usuario> obtenerUsuarios() {
        log.info("Obteniendo lista de todos los usuarios");
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        log.info("Obteniendo usuario por ID: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        log.info("Obteniendo usuario por email: {}", email);
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario usuarioDetails) {
        log.info("Actualizando usuario con ID: {}", id);

        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setUsuarioNombre(usuarioDetails.getUsuarioNombre());
        usuario.setEmail(usuarioDetails.getEmail());
        usuario.setUpdatedat(LocalDateTime.now());

        if (usuarioDetails.getPassword() != null && !usuarioDetails.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDetails.getPassword()));
        }

        if (usuarioDetails.getRol() != null) {
            RolUsuario rol = rolUsuarioRepository.findById(usuarioDetails.getRol().getRolId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
            usuario.setRol(rol);
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado exitosamente: {}", usuarioActualizado.getEmail());
        return usuarioActualizado;
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        Usuario usuario = obtenerUsuarioPorId(id);
        usuarioRepository.delete(usuario);
        log.info("Usuario eliminado exitosamente: {}", usuario.getEmail());
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
