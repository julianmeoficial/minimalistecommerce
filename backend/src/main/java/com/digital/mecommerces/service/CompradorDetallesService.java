package com.digital.mecommerces.service;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.enums.TipoUsuario;
import com.digital.mecommerces.exception.BusinessException;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.CompradorDetalles;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.repository.CompradorDetallesRepository;
import com.digital.mecommerces.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CompradorDetallesService {

    private final CompradorDetallesRepository compradorDetallesRepository;
    private final UsuarioRepository usuarioRepository;

    public CompradorDetallesService(CompradorDetallesRepository compradorDetallesRepository,
                                    UsuarioRepository usuarioRepository) {
        this.compradorDetallesRepository = compradorDetallesRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Cacheable(value = "compradorDetalles", key = "#usuarioId")
    public CompradorDetalles obtenerDetallesPorUsuarioId(Long usuarioId) {
        log.info("🔍 Obteniendo detalles de comprador para usuario ID: {}", usuarioId);
        return compradorDetallesRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Detalles de comprador no encontrados para usuario ID: " + usuarioId));
    }

    @Transactional
    @CacheEvict(value = "compradorDetalles", key = "#usuarioId")
    public CompradorDetalles crearOActualizarDetalles(Long usuarioId, CompradorDetalles detalles) {
        log.info("💾 Creando/actualizando detalles de comprador para usuario ID: {}", usuarioId);

        // Verificar que el usuario existe y es comprador o administrador
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        // Validar que sea comprador o administrador
        String rolNombre = usuario.getRol().getNombre();
        if (!RoleConstants.ROLE_COMPRADOR.equals(rolNombre) && !RoleConstants.ROLE_ADMINISTRADOR.equals(rolNombre)) {
            try {
                TipoUsuario tipo = TipoUsuario.fromCodigo(rolNombre);
                if (tipo != TipoUsuario.COMPRADOR && tipo != TipoUsuario.ADMINISTRADOR) {
                    throw new BusinessException("El usuario no tiene rol de comprador. Rol actual: " + tipo.getDescripcion());
                }
            } catch (IllegalArgumentException e) {
                throw new BusinessException("El usuario no tiene un rol válido del sistema: " + rolNombre);
            }
        }

        // Buscar detalles existentes o crear nuevos
        CompradorDetalles compradorDetalles = compradorDetallesRepository.findByUsuarioId(usuarioId)
                .orElse(new CompradorDetalles(usuario));

        // Actualizar campos
        if (detalles.getFechaNacimiento() != null) {
            compradorDetalles.setFechaNacimiento(detalles.getFechaNacimiento());
        }

        if (detalles.getDireccionEnvio() != null) {
            compradorDetalles.setDireccionEnvio(detalles.getDireccionEnvio());
        }

        if (detalles.getTelefono() != null) {
            compradorDetalles.setTelefono(detalles.getTelefono());
        }

        if (detalles.getDireccionAlternativa() != null) {
            compradorDetalles.setDireccionAlternativa(detalles.getDireccionAlternativa());
        }

        if (detalles.getTelefonoAlternativo() != null) {
            compradorDetalles.setTelefonoAlternativo(detalles.getTelefonoAlternativo());
        }

        if (detalles.getPreferencias() != null) {
            compradorDetalles.setPreferencias(detalles.getPreferencias());
        }

        if (detalles.getLimiteCompra() != null) {
            compradorDetalles.setLimiteCompra(detalles.getLimiteCompra());
        }

        CompradorDetalles resultado = compradorDetallesRepository.save(compradorDetalles);
        log.info("✅ Detalles de comprador guardados exitosamente para usuario: {}", usuario.getEmail());

        return resultado;
    }

    @Transactional
    @CacheEvict(value = "compradorDetalles", key = "#usuarioId")
    public void eliminarDetalles(Long usuarioId) {
        log.info("🗑️ Eliminando detalles de comprador para usuario ID: {}", usuarioId);

        if (!compradorDetallesRepository.existsByUsuarioId(usuarioId)) {
            throw new ResourceNotFoundException("Detalles de comprador no encontrados para usuario ID: " + usuarioId);
        }

        compradorDetallesRepository.deleteByUsuarioId(usuarioId);
        log.info("✅ Detalles de comprador eliminados exitosamente");
    }

    public List<CompradorDetalles> obtenerTodosLosCompradores() {
        log.info("📋 Obteniendo todos los compradores activos");
        return compradorDetallesRepository.findCompradoresActivos();
    }

    public List<CompradorDetalles> obtenerCompradoresPorRol() {
        log.info("👥 Obteniendo compradores por rol del sistema");
        return compradorDetallesRepository.findCompradoriesByRolSistema();
    }

    public List<CompradorDetalles> obtenerCompradoresVIP() {
        log.info("👑 Obteniendo compradores VIP (calificación >= 4.5)");
        return compradorDetallesRepository.findCompradoresVIP();
    }

    public List<CompradorDetalles> obtenerCompradoriesFrecuentes() {
        log.info("🛒 Obteniendo compradores frecuentes (5+ compras)");
        return compradorDetallesRepository.findCompradoriesFrecuentes();
    }

    public List<CompradorDetalles> obtenerCompradoresNuevos() {
        log.info("🆕 Obteniendo compradores nuevos (sin compras)");
        return compradorDetallesRepository.findCompradoresNuevos();
    }

    public List<CompradorDetalles> obtenerCompradoresConInformacionCompleta() {
        log.info("✅ Obteniendo compradores con información completa");
        return compradorDetallesRepository.findConInformacionCompleta();
    }

    public List<CompradorDetalles> obtenerCompradoresConInformacionIncompleta() {
        log.info("⚠️ Obteniendo compradores con información incompleta");
        return compradorDetallesRepository.findConInformacionIncompleta();
    }

    @Transactional
    public void registrarCompra(Long usuarioId, BigDecimal monto) {
        log.info("🛒 Registrando compra para comprador ID: {} por monto: ${}", usuarioId, monto);

        CompradorDetalles compradorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        compradorDetalles.registrarCompra(monto);
        compradorDetallesRepository.save(compradorDetalles);

        log.info("✅ Compra registrada. Total de compras: {}", compradorDetalles.getTotalCompras());
    }

    @Transactional
    public void actualizarCalificacion(Long usuarioId, BigDecimal nuevaCalificacion) {
        log.info("⭐ Actualizando calificación para comprador ID: {} a: {}", usuarioId, nuevaCalificacion);

        if (nuevaCalificacion.compareTo(BigDecimal.ZERO) < 0 ||
                nuevaCalificacion.compareTo(new BigDecimal("5.00")) > 0) {
            throw new BusinessException("La calificación debe estar entre 0 y 5");
        }

        CompradorDetalles compradorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        compradorDetalles.actualizarCalificacion(nuevaCalificacion);
        compradorDetallesRepository.save(compradorDetalles);

        log.info("✅ Calificación actualizada exitosamente");
    }

    @Transactional
    public void configurarNotificaciones(Long usuarioId, boolean email, boolean sms) {
        log.info("🔔 Configurando notificaciones para comprador ID: {} - Email: {}, SMS: {}",
                usuarioId, email, sms);

        CompradorDetalles compradorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        compradorDetalles.configurarNotificaciones(email, sms);
        compradorDetallesRepository.save(compradorDetalles);

        log.info("✅ Notificaciones configuradas exitosamente");
    }

    public String obtenerNivelComprador(Long usuarioId) {
        CompradorDetalles compradorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        return compradorDetalles.getNivelComprador();
    }

    public boolean puedeComprar(Long usuarioId) {
        try {
            CompradorDetalles compradorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
            return compradorDetalles.puedeComprar();
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public boolean tieneInformacionCompleta(Long usuarioId) {
        try {
            CompradorDetalles compradorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
            return compradorDetalles.tieneInformacionCompleta();
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public List<CompradorDetalles> obtenerCompradoresActivosDesde(LocalDateTime fecha) {
        log.info("📊 Obteniendo compradores activos desde: {}", fecha);
        return compradorDetallesRepository.findCompradoresActivosDesde(fecha);
    }

    public List<CompradorDetalles> obtenerCompradoresInactivos(LocalDateTime fecha) {
        log.info("😴 Obteniendo compradores inactivos desde: {}", fecha);
        return compradorDetallesRepository.findCompradoresInactivos(fecha);
    }

    public List<CompradorDetalles> obtenerCompradoresPorNivel(String nivel) {
        log.info("🏆 Obteniendo compradores por nivel: {}", nivel);

        return switch (nivel.toUpperCase()) {
            case "NUEVO", "BRONCE" -> compradorDetallesRepository.findCompradoriesNivel1();
            case "PLATA" -> compradorDetallesRepository.findCompradoriesNivel2();
            case "ORO" -> compradorDetallesRepository.findCompradoriesNivel3();
            case "PLATINO" -> compradorDetallesRepository.findCompradoriesNivel4();
            default -> throw new BusinessException("Nivel de comprador no válido: " + nivel);
        };
    }

    public List<CompradorDetalles> obtenerCompradoresParaCampanaEmail(Integer minCompras) {
        log.info("📧 Obteniendo compradores para campaña de email con mínimo {} compras", minCompras);
        return compradorDetallesRepository.findParaCampanaEmail(minCompras);
    }

    public List<CompradorDetalles> obtenerCompradoresParaCampanaSms() {
        log.info("📱 Obteniendo compradores para campaña SMS");
        return compradorDetallesRepository.findParaCampanaSms();
    }

    // Estadísticas
    public long contarCompradoresActivos() {
        return compradorDetallesRepository.countCompradoresActivos();
    }

    public long contarCompradoresConCompras() {
        return compradorDetallesRepository.countCompradoresConCompras();
    }

    public BigDecimal obtenerPromedioCalificacion() {
        return compradorDetallesRepository.findPromedioCalificacion();
    }

    public Double obtenerPromedioTotalCompras() {
        return compradorDetallesRepository.findPromedioTotalCompras();
    }

    public Long obtenerTotalComprasGenerales() {
        return compradorDetallesRepository.sumTotalComprasGenerales();
    }

    @Transactional
    public void crearCompradorCompleto(Usuario usuario, String direccionEnvio, String telefono) {
        log.info("🏗️ Creando comprador completo para usuario: {}", usuario.getEmail());

        if (!RoleConstants.ROLE_COMPRADOR.equals(usuario.getRol().getNombre()) &&
                !RoleConstants.ROLE_ADMINISTRADOR.equals(usuario.getRol().getNombre())) {
            throw new BusinessException("Solo se pueden crear detalles para usuarios compradores o administradores");
        }

        CompradorDetalles compradorDetalles = new CompradorDetalles(usuario);
        compradorDetalles.setDireccionEnvio(direccionEnvio);
        compradorDetalles.setTelefono(telefono);

        compradorDetallesRepository.save(compradorDetalles);
        log.info("✅ Comprador completo creado exitosamente");
    }
}
