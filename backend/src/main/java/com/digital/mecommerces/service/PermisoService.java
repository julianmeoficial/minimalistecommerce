package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.Permiso;
import com.digital.mecommerces.repository.PermisoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PermisoService {
    private final PermisoRepository permisoRepository;

    public PermisoService(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    public List<Permiso> obtenerPermisos() {
        return permisoRepository.findAll();
    }

    public Permiso obtenerPermisoPorId(Long id) {
        return permisoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id: " + id));
    }

    public Permiso obtenerPermisoPorCodigo(String codigo) {
        return permisoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con código: " + codigo));
    }

    public Permiso crearPermiso(Permiso permiso) {
        return permisoRepository.save(permiso);
    }

    public Permiso actualizarPermiso(Long id, Permiso permisoDetails) {
        Permiso permiso = obtenerPermisoPorId(id);
        permiso.setCodigo(permisoDetails.getCodigo());
        permiso.setDescripcion(permisoDetails.getDescripcion());
        return permisoRepository.save(permiso);
    }

    public void eliminarPermiso(Long id) {
        Permiso permiso = obtenerPermisoPorId(id);
        permisoRepository.delete(permiso);
    }
}
