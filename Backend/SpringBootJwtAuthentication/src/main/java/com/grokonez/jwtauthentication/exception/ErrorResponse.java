package com.grokonez.jwtauthentication.exception;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Standard error response DTO for API responses
 * Provides consistent error information across all endpoints
 */
@Data
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String errorCode;
    private String path;
    private Object details;

    /**
     * Default constructor
     */
    public ErrorResponse() {
    }

    /**
     * Constructor with basic error info
     */
    public ErrorResponse(int status, String error, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * Constructor with error code
     */
    public ErrorResponse(int status, String error, String message, String errorCode) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.errorCode = errorCode;
    }
}

