package com.digital.mecommerces.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class ImageStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-file-size:5242880}") // 5MB por defecto
    private long maxFileSize;

    private Path fileStorageLocation;

    public ImageStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Directorio de almacenamiento creado en: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("No se pudo crear el directorio de almacenamiento", ex);
            throw new RuntimeException("No se pudo crear el directorio de almacenamiento", ex);
        }
    }

    public String store(MultipartFile file, String tipo) {
        try {
            // Validar archivo
            if (file.isEmpty()) {
                throw new RuntimeException("No se puede almacenar un archivo vacío");
            }

            // Validar tamaño
            if (file.getSize() > maxFileSize) {
                throw new RuntimeException("El archivo excede el tamaño máximo permitido");
            }

            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Solo se permiten archivos de imagen");
            }

            // Limpiar el nombre del archivo
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName);

            // Generar nombre único
            String fileName = UUID.randomUUID().toString() + "_" + tipo + fileExtension;

            // Verificar que el nombre no contenga caracteres peligrosos
            if (fileName.contains("..")) {
                throw new RuntimeException("El nombre del archivo contiene una secuencia de ruta no válida: " + fileName);
            }

            // Crear el directorio específico por tipo si no existe
            Path tipoPath = this.fileStorageLocation.resolve(tipo);
            Files.createDirectories(tipoPath);

            // Copiar archivo al directorio de destino
            Path targetLocation = tipoPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("Archivo almacenado exitosamente: {}", targetLocation);

            // Retornar la URL relativa
            return "/uploads/" + tipo + "/" + fileName;

        } catch (IOException ex) {
            log.error("Error almacenando archivo", ex);
            throw new RuntimeException("Error almacenando archivo", ex);
        }
    }

    public boolean delete(String fileName) {
        try {
            // Buscar el archivo en todos los subdirectorios
            String[] tipos = {"producto", "usuario", "categoria", "general"};

            for (String tipo : tipos) {
                Path filePath = this.fileStorageLocation.resolve(tipo).resolve(fileName);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("Archivo eliminado exitosamente: {}", filePath);
                    return true;
                }
            }

            log.warn("Archivo no encontrado para eliminar: {}", fileName);
            return false;

        } catch (IOException ex) {
            log.error("Error eliminando archivo: {}", fileName, ex);
            return false;
        }
    }

    public Path load(String fileName, String tipo) {
        return this.fileStorageLocation.resolve(tipo).resolve(fileName);
    }

    public boolean exists(String fileName, String tipo) {
        Path filePath = this.fileStorageLocation.resolve(tipo).resolve(fileName);
        return Files.exists(filePath);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }
}
