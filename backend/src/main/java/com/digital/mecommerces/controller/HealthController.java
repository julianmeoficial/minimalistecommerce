package com.digital.mecommerces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para verificaciones de salud del sistema
 * Optimizado para el sistema medbcommerce 3.0
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "APIs para verificación de salud del sistema")
@Slf4j
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping
    @Operation(summary = "Verificación completa de salud", description = "Verifica todos los componentes del sistema")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        log.info("🏥 Ejecutando verificación completa de salud del sistema");

        Map<String, Object> status = new HashMap<>();
        boolean isHealthy = true;

        try {
            // Verificar conexión a base de datos
            Map<String, Object> dbStatus = checkDatabaseConnection();
            status.put("database", dbStatus);

            if (!"UP".equals(dbStatus.get("status"))) {
                isHealthy = false;
            }

            // Verificar memoria del sistema
            Map<String, Object> memoryStatus = checkMemoryUsage();
            status.put("memory", memoryStatus);

            // Verificar espacio en disco
            Map<String, Object> diskStatus = checkDiskSpace();
            status.put("disk", diskStatus);

            // Estado general del sistema
            status.put("status", isHealthy ? "UP" : "DOWN");
            status.put("timestamp", LocalDateTime.now());
            status.put("application", "MeCommerces API");
            status.put("version", "3.0.0");
            status.put("environment", System.getProperty("spring.profiles.active", "development"));

            log.info("✅ Verificación de salud completada - Estado: {}", isHealthy ? "SALUDABLE" : "CON PROBLEMAS");
            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("❌ Error en verificación de salud: {}", e.getMessage());
            status.put("status", "DOWN");
            status.put("error", e.getMessage());
            status.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(503).body(status);
        }
    }

    @GetMapping("/simple")
    @Operation(summary = "Verificación simple de salud")
    public ResponseEntity<Map<String, String>> simpleHealth() {
        log.debug("🏥 Verificación simple de salud");

        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("service", "MeCommerces API");

        return ResponseEntity.ok(status);
    }

    @GetMapping("/database")
    @Operation(summary = "Verificación específica de base de datos")
    public ResponseEntity<Map<String, Object>> databaseHealth() {
        log.info("🏥 Verificando salud de base de datos");

        Map<String, Object> status = checkDatabaseConnection();

        if ("UP".equals(status.get("status"))) {
            return ResponseEntity.ok(status);
        } else {
            return ResponseEntity.status(503).body(status);
        }
    }

    @GetMapping("/memory")
    @Operation(summary = "Verificación de uso de memoria")
    public ResponseEntity<Map<String, Object>> memoryHealth() {
        log.info("🏥 Verificando uso de memoria");

        Map<String, Object> memoryStatus = checkMemoryUsage();
        return ResponseEntity.ok(memoryStatus);
    }

    @GetMapping("/disk")
    @Operation(summary = "Verificación de espacio en disco")
    public ResponseEntity<Map<String, Object>> diskHealth() {
        log.info("🏥 Verificando espacio en disco");

        Map<String, Object> diskStatus = checkDiskSpace();
        return ResponseEntity.ok(diskStatus);
    }

    @GetMapping("/detailed")
    @Operation(summary = "Verificación detallada del sistema")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        log.info("🏥 Ejecutando verificación detallada del sistema");

        Map<String, Object> detailedStatus = new HashMap<>();

        // Información del sistema
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        systemInfo.put("osArch", System.getProperty("os.arch"));
        systemInfo.put("userTimezone", System.getProperty("user.timezone"));

        // Información de la JVM
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> jvmInfo = new HashMap<>();
        jvmInfo.put("availableProcessors", runtime.availableProcessors());
        jvmInfo.put("totalMemoryMB", runtime.totalMemory() / (1024 * 1024));
        jvmInfo.put("freeMemoryMB", runtime.freeMemory() / (1024 * 1024));
        jvmInfo.put("maxMemoryMB", runtime.maxMemory() / (1024 * 1024));
        jvmInfo.put("usedMemoryMB", (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024));

        detailedStatus.put("system", systemInfo);
        detailedStatus.put("jvm", jvmInfo);
        detailedStatus.put("database", checkDatabaseConnection());
        detailedStatus.put("memory", checkMemoryUsage());
        detailedStatus.put("disk", checkDiskSpace());
        detailedStatus.put("timestamp", LocalDateTime.now());
        detailedStatus.put("uptime", getSystemUptime());

        return ResponseEntity.ok(detailedStatus);
    }

    // Métodos privados para verificaciones específicas

    private Map<String, Object> checkDatabaseConnection() {
        Map<String, Object> dbStatus = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(5); // Timeout de 5 segundos

            if (isValid) {
                dbStatus.put("status", "UP");
                dbStatus.put("message", "Base de datos conectada correctamente");
                dbStatus.put("connectionTimeout", "5s");

                // Información adicional de la base de datos
                dbStatus.put("databaseProductName", connection.getMetaData().getDatabaseProductName());
                dbStatus.put("databaseProductVersion", connection.getMetaData().getDatabaseProductVersion());
                dbStatus.put("driverName", connection.getMetaData().getDriverName());
                dbStatus.put("driverVersion", connection.getMetaData().getDriverVersion());
            } else {
                dbStatus.put("status", "DOWN");
                dbStatus.put("message", "La conexión a base de datos falló");
            }

        } catch (Exception e) {
            log.error("❌ Error conectando a base de datos: {}", e.getMessage());
            dbStatus.put("status", "DOWN");
            dbStatus.put("message", "Error de conexión a base de datos");
            dbStatus.put("error", e.getMessage());
        }

        return dbStatus;
    }

    private Map<String, Object> checkMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        double usedPercentage = (double) usedMemory / maxMemory * 100;

        Map<String, Object> memoryStatus = new HashMap<>();
        memoryStatus.put("totalMB", totalMemory / (1024 * 1024));
        memoryStatus.put("freeMB", freeMemory / (1024 * 1024));
        memoryStatus.put("usedMB", usedMemory / (1024 * 1024));
        memoryStatus.put("maxMB", maxMemory / (1024 * 1024));
        memoryStatus.put("usedPercentage", Math.round(usedPercentage * 100.0) / 100.0);

        // Determinar estado basado en uso de memoria
        if (usedPercentage < 70) {
            memoryStatus.put("status", "UP");
            memoryStatus.put("message", "Uso de memoria normal");
        } else if (usedPercentage < 85) {
            memoryStatus.put("status", "WARNING");
            memoryStatus.put("message", "Uso de memoria alto");
        } else {
            memoryStatus.put("status", "DOWN");
            memoryStatus.put("message", "Uso de memoria crítico");
        }

        return memoryStatus;
    }

    private Map<String, Object> checkDiskSpace() {
        Map<String, Object> diskStatus = new HashMap<>();

        try {
            java.io.File root = new java.io.File("/");
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;

            double usedPercentage = (double) usedSpace / totalSpace * 100;

            diskStatus.put("totalGB", totalSpace / (1024 * 1024 * 1024));
            diskStatus.put("freeGB", freeSpace / (1024 * 1024 * 1024));
            diskStatus.put("usedGB", usedSpace / (1024 * 1024 * 1024));
            diskStatus.put("usedPercentage", Math.round(usedPercentage * 100.0) / 100.0);

            // Determinar estado basado en espacio disponible
            if (usedPercentage < 80) {
                diskStatus.put("status", "UP");
                diskStatus.put("message", "Espacio en disco suficiente");
            } else if (usedPercentage < 90) {
                diskStatus.put("status", "WARNING");
                diskStatus.put("message", "Espacio en disco bajo");
            } else {
                diskStatus.put("status", "DOWN");
                diskStatus.put("message", "Espacio en disco crítico");
            }

        } catch (Exception e) {
            log.error("❌ Error verificando espacio en disco: {}", e.getMessage());
            diskStatus.put("status", "DOWN");
            diskStatus.put("message", "Error verificando espacio en disco");
            diskStatus.put("error", e.getMessage());
        }

        return diskStatus;
    }

    private String getSystemUptime() {
        try {
            long uptime = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
            long seconds = (uptime / 1000) % 60;
            long minutes = (uptime / (1000 * 60)) % 60;
            long hours = (uptime / (1000 * 60 * 60)) % 24;
            long days = uptime / (1000 * 60 * 60 * 24);

            return String.format("%d días, %d horas, %d minutos, %d segundos", days, hours, minutes, seconds);
        } catch (Exception e) {
            return "No disponible";
        }
    }
}
