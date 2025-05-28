package com.digital.mecommerces.service;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.enums.TipoUsuario;
import com.digital.mecommerces.exception.BusinessException;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.AdminDetalles;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.AdminDetallesRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AdminDetallesService {

    private final AdminDetallesRepository adminDetallesRepository;
    private final UsuarioRepository usuarioRepository;

    public AdminDetallesService(AdminDetallesRepository adminDetallesRepository,
                                UsuarioRepository usuarioRepository) {
        this.adminDetallesRepository = adminDetallesRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Cacheable(value = "adminDetalles", key = "#usuarioId")
    public AdminDetalles obtenerDetallesPorUsuarioId(Long usuarioId) {
        log.info("🔍 Obteniendo detalles de administrador para usuario ID: {}", usuarioId);
        return adminDetallesRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Detalles de administrador no encontrados para usuario ID: " + usuarioId));
    }

    @Transactional
    @CacheEvict(value = "adminDetalles", key = "#usuarioId")
    public AdminDetalles crearOActualizarDetalles(Long usuarioId, AdminDetalles detalles) {
        log.info("💾 Creando/actualizando detalles de administrador para usuario ID: {}", usuarioId);

        // Verificar que el usuario existe y es administrador
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        // Validar que sea realmente administrador
        if (!RoleConstants.ROLE_ADMINISTRADOR.equals(usuario.getRol().getNombre())) {
            try {
                TipoUsuario tipo = TipoUsuario.fromCodigo(usuario.getRol().getNombre());
                if (tipo != TipoUsuario.ADMINISTRADOR) {
                    throw new BusinessException("El usuario no tiene rol de administrador. Rol actual: " + tipo.getDescripcion());
                }
            } catch (IllegalArgumentException e) {
                throw new BusinessException("El usuario no tiene un rol válido del sistema: " + usuario.getRol().getNombre());
            }
        }

        // Buscar detalles existentes o crear nuevos
        AdminDetalles adminDetalles = adminDetallesRepository.findByUsuarioId(usuarioId)
                .orElse(new AdminDetalles(usuario));

        // Actualizar campos
        if (detalles.getRegion() != null) {
            adminDetalles.setRegion(detalles.getRegion());
        }

        if (detalles.getNivelAcceso() != null) {
            adminDetalles.setNivelAcceso(detalles.getNivelAcceso());
        }

        if (detalles.getConfiguraciones() != null) {
            adminDetalles.setConfiguraciones(detalles.getConfiguraciones());
        }

        adminDetalles.registrarActividad("Detalles actualizados desde AdminDetallesService");

        AdminDetalles resultado = adminDetallesRepository.save(adminDetalles);
        log.info("✅ Detalles de administrador guardados exitosamente para usuario: {}", usuario.getEmail());

        return resultado;
    }

    @Transactional
    @CacheEvict(value = "adminDetalles", key = "#usuarioId")
    public void eliminarDetalles(Long usuarioId) {
        log.info("🗑️ Eliminando detalles de administrador para usuario ID: {}", usuarioId);

        if (!adminDetallesRepository.existsByUsuarioId(usuarioId)) {
            throw new ResourceNotFoundException("Detalles de administrador no encontrados para usuario ID: " + usuarioId);
        }

        adminDetallesRepository.deleteByUsuarioId(usuarioId);
        log.info("✅ Detalles de administrador eliminados exitosamente");
    }

    public List<AdminDetalles> obtenerTodosLosAdministradores() {
        log.info("📋 Obteniendo todos los administradores activos");
        return adminDetallesRepository.findAdministradoresActivos();
    }

    public List<AdminDetalles> obtenerAdministradoresPorRegion(String region) {
        log.info("🌍 Obteniendo administradores por región: {}", region);
        return adminDetallesRepository.findByRegion(region);
    }

    public List<AdminDetalles> obtenerAdministradoresPorNivelAcceso(String nivelAcceso) {
        log.info("🔐 Obteniendo administradores por nivel de acceso: {}", nivelAcceso);
        return adminDetallesRepository.findByNivelAcceso(nivelAcceso);
    }

    public List<AdminDetalles> obtenerAdministradoresSuper() {
        log.info("👑 Obteniendo administradores SUPER");
        return adminDetallesRepository.findAdministradoresSuper();
    }

    @Transactional
    public void registrarLogin(Long usuarioId, String ipAcceso) {
        log.info("🔐 Registrando login para administrador ID: {} desde IP: {}", usuarioId, ipAcceso);

        AdminDetalles adminDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        adminDetalles.iniciarSesion(ipAcceso);
        adminDetallesRepository.save(adminDetalles);
    }

    @Transactional
    public void registrarLogout(Long usuarioId) {
        log.info("🚪 Registrando logout para administrador ID: {}", usuarioId);

        AdminDetalles adminDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        adminDetalles.cerrarSesion();
        adminDetallesRepository.save(adminDetalles);
    }

    @Transactional
    public void registrarActividad(Long usuarioId, String actividad) {
        log.info("📝 Registrando actividad para administrador ID: {}", usuarioId);

        AdminDetalles adminDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        adminDetalles.registrarActividad(actividad);
        adminDetallesRepository.save(adminDetalles);
    }

    public boolean esAdministradorActivo(Long usuarioId) {
        try {
            AdminDetalles adminDetalles = obtenerDetallesPorUsuarioId(usuarioId);
            return adminDetalles.esAdministradorActivo();
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public boolean tieneNivelAccesoTotal(Long usuarioId) {
        try {
            AdminDetalles adminDetalles = obtenerDetallesPorUsuarioId(usuarioId);
            return adminDetalles.tieneNivelAccesoTotal();
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public List<AdminDetalles> obtenerAdministradoresConSesionesActivas() {
        log.info("📊 Obteniendo administradores con sesiones activas");
        return adminDetallesRepository.findConSesionesActivas();
    }

    public List<AdminDetalles> obtenerAdministradoresActivosRecientes(LocalDateTime desde) {
        log.info("⏰ Obteniendo administradores activos desde: {}", desde);
        return adminDetallesRepository.findAdministradoresActivosDesde(desde);
    }

    public long contarAdministradoresActivos() {
        return adminDetallesRepository.countAdministradoresActivos();
    }

    public long contarAdministradoresConSesionesActivas() {
        return adminDetallesRepository.countConSesionesActivas();
    }

    @Transactional
    public void crearAdministradorCompleto(Usuario usuario, String region, String nivelAcceso) {
        log.info("🏗️ Creando administrador completo para usuario: {}", usuario.getEmail());

        if (!RoleConstants.ROLE_ADMINISTRADOR.equals(usuario.getRol().getNombre())) {
            throw new BusinessException("Solo se pueden crear detalles para usuarios con rol ADMINISTRADOR");
        }

        AdminDetalles adminDetalles = new AdminDetalles(usuario, region, nivelAcceso);
        adminDetallesRepository.save(adminDetalles);

        log.info("✅ Administrador completo creado exitosamente");
    }
}
