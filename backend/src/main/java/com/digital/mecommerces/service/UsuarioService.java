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

        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado: " + registroDTO.getEmail());
        }

        // Obtener el rol
        RolUsuario rol = rolUsuarioRepository.findById(registroDTO.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + registroDTO.getRolId()));

        // Crear usuario
        Usuario usuario = new Usuario(
                registroDTO.getNombre(),
                registroDTO.getEmail(),
                passwordEncoder.encode(registroDTO.getPassword()),
                rol
        );

        // CRÍTICO: Guardar usuario PRIMERO para obtener el ID
        usuario = usuarioRepository.saveAndFlush(usuario);
        log.info("Usuario guardado con ID: {}", usuario.getUsuarioId());

        // Ahora crear detalles específicos según el rol CON el ID ya asignado
        try {
            String rolNombre = rol.getNombre();
            log.info("Creando detalles para rol: {}", rolNombre);

            if ("COMPRADOR".equals(rolNombre)) {
                crearDetallesComprador(usuario, registroDTO);
            } else if ("VENDEDOR".equals(rolNombre)) {
                crearDetallesVendedor(usuario, registroDTO);
            } else if ("ADMINISTRADOR".equals(rolNombre)) {
                crearDetallesAdmin(usuario, registroDTO);
            } else {
                log.warn("Rol no reconocido para crear detalles: {}", rolNombre);
            }
        } catch (Exception e) {
            log.error("Error creando detalles para usuario {}: {}", usuario.getEmail(), e.getMessage(), e);
            // El usuario ya está guardado, pero falló la creación de detalles
            // En caso de error, eliminamos el usuario para mantener la integridad
            usuarioRepository.delete(usuario);
            throw new RuntimeException("Error al crear detalles específicos del rol: " + e.getMessage(), e);
        }

        log.info("Usuario registrado exitosamente: {}", usuario.getEmail());
        return usuario;
    }

    private void crearDetallesComprador(Usuario usuario, RegistroDTO registroDTO) {
        try {
            log.info("Creando detalles de comprador para usuario ID: {}", usuario.getUsuarioId());

            // Verificar que el usuario tenga ID
            if (usuario.getUsuarioId() == null) {
                throw new IllegalStateException("El usuario debe tener un ID válido antes de crear detalles");
            }

            // Verificar si ya existen detalles para este usuario
            if (compradorDetallesRepository.existsById(usuario.getUsuarioId())) {
                log.warn("Ya existen detalles de comprador para el usuario ID: {}", usuario.getUsuarioId());
                return;
            }

            CompradorDetalles compradorDetalles = new CompradorDetalles();
            // CRÍTICO: Establecer el ID manualmente
            compradorDetalles.setUsuarioId(usuario.getUsuarioId());
            compradorDetalles.setUsuario(usuario);

            // Establecer datos del registro
            if (registroDTO.getFechaNacimiento() != null) {
                compradorDetalles.setFechaNacimiento(registroDTO.getFechaNacimiento());
            }
            if (registroDTO.getPreferencias() != null) {
                compradorDetalles.setPreferencias(registroDTO.getPreferencias());
            }
            if (registroDTO.getDireccionEnvio() != null) {
                compradorDetalles.setDireccionEnvio(registroDTO.getDireccionEnvio());
            }
            if (registroDTO.getTelefono() != null) {
                compradorDetalles.setTelefono(registroDTO.getTelefono());
            }
            if (registroDTO.getDireccionAlternativa() != null) {
                compradorDetalles.setDireccionAlternativa(registroDTO.getDireccionAlternativa());
            }
            if (registroDTO.getTelefonoAlternativo() != null) {
                compradorDetalles.setTelefonoAlternativo(registroDTO.getTelefonoAlternativo());
            }

            // Establecer valores de notificaciones
            compradorDetalles.setNotificacionEmail(
                    registroDTO.getNotificacionEmail() != null ? registroDTO.getNotificacionEmail() : true
            );
            compradorDetalles.setNotificacionSms(
                    registroDTO.getNotificacionSms() != null ? registroDTO.getNotificacionSms() : false
            );

            // Establecer valores predeterminados
            compradorDetalles.setCalificacion(new java.math.BigDecimal("5.00"));
            compradorDetalles.setTotalCompras(0);

            CompradorDetalles savedDetails = compradorDetallesRepository.save(compradorDetalles);
            log.info("Detalles de comprador creados exitosamente para usuario: {} con ID: {}", 
                    usuario.getEmail(), savedDetails.getUsuarioId());

        } catch (Exception e) {
            log.error("Error creando detalles de comprador para usuario {}: {}", usuario.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Error al crear detalles del comprador: " + e.getMessage(), e);
        }
    }

    private void crearDetallesVendedor(Usuario usuario, RegistroDTO registroDTO) {
        try {
            log.info("Creando detalles de vendedor para usuario ID: {}", usuario.getUsuarioId());

            // Verificar que el usuario tenga ID
            if (usuario.getUsuarioId() == null) {
                throw new IllegalStateException("El usuario debe tener un ID válido antes de crear detalles");
            }

            // Verificar si ya existen detalles para este usuario
            if (vendedorDetallesRepository.existsById(usuario.getUsuarioId())) {
                log.warn("Ya existen detalles de vendedor para el usuario ID: {}", usuario.getUsuarioId());
                return;
            }

            VendedorDetalles vendedorDetalles = new VendedorDetalles();
            // CRÍTICO: Establecer el ID manualmente
            vendedorDetalles.setUsuarioId(usuario.getUsuarioId());
            vendedorDetalles.setUsuario(usuario);

            // Establecer datos del registro
            if (registroDTO.getRut() != null) {
                vendedorDetalles.setRut(registroDTO.getRut());
            }
            if (registroDTO.getEspecialidad() != null) {
                vendedorDetalles.setEspecialidad(registroDTO.getEspecialidad());
            }
            if (registroDTO.getDireccionComercial() != null) {
                vendedorDetalles.setDireccionComercial(registroDTO.getDireccionComercial());
            }
            if (registroDTO.getNumRegistroFiscal() != null) {
                vendedorDetalles.setNumRegistroFiscal(registroDTO.getNumRegistroFiscal());
            }
            if (registroDTO.getDocumentoComercial() != null) {
                vendedorDetalles.setDocumentoComercial(registroDTO.getDocumentoComercial());
            }
            if (registroDTO.getTipoDocumento() != null) {
                vendedorDetalles.setTipoDocumento(registroDTO.getTipoDocumento());
            }
            if (registroDTO.getBanco() != null) {
                vendedorDetalles.setBanco(registroDTO.getBanco());
            }
            if (registroDTO.getTipoCuenta() != null) {
                vendedorDetalles.setTipoCuenta(registroDTO.getTipoCuenta());
            }
            if (registroDTO.getNumeroCuenta() != null) {
                vendedorDetalles.setNumeroCuenta(registroDTO.getNumeroCuenta());
            }

            // Valores predeterminados
            vendedorDetalles.setVerificado(false);

            VendedorDetalles savedDetails = vendedorDetallesRepository.save(vendedorDetalles);
            log.info("Detalles de vendedor creados exitosamente para usuario: {} con ID: {}", 
                    usuario.getEmail(), savedDetails.getUsuarioId());

        } catch (Exception e) {
            log.error("Error creando detalles de vendedor para usuario {}: {}", usuario.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Error al crear detalles del vendedor: " + e.getMessage(), e);
        }
    }

    private void crearDetallesAdmin(Usuario usuario, RegistroDTO registroDTO) {
        try {
            log.info("Creando detalles de administrador para usuario ID: {}", usuario.getUsuarioId());

            // Verificar que el usuario tenga ID
            if (usuario.getUsuarioId() == null) {
                throw new IllegalStateException("El usuario debe tener un ID válido antes de crear detalles");
            }

            // Verificar si ya existen detalles para este usuario
            if (adminDetallesRepository.existsById(usuario.getUsuarioId())) {
                log.warn("Ya existen detalles de administrador para el usuario ID: {}", usuario.getUsuarioId());
                return;
            }

            AdminDetalles adminDetalles = new AdminDetalles();
            // CRÍTICO: Establecer el ID manualmente
            adminDetalles.setUsuarioId(usuario.getUsuarioId());
            adminDetalles.setUsuario(usuario);

            // Establecer datos del registro
            if (registroDTO.getRegion() != null) {
                adminDetalles.setRegion(registroDTO.getRegion());
            }
            if (registroDTO.getNivelAcceso() != null) {
                adminDetalles.setNivelAcceso(registroDTO.getNivelAcceso());
            }
            if (registroDTO.getIpAcceso() != null) {
                adminDetalles.setIpAcceso(registroDTO.getIpAcceso());
            }

            // Valores predeterminados
            adminDetalles.setUltimaAccion("Usuario creado");
            adminDetalles.setUltimoLogin(java.time.LocalDateTime.now());

            AdminDetalles savedDetails = adminDetallesRepository.save(adminDetalles);
            log.info("Detalles de administrador creados exitosamente para usuario: {} con ID: {}", 
                    usuario.getEmail(), savedDetails.getUsuarioId());

        } catch (Exception e) {
            log.error("Error creando detalles de administrador para usuario {}: {}", usuario.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Error al crear detalles del administrador: " + e.getMessage(), e);
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
