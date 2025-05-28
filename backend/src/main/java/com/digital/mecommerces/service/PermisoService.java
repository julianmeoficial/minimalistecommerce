package com.digital.mecommerces.service;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.exception.BusinessException;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.Permiso;
import com.digital.mecommerces.repository.PermisoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PermisoService {

    private final PermisoRepository permisoRepository;

    public PermisoService(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    @Cacheable("permisos")
    public List<Permiso> obtenerTodosLosPermisos() {
        log.info("📋 Obteniendo todos los permisos");
        return permisoRepository.findPermisosActivosOrdenados();
    }

    @Cacheable("permisosDelSistema")
    public List<Permiso> obtenerPermisosDelSistema() {
        log.info("⚙️ Obteniendo permisos del sistema optimizado");
        return permisoRepository.findPermisosDelSistema();
    }

    public List<Permiso> obtenerPermisosPersonalizados() {
        log.info("🎨 Obteniendo permisos personalizados");
        return permisoRepository.findPermisosPersonalizados();
    }

    @Cacheable(value = "permiso", key = "#id")
    public Permiso obtenerPermisoPorId(Long id) {
        log.info("🔍 Obteniendo permiso por ID: {}", id);
        return permisoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));
    }

    public Permiso obtenerPermisoPorCodigo(String codigo) {
        log.info("📝 Obteniendo permiso por código: {}", codigo);
        return permisoRepository.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con código: " + codigo));
    }

    public List<Permiso> obtenerPermisosPorCategoria(String categoria) {
        log.info("📂 Obteniendo permisos por categoría: {}", categoria);
        return permisoRepository.findByCategoria(categoria.toUpperCase());
    }

    public List<Permiso> obtenerPermisosPorNivel(Integer nivel) {
        log.info("🏆 Obteniendo permisos por nivel: {}", nivel);
        return permisoRepository.findByNivel(nivel);
    }

    public List<Permiso> obtenerPermisosAdministrativos() {
        log.info("👑 Obteniendo permisos administrativos (nivel <= 2)");
        return permisoRepository.findPermisosAdministrativos();
    }

    public List<Permiso> obtenerPermisosBasicos() {
        log.info("📚 Obteniendo permisos básicos (nivel >= 3)");
        return permisoRepository.findPermisosBasicos();
    }

    @Transactional
    @CacheEvict(value = {"permisos", "permisosDelSistema", "permiso"}, allEntries = true)
    public Permiso crearPermiso(Permiso permiso) {
        log.info("➕ Creando nuevo permiso: {}", permiso.getCodigo());

        // Validar que no existe un permiso con el mismo código
        if (permisoRepository.existsByCodigo(permiso.getCodigo())) {
            throw new BusinessException("Ya existe un permiso con el código: " + permiso.getCodigo());
        }

        // Verificar si es un permiso crítico del sistema
        if (esPermisoCriticoDelSistema(permiso.getCodigo())) {
            log.warn("⚠️ Intentando crear permiso crítico del sistema: {}", permiso.getCodigo());
            throw new BusinessException("No se puede crear manualmente un permiso crítico del sistema");
        }

        // Validar nivel de permiso
        if (permiso.getNivel() == null || permiso.getNivel() < 1) {
            permiso.setNivel(999); // Nivel bajo para permisos personalizados
        }

        // Asignar categoría por defecto si no se especifica
        if (permiso.getCategoria() == null || permiso.getCategoria().isEmpty()) {
            permiso.setCategoria("GENERAL");
        }

        // Validar jerarquía si tiene padre
        if (permiso.getPermisoPadre() != null) {
            Permiso padre = obtenerPermisoPorId(permiso.getPermisoPadre().getPermisoId());
            permiso.setPermisoPadre(padre);
            permiso.setPermisopadreId(padre.getPermisoId());
        }

        Permiso nuevoPermiso = permisoRepository.save(permiso);
        log.info("✅ Permiso creado exitosamente: {} con ID: {}",
                nuevoPermiso.getCodigo(), nuevoPermiso.getPermisoId());

        return nuevoPermiso;
    }

    @Transactional
    @CacheEvict(value = {"permisos", "permisosDelSistema", "permiso"}, allEntries = true)
    public Permiso actualizarPermiso(Long id, Permiso permisoDetails) {
        log.info("✏️ Actualizando permiso ID: {}", id);

        Permiso permiso = obtenerPermisoPorId(id);

        // Verificar que no sea un permiso crítico del sistema
        if (permiso.esPermisoDelSistema()) {
            throw new BusinessException("No se pueden modificar permisos críticos del sistema");
        }

        // Verificar código único si se está cambiando
        if (permisoDetails.getCodigo() != null && !permiso.getCodigo().equals(permisoDetails.getCodigo())) {
            if (permisoRepository.existsByCodigoAndPermisoIdNot(permisoDetails.getCodigo(), id)) {
                throw new BusinessException("Ya existe un permiso con el código: " + permisoDetails.getCodigo());
            }
            permiso.setCodigo(permisoDetails.getCodigo());
        }

        // Actualizar otros campos
        if (permisoDetails.getDescripcion() != null) {
            permiso.setDescripcion(permisoDetails.getDescripcion());
        }

        if (permisoDetails.getNivel() != null) {
            permiso.setNivel(permisoDetails.getNivel());
        }

        if (permisoDetails.getCategoria() != null) {
            permiso.setCategoria(permisoDetails.getCategoria());
        }

        if (permisoDetails.getActivo() != null) {
            permiso.setActivo(permisoDetails.getActivo());
        }

        Permiso permisoActualizado = permisoRepository.save(permiso);
        log.info("✅ Permiso actualizado exitosamente: {}", permisoActualizado.getCodigo());

        return permisoActualizado;
    }

    @Transactional
    @CacheEvict(value = {"permisos", "permisosDelSistema", "permiso"}, allEntries = true)
    public void eliminarPermiso(Long id) {
        log.info("🗑️ Eliminando permiso ID: {}", id);

        Permiso permiso = obtenerPermisoPorId(id);

        // Verificar que no sea un permiso crítico del sistema
        if (permiso.esPermisoDelSistema()) {
            throw new BusinessException("No se pueden eliminar permisos críticos del sistema: " + permiso.getCodigo());
        }

        // Verificar que no tenga subpermisos
        List<Permiso> subpermisos = permisoRepository.findByPermisopadreId(id);
        if (!subpermisos.isEmpty()) {
            throw new BusinessException("No se puede eliminar el permiso porque tiene " +
                    subpermisos.size() + " subpermisos asociados");
        }

        // Verificar que no esté asignado a ningún rol
        List<Permiso> permisosAsignados = permisoRepository.findPermisosAsignados();
        boolean estaAsignado = permisosAsignados.stream()
                .anyMatch(p -> p.getPermisoId().equals(id));

        if (estaAsignado) {
            throw new BusinessException("No se puede eliminar el permiso porque está asignado a uno o más roles");
        }

        permisoRepository.delete(permiso);
        log.info("✅ Permiso eliminado exitosamente: {}", permiso.getCodigo());
    }

    public boolean existePermisoPorCodigo(String codigo) {
        return permisoRepository.existsByCodigo(codigo);
    }

    public boolean esPermisoDelSistema(String codigo) {
        return permisoRepository.esPermisoDelSistema(codigo);
    }

    public List<Permiso> obtenerPermisosAsignados() {
        log.info("📊 Obteniendo permisos asignados a roles");
        return permisoRepository.findPermisosAsignados();
    }

    public List<Permiso> obtenerPermisosNoAsignados() {
        log.info("📪 Obteniendo permisos no asignados");
        return permisoRepository.findPermisosNoAsignados();
    }

    public List<Permiso> obtenerPermisosPorRol(Long rolId) {
        log.info("👥 Obteniendo permisos para rol ID: {}", rolId);
        return permisoRepository.findPermisosPorRol(rolId);
    }

    public List<Permiso> obtenerPermisosRecomendadosParaRol(String rolNombre) {
        log.info("💡 Obteniendo permisos recomendados para rol: {}", rolNombre);

        return switch (rolNombre.toUpperCase()) {
            case "ADMINISTRADOR" -> permisoRepository.findPermisosRecomendadosAdministrador();
            case "VENDEDOR" -> permisoRepository.findPermisosRecomendadosVendedor();
            case "COMPRADOR" -> permisoRepository.findPermisosRecomendadosComprador();
            default -> List.of();
        };
    }

    @Transactional
    public void crearPermisosDelSistema() {
        log.info("🏗️ Verificando y creando permisos del sistema si no existen");

        // Crear permisos básicos del sistema
        crearPermisoSiNoExiste(RoleConstants.PERM_ADMIN_TOTAL,
                "Acceso total de administrador al sistema", 1, "ADMINISTRACION");

        crearPermisoSiNoExiste(RoleConstants.PERM_GESTIONAR_USUARIOS,
                "Gestionar usuarios del sistema", 2, "ADMINISTRACION");

        crearPermisoSiNoExiste(RoleConstants.PERM_GESTIONAR_CATEGORIAS,
                "Gestionar categorías de productos", 2, "GESTION");

        crearPermisoSiNoExiste(RoleConstants.PERM_VENDER_PRODUCTOS,
                "Crear, editar y gestionar productos para venta", 3, "VENTAS");

        crearPermisoSiNoExiste(RoleConstants.PERM_COMPRAR_PRODUCTOS,
                "Realizar compras y gestionar órdenes", 4, "COMPRAS");

        log.info("✅ Verificación de permisos del sistema completada");
    }

    private void crearPermisoSiNoExiste(String codigo, String descripcion, Integer nivel, String categoria) {
        if (!permisoRepository.existsByCodigo(codigo)) {
            Permiso permiso = new Permiso(codigo, descripcion, nivel);
            permiso.setCategoria(categoria);
            permisoRepository.save(permiso);
            log.info("✅ Permiso del sistema creado: {}", codigo);
        } else {
            log.info("ℹ️ Permiso del sistema ya existe: {}", codigo);
        }
    }

    private boolean esPermisoCriticoDelSistema(String codigo) {
        return RoleConstants.PERM_ADMIN_TOTAL.equals(codigo) ||
                RoleConstants.PERM_GESTIONAR_USUARIOS.equals(codigo) ||
                RoleConstants.PERM_GESTIONAR_CATEGORIAS.equals(codigo) ||
                RoleConstants.PERM_VENDER_PRODUCTOS.equals(codigo) ||
                RoleConstants.PERM_COMPRAR_PRODUCTOS.equals(codigo);
    }

    // Métodos de estadísticas
    public long contarPermisos() {
        return permisoRepository.countPermisosActivos();
    }

    public long contarPermisosInactivos() {
        return permisoRepository.countPermisosInactivos();
    }

    public List<Object[]> obtenerEstadisticasPorCategoria() {
        return permisoRepository.countPermisosPorCategoria();
    }

    public List<Object[]> obtenerEstadisticasPorNivel() {
        return permisoRepository.countPermisosPorNivel();
    }

    public List<Object[]> obtenerPermisosMasAsignados() {
        return permisoRepository.findPermisosMasAsignados();
    }

    // Métodos para validación de jerarquías
    public List<Permiso> obtenerPermisosRaiz() {
        return permisoRepository.findPermisosRaiz();
    }

    public List<Permiso> obtenerSubpermisos(Long padreId) {
        return permisoRepository.findSubpermisosPorPadre(padreId);
    }

    public List<Permiso> obtenerPermisosOrdenadosPorImportancia() {
        return permisoRepository.findPermisosOrdenadosPorImportancia();
    }

    public List<Permiso> obtenerPermisosCompatiblesConNivel(Integer nivelUsuario) {
        return permisoRepository.findPermisosCompatiblesConNivel(nivelUsuario);
    }
}
