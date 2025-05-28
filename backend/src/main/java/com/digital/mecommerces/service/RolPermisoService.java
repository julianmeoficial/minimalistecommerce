package com.digital.mecommerces.service;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.exception.BusinessException;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.Permiso;
import com.digital.mecommerces.model.RolPermiso;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.repository.PermisoRepository;
import com.digital.mecommerces.repository.RolPermisoRepository;
import com.digital.mecommerces.repository.RolUsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RolPermisoService {

    private final RolPermisoRepository rolPermisoRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PermisoRepository permisoRepository;

    public RolPermisoService(RolPermisoRepository rolPermisoRepository,
                             RolUsuarioRepository rolUsuarioRepository,
                             PermisoRepository permisoRepository) {
        this.rolPermisoRepository = rolPermisoRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.permisoRepository = permisoRepository;
    }

    public List<RolPermiso> obtenerTodosRolPermisos() {
        log.info("📋 Obteniendo todos los rol-permisos del sistema");
        return rolPermisoRepository.findAll();
    }

    public List<RolPermiso> obtenerPermisosPorRol(Long rolId) {
        log.info("👥 Obteniendo permisos para rol ID: {}", rolId);
        return rolPermisoRepository.findByRolIdOrderByPermisoNivel(rolId);
    }

    public List<Permiso> obtenerPermisosDeRol(Long rolId) {
        log.info("📝 Obteniendo lista de permisos para rol ID: {}", rolId);
        return rolPermisoRepository.findByRolIdOrderByPermisoNivel(rolId).stream()
                .map(RolPermiso::getPermiso)
                .collect(Collectors.toList());
    }

    public List<String> obtenerCodigosPermisosDeRol(Long rolId) {
        log.info("🔑 Obteniendo códigos de permisos para rol ID: {}", rolId);
        return rolPermisoRepository.findByRolIdOrderByPermisoNivel(rolId).stream()
                .map(rp -> rp.getPermiso().getCodigo())
                .collect(Collectors.toList());
    }

    @Transactional
    public RolPermiso asignarPermisoARol(Long rolId, Long permisoId) {
        log.info("➕ Asignando permiso ID: {} a rol ID: {}", permisoId, rolId);

        // Verificar que el rol existe
        RolUsuario rol = rolUsuarioRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        // Verificar que el permiso existe
        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + permisoId));

        // Verificar si la relación ya existe
        if (rolPermisoRepository.existsByRolIdAndPermisoId(rolId, permisoId)) {
            log.warn("⚠️ La relación rol-permiso ya existe: rolId={}, permisoId={}", rolId, permisoId);
            throw new BusinessException("El permiso ya está asignado al rol");
        }

        // Validar compatibilidad entre rol y permiso
        validarCompatibilidadRolPermiso(rol, permiso);

        // Crear nueva relación
        RolPermiso rolPermiso = new RolPermiso(rol, permiso, "SYSTEM");
        RolPermiso saved = rolPermisoRepository.save(rolPermiso);

        log.info("✅ Permiso asignado exitosamente: {} -> {}", rol.getNombre(), permiso.getCodigo());
        return saved;
    }

    @Transactional
    public void eliminarPermisoDeRol(Long rolId, Long permisoId) {
        log.info("➖ Eliminando permiso ID: {} del rol ID: {}", permisoId, rolId);

        // Verificar que el rol existe
        if (!rolUsuarioRepository.existsById(rolId)) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + rolId);
        }

        // Verificar que el permiso existe
        if (!permisoRepository.existsById(permisoId)) {
            throw new ResourceNotFoundException("Permiso no encontrado con ID: " + permisoId);
        }

        // Verificar que la relación existe
        if (!rolPermisoRepository.existsByRolIdAndPermisoId(rolId, permisoId)) {
            throw new ResourceNotFoundException("No existe relación entre el rol y el permiso especificados");
        }

        // Validar que no se elimine un permiso crítico
        RolUsuario rol = rolUsuarioRepository.findById(rolId).get();
        Permiso permiso = permisoRepository.findById(permisoId).get();

        validarEliminacionPermisoSegura(rol, permiso);

        // Eliminar la relación específica
        rolPermisoRepository.deleteByRolIdAndPermisoId(rolId, permisoId);
        log.info("✅ Permiso eliminado exitosamente del rol: {} <- {}", rol.getNombre(), permiso.getCodigo());
    }

    @Transactional
    public void eliminarTodosPermisosDeRol(Long rolId) {
        log.info("🗑️ Eliminando todos los permisos del rol ID: {}", rolId);

        // Verificar que el rol existe
        RolUsuario rol = rolUsuarioRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        // Verificar que no sea un rol crítico del sistema
        if (esRolCriticoDelSistema(rol.getNombre())) {
            throw new BusinessException("No se pueden eliminar todos los permisos de un rol crítico del sistema: " + rol.getNombre());
        }

        // Contar permisos antes de eliminar
        long cantidadPermisos = rolPermisoRepository.countByRolId(rolId);

        // Eliminar todos los permisos del rol
        rolPermisoRepository.deleteByRolId(rolId);
        log.info("✅ {} permisos eliminados del rol: {}", cantidadPermisos, rol.getNombre());
    }

    @Transactional
    public void asignarMultiplesPermisosARol(Long rolId, List<Long> permisosIds) {
        log.info("📋 Asignando {} permisos al rol ID: {}", permisosIds.size(), rolId);

        // Verificar que el rol existe
        RolUsuario rol = rolUsuarioRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + rolId));

        int asignados = 0;
        int omitidos = 0;

        for (Long permisoId : permisosIds) {
            try {
                // Verificar si ya existe la relación
                if (!rolPermisoRepository.existsByRolIdAndPermisoId(rolId, permisoId)) {
                    Permiso permiso = permisoRepository.findById(permisoId)
                            .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + permisoId));

                    // Validar compatibilidad
                    if (esPermisoCompatibleConRol(rol, permiso)) {
                        RolPermiso rolPermiso = new RolPermiso(rol, permiso, "SYSTEM");
                        rolPermisoRepository.save(rolPermiso);
                        asignados++;
                    } else {
                        log.warn("⚠️ Permiso {} no es compatible con rol {}", permiso.getCodigo(), rol.getNombre());
                        omitidos++;
                    }
                } else {
                    omitidos++;
                }
            } catch (Exception e) {
                log.warn("⚠️ Error asignando permiso ID: {} al rol ID: {} - {}", permisoId, rolId, e.getMessage());
                omitidos++;
            }
        }

        log.info("✅ Asignados: {} permisos, Omitidos: {} al rol: {}", asignados, omitidos, rol.getNombre());
    }

    public boolean tienePermiso(Long rolId, String codigoPermiso) {
        log.debug("🔍 Verificando si rol ID: {} tiene permiso: {}", rolId, codigoPermiso);
        return rolPermisoRepository.rolTienePermiso(
                rolUsuarioRepository.findById(rolId).map(RolUsuario::getNombre).orElse(""),
                codigoPermiso
        );
    }

    public boolean rolTienePermisoDelSistema(String rolNombre, String codigoPermiso) {
        return rolPermisoRepository.rolTienePermiso(rolNombre, codigoPermiso);
    }

    // Métodos específicos para roles del sistema optimizado
    public List<RolPermiso> obtenerPermisosAdministrador() {
        log.info("👑 Obteniendo permisos del administrador");
        return rolPermisoRepository.findPermisosAdministrador();
    }

    public List<RolPermiso> obtenerPermisosVendedor() {
        log.info("🏪 Obteniendo permisos del vendedor");
        return rolPermisoRepository.findPermisosVendedor();
    }

    public List<RolPermiso> obtenerPermisosComprador() {
        log.info("🛒 Obteniendo permisos del comprador");
        return rolPermisoRepository.findPermisosComprador();
    }

    @Transactional
    public void configurarPermisosBasicosParaRol(String rolNombre) {
        log.info("⚙️ Configurando permisos básicos para rol: {}", rolNombre);

        RolUsuario rol = rolUsuarioRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + rolNombre));

        List<String> permisosBasicos = obtenerPermisosBasicosPorRol(rolNombre);

        for (String codigoPermiso : permisosBasicos) {
            try {
                Permiso permiso = permisoRepository.findByCodigo(codigoPermiso)
                        .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado: " + codigoPermiso));

                if (!rolPermisoRepository.existsByRolIdAndPermisoId(rol.getRolId(), permiso.getPermisoId())) {
                    RolPermiso rolPermiso = new RolPermiso(rol, permiso, "SYSTEM");
                    rolPermisoRepository.save(rolPermiso);
                    log.info("✅ Permiso básico asignado: {} -> {}", rolNombre, codigoPermiso);
                }
            } catch (Exception e) {
                log.warn("⚠️ Error asignando permiso básico {} a {}: {}", codigoPermiso, rolNombre, e.getMessage());
            }
        }
    }

    @Transactional
    public void inicializarPermisosDelSistema() {
        log.info("🏗️ Inicializando permisos del sistema para todos los roles");

        configurarPermisosBasicosParaRol(RoleConstants.ROLE_ADMINISTRADOR);
        configurarPermisosBasicosParaRol(RoleConstants.ROLE_VENDEDOR);
        configurarPermisosBasicosParaRol(RoleConstants.ROLE_COMPRADOR);

        log.info("✅ Inicialización de permisos del sistema completada");
    }

    // Métodos de estadísticas y análisis
    public List<Object[]> obtenerEstadisticasAsignacionesPorRol() {
        return rolPermisoRepository.countAsignacionesPorRol();
    }

    public List<Object[]> obtenerEstadisticasAsignacionesPorPermiso() {
        return rolPermisoRepository.countAsignacionesPorPermiso();
    }

    public List<RolUsuario> obtenerRolesConPermiso(String codigoPermiso) {
        return rolPermisoRepository.findRolesConPermiso(codigoPermiso);
    }

    public List<Permiso> obtenerPermisosDeRolPorNombre(String rolNombre) {
        return rolPermisoRepository.findPermisosDeRol(rolNombre);
    }

    public long contarAsignaciones() {
        return rolPermisoRepository.countTotalAsignaciones();
    }

    // Métodos privados de validación y utilidad

    private void validarCompatibilidadRolPermiso(RolUsuario rol, Permiso permiso) {
        if (!esPermisoCompatibleConRol(rol, permiso)) {
            throw new BusinessException("El permiso " + permiso.getCodigo() +
                    " no es compatible con el rol " + rol.getNombre());
        }
    }

    private boolean esPermisoCompatibleConRol(RolUsuario rol, Permiso permiso) {
        String rolNombre = rol.getNombre();
        String permisoCodigo = permiso.getCodigo();

        return switch (rolNombre) {
            case "ADMINISTRADOR" -> true; // Admin puede tener cualquier permiso
            case "VENDEDOR" -> RoleConstants.PERM_VENDER_PRODUCTOS.equals(permisoCodigo) ||
                    RoleConstants.PERM_GESTIONAR_CATEGORIAS.equals(permisoCodigo);
            case "COMPRADOR" -> RoleConstants.PERM_COMPRAR_PRODUCTOS.equals(permisoCodigo);
            default -> true; // Roles personalizados pueden tener cualquier permiso
        };
    }

    private void validarEliminacionPermisoSegura(RolUsuario rol, Permiso permiso) {
        // No permitir eliminar permisos críticos de roles del sistema
        if (esRolCriticoDelSistema(rol.getNombre()) && esPermisoCritico(permiso.getCodigo())) {
            throw new BusinessException("No se puede eliminar el permiso crítico " + permiso.getCodigo() +
                    " del rol del sistema " + rol.getNombre());
        }
    }

    private boolean esRolCriticoDelSistema(String rolNombre) {
        return RoleConstants.ROLE_ADMINISTRADOR.equals(rolNombre) ||
                RoleConstants.ROLE_VENDEDOR.equals(rolNombre) ||
                RoleConstants.ROLE_COMPRADOR.equals(rolNombre);
    }

    private boolean esPermisoCritico(String codigoPermiso) {
        return RoleConstants.PERM_ADMIN_TOTAL.equals(codigoPermiso) ||
                RoleConstants.PERM_VENDER_PRODUCTOS.equals(codigoPermiso) ||
                RoleConstants.PERM_COMPRAR_PRODUCTOS.equals(codigoPermiso);
    }

    private List<String> obtenerPermisosBasicosPorRol(String rolNombre) {
        return switch (rolNombre) {
            case "ADMINISTRADOR" -> List.of(
                    RoleConstants.PERM_ADMIN_TOTAL,
                    RoleConstants.PERM_GESTIONAR_USUARIOS,
                    RoleConstants.PERM_GESTIONAR_CATEGORIAS,
                    RoleConstants.PERM_VENDER_PRODUCTOS,
                    RoleConstants.PERM_COMPRAR_PRODUCTOS
            );
            case "VENDEDOR" -> List.of(
                    RoleConstants.PERM_VENDER_PRODUCTOS,
                    RoleConstants.PERM_GESTIONAR_CATEGORIAS
            );
            case "COMPRADOR" -> List.of(
                    RoleConstants.PERM_COMPRAR_PRODUCTOS
            );
            default -> List.of();
        };
    }
}
