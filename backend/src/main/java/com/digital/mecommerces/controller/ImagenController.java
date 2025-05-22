package com.digital.mecommerces.controller;

import com.digital.mecommerces.service.ImageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/imagenes")
@Slf4j
public class ImagenController {

    @Autowired
    private ImageStorageService imageStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "tipo", defaultValue = "producto") String tipo) {
        try {
            log.info("Subiendo imagen de tipo: {}, tamaño: {} bytes", tipo, file.getSize());

            // Validar archivo
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "mensaje", "Archivo vacío"
                ));
            }

            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "mensaje", "El archivo debe ser una imagen"
                ));
            }

            // Validar tamaño (máximo 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "mensaje", "El archivo no puede ser mayor a 5MB"
                ));
            }

            // Guardar archivo
            String url = imageStorageService.store(file, tipo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("url", url);
            response.put("mensaje", "Imagen subida exitosamente");
            response.put("tamanio", file.getSize());
            response.put("tipo", contentType);

            log.info("Imagen subida exitosamente: {}", url);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error subiendo imagen: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "mensaje", "Error subiendo imagen: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable String filename) {
        try {
            log.info("Eliminando imagen: {}", filename);

            boolean deleted = imageStorageService.delete(filename);

            if (deleted) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "mensaje", "Imagen eliminada exitosamente"
                ));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            log.error("Error eliminando imagen: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "mensaje", "Error eliminando imagen: " + e.getMessage()
            ));
        }
    }
}
