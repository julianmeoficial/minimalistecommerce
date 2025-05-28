package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.VendedorDetallesDTO;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.model.VendedorDetalles;
import com.digital.mecommerces.service.UsuarioService;
import com.digital.mecommerces.service.VendedorDetallesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador para gestión de detalles de vendedores
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/vendedor-detalles")
@Tag(name = "Detalles de Vendedor", description = "APIs para gestión de información específica de vendedores")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class VendedorDetallesController {

    private final VendedorDetallesService vendedorDetallesService;
    private final UsuarioService usuarioService;

    public VendedorDetallesController(VendedorDetallesService vendedorDetallesService,
                                      UsuarioService usuarioService) {
        this.vendedorDetallesService = vendedorDetallesService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/mi-perfil")
    @Operation(summary = "Obtener mi perfil de vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<VendedorDetallesDTO> obtenerMiPerfil() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Obteniendo perfil de vendedor para usuario ID: {}", usuarioId);

        VendedorDetalles detalles = vendedorDetallesService.obtenerDetallesPorUsuarioId(usuarioId);
        VendedorDetallesDTO detallesDTO = VendedorDetallesDTO.fromEntity(detalles);

        return ResponseEntity.ok(detallesDTO);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener detalles de vendedor por ID de usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<VendedorDetallesDTO> obtenerDetallesVendedor(@PathVariable Long usuarioId) {
        log.info("🏪 Admin obteniendo detalles de vendedor para usuario ID: {}", usuarioId);

        VendedorDetalles detalles = vendedorDetallesService.obtenerDetallesPorUsuarioId(usuarioId);
        VendedorDetallesDTO detallesDTO = VendedorDetallesDTO.fromEntity(detalles);

        return ResponseEntity.ok(detallesDTO.toPublic());
    }

    @PostMapping("/usuario/{usuarioId}")
    @Operation(summary = "Crear detalles de vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<VendedorDetallesDTO> crearDetallesVendedor(@PathVariable Long usuarioId,
                                                                     @Valid @RequestBody VendedorDetallesDTO detallesDTO) {
        log.info("🏪 Creando detalles de vendedor para usuario ID: {}", usuarioId);

        VendedorDetalles detalles = new VendedorDetalles();
        detalles.setRfc(detallesDTO.getRfc());
        detalles.setEspecialidad(detallesDTO.getEspecialidad());
        detalles.setDireccionComercial(detallesDTO.getDireccionComercial());
        detalles.setNumRegistroFiscal(detallesDTO.getNumRegistroFiscal());
        detalles.setVerificado(detallesDTO.getVerificado());
        detalles.setDocumentoComercial(detallesDTO.getDocumentoComercial());
        detalles.setTipoDocumento(detallesDTO.getTipoDocumento());
        detalles.setBanco(detallesDTO.getBanco());
        detalles.setTipoCuenta(detallesDTO.getTipoCuenta());
        detalles.setNumeroCuenta(detallesDTO.getNumeroCuenta());

        VendedorDetalles creados = vendedorDetallesService.crearOActualizarDetalles(usuarioId, detalles);
        VendedorDetallesDTO respuesta = VendedorDetallesDTO.fromEntity(creados);

        log.info("✅ Detalles de vendedor creados exitosamente");
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/mi-perfil")
    @Operation(summary = "Actualizar mi perfil de vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<VendedorDetallesDTO> actualizarMiPerfil(@Valid @RequestBody VendedorDetallesDTO detallesDTO) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Actualizando perfil de vendedor para usuario ID: {}", usuarioId);

        VendedorDetalles detalles = new VendedorDetalles();
        detalles.setRfc(detallesDTO.getRfc());
        detalles.setEspecialidad(detallesDTO.getEspecialidad());
        detalles.setDireccionComercial(detallesDTO.getDireccionComercial());
        detalles.setNumRegistroFiscal(detallesDTO.getNumRegistroFiscal());
        detalles.setDocumentoComercial(detallesDTO.getDocumentoComercial());
        detalles.setTipoDocumento(detallesDTO.getTipoDocumento());
        detalles.setBanco(detallesDTO.getBanco());
        detalles.setTipoCuenta(detallesDTO.getTipoCuenta());
        detalles.setNumeroCuenta(detallesDTO.getNumeroCuenta());

        VendedorDetalles actualizados = vendedorDetallesService.crearOActualizarDetalles(usuarioId, detalles);
        VendedorDetallesDTO respuesta = VendedorDetallesDTO.fromEntity(actualizados);

        log.info("✅ Perfil de vendedor actualizado exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/usuario/{usuarioId}")
    @Operation(summary = "Eliminar detalles de vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Void> eliminarDetallesVendedor(@PathVariable Long usuarioId) {
        log.info("🏪 Eliminando detalles de vendedor para usuario ID: {}", usuarioId);

        vendedorDetallesService.eliminarDetalles(usuarioId);

        log.info("✅ Detalles de vendedor eliminados exitosamente");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/todos")
    @Operation(summary = "Obtener todos los vendedores")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<List<VendedorDetallesDTO>> obtenerTodosLosVendedores() {
        log.info("🏪 Obteniendo todos los vendedores");

        List<VendedorDetalles> vendedores = vendedorDetallesService.obtenerTodosLosVendedores();
        List<VendedorDetallesDTO> vendedoresDTO = vendedores.stream()
                .map(VendedorDetallesDTO::fromEntity)
                .map(VendedorDetallesDTO::toPublic)
                .toList();

        log.info("✅ {} vendedores encontrados", vendedoresDTO.size());
        return ResponseEntity.ok(vendedoresDTO);
    }

    @GetMapping("/verificados")
    @Operation(summary = "Obtener vendedores verificados")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<VendedorDetallesDTO>> obtenerVendedoresVerificados() {
        log.info("🏪 Obteniendo vendedores verificados");

        List<VendedorDetalles> vendedores = vendedorDetallesService.obtenerVendedoresVerificados();
        List<VendedorDetallesDTO> vendedoresDTO = vendedores.stream()
                .map(VendedorDetallesDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(vendedoresDTO);
    }

    @GetMapping("/pendientes-verificacion")
    @Operation(summary = "Obtener vendedores pendientes de verificación")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<VendedorDetallesDTO>> obtenerVendedoresPendientesVerificacion() {
        log.info("🏪 Obteniendo vendedores pendientes de verificación");

        List<VendedorDetalles> vendedores = vendedorDetallesService.obtenerVendedoresPendientesVerificacion();
        List<VendedorDetallesDTO> vendedoresDTO = vendedores.stream()
                .map(VendedorDetallesDTO::fromEntity)
                .map(VendedorDetallesDTO::toAdminView)
                .toList();

        return ResponseEntity.ok(vendedoresDTO);
    }

    @PostMapping("/verificar/{usuarioId}")
    @Operation(summary = "Verificar vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> verificarVendedor(@PathVariable Long usuarioId,
                                                                 @RequestBody Map<String, String> request) {
        String motivoVerificacion = request.get("motivo");
        log.info("🏪 Verificando vendedor ID: {} con motivo: {}", usuarioId, motivoVerificacion);

        vendedorDetallesService.verificarVendedor(usuarioId, motivoVerificacion);

        Map<String, Object> response = Map.of(
                "mensaje", "Vendedor verificado exitosamente",
                "usuarioId", usuarioId,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/rechazar-verificacion/{usuarioId}")
    @Operation(summary = "Rechazar verificación de vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> rechazarVerificacion(@PathVariable Long usuarioId,
                                                                    @RequestBody Map<String, String> request) {
        String motivoRechazo = request.get("motivo");
        log.info("🏪 Rechazando verificación del vendedor ID: {} con motivo: {}", usuarioId, motivoRechazo);

        vendedorDetallesService.rechazarVerificacion(usuarioId, motivoRechazo);

        Map<String, Object> response = Map.of(
                "mensaje", "Verificación rechazada",
                "usuarioId", usuarioId,
                "motivo", motivoRechazo,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de vendedores")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.info("🏪 Obteniendo estadísticas de vendedores");

        Map<String, Object> estadisticas = Map.of(
                "total", vendedorDetallesService.contarVendedores(),
                "verificados", vendedorDetallesService.contarVendedoresVerificados(),
                "pendientesVerificacion", vendedorDetallesService.contarVendedoresPendientesVerificacion(),
                "conInformacionCompleta", vendedorDetallesService.contarVendedoresConInformacionCompleta(),
                "destacados", vendedorDetallesService.contarVendedoresDestacados(),
                "porEspecialidad", vendedorDetallesService.obtenerEstadisticasPorEspecialidad(),
                "calificacionPromedio", vendedorDetallesService.obtenerCalificacionPromedioGeneral(),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(estadisticas);
    }

    @PostMapping("/solicitar-verificacion")
    @Operation(summary = "Solicitar verificación como vendedor")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> solicitarVerificacion(@RequestBody VendedorDetallesDTO detallesDTO) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Solicitud de verificación para vendedor ID: {}", usuarioId);

        try {
            // Validar información completa
            if (!detallesDTO.tieneInformacionCompleta()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Información incompleta para verificación",
                        "timestamp", LocalDateTime.now()
                ));
            }

            VendedorDetalles detalles = new VendedorDetalles();
            detalles.setRfc(detallesDTO.getRfc());
            detalles.setEspecialidad(detallesDTO.getEspecialidad());
            detalles.setDireccionComercial(detallesDTO.getDireccionComercial());
            detalles.setNumRegistroFiscal(detallesDTO.getNumRegistroFiscal());
            detalles.setDocumentoComercial(detallesDTO.getDocumentoComercial());
            detalles.setTipoDocumento(detallesDTO.getTipoDocumento());
            detalles.setBanco(detallesDTO.getBanco());
            detalles.setTipoCuenta(detallesDTO.getTipoCuenta());
            detalles.setNumeroCuenta(detallesDTO.getNumeroCuenta());

            vendedorDetallesService.solicitarVerificacion(usuarioId, detalles);

            Map<String, Object> response = Map.of(
                    "mensaje", "Solicitud de verificación enviada exitosamente",
                    "usuarioId", usuarioId,
                    "estado", "PENDIENTE_VERIFICACION",
                    "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error procesando solicitud de verificación: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error procesando solicitud: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/especialidades")
    @Operation(summary = "Obtener lista de especialidades disponibles")
    public ResponseEntity<List<String>> obtenerEspecialidades() {
        log.info("🏪 Obteniendo lista de especialidades");

        List<String> especialidades = List.of(
                "Electrónica",
                "Ropa y Moda",
                "Hogar y Jardín",
                "Deportes y Fitness",
                "Libros y Medios",
                "Juguetes y Niños",
                "Belleza y Cuidado Personal",
                "Salud y Bienestar",
                "Automotriz",
                "Alimentos y Bebidas",
                "Arte y Manualidades",
                "Mascotas",
                "Tecnología",
                "Instrumentos Musicales",
                "Joyería y Accesorios",
                "General"
        );

        return ResponseEntity.ok(especialidades);
    }

    @GetMapping("/mi-estado-verificacion")
    @Operation(summary = "Obtener estado de verificación del vendedor autenticado")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerMiEstadoVerificacion() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🏪 Obteniendo estado de verificación para vendedor ID: {}", usuarioId);

        try {
            VendedorDetalles detalles = vendedorDetallesService.obtenerDetallesPorUsuarioId(usuarioId);

            if (detalles == null) {
                return ResponseEntity.ok(Map.of(
                        "estado", "SIN_DETALLES",
                        "mensaje", "No hay información de vendedor registrada",
                        "requiereAccion", true,
                        "timestamp", LocalDateTime.now()
                ));
            }

            VendedorDetallesDTO detallesDTO = VendedorDetallesDTO.fromEntity(detalles);

            Map<String, Object> estadoVerificacion = Map.of(
                    "estado", detallesDTO.obtenerEstadoVerificacion(),
                    "verificado", detallesDTO.getVerificado(),
                    "fechaVerificacion", detallesDTO.getFechaVerificacion(),
                    "informacionCompleta", detallesDTO.tieneInformacionCompleta(),
                    "puedeVender", detallesDTO.puedeVender(),
                    "requiereVerificacion", detallesDTO.requiereVerificacion(),
                    "ventasTotales", detallesDTO.getVentasTotales(),
                    "calificacion", detallesDTO.getCalificacion(),
                    "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(estadoVerificacion);

        } catch (Exception e) {
            log.error("❌ Error obteniendo estado de verificación: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error obteniendo estado de verificación",
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    // Método auxiliar para obtener usuario autenticado
    private Long obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
        return usuario.getUsuarioId();
    }
}
