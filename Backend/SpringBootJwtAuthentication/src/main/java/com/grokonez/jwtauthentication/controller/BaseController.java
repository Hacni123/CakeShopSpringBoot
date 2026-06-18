package com.grokonez.jwtauthentication.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

/**
 * Base Controller Class
 * Provides common functionality for all controllers
 * Demonstrates abstraction and inheritance (OOP principles)
 * Template method pattern for common operations
 */
public abstract class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Template method for successful response
     */
    protected <T> ResponseEntity<T> success(T data, HttpStatus status) {
        logger.debug("Returning successful response with status: {}", status);
        return new ResponseEntity<>(data, status);
    }

    /**
     * Template method for successful response with OK status
     */
    protected <T> ResponseEntity<T> success(T data) {
        return success(data, HttpStatus.OK);
    }

    /**
     * Template method for created response
     */
    protected <T> ResponseEntity<T> created(T data) {
        return success(data, HttpStatus.CREATED);
    }

    /**
     * Template method for no content response
     */
    protected ResponseEntity<?> noContent() {
        logger.debug("Returning no content response");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Template method for error response
     */
    protected ResponseEntity<?> error(String message, HttpStatus status) {
        logger.error("Returning error response: {} with status: {}", message, status);
        return new ResponseEntity<>(message, status);
    }

    /**
     * Log method entrance
     */
    protected void logMethodEntry(String methodName, Object... args) {
        logger.info("Entering method: {} with args: {}", methodName, args);
    }

    /**
     * Log method exit
     */
    protected void logMethodExit(String methodName) {
        logger.info("Exiting method: {}", methodName);
    }

    /**
     * Log error
     */
    protected void logError(String methodName, Exception ex) {
        logger.error("Error in method {}: {}", methodName, ex.getMessage(), ex);
    }
}

