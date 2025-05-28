package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.repository.UsuarioRepository;
import com.digital.mecommerces.repository.RolUsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio para gestión de usuarios - MÉTODOS ADICIONALES
 * Estos métodos se agregan al UsuarioService existente
 */
@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, 
                         RolUsuarioRepository rolUsuarioRepository,
                         PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ... métodos existentes ...

    // === MÉTODOS PARA PAGINACIÓN ===

    public Page<Usuario> obtenerUsuariosPaginados(Pageable pageable) {
        log.info("👥 Obteniendo usuarios con paginación");
        return usuarioRepository.findAll(pageable);
    }

    public Page<Usuario> obtenerUsuariosActivos(Pageable pageable) {
        log.info("👥 Obteniendo usuarios activos con paginación");
        List<Usuario> usuariosActivos = usuarioRepository.findUsuariosActivos();
        return convertListToPage(usuariosActivos, pageable);
    }

    public Page<Usuario> obtenerUsuariosInactivos(Pageable pageable) {
        log.info("👥 Obteniendo usuarios inactivos con paginación");
        List<Usuario> usuariosInactivos = usuarioRepository.findUsuariosInactivos();
        return convertListToPage(usuariosInactivos, pageable);
    }

    public Page<Usuario> obtenerUsuariosPorRol(Long rolId, Pageable pageable) {
        log.info("👥 Obteniendo usuarios por rol ID: {} con paginación", rolId);
        List<Usuario> usuarios = usuarioRepository.findByRolRolId(rolId);
        return convertListToPage(usuarios, pageable);
    }

    public Page<Usuario> buscarUsuarios(String query, Pageable pageable) {
        log.info("🔍 Buscando usuarios con término: {}", query);

        if (query == null || query.trim().isEmpty()) {
            return obtenerUsuariosPaginados(pageable);
        }

        List<Usuario> usuarios = usuarioRepository.findByTextoEnNombreOEmail(query);
        return convertListToPage(usuarios, pageable);
    }

    private Page<Usuario> convertListToPage(List<Usuario> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        if (start > list.size()) {
            return Page.empty(pageable);
        }

        List<Usuario> subList = list.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(subList, pageable, list.size());
    }

    // === MÉTODOS PARA CONTADORES ===

    public long contarUsuarios() {
        log.info("📊 Contando todos los usuarios");
        return usuarioRepository.count();
    }

    public long contarUsuariosActivos() {
        log.info("📊 Contando usuarios activos");
        return usuarioRepository.countUsuariosActivos();
    }

    public long contarUsuariosInactivos() {
        log.info("📊 Contando usuarios inactivos");
        return usuarioRepository.countUsuariosInactivos();
    }

    public long contarAdministradores() {
        log.info("📊 Contando administradores");
        return usuarioRepository.countByRolNombre("ADMINISTRADOR");
    }

    public long contarVendedores() {
        log.info("📊 Contando vendedores");
        return usuarioRepository.countByRolNombre("VENDEDOR");
    }

    public long contarCompradores() {
        log.info("📊 Contando compradores");
        return usuarioRepository.countByRolNombre("COMPRADOR");
    }

    public long contarUsuariosRecientes() {
        log.info("📊 Contando usuarios recientes (últimos 30 días)");
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(30);
        List<Usuario> usuariosRecientes = usuarioRepository.findUsuariosCreadosDesde(fechaLimite);
        return usuariosRecientes.size();
    }

    public long contarUsuariosConDetalles() {
        log.info("📊 Contando usuarios con detalles específicos");
        // Esto requerirá joins con las tablas de detalles
        List<Usuario> compradoresConDetalles = usuarioRepository.findCompradoresConDetalles();
        List<Usuario> vendedoresConDetalles = usuarioRepository.findVendedoresConDetalles();
        List<Usuario> adminsConDetalles = usuarioRepository.findAdministradoresConDetalles();

        return compradoresConDetalles.size() + vendedoresConDetalles.size() + adminsConDetalles.size();
    }

    // === MÉTODOS PARA GESTIÓN DE ESTADO ===

    @Transactional
    public void activarUsuario(Long id) {
        log.info("✅ Activando usuario ID: {}", id);

        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setActivo(true);
        usuario.setUpdatedAt(LocalDateTime.now());

        usuarioRepository.save(usuario);
        log.info("✅ Usuario activado exitosamente");
    }

    @Transactional
    public void desactivarUsuario(Long id) {
        log.info("❌ Desactivando usuario ID: {}", id);

        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setActivo(false);
        usuario.setUpdatedAt(LocalDateTime.now());

        usuarioRepository.save(usuario);
        log.info("✅ Usuario desactivado exitosamente");
    }

    @Transactional
    public Usuario cambiarRolUsuario(Long id, Long nuevoRolId) {
        log.info("🔄 Cambiando rol del usuario ID: {} al rol ID: {}", id, nuevoRolId);

        Usuario usuario = obtenerUsuarioPorId(id);
        RolUsuario nuevoRol = rolUsuarioRepository.findById(nuevoRolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + nuevoRolId));

        usuario.setRol(nuevoRol);
        usuario.setUpdatedAt(LocalDateTime.now());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("✅ Rol de usuario cambiado exitosamente");
        return usuarioActualizado;
    }

    // === MÉTODOS PARA GESTIÓN DE CONTRASEÑAS ===

    @Transactional
    public String resetearPassword(Long id) {
        log.info("🔒 Reseteando contraseña del usuario ID: {}", id);

        Usuario usuario = obtenerUsuarioPorId(id);

        // Generar nueva contraseña temporal
        String nuevaPassword = generarPasswordTemporal();
        String passwordEncriptada = passwordEncoder.encode(nuevaPassword);

        usuario.setPassword(passwordEncriptada);
        usuario.setUpdatedAt(LocalDateTime.now());

        usuarioRepository.save(usuario);

        log.info("✅ Contraseña reseteada exitosamente para usuario: {}", usuario.getEmail());

        // En un sistema real, aquí se enviaría un email
        // emailService.enviarNuevaPassword(usuario.getEmail(), nuevaPassword);

        return nuevaPassword;
    }

    @Transactional
    public void cambiarPassword(String email, String passwordActual, String passwordNueva) {
        log.info("🔒 Cambiando contraseña del usuario: {}", email);

        Usuario usuario = obtenerUsuarioPorEmail(email);

        // Verificar contraseña actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // Validar nueva contraseña
        if (passwordNueva == null || passwordNueva.length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        String passwordEncriptada = passwordEncoder.encode(passwordNueva);
        usuario.setPassword(passwordEncriptada);
        usuario.setUpdatedAt(LocalDateTime.now());

        usuarioRepository.save(usuario);
        log.info("✅ Contraseña cambiada exitosamente");
    }

    // === MÉTODOS PARA ESTADÍSTICAS ===

    public Map<String, Long> obtenerDistribucionPorRol() {
        log.info("📊 Obteniendo distribución de usuarios por rol");

        Map<String, Long> distribucion = new HashMap<>();
        distribucion.put("ADMINISTRADOR", contarAdministradores());
        distribucion.put("VENDEDOR", contarVendedores());
        distribucion.put("COMPRADOR", contarCompradores());

        return distribucion;
    }

    public List<Usuario> obtenerUsuariosRecientes(int limite) {
        log.info("📊 Obteniendo {} usuarios más recientes", limite);
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .limit(limite)
                .toList();
    }

    public List<Usuario> obtenerUltimosLogins(int limite) {
        log.info("📊 Obteniendo {} últimos logins", limite);
        List<Usuario> usuarios = usuarioRepository.findUsuariosConLogin();
        return usuarios.stream()
                .sorted((u1, u2) -> u2.getUltimoLogin().compareTo(u1.getUltimoLogin()))
                .limit(limite)
                .toList();
    }

    // === MÉTODOS PARA NOTIFICACIONES ===

    public int enviarNotificacion(List<Long> usuariosIds, String mensaje) {
        log.info("📧 Enviando notificación a {} usuarios", usuariosIds.size());

        int notificacionesEnviadas = 0;

        for (Long usuarioId : usuariosIds) {
            try {
                Usuario usuario = obtenerUsuarioPorId(usuarioId);

                // En un sistema real, aquí se implementaría el envío real
                // notificationService.enviarNotificacion(usuario, mensaje);

                log.debug("📧 Notificación enviada a: {}", usuario.getEmail());
                notificacionesEnviadas++;

            } catch (Exception e) {
                log.error("❌ Error enviando notificación al usuario ID {}: {}", usuarioId, e.getMessage());
            }
        }

        log.info("✅ {} notificaciones enviadas exitosamente", notificacionesEnviadas);
        return notificacionesEnviadas;
    }

    // === MÉTODOS PARA LOGIN Y REGISTRO ===

    @Transactional
    public void registrarLogin(String email) {
        log.info("🔐 Registrando login para usuario: {}", email);

        try {
            Usuario usuario = obtenerUsuarioPorEmail(email);
            usuario.setUltimoLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);

        } catch (Exception e) {
            log.error("❌ Error registrando login para {}: {}", email, e.getMessage());
        }
    }

    public boolean existeEmail(String email) {
        log.debug("🔍 Verificando si existe email: {}", email);
        return usuarioRepository.existsByEmail(email);
    }

    // === MÉTODOS AUXILIARES ===

    private String generarPasswordTemporal() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Método ya existe en el servicio original, solo verificamos que esté
    public Usuario obtenerUsuarioPorId(Long id) {
        log.info("🔍 Obteniendo usuario por ID: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    // Método ya existe en el servicio original, solo verificamos que esté
    public Usuario obtenerUsuarioPorEmail(String email) {
        log.info("🔍 Obteniendo usuario por email: {}", email);
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    // Método ya existe en el servicio original, solo verificamos que esté
    public List<Usuario> obtenerUsuarios() {
        log.info("👥 Obteniendo todos los usuarios");
        return usuarioRepository.findAll();
    }
}
