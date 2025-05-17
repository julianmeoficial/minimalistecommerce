package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.RolPermiso;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.model.Permiso;
import com.digital.mecommerces.repository.RolPermisoRepository;
import com.digital.mecommerces.repository.RolUsuarioRepository;
import com.digital.mecommerces.repository.PermisoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolPermisoService {
    private final RolPermisoRepository rolPermisoRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PermisoRepository permisoRepository;

    public RolPermisoService(
            RolPermisoRepository rolPermisoRepository,
            RolUsuarioRepository rolUsuarioRepository,
            PermisoRepository permisoRepository
    ) {
        this.rolPermisoRepository = rolPermisoRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.permisoRepository = permisoRepository;
    }

    public List<RolPermiso> obtenerTodosRolPermisos() {
        return rolPermisoRepository.findAll();
    }

    public List<RolPermiso> obtenerPermisosPorRol(Long rolId) {
        return rolPermisoRepository.findByRolRolId(rolId);
    }

    public List<Permiso> obtenerPermisosDeRol(Long rolId) {
        return rolPermisoRepository.findByRolRolId(rolId).stream()
                .map(RolPermiso::getPermiso)
                .collect(Collectors.toList());
    }

    @Transactional
    public RolPermiso asignarPermisoARol(Long rolId, Long permisoId) {
        RolUsuario rol = rolUsuarioRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + rolId));

        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id: " + permisoId));

        RolPermiso rolPermiso = new RolPermiso(rol, permiso);
        return rolPermisoRepository.save(rolPermiso);
    }

    @Transactional
    public void eliminarPermisoDeRol(Long rolId, Long permisoId) {
        RolUsuario rol = rolUsuarioRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + rolId));

        Permiso permiso = permisoRepository.findById(permisoId)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id: " + permisoId));

        List<RolPermiso> relaciones = rolPermisoRepository.findAll().stream()
                .filter(rp -> rp.getRol().getRolId().equals(rolId) && rp.getPermiso().getPermisoId().equals(permisoId))
                .collect(Collectors.toList());

        rolPermisoRepository.deleteAll(relaciones);
    }

    @Transactional
    public void eliminarTodosPermisosDeRol(Long rolId) {
        if (!rolUsuarioRepository.existsById(rolId)) {
            throw new ResourceNotFoundException("Rol no encontrado con id: " + rolId);
        }

        rolPermisoRepository.deleteByRolRolId(rolId);
    }
}

