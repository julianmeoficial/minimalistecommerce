package com.digital.mecommerces.controller;

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

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> status = new HashMap<>();

        try {
            // Test database connection
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(2);
                status.put("database", isValid ? "UP" : "DOWN");
            }
        } catch (Exception e) {
            status.put("database", "DOWN");
            status.put("database_error", e.getMessage());
        }

        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("application", "MeCommerces API");
        status.put("version", "1.0.0");

        return ResponseEntity.ok(status);
    }

    @GetMapping("/simple")
    public ResponseEntity<Map<String, String>> simpleHealth() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(status);
    }

    @GetMapping("/db")
    public ResponseEntity<Map<String, String>> databaseHealth() {
        Map<String, String> status = new HashMap<>();

        try {
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(2)) {
                    status.put("database", "UP");
                    status.put("message", "Database connection successful");
                } else {
                    status.put("database", "DOWN");
                    status.put("message", "Database connection failed");
                }
            }
        } catch (Exception e) {
            status.put("database", "DOWN");
            status.put("message", "Database error: " + e.getMessage());
        }

        return ResponseEntity.ok(status);
    }
}