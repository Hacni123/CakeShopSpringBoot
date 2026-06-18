package com.grokonez.jwtauthentication.exception;

/**
 * Error codes for standardized error handling
 * Implements enumeration pattern for type-safety
 */
public enum ErrorCode {

    RESOURCE_NOT_FOUND("ERR_001", "Resource not found"),
    INVALID_INPUT("ERR_002", "Invalid input provided"),
    INSUFFICIENT_STOCK("ERR_003", "Insufficient stock available"),
    PAYMENT_FAILED("ERR_004", "Payment processing failed"),
    AUTHENTICATION_FAILED("ERR_005", "Authentication failed"),
    AUTHORIZATION_FAILED("ERR_006", "Authorization failed"),
    DUPLICATE_RESOURCE("ERR_007", "Resource already exists"),
    DATABASE_ERROR("ERR_008", "Database operation failed"),
    INTERNAL_SERVER_ERROR("ERR_009", "Internal server error"),
    INVALID_OPERATION("ERR_010", "Invalid operation"),
    CONCURRENT_MODIFICATION("ERR_011", "Concurrent modification detected");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

