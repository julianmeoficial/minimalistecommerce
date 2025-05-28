package com.digital.mecommerces.service;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.enums.TipoUsuario;
import com.digital.mecommerces.exception.BusinessException;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.repository.RolUsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class RolUsuarioService {

    private final RolUsuarioRepository rolUsuarioRepository;

    public RolUsuarioService(RolUsuarioRepository rolUsuarioRepository) {
        this.rolUsuarioRepository = rolUsuarioRepository;
    }

    @Cacheable("roles")
    public List<RolUsuario> obtenerRoles() {
        log.info("📋 Obteniendo todos los roles del sistema");
        return rolUsuarioRepository.findAllOrderByNombre();
    }

    @Cacheable("rolesDelSistema")
    public List<RolUsuario> obtenerRolesDelSistema() {
        log.info("⚙️ Obteniendo roles del sistema optimizado");
        return rolUsuarioRepository.findRolesDelSistema();
    }

    public List<RolUsuario> obtenerRolesPersonalizados() {
        log.info("🎨 Obteniendo roles personalizados");
        return rolUsuarioRepository.findRolesPersonalizados();
    }

    @Cacheable(value = "rol", key = "#id")
    public RolUsuario obtenerRolPorId(Long id) {
        log.info("🔍 Obteniendo rol por ID: {}", id);
        return rolUsuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
    }

    public RolUsuario obtenerRolPorNombre(String nombre) {
        log.info("📝 Obteniendo rol por nombre: {}", nombre);
        return rolUsuarioRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con nombre: " + nombre));
    }

    // Métodos específicos para roles del sistema optimizado
    public RolUsuario obtenerRolAdministrador() {
        log.info("👑 Obteniendo rol ADMINISTRADOR");
        return rolUsuarioRepository.findRolAdministrador()
                .orElseThrow(() -> new ResourceNotFoundException("Rol ADMINISTRADOR no encontrado"));
    }

    public RolUsuario obtenerRolVendedor() {
        log.info("🏪 Obteniendo rol VENDEDOR");
        return rolUsuarioRepository.findRolVendedor()
                .orElseThrow(() -> new ResourceNotFoundException("Rol VENDEDOR no encontrado"));
    }

    public RolUsuario obtenerRolComprador() {
        log.info("🛒 Obteniendo rol COMPRADOR");
        return rolUsuarioRepository.findRolComprador()
                .orElseThrow(() -> new ResourceNotFoundException("Rol COMPRADOR no encontrado"));
    }

    @Transactional
    @CacheEvict(value = {"roles", "rolesDelSistema", "rol"}, allEntries = true)
    public RolUsuario crearRol(RolUsuario rol) {
        log.info("➕ Creando nuevo rol: {}", rol.getNombre());

        // Validar que no existe un rol con el mismo nombre
        if (rolUsuarioRepository.existsByNombre(rol.getNombre())) {
            throw new BusinessException("Ya existe un rol con el nombre: " + rol.getNombre());
        }

        // Verificar si es un rol del sistema usando enum
        try {
            TipoUsuario tipo = TipoUsuario.fromCodigo(rol.getNombre().toUpperCase());
            log.warn("⚠️ Intentando crear rol del sistema manualmente: {} - {}",
                    tipo.getCodigo(), tipo.getDescripcion());
            throw new BusinessException("No se puede crear manualmente un rol del sistema. Use la inicialización automática.");
        } catch (IllegalArgumentException e) {
            log.info("📝 Creando rol personalizado: {}", rol.getNombre());
        }

        // Asignar descripción por defecto si no se proporciona
        if (rol.getDescripcion() == null || rol.getDescripcion().isEmpty()) {
            rol.setDescripcion("Rol personalizado: " + rol.getNombre());
        }

        RolUsuario nuevoRol = rolUsuarioRepository.save(rol);
        log.info("✅ Rol creado exitosamente: {} con ID: {}", nuevoRol.getNombre(), nuevoRol.getRolId());

        return nuevoRol;
    }

    @Transactional
    @CacheEvict(value = {"roles", "rolesDelSistema", "rol"}, allEntries = true)
    public RolUsuario actualizarRol(Long id, RolUsuario rolDetails) {
        log.info("✏️ Actualizando rol ID: {}", id);

        RolUsuario rol = obtenerRolPorId(id);

        // Verificar que no sea un rol crítico del sistema
        if (rol.esRolDelSistema()) {
            throw new BusinessException("No se pueden modificar roles críticos del sistema: " + rol.getNombre());
        }

        // Verificar nombre único si se está cambiando
        if (rolDetails.getNombre() != null && !rol.getNombre().equals(rolDetails.getNombre())) {
            if (rolUsuarioRepository.existsByNombreAndRolIdNot(rolDetails.getNombre(), id)) {
                throw new BusinessException("Ya existe un rol con el nombre: " + rolDetails.getNombre());
            }
            rol.setNombre(rolDetails.getNombre());
        }

        // Actualizar descripción
        if (rolDetails.getDescripcion() != null) {
            rol.setDescripcion(rolDetails.getDescripcion());
        }

        RolUsuario rolActualizado = rolUsuarioRepository.save(rol);
        log.info("✅ Rol actualizado exitosamente: {}", rolActualizado.getNombre());

        return rolActualizado;
    }

    @Transactional
    @CacheEvict(value = {"roles", "rolesDelSistema", "rol"}, allEntries = true)
    public void eliminarRol(Long id) {
        log.info("🗑️ Eliminando rol ID: {}", id);

        RolUsuario rol = obtenerRolPorId(id);

        // Verificar que no sea un rol crítico del sistema
        if (rol.esRolDelSistema()) {
            throw new BusinessException("No se pueden eliminar roles críticos del sistema: " + rol.getNombre());
        }

        // Verificar que no tenga usuarios asignados
        if (rol.tieneUsuarios()) {
            throw new BusinessException("No se puede eliminar el rol porque tiene " +
                    rol.getNumeroUsuarios() + " usuarios asignados");
        }

        // Verificar que no tenga permisos asignados críticos
        if (rol.tienePermisos()) {
            log.info("ℹ️ El rol {} tiene {} permisos asignados que también serán eliminados",
                    rol.getNombre(), rol.getNumeroPermisos());
        }

        rolUsuarioRepository.delete(rol);
        log.info("✅ Rol eliminado exitosamente: {}", rol.getNombre());
    }

    public boolean existeRolPorNombre(String nombre) {
        return rolUsuarioRepository.existsByNombre(nombre);
    }

    public boolean esRolDelSistema(String nombre) {
        return rolUsuarioRepository.esRolDelSistema(nombre);
    }

    public List<RolUsuario> obtenerRolesConUsuarios() {
        log.info("👥 Obteniendo roles que tienen usuarios asignados");
        return rolUsuarioRepository.findRolesConUsuarios();
    }

    public List<RolUsuario> obtenerRolesSinUsuarios() {
        log.info("📪 Obteniendo roles sin usuarios asignados");
        return rolUsuarioRepository.findRolesSinUsuarios();
    }

    public List<RolUsuario> obtenerRolesConPermisos() {
        log.info("🔑 Obteniendo roles que tienen permisos asignados");
        return rolUsuarioRepository.findRolesConPermisos();
    }

    public List<RolUsuario> obtenerRolesSinPermisos() {
        log.info("🔒 Obteniendo roles sin permisos asignados");
        return rolUsuarioRepository.findRolesSinPermisos();
    }

    public List<RolUsuario> obtenerRolesConPermiso(String codigoPermiso) {
        log.info("🔍 Obteniendo roles que tienen el permiso: {}", codigoPermiso);
        return rolUsuarioRepository.findRolesConPermiso(codigoPermiso);
    }

    @Transactional
    public void crearRolesDelSistema() {
        log.info("🏗️ Verificando y creando roles del sistema si no existen");

        for (TipoUsuario tipo : TipoUsuario.values()) {
            if (!rolUsuarioRepository.existsByNombre(tipo.getCodigo())) {
                RolUsuario rol = new RolUsuario(tipo.getCodigo(), tipo.getDescripcion());
                rolUsuarioRepository.save(rol);
                log.info("✅ Rol del sistema creado: {} - {}", tipo.getCodigo(), tipo.getDescripcion());
            } else {
                log.info("ℹ️ Rol del sistema ya existe: {}", tipo.getCodigo());
            }
        }
    }

    @Transactional
    public void verificarConfiguracionRolesDelSistema() {
        log.info("🔍 Verificando configuración de roles del sistema");

        // Verificar que existan los roles básicos
        boolean rolesCompletos = true;

        for (TipoUsuario tipo : TipoUsuario.values()) {
            if (!rolUsuarioRepository.existsByNombre(tipo.getCodigo())) {
                log.warn("⚠️ Falta rol del sistema: {}", tipo.getCodigo());
                rolesCompletos = false;
            }
        }

        if (!rolesCompletos) {
            log.info("🏗️ Creando roles faltantes del sistema");
            crearRolesDelSistema();
        }

        // Verificar configuración específica de cada rol
        verificarConfiguracionRolEspecifico(RoleConstants.ROLE_ADMINISTRADOR);
        verificarConfiguracionRolEspecifico(RoleConstants.ROLE_VENDEDOR);
        verificarConfiguracionRolEspecifico(RoleConstants.ROLE_COMPRADOR);

        log.info("✅ Verificación de configuración de roles completada");
    }

    private void verificarConfiguracionRolEspecifico(String rolNombre) {
        rolUsuarioRepository.findByNombre(rolNombre).ifPresentOrElse(
                rol -> log.info("✅ Rol {} configurado correctamente", rolNombre),
                () -> log.warn("⚠️ Rol {} no encontrado", rolNombre)
        );
    }

    // Métodos de estadísticas y análisis
    public List<Object[]> obtenerEstadisticasUsuariosPorRol() {
        return rolUsuarioRepository.countUsuariosPorRol();
    }

    public List<Object[]> obtenerEstadisticasPermisosPorRol() {
        return rolUsuarioRepository.countPermisosPorRol();
    }

    public List<RolUsuario> obtenerRolesMasUtilizados() {
        return rolUsuarioRepository.findRolesMasUtilizados();
    }

    public List<RolUsuario> obtenerRolesMenosUtilizados() {
        return rolUsuarioRepository.findRolesMenosUtilizados();
    }

    public List<Object[]> obtenerDistribucionUsuariosRolesSistema() {
        return rolUsuarioRepository.findDistribucionUsuariosRolesSistema();
    }

    public List<Object[]> obtenerResumenRoles() {
        return rolUsuarioRepository.findResumenRoles();
    }

    // Métodos de conteo
    public long contarRoles() {
        return rolUsuarioRepository.countTotalRoles();
    }

    public long contarRolesDelSistema() {
        return rolUsuarioRepository.countRolesDelSistema();
    }

    public long contarRolesPersonalizados() {
        return rolUsuarioRepository.countRolesPersonalizados();
    }

    // Validación de integridad del sistema
    public boolean validarIntegridadDelSistema() {
        log.info("🔍 Validando integridad del sistema de roles");

        boolean integridadCompleta = true;

        // Verificar que existan todos los roles del sistema
        long rolesDelSistema = rolUsuarioRepository.countRolesSistemaConfigurados();
        if (rolesDelSistema < TipoUsuario.values().length) {
            log.warn("⚠️ Faltan roles del sistema. Esperados: {}, Encontrados: {}",
                    TipoUsuario.values().length, rolesDelSistema);
            integridadCompleta = false;
        }

        // Verificar configuración específica
        boolean adminConfigurado = rolUsuarioRepository.findAdministradorConfigurado().isPresent();
        boolean vendedorConfigurado = rolUsuarioRepository.findVendedorConfigurado().isPresent();
        boolean compradorConfigurado = rolUsuarioRepository.findCompradorConfigurado().isPresent();

        if (!adminConfigurado) {
            log.warn("⚠️ Rol ADMINISTRADOR no tiene permisos configurados correctamente");
            integridadCompleta = false;
        }

        if (!vendedorConfigurado) {
            log.warn("⚠️ Rol VENDEDOR no tiene permisos configurados correctamente");
            integridadCompleta = false;
        }

        if (!compradorConfigurado) {
            log.warn("⚠️ Rol COMPRADOR no tiene permisos configurados correctamente");
            integridadCompleta = false;
        }

        if (integridadCompleta) {
            log.info("✅ Integridad del sistema de roles verificada correctamente");
        } else {
            log.warn("⚠️ Se encontraron problemas de integridad en el sistema de roles");
        }

        return integridadCompleta;
    }
}
