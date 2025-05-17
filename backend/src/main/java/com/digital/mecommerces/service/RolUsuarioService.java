package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.RolUsuario;
import com.digital.mecommerces.repository.RolUsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RolUsuarioService {
    private final RolUsuarioRepository rolUsuarioRepository;

    public RolUsuarioService(RolUsuarioRepository rolUsuarioRepository) {
        this.rolUsuarioRepository = rolUsuarioRepository;
    }

    public List<RolUsuario> obtenerRoles() {
        return rolUsuarioRepository.findAll();
    }

    public RolUsuario obtenerRolPorId(Long id) {
        return rolUsuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
    }

    public RolUsuario obtenerRolPorNombre(String nombre) {
        return rolUsuarioRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con nombre: " + nombre));
    }

    public RolUsuario crearRol(RolUsuario rol) {
        return rolUsuarioRepository.save(rol);
    }

    public RolUsuario actualizarRol(Long id, RolUsuario rolDetails) {
        RolUsuario rol = obtenerRolPorId(id);
        rol.setNombre(rolDetails.getNombre());
        rol.setDescripcion(rolDetails.getDescripcion());
        return rolUsuarioRepository.save(rol);
    }

    public void eliminarRol(Long id) {
        RolUsuario rol = obtenerRolPorId(id);
        rolUsuarioRepository.delete(rol);
    }
}
