package com.grokonez.jwtauthentication.pattern.strategy;

import java.math.BigDecimal;

/**
 * Pricing Strategy Interface - Implements Strategy Design Pattern
 * Allows runtime selection of pricing algorithms
 */
public interface PricingStrategy {

    /**
     * Calculate final price based on strategy
     *
     * @param basePrice Original price
     * @param quantity Number of items
     * @return Final calculated price
     */
    BigDecimal calculatePrice(BigDecimal basePrice, int quantity);

    /**
     * Get strategy name
     */
    String getStrategyName();
}

