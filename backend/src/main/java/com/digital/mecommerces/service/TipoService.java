package com.digital.mecommerces.service;

import com.digital.mecommerces.model.Tipo;
import com.digital.mecommerces.repository.TipoRepository;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TipoService {

    private final TipoRepository tipoRepository;

    public TipoService(TipoRepository tipoRepository) {
        this.tipoRepository = tipoRepository;
    }

    // Obtener todos los tipos
    public List<Tipo> obtenerTipos() {
        return tipoRepository.findAll();
    }

    // Obtener un tipo por ID
    public Tipo obtenerTipoPorId(Long id) {
        return tipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado con id: " + id));
    }

    // Crear un nuevo tipo
    public Tipo crearTipo(Tipo tipo) {
        return tipoRepository.save(tipo);
    }

    // Actualizar un tipo existente
    public Tipo actualizarTipo(Long id, Tipo tipoDetails) {
        Tipo tipo = tipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado con id: " + id));

        tipo.setTipoNombre(tipoDetails.getTipoNombre());
        tipo.setTipoCategoria(tipoDetails.getTipoCategoria());

        return tipoRepository.save(tipo);
    }

    // Eliminar un tipo
    public void eliminarTipo(Long id) {
        Tipo tipo = tipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado con id: " + id));

        tipoRepository.delete(tipo);
    }
}
