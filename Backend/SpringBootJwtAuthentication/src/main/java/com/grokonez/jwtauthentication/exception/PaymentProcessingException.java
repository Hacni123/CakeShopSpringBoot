package com.grokonez.jwtauthentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for payment processing failures
 */
@ResponseStatus(value = HttpStatus.PAYMENT_REQUIRED)
public class PaymentProcessingException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String transactionId;
    private String paymentMethod;

    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, String transactionId, String paymentMethod) {
        super(message);
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}

