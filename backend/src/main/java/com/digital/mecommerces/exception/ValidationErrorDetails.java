package com.digital.mecommerces.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorDetails extends ErrorDetails {
    private Map<String, String> validationErrors;

    public ValidationErrorDetails() {
        super();
    }

    public ValidationErrorDetails(LocalDateTime timestamp, String message, String errorCode, String details, Map<String, String> validationErrors) {
        super(timestamp, message, errorCode, details);
        this.validationErrors = validationErrors;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
