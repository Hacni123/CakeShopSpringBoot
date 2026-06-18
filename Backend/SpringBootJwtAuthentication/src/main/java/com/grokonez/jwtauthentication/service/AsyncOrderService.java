package com.grokonez.jwtauthentication.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

/**
 * Async Order Service
 * Demonstrates multi-threading using Spring's @Async annotation
 * Performs long-running operations asynchronously
 */
@Service
public class AsyncOrderService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncOrderService.class);

    /**
     * Process order asynchronously
     * Returns CompletableFuture for non-blocking operations
     */
    @Async("orderProcessingExecutor")
    public CompletableFuture<String> processOrderAsync(Long orderId) {
        try {
            logger.info("Starting async order processing for orderId: {}", orderId);
            Thread.sleep(2000); // Simulate long-running operation
            logger.info("Order {} processing completed", orderId);
            return CompletableFuture.completedFuture("Order " + orderId + " processed successfully");
        } catch (InterruptedException e) {
            logger.error("Error processing order: {}", orderId, e);
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Send order confirmation email asynchronously
     */
    @Async("notificationExecutor")
    public void sendOrderConfirmationEmail(Long orderId, String email) {
        try {
            logger.info("Sending order confirmation email to: {} for orderId: {}", email, orderId);
            Thread.sleep(1000); // Simulate email sending
            logger.info("Email sent successfully to: {}", email);
        } catch (InterruptedException e) {
            logger.error("Error sending email for order: {}", orderId, e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Generate order invoice asynchronously
     */
    @Async("asyncExecutor")
    public CompletableFuture<byte[]> generateInvoiceAsync(Long orderId) {
        try {
            logger.info("Generating invoice for orderId: {}", orderId);
            Thread.sleep(1500); // Simulate invoice generation
            logger.info("Invoice generated for orderId: {}", orderId);
            return CompletableFuture.completedFuture(("Invoice for order " + orderId).getBytes());
        } catch (InterruptedException e) {
            logger.error("Error generating invoice for order: {}", orderId, e);
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Update order inventory asynchronously
     */
    @Async("asyncExecutor")
    public void updateInventoryAsync(Long orderId) {
        try {
            logger.info("Updating inventory for orderId: {}", orderId);
            Thread.sleep(1000); // Simulate inventory update
            logger.info("Inventory updated for orderId: {}", orderId);
        } catch (InterruptedException e) {
            logger.error("Error updating inventory for order: {}", orderId, e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Process multiple orders concurrently
     */
    @Async("orderProcessingExecutor")
    public CompletableFuture<Void> processBatchOrders(java.util.List<Long> orderIds) {
        try {
            logger.info("Starting batch processing for {} orders", orderIds.size());
            java.util.List<CompletableFuture<String>> futures = orderIds.stream()
                    .map(this::processOrderAsync)
                    .collect(java.util.stream.Collectors.toList());

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        logger.info("Batch processing completed for {} orders", orderIds.size());
                        return null;
                    });
        } catch (Exception e) {
            logger.error("Error processing batch orders", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}

