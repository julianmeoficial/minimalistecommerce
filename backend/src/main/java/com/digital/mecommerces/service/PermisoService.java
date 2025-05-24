package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.Permiso;
import com.digital.mecommerces.repository.PermisoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PermisoService {

    private final PermisoRepository permisoRepository;

    public PermisoService(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    public List<Permiso> obtenerPermisos() {
        log.info("Obteniendo todos los permisos");
        return permisoRepository.findAllByOrderByNivelAsc();
    }

    public Permiso obtenerPermisoPorId(Long id) {
        log.info("Obteniendo permiso por ID: {}", id);
        return permisoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id: " + id));
    }

    public Optional<Permiso> obtenerPermisoPorCodigo(String codigo) {
        log.info("Obteniendo permiso por código: {}", codigo);
        return permisoRepository.findByCodigo(codigo);
    }

    public List<Permiso> obtenerPermisosPadre() {
        log.info("Obteniendo permisos padre (sin padre)");
        return permisoRepository.findByPermisoPadreIsNull();
    }

    public List<Permiso> obtenerPermisosHijos(Long permisopadreId) {
        log.info("Obteniendo permisos hijos del permiso: {}", permisopadreId);
        return permisoRepository.findByPermisopadreId(permisopadreId);
    }

    public List<Permiso> obtenerPermisosPorNivel(Integer nivel) {
        log.info("Obteniendo permisos por nivel: {}", nivel);
        return permisoRepository.findByNivel(nivel);
    }

    @Transactional
    public Permiso crearPermiso(Permiso permiso) {
        log.info("Creando nuevo permiso: {}", permiso.getCodigo());

        // Verificar que el código no exista
        if (permisoRepository.existsByCodigo(permiso.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un permiso con el código: " + permiso.getCodigo());
        }

        // Establecer nivel por defecto si no se proporciona
        if (permiso.getNivel() == null) {
            permiso.setNivel(0);
        }

        Permiso nuevoPermiso = permisoRepository.save(permiso);
        log.info("Permiso creado exitosamente con ID: {}", nuevoPermiso.getPermisoId());
        return nuevoPermiso;
    }

    @Transactional
    public Permiso actualizarPermiso(Long id, Permiso permisoActualizado) {
        log.info("Actualizando permiso con ID: {}", id);

        Permiso permiso = obtenerPermisoPorId(id);

        // Verificar que el código no esté en uso por otro permiso
        if (!permiso.getCodigo().equals(permisoActualizado.getCodigo()) &&
                permisoRepository.existsByCodigo(permisoActualizado.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un permiso con el código: " + permisoActualizado.getCodigo());
        }

        // Actualizar campos
        permiso.setCodigo(permisoActualizado.getCodigo());
        permiso.setDescripcion(permisoActualizado.getDescripcion());
        permiso.setNivel(permisoActualizado.getNivel());

        // Actualizar permiso padre si se proporciona
        if (permisoActualizado.getPermisoPadre() != null) {
            Permiso padre = obtenerPermisoPorId(permisoActualizado.getPermisoPadre().getPermisoId());
            permiso.setPermisoPadre(padre);
        }

        Permiso actualizado = permisoRepository.save(permiso);
        log.info("Permiso actualizado exitosamente: {}", id);
        return actualizado;
    }

    @Transactional
    public void eliminarPermiso(Long id) {
        log.info("Eliminando permiso con ID: {}", id);

        Permiso permiso = obtenerPermisoPorId(id);

        // Verificar que no tenga permisos hijos
        if (!permiso.getPermisosHijos().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el permiso porque tiene permisos hijos asociados");
        }

        permisoRepository.deleteById(id);
        log.info("Permiso eliminado exitosamente: {}", id);
    }

    public List<Permiso> buscarPermisos(String termino) {
        log.info("Buscando permisos con término: {}", termino);
        return permisoRepository.findByCodigoContainingIgnoreCase(termino);
    }

    public List<Permiso> obtenerJerarquiaCompleta() {
        log.info("Obteniendo jerarquía completa de permisos");
        return permisoRepository.findAllWithHijos();
    }

    public List<Permiso> obtenerPermisosHoja() {
        log.info("Obteniendo permisos hoja (sin hijos)");
        return permisoRepository.findPermisosHoja();
    }

    public boolean existePermiso(String codigo) {
        return permisoRepository.existsByCodigo(codigo);
    }

    public long contarPermisos() {
        return permisoRepository.count();
    }

    public long contarPermisosPorNivel(Integer nivel) {
        return permisoRepository.countByNivel(nivel);
    }

    @Transactional
    public Permiso crearPermisoConPadre(String codigo, String descripcion, Integer nivel, Long permisopadreId) {
        log.info("Creando permiso {} con padre ID: {}", codigo, permisopadreId);

        Permiso padre = null;
        if (permisopadreId != null) {
            padre = obtenerPermisoPorId(permisopadreId);
        }

        Permiso permiso = new Permiso(codigo, descripcion, nivel, padre);
        return crearPermiso(permiso);
    }

    public List<Permiso> obtenerPermisosPorCodigos(List<String> codigos) {
        log.info("Obteniendo permisos por códigos: {}", codigos);
        return permisoRepository.findByCodigoIn(codigos);
    }
    public List<Permiso> obtenerRutaPermiso(Long permisoId) {
        log.info("Obteniendo ruta del permiso: {}", permisoId);

        List<Permiso> ruta = new ArrayList<>();
        Permiso permisoActual = obtenerPermisoPorId(permisoId);

        // Construir la ruta hacia arriba hasta llegar a la raíz
        while (permisoActual != null) {
            ruta.add(0, permisoActual); // Agregar al inicio para mantener orden
            permisoActual = permisoActual.getPermisoPadre();
        }

        return ruta;
    }

    public List<Permiso> obtenerTodosDescendientes(Long permisoId) {
        log.info("Obteniendo todos los descendientes del permiso: {}", permisoId);

        List<Permiso> todosDescendientes = new ArrayList<>();
        obtenerDescendientesRecursivo(permisoId, todosDescendientes);
        return todosDescendientes;
    }

    private void obtenerDescendientesRecursivo(Long permisoId, List<Permiso> resultado) {
        List<Permiso> hijosDirectos = permisoRepository.findDescendientesDirectos(permisoId);

        for (Permiso hijo : hijosDirectos) {
            resultado.add(hijo);
            // Llamada recursiva para obtener nietos, bisnietos, etc.
            obtenerDescendientesRecursivo(hijo.getPermisoId(), resultado);
        }
    }

    public List<Permiso> obtenerPermisosRaiz() {
        log.info("Obteniendo permisos raíz");
        return permisoRepository.findPermisosRaiz();
    }
}
