package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.dto.CompradorDetallesDTO;
import com.digital.mecommerces.model.CompradorDetalles;
import com.digital.mecommerces.model.Usuario;
import com.digital.mecommerces.service.CompradorDetallesService;
import com.digital.mecommerces.service.UsuarioService;
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
 * Controlador para gestión de detalles de compradores
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/comprador-detalles")
@Tag(name = "Detalles de Comprador", description = "APIs para gestión de información específica de compradores")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class CompradorDetallesController {

    private final CompradorDetallesService compradorDetallesService;
    private final UsuarioService usuarioService;

    public CompradorDetallesController(CompradorDetallesService compradorDetallesService,
                                       UsuarioService usuarioService) {
        this.compradorDetallesService = compradorDetallesService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/mi-perfil")
    @Operation(summary = "Obtener mi perfil de comprador")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<CompradorDetallesDTO> obtenerMiPerfil() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Obteniendo perfil de comprador para usuario ID: {}", usuarioId);

        CompradorDetalles detalles = compradorDetallesService.obtenerDetallesPorUsuarioId(usuarioId);
        CompradorDetallesDTO detallesDTO = CompradorDetallesDTO.fromEntity(detalles);

        return ResponseEntity.ok(detallesDTO);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener detalles de comprador por ID de usuario")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<CompradorDetallesDTO> obtenerDetallesComprador(@PathVariable Long usuarioId) {
        log.info("🛒 Admin obteniendo detalles de comprador para usuario ID: {}", usuarioId);

        CompradorDetalles detalles = compradorDetallesService.obtenerDetallesPorUsuarioId(usuarioId);
        CompradorDetallesDTO detallesDTO = CompradorDetallesDTO.fromEntity(detalles);

        return ResponseEntity.ok(detallesDTO.toPublic());
    }

    @PostMapping("/usuario/{usuarioId}")
    @Operation(summary = "Crear detalles de comprador")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<CompradorDetallesDTO> crearDetallesComprador(@PathVariable Long usuarioId,
                                                                       @Valid @RequestBody CompradorDetallesDTO detallesDTO) {
        log.info("🛒 Creando detalles de comprador para usuario ID: {}", usuarioId);

        CompradorDetalles detalles = new CompradorDetalles();
        detalles.setFechaNacimiento(detallesDTO.getFechaNacimiento());
        detalles.setDireccionEnvio(detallesDTO.getDireccionEnvio());
        detalles.setTelefono(detallesDTO.getTelefono());
        detalles.setDireccionAlternativa(detallesDTO.getDireccionAlternativa());
        detalles.setTelefonoAlternativo(detallesDTO.getTelefonoAlternativo());
        detalles.setNotificacionEmail(detallesDTO.getNotificacionEmail());
        detalles.setNotificacionSms(detallesDTO.getNotificacionSms());

        CompradorDetalles creados = compradorDetallesService.crearOActualizarDetalles(usuarioId, detalles);
        CompradorDetallesDTO respuesta = CompradorDetallesDTO.fromEntity(creados);

        log.info("✅ Detalles de comprador creados exitosamente");
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/mi-perfil")
    @Operation(summary = "Actualizar mi perfil de comprador")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<CompradorDetallesDTO> actualizarMiPerfil(@Valid @RequestBody CompradorDetallesDTO detallesDTO) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        log.info("🛒 Actualizando perfil de comprador para usuario ID: {}", usuarioId);

        CompradorDetalles detalles = new CompradorDetalles();
        detalles.setFechaNacimiento(detallesDTO.getFechaNacimiento());
        detalles.setDireccionEnvio(detallesDTO.getDireccionEnvio());
        detalles.setTelefono(detallesDTO.getTelefono());
        detalles.setDireccionAlternativa(detallesDTO.getDireccionAlternativa());
        detalles.setTelefonoAlternativo(detallesDTO.getTelefonoAlternativo());
        detalles.setNotificacionEmail(detallesDTO.getNotificacionEmail());
        detalles.setNotificacionSms(detallesDTO.getNotificacionSms());
        detalles.setPreferencias(detallesDTO.getPreferencias());

        CompradorDetalles actualizados = compradorDetallesService.crearOActualizarDetalles(usuarioId, detalles);
        CompradorDetallesDTO respuesta = CompradorDetallesDTO.fromEntity(actualizados);

        log.info("✅ Perfil de comprador actualizado exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/usuario/{usuarioId}")
    @Operation(summary = "Eliminar detalles de comprador")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Void> eliminarDetallesComprador(@PathVariable Long usuarioId) {
        log.info("🛒 Eliminando detalles de comprador para usuario ID: {}", usuarioId);

        compradorDetallesService.eliminarDetalles(usuarioId);

        log.info("✅ Detalles de comprador eliminados exitosamente");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/todos")
    @Operation(summary = "Obtener todos los compradores")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or hasAuthority('" + RoleConstants.PERM_GESTIONAR_USUARIOS + "')")
    public ResponseEntity<List<CompradorDetallesDTO>> obtenerTodosLosCompradores() {
        log.info("🛒 Obteniendo todos los compradores");

        List<CompradorDetalles> compradores = compradorDetallesService.obtenerTodosLosCompradores();
        List<CompradorDetallesDTO> compradoresDTO = compradores.stream()
                .map(CompradorDetallesDTO::fromEntity)
                .map(CompradorDetallesDTO::toPublic)
                .toList();

        log.info("✅ {} compradores encontrados", compradoresDTO.size());
        return ResponseEntity.ok(compradoresDTO);
    }

    @GetMapping("/verificados")
    @Operation(summary = "Obtener compradores con información completa")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<List<CompradorDetallesDTO>> obtenerCompradoresVerificados() {
        log.info("🛒 Obteniendo compradores con información completa");

        List<CompradorDetalles> compradores = compradorDetallesService.obtenerCompradoresConInformacionCompleta();
        List<CompradorDetallesDTO> compradoresDTO = compradores.stream()
                .map(CompradorDetallesDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(compradoresDTO);
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de compradores")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.info("🛒 Obteniendo estadísticas de compradores");

        Map<String, Object> estadisticas = Map.of(
                "total", compradorDetallesService.contarCompradores(),
                "conInformacionCompleta", compradorDetallesService.contarCompradoresConInformacionCompleta(),
                "vip", compradorDetallesService.contarCompradoresVIP(),
                "frecuentes", compradorDetallesService.contarCompradoresFrecuentes(),
                "notificacionesEmail", compradorDetallesService.contarCompradoresConNotificacionEmail(),
                "notificacionesSms", compradorDetallesService.contarCompradoresConNotificacionSms(),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(estadisticas);
    }

    @PostMapping("/notificaciones/email")
    @Operation(summary = "Actualizar preferencias de email")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> actualizarNotificacionesEmail(@RequestBody Map<String, Boolean> request) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        Boolean recibirEmails = request.get("notificacionEmail");

        log.info("🛒 Actualizando preferencias de email para usuario ID: {} a: {}", usuarioId, recibirEmails);

        compradorDetallesService.actualizarNotificacionEmail(usuarioId, recibirEmails);

        Map<String, Object> response = Map.of(
                "mensaje", "Preferencias de email actualizadas",
                "notificacionEmail", recibirEmails,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/notificaciones/sms")
    @Operation(summary = "Actualizar preferencias de SMS")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_COMPRAR_PRODUCTOS + "') or hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "')")
    public ResponseEntity<Map<String, Object>> actualizarNotificacionesSms(@RequestBody Map<String, Boolean> request) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        Boolean recibirSms = request.get("notificacionSms");

        log.info("🛒 Actualizando preferencias de SMS para usuario ID: {} a: {}", usuarioId, recibirSms);

        compradorDetallesService.actualizarNotificacionSms(usuarioId, recibirSms);

        Map<String, Object> response = Map.of(
                "mensaje", "Preferencias de SMS actualizadas",
                "notificacionSms", recibirSms,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    // Método auxiliar para obtener usuario autenticado
    private Long obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
        return usuario.getUsuarioId();
    }
}
