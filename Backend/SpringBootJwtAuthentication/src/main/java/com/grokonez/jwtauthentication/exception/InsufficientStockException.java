package com.grokonez.jwtauthentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for insufficient stock/inventory
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InsufficientStockException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private Long productId;
    private Integer requestedQuantity;
    private Integer availableQuantity;

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Long productId, Integer requested, Integer available) {
        super(message);
        this.productId = productId;
        this.requestedQuantity = requested;
        this.availableQuantity = available;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
}

