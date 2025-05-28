package com.digital.mecommerces.controller;

import com.digital.mecommerces.constants.RoleConstants;
import com.digital.mecommerces.service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para gestión de imágenes del sistema
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/imagenes")
@Tag(name = "Gestión de Imágenes", description = "APIs para subir, obtener y gestionar imágenes del sistema")
@Slf4j
public class ImagenController {

    private final ImageStorageService imageStorageService;

    // Tipos de contenido de imagen permitidos
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    // Tamaño máximo de archivo (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public ImagenController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/upload")
    @Operation(summary = "Subir imagen", description = "Sube una imagen al sistema con validaciones")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or " +
            "hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "') or " +
            "hasAuthority('" + RoleConstants.PERM_GESTIONAR_CATEGORIAS + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "tipo", defaultValue = "producto") String tipo,
            @RequestParam(value = "categoria", required = false) String categoria) {

        log.info("📸 Subiendo imagen de tipo: {}, tamaño: {} bytes", tipo, file.getSize());

        try {
            // Validar archivo
            Map<String, Object> validationResult = validateFile(file);
            if (!(Boolean) validationResult.get("valid")) {
                return ResponseEntity.badRequest().body(validationResult);
            }

            // Validar tipo
            if (!isValidImageType(tipo)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "mensaje", "Tipo de imagen inválido. Tipos permitidos: producto, categoria, usuario, sistema",
                        "timestamp", LocalDateTime.now()
                ));
            }

            // Guardar archivo
            String url = imageStorageService.store(file, tipo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("url", url);
            response.put("mensaje", "Imagen subida exitosamente");
            response.put("tipo", tipo);
            response.put("categoria", categoria);
            response.put("nombreOriginal", file.getOriginalFilename());
            response.put("tamanio", file.getSize());
            response.put("contentType", file.getContentType());
            response.put("timestamp", LocalDateTime.now());

            log.info("✅ Imagen subida exitosamente: {}", url);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error subiendo imagen: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "mensaje", "Error subiendo imagen: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/upload-multiple")
    @Operation(summary = "Subir múltiples imágenes")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or " +
            "hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> uploadMultipleImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "tipo", defaultValue = "producto") String tipo) {

        log.info("📸 Subiendo {} imágenes de tipo: {}", files.length, tipo);

        if (files.length > 10) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "mensaje", "Máximo 10 imágenes permitidas por solicitud",
                    "timestamp", LocalDateTime.now()
            ));
        }

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> resultados = new java.util.ArrayList<>();
        int exitosos = 0;
        int fallidos = 0;

        for (MultipartFile file : files) {
            try {
                Map<String, Object> validationResult = validateFile(file);
                if ((Boolean) validationResult.get("valid")) {
                    String url = imageStorageService.store(file, tipo);

                    Map<String, Object> resultado = new HashMap<>();
                    resultado.put("success", true);
                    resultado.put("url", url);
                    resultado.put("nombreOriginal", file.getOriginalFilename());
                    resultado.put("tamanio", file.getSize());

                    resultados.add(resultado);
                    exitosos++;
                } else {
                    Map<String, Object> resultado = new HashMap<>();
                    resultado.put("success", false);
                    resultado.put("error", validationResult.get("mensaje"));
                    resultado.put("nombreOriginal", file.getOriginalFilename());

                    resultados.add(resultado);
                    fallidos++;
                }
            } catch (Exception e) {
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("success", false);
                resultado.put("error", e.getMessage());
                resultado.put("nombreOriginal", file.getOriginalFilename());

                resultados.add(resultado);
                fallidos++;
            }
        }

        response.put("success", exitosos > 0);
        response.put("mensaje", String.format("Procesadas %d imágenes: %d exitosas, %d fallidas",
                files.length, exitosos, fallidos));
        response.put("resultados", resultados);
        response.put("exitosos", exitosos);
        response.put("fallidos", fallidos);
        response.put("timestamp", LocalDateTime.now());

        log.info("✅ Proceso de subida múltiple completado: {} exitosos, {} fallidos", exitosos, fallidos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{filename}")
    @Operation(summary = "Obtener imagen", description = "Descarga una imagen por su nombre de archivo")
    public ResponseEntity<Resource> obtenerImagen(@PathVariable String filename) {
        log.debug("📸 Obteniendo imagen: {}", filename);

        try {
            Resource resource = imageStorageService.load(filename);

            if (resource.exists() && resource.isReadable()) {
                // Determinar tipo de contenido
                String contentType = determineContentType(filename);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                log.warn("❌ Imagen no encontrada o no legible: {}", filename);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("❌ Error obteniendo imagen {}: {}", filename, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{filename}")
    @Operation(summary = "Eliminar imagen")
    @PreAuthorize("hasAuthority('" + RoleConstants.PERM_ADMIN_TOTAL + "') or " +
            "hasAuthority('" + RoleConstants.PERM_VENDER_PRODUCTOS + "')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> eliminarImagen(@PathVariable String filename) {
        log.info("📸 Eliminando imagen: {}", filename);

        try {
            boolean deleted = imageStorageService.delete(filename);

            if (deleted) {
                Map<String, Object> response = Map.of(
                        "success", true,
                        "mensaje", "Imagen eliminada exitosamente",
                        "filename", filename,
                        "timestamp", LocalDateTime.now()
                );

                log.info("✅ Imagen eliminada exitosamente: {}", filename);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("❌ Error eliminando imagen {}: {}", filename, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "mensaje", "Error eliminando imagen: " + e.getMessage(),
                    "filename", filename,
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/info/{filename}")
    @Operation(summary = "Obtener información de imagen")
    public ResponseEntity<Map<String, Object>> obtenerInfoImagen(@PathVariable String filename) {
        log.debug("📸 Obteniendo información de imagen: {}", filename);

        try {
            Resource resource = imageStorageService.load(filename);

            if (resource.exists()) {
                Map<String, Object> info = new HashMap<>();
                info.put("filename", filename);
                info.put("exists", true);
                info.put("readable", resource.isReadable());
                info.put("contentLength", resource.contentLength());
                info.put("lastModified", resource.lastModified());
                info.put("contentType", determineContentType(filename));
                info.put("url", "/api/imagenes/" + filename);
                info.put("timestamp", LocalDateTime.now());

                return ResponseEntity.ok(info);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("❌ Error obteniendo información de imagen {}: {}", filename, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error obteniendo información de imagen",
                    "filename", filename,
                    "detalle", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/tipos")
    @Operation(summary = "Obtener tipos de imagen permitidos")
    public ResponseEntity<Map<String, Object>> obtenerTiposPermitidos() {
        Map<String, Object> info = Map.of(
                "tiposPermitidos", Arrays.asList("producto", "categoria", "usuario", "sistema"),
                "formatosPermitidos", Arrays.asList("jpg", "jpeg", "png", "gif", "webp"),
                "tamanosMaximos", Map.of(
                        "fileSize", "5MB",
                        "dimensions", "No hay límite específico"
                ),
                "contentTypesPermitidos", ALLOWED_CONTENT_TYPES,
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(info);
    }

    // Métodos privados de utilidad

    private Map<String, Object> validateFile(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        // Verificar que el archivo no esté vacío
        if (file.isEmpty()) {
            result.put("valid", false);
            result.put("mensaje", "El archivo está vacío");
            return result;
        }

        // Verificar tipo de contenido
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            result.put("valid", false);
            result.put("mensaje", "Tipo de archivo no permitido. Tipos permitidos: " + ALLOWED_CONTENT_TYPES);
            return result;
        }

        // Verificar tamaño
        if (file.getSize() > MAX_FILE_SIZE) {
            result.put("valid", false);
            result.put("mensaje", "El archivo excede el tamaño máximo de 5MB");
            return result;
        }

        // Verificar nombre de archivo
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            result.put("valid", false);
            result.put("mensaje", "Nombre de archivo inválido");
            return result;
        }

        result.put("valid", true);
        result.put("mensaje", "Archivo válido");
        return result;
    }

    private boolean isValidImageType(String tipo) {
        return Arrays.asList("producto", "categoria", "usuario", "sistema").contains(tipo.toLowerCase());
    }

    private String determineContentType(String filename) {
        if (filename == null) return "application/octet-stream";

        String lowerCaseFilename = filename.toLowerCase();

        if (lowerCaseFilename.endsWith(".jpg") || lowerCaseFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerCaseFilename.endsWith(".png")) {
            return "image/png";
        } else if (lowerCaseFilename.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerCaseFilename.endsWith(".webp")) {
            return "image/webp";
        } else {
            return "application/octet-stream";
        }
    }
}
