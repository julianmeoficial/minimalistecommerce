package com.digital.mecommerces.service;

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
        log.info("Obteniendo todos los roles-permisos");
        return rolPermisoRepository.findAll();
    }

    public List<RolPermiso> obtenerPermisosPorRol(Long rolId) {
        log.info("Obteniendo permisos para rol ID: {}", rolId);
        return rolPermisoRepository.findByRolId(rolId);
    }

    public List<Permiso> obtenerPermisosDeRol(Long rolId) {
        log.info("Obteniendo lista de permisos para rol ID: {}", rolId);
        return rolPermisoRepository.findByRolId(rolId)
                .stream()
                .map(RolPermiso::getPermiso)
                .collect(Collectors.toList());
    }

    @Transactional
    public RolPermiso asignarPermisoARol(Long rolId, Long permisoId) {
        log.info("Asignando permiso ID {} a rol ID {}", permisoId, rolId);

        // Verificar que el rol existe
        RolUsuario rol = rolUsuarioRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + rolId));

        // Verificar que el permiso existe
        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id: " + permisoId));

        // Verificar si la relación ya existe
        if (rolPermisoRepository.existsByRolIdAndPermisoId(rolId, permisoId)) {
            log.warn("La relación rol-permiso ya existe: rolId={}, permisoId={}", rolId, permisoId);
            throw new IllegalArgumentException("El permiso ya está asignado a este rol");
        }

        // Crear nueva relación
        RolPermiso rolPermiso = new RolPermiso(rolId, permisoId);
        rolPermiso.setRol(rol);
        rolPermiso.setPermiso(permiso);

        RolPermiso saved = rolPermisoRepository.save(rolPermiso);
        log.info("Permiso asignado exitosamente: rolId={}, permisoId={}", rolId, permisoId);
        return saved;
    }

    @Transactional
    public void eliminarPermisoDeRol(Long rolId, Long permisoId) {
        log.info("Eliminando permiso ID {} del rol ID {}", permisoId, rolId);

        // Verificar que el rol existe
        if (!rolUsuarioRepository.existsById(rolId)) {
            throw new ResourceNotFoundException("Rol no encontrado con id: " + rolId);
        }

        // Verificar que el permiso existe
        if (!permisoRepository.existsById(permisoId)) {
            throw new ResourceNotFoundException("Permiso no encontrado con id: " + permisoId);
        }

        // Verificar que la relación existe
        if (!rolPermisoRepository.existsByRolIdAndPermisoId(rolId, permisoId)) {
            throw new ResourceNotFoundException("No existe relación entre el rol y el permiso especificados");
        }

        // Eliminar la relación específica
        rolPermisoRepository.deleteByRolIdAndPermisoId(rolId, permisoId);
        log.info("Permiso eliminado exitosamente del rol: rolId={}, permisoId={}", rolId, permisoId);
    }

    @Transactional
    public void eliminarTodosPermisosDeRol(Long rolId) {
        log.info("Eliminando todos los permisos del rol ID: {}", rolId);

        // Verificar que el rol existe
        if (!rolUsuarioRepository.existsById(rolId)) {
            throw new ResourceNotFoundException("Rol no encontrado con id: " + rolId);
        }

        // Contar permisos antes de eliminar
        long cantidadPermisos = rolPermisoRepository.countByRolId(rolId);

        // Eliminar todos los permisos del rol
        rolPermisoRepository.deleteByRolId(rolId);
        log.info("Eliminados {} permisos del rol ID: {}", cantidadPermisos, rolId);
    }

    public boolean tienePermiso(Long rolId, String codigoPermiso) {
        log.debug("Verificando si rol ID {} tiene permiso: {}", rolId, codigoPermiso);

        List<RolPermiso> permisos = rolPermisoRepository.findByRolId(rolId);
        boolean tienePermiso = permisos.stream()
                .anyMatch(rp -> rp.getPermiso().getCodigo().equals(codigoPermiso));

        log.debug("Rol ID {}: {} permiso {}", rolId, tienePermiso ? "tiene" : "no tiene", codigoPermiso);
        return tienePermiso;
    }

    public List<String> obtenerCodigosPermisosDeRol(Long rolId) {
        log.info("Obteniendo códigos de permisos para rol ID: {}", rolId);
        return rolPermisoRepository.findByRolId(rolId).stream()
                .map(rp -> rp.getPermiso().getCodigo())
                .collect(Collectors.toList());
    }

    @Transactional
    public void asignarMultiplesPermisosARol(Long rolId, List<Long> permisosIds) {
        log.info("Asignando {} permisos al rol ID: {}", permisosIds.size(), rolId);

        // Verificar que el rol existe
        RolUsuario rol = rolUsuarioRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + rolId));

        int asignados = 0;
        for (Long permisoId : permisosIds) {
            try {
                if (!rolPermisoRepository.existsByRolIdAndPermisoId(rolId, permisoId)) {
                    Permiso permiso = permisoRepository.findById(permisoId)
                            .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id: " + permisoId));

                    RolPermiso rolPermiso = new RolPermiso(rolId, permisoId);
                    rolPermiso.setRol(rol);
                    rolPermiso.setPermiso(permiso);
                    rolPermisoRepository.save(rolPermiso);
                    asignados++;
                }
            } catch (Exception e) {
                log.warn("Error asignando permiso ID {} al rol ID {}: {}", permisoId, rolId, e.getMessage());
            }
        }

        log.info("Asignados {} de {} permisos al rol ID: {}", asignados, permisosIds.size(), rolId);
    }
}
