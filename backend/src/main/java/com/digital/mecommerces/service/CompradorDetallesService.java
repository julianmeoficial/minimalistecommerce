package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.CompradorDetalles;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.CompradorDetallesRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class CompradorDetallesService {
    private final CompradorDetallesRepository compradorDetallesRepository;
    private final UsuarioRepository usuarioRepository;

    public CompradorDetallesService(
            CompradorDetallesRepository compradorDetallesRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.compradorDetallesRepository = compradorDetallesRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public CompradorDetalles obtenerDetallesPorUsuarioId(Long usuarioId) {
        return compradorDetallesRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Detalles de comprador no encontrados para el usuario con id: " + usuarioId));
    }

    public CompradorDetalles crearOActualizarDetalles(Long usuarioId, CompradorDetalles detalles) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        detalles.setUsuario(usuario);
        return compradorDetallesRepository.save(detalles);
    }

    public void eliminarDetalles(Long usuarioId) {
        if (!compradorDetallesRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Detalles de comprador no encontrados para el usuario con id: " + usuarioId);
        }
        compradorDetallesRepository.deleteById(usuarioId);
    }
}
