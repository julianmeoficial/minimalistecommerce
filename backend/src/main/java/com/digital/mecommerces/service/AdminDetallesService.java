package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.AdminDetalles;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.AdminDetallesRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminDetallesService {
    private final AdminDetallesRepository adminDetallesRepository;
    private final UsuarioRepository usuarioRepository;

    public AdminDetallesService(
            AdminDetallesRepository adminDetallesRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.adminDetallesRepository = adminDetallesRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public AdminDetalles obtenerDetallesPorUsuarioId(Long usuarioId) {
        return adminDetallesRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Detalles de administrador no encontrados para el usuario con id: " + usuarioId));
    }

    public AdminDetalles crearOActualizarDetalles(Long usuarioId, AdminDetalles detalles) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        detalles.setUsuario(usuario);
        return adminDetallesRepository.save(detalles);
    }

    public void eliminarDetalles(Long usuarioId) {
        if (!adminDetallesRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Detalles de administrador no encontrados para el usuario con id: " + usuarioId);
        }
        adminDetallesRepository.deleteById(usuarioId);
    }
}
