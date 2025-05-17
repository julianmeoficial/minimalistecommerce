package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.VendedorDetalles;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.VendedorDetallesRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class VendedorDetallesService {
    private final VendedorDetallesRepository vendedorDetallesRepository;
    private final UsuarioRepository usuarioRepository;

    public VendedorDetallesService(
            VendedorDetallesRepository vendedorDetallesRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.vendedorDetallesRepository = vendedorDetallesRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public VendedorDetalles obtenerDetallesPorUsuarioId(Long usuarioId) {
        return vendedorDetallesRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Detalles de vendedor no encontrados para el usuario con id: " + usuarioId));
    }

    public VendedorDetalles crearOActualizarDetalles(Long usuarioId, VendedorDetalles detalles) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        detalles.setUsuario(usuario);
        return vendedorDetallesRepository.save(detalles);
    }

    public void eliminarDetalles(Long usuarioId) {
        if (!vendedorDetallesRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Detalles de vendedor no encontrados para el usuario con id: " + usuarioId);
        }
        vendedorDetallesRepository.deleteById(usuarioId);
    }
}
