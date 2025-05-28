package com.digital.mecommerces.service;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.enums.TipoUsuario;
import com.digital.mecommerces.exception.BusinessException;
import com.digital.mecommerces.exception.ResourceNotFoundException;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.model.VendedorDetalles;
import com.digital.mecommerces.repository.UsuarioRepository;
import com.digital.mecommerces.repository.VendedorDetallesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class VendedorDetallesService {

    private final VendedorDetallesRepository vendedorDetallesRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public VendedorDetallesService(VendedorDetallesRepository vendedorDetallesRepository,
                                   UsuarioRepository usuarioRepository,
                                   EmailService emailService) {
        this.vendedorDetallesRepository = vendedorDetallesRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    @Cacheable(value = "vendedorDetalles", key = "#usuarioId")
    public VendedorDetalles obtenerDetallesPorUsuarioId(Long usuarioId) {
        log.info("🔍 Obteniendo detalles de vendedor para usuario ID: {}", usuarioId);
        return vendedorDetallesRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Detalles de vendedor no encontrados para usuario ID: " + usuarioId));
    }

    @Transactional
    @CacheEvict(value = "vendedorDetalles", key = "#usuarioId")
    public VendedorDetalles crearOActualizarDetalles(Long usuarioId, VendedorDetalles detalles) {
        log.info("💾 Creando/actualizando detalles de vendedor para usuario ID: {}", usuarioId);

        // Verificar que el usuario existe y es vendedor o administrador
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        // Validar que sea vendedor o administrador
        validarUsuarioPuedeSerVendedor(usuario);

        // Buscar detalles existentes o crear nuevos
        VendedorDetalles vendedorDetalles = vendedorDetallesRepository.findByUsuarioId(usuarioId)
                .orElse(new VendedorDetalles(usuario));

        // Actualizar campos
        actualizarCamposVendedor(vendedorDetalles, detalles);

        VendedorDetalles resultado = vendedorDetallesRepository.save(vendedorDetalles);
        log.info("✅ Detalles de vendedor guardados exitosamente para usuario: {}", usuario.getEmail());

        return resultado;
    }

    @Transactional
    @CacheEvict(value = "vendedorDetalles", key = "#usuarioId")
    public void eliminarDetalles(Long usuarioId) {
        log.info("🗑️ Eliminando detalles de vendedor para usuario ID: {}", usuarioId);

        if (!vendedorDetallesRepository.existsByUsuarioId(usuarioId)) {
            throw new ResourceNotFoundException("Detalles de vendedor no encontrados para usuario ID: " + usuarioId);
        }

        vendedorDetallesRepository.deleteByUsuarioId(usuarioId);
        log.info("✅ Detalles de vendedor eliminados exitosamente");
    }

    public List<VendedorDetalles> obtenerTodosLosVendedores() {
        log.info("📋 Obteniendo todos los vendedores activos");
        return vendedorDetallesRepository.findVendedoresActivos();
    }

    public List<VendedorDetalles> obtenerVendedoresPorRol() {
        log.info("👥 Obteniendo vendedores por rol del sistema");
        return vendedorDetallesRepository.findVendedoresByRolSistema();
    }

    public List<VendedorDetalles> obtenerVendedoresVerificados() {
        log.info("✅ Obteniendo vendedores verificados");
        return vendedorDetallesRepository.findVendedoresVerificadosActivos();
    }

    public List<VendedorDetalles> obtenerVendedoresNoVerificados() {
        log.info("⏳ Obteniendo vendedores no verificados");
        return vendedorDetallesRepository.findVendedoresNoVerificadosActivos();
    }

    public List<VendedorDetalles> obtenerVendedoresPendientesVerificacion() {
        log.info("📋 Obteniendo vendedores pendientes de verificación");
        return vendedorDetallesRepository.findPendientesVerificacion();
    }

    public List<VendedorDetalles> obtenerVendedoresConProductos() {
        log.info("📦 Obteniendo vendedores con productos");
        return vendedorDetallesRepository.findVendedoresConProductos();
    }

    public List<VendedorDetalles> obtenerVendedoresSinProductos() {
        log.info("📪 Obteniendo vendedores sin productos");
        return vendedorDetallesRepository.findVendedoresSinProductos();
    }

    public List<VendedorDetalles> obtenerVendedoresPorEspecialidad(String especialidad) {
        log.info("🎯 Obteniendo vendedores por especialidad: {}", especialidad);
        return vendedorDetallesRepository.findByEspecialidadYActivo(especialidad);
    }

    @Transactional
    public void verificarVendedor(Long usuarioId) {
        log.info("✅ Verificando vendedor ID: {}", usuarioId);

        VendedorDetalles vendedorDetalles = obtenerDetallesPorUsuarioId(usuarioId);

        // Validar que el vendedor tenga información completa
        if (!vendedorDetalles.tieneInformacionCompleta()) {
            throw new BusinessException("El vendedor debe completar toda su información antes de ser verificado");
        }

        vendedorDetalles.verificarVendedor();
        vendedorDetallesRepository.save(vendedorDetalles);

        // Enviar email de notificación
        emailService.enviarEmailVendedorVerificado(vendedorDetalles.getUsuario());

        log.info("✅ Vendedor verificado exitosamente: {}", vendedorDetalles.getUsuario().getEmail());
    }

    @Transactional
    public void revocarVerificacion(Long usuarioId, String motivo) {
        log.info("❌ Revocando verificación de vendedor ID: {}", usuarioId);

        VendedorDetalles vendedorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        vendedorDetalles.revocarVerificacion();
        vendedorDetallesRepository.save(vendedorDetalles);

        log.info("❌ Verificación revocada para vendedor: {} - Motivo: {}",
                vendedorDetalles.getUsuario().getEmail(), motivo);
    }

    @Transactional
    public void actualizarInformacionBancaria(Long usuarioId, String banco, String tipoCuenta, String numeroCuenta) {
        log.info("🏦 Actualizando información bancaria para vendedor ID: {}", usuarioId);

        VendedorDetalles vendedorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        vendedorDetalles.configurarInformacionBancaria(banco, tipoCuenta, numeroCuenta);
        vendedorDetallesRepository.save(vendedorDetalles);

        log.info("✅ Información bancaria actualizada exitosamente");
    }

    @Transactional
    public void actualizarDocumentacion(Long usuarioId, String tipoDocumento, String documentoComercial) {
        log.info("📄 Actualizando documentación para vendedor ID: {}", usuarioId);

        VendedorDetalles vendedorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        vendedorDetalles.configurarDocumentacion(tipoDocumento, documentoComercial);
        vendedorDetallesRepository.save(vendedorDetalles);

        log.info("✅ Documentación actualizada exitosamente");
    }

    @Transactional
    public void registrarVenta(Long usuarioId) {
        log.info("🛒 Registrando venta para vendedor ID: {}", usuarioId);

        VendedorDetalles vendedorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        vendedorDetalles.registrarVenta();
        vendedorDetallesRepository.save(vendedorDetalles);

        log.info("✅ Venta registrada. Total de ventas: {}", vendedorDetalles.getVentasTotales());
    }

    @Transactional
    public void actualizarCalificacion(Long usuarioId, Double nuevaCalificacion) {
        log.info("⭐ Actualizando calificación para vendedor ID: {} a: {}", usuarioId, nuevaCalificacion);

        if (nuevaCalificacion < 0.0 || nuevaCalificacion > 5.0) {
            throw new BusinessException("La calificación debe estar entre 0 y 5");
        }

        VendedorDetalles vendedorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        vendedorDetalles.actualizarCalificacion(nuevaCalificacion);
        vendedorDetallesRepository.save(vendedorDetalles);

        log.info("✅ Calificación actualizada exitosamente");
    }

    public String obtenerNivelVendedor(Long usuarioId) {
        VendedorDetalles vendedorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        return vendedorDetalles.getNivelVendedor();
    }

    public String obtenerEstadoVerificacion(Long usuarioId) {
        VendedorDetalles vendedorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
        return vendedorDetalles.getEstadoVerificacion();
    }

    public boolean puedeVender(Long usuarioId) {
        try {
            VendedorDetalles vendedorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
            return vendedorDetalles.puedeVender();
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public boolean tieneInformacionCompleta(Long usuarioId) {
        try {
            VendedorDetalles vendedorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
            return vendedorDetalles.tieneInformacionCompleta();
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public boolean tieneInformacionBancaria(Long usuarioId) {
        try {
            VendedorDetalles vendedorDetalles = obtenerDetallesPorUsuarioId(usuarioId);
            return vendedorDetalles.tieneInformacionBancaria();
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public List<VendedorDetalles> obtenerVendedoresConInformacionCompleta() {
        log.info("✅ Obteniendo vendedores con información completa");
        return vendedorDetallesRepository.findConInformacionCompleta();
    }

    public List<VendedorDetalles> obtenerVendedoresConInformacionIncompleta() {
        log.info("⚠️ Obteniendo vendedores con información incompleta");
        return vendedorDetallesRepository.findConInformacionIncompleta();
    }

    public List<VendedorDetalles> obtenerVendedoresQueRequierenDocumentacion() {
        log.info("📄 Obteniendo vendedores que requieren documentación");
        return vendedorDetallesRepository.findQueRequierenDocumentacion();
    }

    // Estadísticas
    public long contarVendedoresActivos() {
        return vendedorDetallesRepository.countVendedoresActivos();
    }

    public long contarVendedoresVerificados() {
        return vendedorDetallesRepository.countVendedoresVerificados();
    }

    public long contarVendedoresNoVerificados() {
        return vendedorDetallesRepository.countVendedoresNoVerificados();
    }

    public long contarPerfilesCompletos() {
        return vendedorDetallesRepository.countPerfilesCompletos();
    }

    public Double obtenerPorcentajeVerificados() {
        return vendedorDetallesRepository.findPorcentajeVerificados();
    }

    public List<Object[]> obtenerEstadisticasPorEspecialidad() {
        return vendedorDetallesRepository.countVendedoresPorEspecialidad();
    }

    public List<Object[]> obtenerTopVendedoresPorProductos() {
        return vendedorDetallesRepository.findTopVendedoresPorProductos();
    }

    public List<Object[]> obtenerEstadisticasPorBanco() {
        return vendedorDetallesRepository.countVendedoresPorBanco();
    }

    @Transactional
    public void crearVendedorCompleto(Usuario usuario, String numRegistroFiscal, String especialidad, String direccionComercial) {
        log.info("🏗️ Creando vendedor completo para usuario: {}", usuario.getEmail());

        if (!RoleConstants.ROLE_VENDEDOR.equals(usuario.getRol().getNombre()) &&
                !RoleConstants.ROLE_ADMINISTRADOR.equals(usuario.getRol().getNombre())) {
            throw new BusinessException("Solo se pueden crear detalles para usuarios vendedores o administradores");
        }

        VendedorDetalles vendedorDetalles = new VendedorDetalles(usuario, numRegistroFiscal, especialidad, direccionComercial);
        vendedorDetallesRepository.save(vendedorDetalles);

        log.info("✅ Vendedor completo creado exitosamente");
    }

    // Métodos privados de validación y utilidad

    private void validarUsuarioPuedeSerVendedor(Usuario usuario) {
        if (!usuario.getActivo()) {
            throw new BusinessException("Usuario inactivo no puede tener detalles de vendedor");
        }

        String rolNombre = usuario.getRol().getNombre();
        if (!RoleConstants.ROLE_VENDEDOR.equals(rolNombre) && !RoleConstants.ROLE_ADMINISTRADOR.equals(rolNombre)) {
            try {
                TipoUsuario tipo = TipoUsuario.fromCodigo(rolNombre);
                if (tipo != TipoUsuario.VENDEDOR && tipo != TipoUsuario.ADMINISTRADOR) {
                    throw new BusinessException("El usuario no tiene rol de vendedor. Rol actual: " + tipo.getDescripcion());
                }
            } catch (IllegalArgumentException e) {
                throw new BusinessException("El usuario no tiene un rol válido del sistema: " + rolNombre);
            }
        }
    }

    private void actualizarCamposVendedor(VendedorDetalles vendedorDetalles, VendedorDetalles detalles) {
        if (detalles.getRfc() != null) {
            vendedorDetalles.setRfc(detalles.getRfc());
        }

        if (detalles.getEspecialidad() != null) {
            vendedorDetalles.setEspecialidad(detalles.getEspecialidad());
        }

        if (detalles.getDireccionComercial() != null) {
            vendedorDetalles.setDireccionComercial(detalles.getDireccionComercial());
        }

        if (detalles.getNumRegistroFiscal() != null) {
            vendedorDetalles.setNumRegistroFiscal(detalles.getNumRegistroFiscal());
        }

        if (detalles.getDocumentoComercial() != null) {
            vendedorDetalles.setDocumentoComercial(detalles.getDocumentoComercial());
        }

        if (detalles.getTipoDocumento() != null) {
            vendedorDetalles.setTipoDocumento(detalles.getTipoDocumento());
        }

        if (detalles.getBanco() != null) {
            vendedorDetalles.setBanco(detalles.getBanco());
        }

        if (detalles.getTipoCuenta() != null) {
            vendedorDetalles.setTipoCuenta(detalles.getTipoCuenta());
        }

        if (detalles.getNumeroCuenta() != null) {
            vendedorDetalles.setNumeroCuenta(detalles.getNumeroCuenta());
        }

        if (detalles.getComision() != null) {
            vendedorDetalles.setComision(detalles.getComision());
        }

        if (detalles.getActivo() != null) {
            vendedorDetalles.setActivo(detalles.getActivo());
        }
    }
}
