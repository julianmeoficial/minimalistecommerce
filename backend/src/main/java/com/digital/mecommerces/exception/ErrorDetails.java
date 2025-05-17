package com.digital.mecommerces.exception;

import java.time.LocalDateTime;

public class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private String errorCode;
    private String details;

    public ErrorDetails() {
    }

    public ErrorDetails(LocalDateTime timestamp, String message, String errorCode, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.errorCode = errorCode;
        this.details = details;
    }

    // Getters y setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
