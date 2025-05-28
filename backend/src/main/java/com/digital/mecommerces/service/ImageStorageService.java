package com.digital.mecommerces.service;

import com.digital.mecommerces.exception.BusinessException;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ImageStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-file-size:5242880}") // 5MB por defecto
    private long maxFileSize;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private final List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private final List<String> allowedMimeTypes = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private Path fileStorageLocation;

    public ImageStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
            // Crear subdirectorios para diferentes tipos de imágenes
            Files.createDirectories(this.fileStorageLocation.resolve("productos"));
            Files.createDirectories(this.fileStorageLocation.resolve("perfiles"));
            Files.createDirectories(this.fileStorageLocation.resolve("categorias"));
            Files.createDirectories(this.fileStorageLocation.resolve("temp"));

            log.info("✅ Directorio de almacenamiento de imágenes creado en: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("❌ No se pudo crear el directorio de almacenamiento de imágenes", ex);
            throw new BusinessException("No se pudo crear el directorio de almacenamiento de imágenes");
        }
    }

    public String almacenarImagenProducto(MultipartFile archivo) {
        return almacenarImagen(archivo, "productos");
    }

    public String almacenarImagenPerfil(MultipartFile archivo) {
        return almacenarImagen(archivo, "perfiles");
    }

    public String almacenarImagenCategoria(MultipartFile archivo) {
        return almacenarImagen(archivo, "categorias");
    }

    public String almacenarImagenTemporal(MultipartFile archivo) {
        return almacenarImagen(archivo, "temp");
    }

    public String almacenarImagen(MultipartFile archivo, String tipo) {
        log.info("📁 Almacenando imagen tipo: {} - Archivo: {}", tipo, archivo.getOriginalFilename());

        // Validaciones básicas
        validarArchivo(archivo);

        try {
            // Limpiar el nombre del archivo
            String nombreOriginal = StringUtils.cleanPath(archivo.getOriginalFilename());

            // Generar nuevo nombre único
            String extension = obtenerExtensionArchivo(nombreOriginal);
            String nuevoNombre = UUID.randomUUID().toString() + "." + extension;

            // Crear directorio del tipo si no existe
            Path directorioTipo = this.fileStorageLocation.resolve(tipo);
            Files.createDirectories(directorioTipo);

            // Ruta completa del archivo
            Path rutaDestino = directorioTipo.resolve(nuevoNombre);

            // Verificar que el archivo no contiene ".." en el path
            if (rutaDestino.normalize().toString().contains("..")) {
                throw new BusinessException("Nombre de archivo inválido: " + nombreOriginal);
            }

            // Copiar archivo al destino
            Files.copy(archivo.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

            // Generar URL pública
            String urlPublica = generarUrlPublica(tipo, nuevoNombre);

            log.info("✅ Imagen almacenada exitosamente: {}", urlPublica);
            return urlPublica;

        } catch (IOException ex) {
            log.error("❌ Error almacenando imagen: {}", ex.getMessage());
            throw new BusinessException("Error almacenando imagen: " + ex.getMessage());
        }
    }

    public boolean eliminarImagen(String nombreArchivo, String tipo) {
        log.info("🗑️ Eliminando imagen: {} del tipo: {}", nombreArchivo, tipo);

        try {
            Path rutaArchivo = this.fileStorageLocation.resolve(tipo).resolve(nombreArchivo);
            boolean eliminado = Files.deleteIfExists(rutaArchivo);

            if (eliminado) {
                log.info("✅ Imagen eliminada exitosamente: {}", nombreArchivo);
            } else {
                log.warn("⚠️ Imagen no encontrada para eliminar: {}", nombreArchivo);
            }

            return eliminado;

        } catch (IOException ex) {
            log.error("❌ Error eliminando imagen: {}", ex.getMessage());
            return false;
        }
    }

    public boolean eliminarImagenPorUrl(String url) {
        log.info("🗑️ Eliminando imagen por URL: {}", url);

        try {
            // Extraer tipo y nombre del archivo de la URL
            String[] partes = extraerInfoDeUrl(url);
            if (partes.length != 2) {
                log.warn("⚠️ URL de imagen inválida: {}", url);
                return false;
            }

            String tipo = partes[0];
            String nombreArchivo = partes[1];

            return eliminarImagen(nombreArchivo, tipo);

        } catch (Exception e) {
            log.error("❌ Error eliminando imagen por URL: {}", e.getMessage());
            return false;
        }
    }

    public Path cargarImagen(String nombreArchivo, String tipo) {
        return this.fileStorageLocation.resolve(tipo).resolve(nombreArchivo);
    }

    public boolean existeImagen(String nombreArchivo, String tipo) {
        Path rutaArchivo = this.fileStorageLocation.resolve(tipo).resolve(nombreArchivo);
        return Files.exists(rutaArchivo);
    }

    public long obtenerTamanoImagen(String nombreArchivo, String tipo) {
        try {
            Path rutaArchivo = this.fileStorageLocation.resolve(tipo).resolve(nombreArchivo);
            return Files.size(rutaArchivo);
        } catch (IOException e) {
            log.error("❌ Error obteniendo tamaño de imagen: {}", e.getMessage());
            return 0;
        }
    }

    public String moverImagenDeTemporal(String nombreArchivo, String tipoDestino) {
        log.info("📦 Moviendo imagen de temporal a: {}", tipoDestino);

        try {
            Path rutaOrigen = this.fileStorageLocation.resolve("temp").resolve(nombreArchivo);

            if (!Files.exists(rutaOrigen)) {
                throw new BusinessException("Imagen temporal no encontrada: " + nombreArchivo);
            }

            // Crear directorio destino si no existe
            Path directorioDestino = this.fileStorageLocation.resolve(tipoDestino);
            Files.createDirectories(directorioDestino);

            Path rutaDestino = directorioDestino.resolve(nombreArchivo);

            // Mover archivo
            Files.move(rutaOrigen, rutaDestino, StandardCopyOption.REPLACE_EXISTING);

            String nuevaUrl = generarUrlPublica(tipoDestino, nombreArchivo);
            log.info("✅ Imagen movida exitosamente a: {}", nuevaUrl);

            return nuevaUrl;

        } catch (IOException ex) {
            log.error("❌ Error moviendo imagen: {}", ex.getMessage());
            throw new BusinessException("Error moviendo imagen: " + ex.getMessage());
        }
    }

    public void limpiarImagenesTemporales() {
        log.info("🧹 Limpiando imágenes temporales antiguas");

        try {
            Path directorioTemp = this.fileStorageLocation.resolve("temp");

            if (!Files.exists(directorioTemp)) {
                return;
            }

            Files.list(directorioTemp)
                    .filter(Files::isRegularFile)
                    .forEach(archivo -> {
                        try {
                            long tiempoCreacion = Files.getLastModifiedTime(archivo).toMillis();
                            long ahora = System.currentTimeMillis();
                            long diferenciaHoras = (ahora - tiempoCreacion) / (1000 * 60 * 60);

                            // Eliminar archivos temporales de más de 24 horas
                            if (diferenciaHoras > 24) {
                                Files.delete(archivo);
                                log.debug("🗑️ Imagen temporal eliminada: {}", archivo.getFileName());
                            }
                        } catch (IOException e) {
                            log.warn("⚠️ Error eliminando imagen temporal: {}", e.getMessage());
                        }
                    });

            log.info("✅ Limpieza de imágenes temporales completada");

        } catch (IOException e) {
            log.error("❌ Error durante limpieza de imágenes temporales: {}", e.getMessage());
        }
    }

    // Métodos privados de validación y utilidad

    private void validarArchivo(MultipartFile archivo) {
        // Verificar que el archivo no esté vacío
        if (archivo.isEmpty()) {
            throw new BusinessException("El archivo está vacío");
        }

        // Verificar tamaño
        if (archivo.getSize() > maxFileSize) {
            throw new BusinessException("El archivo es demasiado grande. Máximo permitido: " +
                    (maxFileSize / 1024 / 1024) + "MB");
        }

        // Verificar tipo MIME
        String mimeType = archivo.getContentType();
        if (mimeType == null || !allowedMimeTypes.contains(mimeType.toLowerCase())) {
            throw new BusinessException("Tipo de archivo no permitido. Tipos permitidos: " +
                    String.join(", ", allowedMimeTypes));
        }

        // Verificar extensión
        String nombreArchivo = archivo.getOriginalFilename();
        if (nombreArchivo == null) {
            throw new BusinessException("Nombre de archivo inválido");
        }

        String extension = obtenerExtensionArchivo(nombreArchivo);
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new BusinessException("Extensión de archivo no permitida. Extensiones permitidas: " +
                    String.join(", ", allowedExtensions));
        }
    }

    private String obtenerExtensionArchivo(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.lastIndexOf('.') == -1) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1);
    }

    private String generarUrlPublica(String tipo, String nombreArchivo) {
        return baseUrl + "/api/imagenes/" + tipo + "/" + nombreArchivo;
    }

    private String[] extraerInfoDeUrl(String url) {
        try {
            // Ejemplo: http://localhost:8080/api/imagenes/productos/imagen.jpg
            String[] partes = url.split("/");
            if (partes.length >= 3) {
                String tipo = partes[partes.length - 2];
                String nombreArchivo = partes[partes.length - 1];
                return new String[]{tipo, nombreArchivo};
            }
        } catch (Exception e) {
            log.warn("⚠️ Error extrayendo información de URL: {}", e.getMessage());
        }

        return new String[0];
    }

    // Getters para configuración
    public String getUploadDir() {
        return uploadDir;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public List<String> getAllowedMimeTypes() {
        return allowedMimeTypes;
    }
}
